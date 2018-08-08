//
//  DiscoverMainVC.m
//  Ees
//
//  Created by xiaodong on 2018/3/13.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import "DiscoverMainVC.h"

#define kDiscoverCellIdentifier @"DiscoverListCell"


@interface DiscoverMainVC ()
{
    NSTimeInterval _lastInterval;
    NSTimeInterval _refreshLastInterval;
}

@property(nonatomic,strong) NSMutableArray *sourceArray;

@property(nonatomic,strong) EXTabBarItem *tabBarItem;
@property(nonatomic,weak) IBOutlet UITableView *tableView;
@property(nonatomic,weak) IBOutlet UIImageView *imageView;


@end



@implementation DiscoverMainVC


#pragma mark-
#pragma mark-----overide---

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self.navigationController setNavigationBarHidden:NO];
    self.tabBarController.tabBar.hidden = NO;
    
    if ([ConfigManager sharedInstance].isChangeSkin) {
        UIImage *selectImage = [UIImage imageNamed:@"tab_person_press_skin.png"];
        self.tabBarItem.selectedImage = [selectImage imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];
        [self.tabBarItem showBadge];
    }
    UIColor *color = kTabBarItemTextColor;
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
    [[UITabBarItem appearance] setTitleTextAttributes:[NSDictionary dictionaryWithObjectsAndKeys:color,UITextAttributeTextColor, nil] forState:UIControlStateSelected];
#pragma clang diagnostic pop
    
    NSTimeInterval curInterval = [[NSDate date] timeIntervalSince1970];
    int hour = 2;
    if (curInterval - _lastInterval >= hour*60*60) {
        [self getDiscoverList];
    }
    _lastInterval = curInterval;
}


- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    if ([ConfigManager sharedInstance].isChangeSkin) {
        [self.tabBarItem hidenBadge];
    }
}


- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.title = @"发现";
    self.imageView.hidden = YES;
    _sourceArray = [NSMutableArray new];
    
    
    self.tabBarItem = (EXTabBarItem *)self.navigationController.tabBarItem;
    self.automaticallyAdjustsScrollViewInsets = NO;
    self.view.backgroundColor = [UIColor whiteColor];
    [self.tableView registerNib:[UINib nibWithNibName:@"DiscoverListCell" bundle:nil] forCellReuseIdentifier:kDiscoverCellIdentifier];
    
    self.tableView.backgroundColor = kViewBackgroundColor;
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.tableView.contentInset = UIEdgeInsetsMake(-15, 0, 10, 0);
    self.tableView.showsVerticalScrollIndicator = NO;
    
    __unsafe_unretained UITableView *tableView = self.tableView;
    
    tableView.mj_header= [MJRefreshNormalHeader headerWithRefreshingBlock:^{
        NSTimeInterval curInterval = [[NSDate date] timeIntervalSince1970];
        if (curInterval - _refreshLastInterval >= 10) {
            [self getDiscoverList];
        } else {
            [self.tableView.mj_header endRefreshing];
        }
        _refreshLastInterval = curInterval;
    }];

    // 设置自动切换透明度(在导航栏下面自动隐藏)
    tableView.mj_header.automaticallyChangeAlpha = YES;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

#pragma mark-
#pragma mark-----private---

- (void)getDiscoverList {
    NSString *pid = [UserInfoManager sharedInstance].userItem.pid;
    NSString *token = [UserInfoManager sharedInstance].userItem.token;
    
    
    NSUserDefaults *userDef = [NSUserDefaults standardUserDefaults];
    NSInteger version = [userDef integerForKey:kDiscoverVersion];
    NSString *strVersion = [NSString stringWithFormat:@"%ld",version];
    
    NSDictionary *dic = @{@"secret":kSecret,@"pid":pid,@"token":token,@"version":strVersion};
    [HttpRequestEngin discoverListWithParam:dic successBlock:^(id  _Nullable responseObject) {
        RespondModel *resModel = [[RespondModel alloc] initObjectsWithDictionary:responseObject objectClass:[DiscoverModel class] objectError:nil];
        if (resModel.success && resModel.objects) {
            self.sourceArray = [NSMutableArray arrayWithArray:[self sortList:resModel.objects]];
            dispatch_async(dispatch_get_main_queue(), ^{
                [self.tableView reloadData];
                [self.tableView.mj_header endRefreshing];
            });
            
            BOOL writeSuccess = [FileTool writeToPlistFile:kDiscoverFileName dic:responseObject];
            if (writeSuccess) {
                [userDef setInteger:resModel.version forKey:kDiscoverVersion];
                [userDef synchronize];
            }
            
            
        } else {
            ErrorModel *model = resModel.error;
            if ([model.code isEqualToString:@"CODE_SYNC_EQUAL"]) {
                NSDictionary *dic = [FileTool readFromPlistFile:kDiscoverFileName];
                RespondModel *resModel = [[RespondModel alloc] initObjectsWithDictionary:dic objectClass:[DiscoverModel class] objectError:nil];
                if (resModel.success && resModel.objects) {
                    self.sourceArray = [NSMutableArray arrayWithArray:[self sortList:resModel.objects]];
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [self.tableView reloadData];
                        [self.tableView.mj_header endRefreshing];
                    });
                }
            } else {
                ErrorCodeEvent *event = [[ErrorCodeEvent alloc] initWithError:model];
                [event errorCodeDone:self needLoading:NO];
            }
        }
    } failBlock:^(NSError * _Nonnull error) {
        
    } progress:^(NSProgress * _Nonnull uploadProgress) {
        
    }];
}

- (NSMutableArray *)sortList:(NSArray *)array {
    NSSortDescriptor *sorter = [NSSortDescriptor sortDescriptorWithKey:@"index" ascending:YES];
    NSArray *sortDescriptors = [[NSArray alloc] initWithObjects:&sorter count:1];
    NSArray *sortedArray = [array sortedArrayUsingDescriptors:sortDescriptors];
    
    NSMutableArray *resultArray = [NSMutableArray new];
    if (sortedArray.count <= 0) {
        return nil;
    }
    DiscoverModel *firstModel = [sortedArray objectAtIndex:0];
    int lastIndex = [firstModel.index intValue];
    NSMutableArray *curArray = [NSMutableArray new];
    [curArray addObject:firstModel];
    [resultArray addObject:curArray];
    
    for (int i = 1; i < sortedArray.count; i++) {
        DiscoverModel *curModel = [sortedArray objectAtIndex:i];
        int curIndex = [curModel.index intValue];
        if (curIndex == lastIndex) {
            [curArray addObject:curModel];
        } else {
            curArray = [NSMutableArray new];
            [curArray addObject:curModel];
            [resultArray addObject:curArray];
        }
        lastIndex = curIndex;
    }
    return resultArray;
}

- (void)configureCell:(DiscoverListCell *)cell atIndexPath:(NSIndexPath *)indexPath
{
    NSMutableArray *rowArray = [self.sourceArray objectAtIndex:indexPath.section];
    cell.model = [rowArray objectAtIndex:indexPath.row];
    if (indexPath.row == rowArray.count-1) {
        cell.lineView.hidden = NO;
    } else {
        cell.lineView.hidden = NO;
    }
}


- (void)cellClickAction:(NSIndexPath *)indexPath {
    NSMutableArray *rowArray = [self.sourceArray objectAtIndex:indexPath.section];
    DiscoverModel *model = [rowArray objectAtIndex:indexPath.row];
    NSUserDefaults *userDef = [NSUserDefaults standardUserDefaults];
    NSInteger version = [userDef integerForKey:kDiscoverVersion];
    NSString *strVersion = [NSString stringWithFormat:@"%ld",version];
    UserInfoManager *userMg = [UserInfoManager sharedInstance];
    NSString *pidstr = [NSString stringWithFormat:@"pid=%@",userMg.userItem.pid];
    NSString *versionStr = [NSString stringWithFormat:@"version=%@",strVersion];
    NSString *tokenStr = [NSString stringWithFormat:@"token=%@",userMg.userItem.token];
    NSString *secretStr = [NSString stringWithFormat:@"secret=%@",kSecret];
    NSString *idStr = [NSString stringWithFormat:@"id=%@",userMg.userItem.id];
    
    NSString *link = model.link;
    link = [link stringByReplacingOccurrencesOfString:@"pid={pid}" withString:pidstr];
    link = [link stringByReplacingOccurrencesOfString:@"version={version}" withString:versionStr];
    link = [link stringByReplacingOccurrencesOfString:@"token={token}" withString:tokenStr];
    link = [link stringByReplacingOccurrencesOfString:@"secret={secret}" withString:secretStr];
    link = [link stringByReplacingOccurrencesOfString:@"id={id}" withString:idStr];
    NSLog(@"-----link %@",link);
    NSString *target = model.target;
    if ([target isEqualToString:@"browser"]) {
        NSURL *url = [NSURL URLWithString:link];
        [[UIApplication sharedApplication] openURL:url];
    } else if ([target isEqualToString:@"app"]) {
        UIStoryboard *board = self.storyboard;
        WebViewVC *ctrl = [board instantiateViewControllerWithIdentifier:@"WebViewVC"];
        ctrl.url = link;
        ctrl.isLinkAction = NO;
        ctrl.title = model.title;
        [self.navigationController pushViewController:ctrl animated:YES];
    } else if ([target isEqualToString:@"default"]) {
        UIStoryboard *board = self.storyboard;
        WebViewVC *ctrl = [board instantiateViewControllerWithIdentifier:@"WebViewVC"];
        ctrl.url = link;
        ctrl.title = model.title;
        ctrl.isLinkAction = YES;
        [self.navigationController pushViewController:ctrl animated:YES];
    } else {
        
    }
}

#pragma mark-
#pragma mark-----tableview delegate---

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return self.sourceArray.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return 0;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    NSMutableArray *rowArray = [self.sourceArray objectAtIndex:section];
    return rowArray.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 46;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    DiscoverListCell *cell = [tableView dequeueReusableCellWithIdentifier:kDiscoverCellIdentifier];
    
    [self configureCell:cell atIndexPath:indexPath];
    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    cell.selectionStyle = UITableViewCellSeparatorStyleNone;
    
    return cell;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [self cellClickAction:indexPath];
}

@end
