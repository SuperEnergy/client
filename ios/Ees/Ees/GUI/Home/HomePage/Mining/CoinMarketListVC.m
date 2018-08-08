//
//  CoinMarketListVC.m
//  Ees
//
//  Created by xiaodong on 2018/4/7.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import "CoinMarketListVC.h"


#define kMarketListCellIdentifier @"CoinMarketCell2"



@interface CoinMarketListVC ()

@property(nonatomic,weak) IBOutlet UITableView *tableView;
@property(nonatomic,weak) IBOutlet UIImageView *imageView;
@property(nonatomic,strong) NSArray *sourceArray;


@end


@implementation CoinMarketListVC

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.title = @"实时行情";
    [self getMarketList];
    
    self.automaticallyAdjustsScrollViewInsets = NO;
    self.view.backgroundColor = kViewBackgroundColor;
    self.imageView.hidden = YES;
    
    [self.tableView registerNib:[UINib nibWithNibName:@"CoinMarketCell2" bundle:nil] forCellReuseIdentifier:kMarketListCellIdentifier];
    self.tableView.backgroundColor = kViewBackgroundColor;
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.tableView.contentInset = kTabelViewEdgeInsets;
    
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark-
#pragma mark------private

- (void)getMarketList {
    
    UserInfoManager *item = [UserInfoManager sharedInstance];
    if (!item.userItem) {
        return;
    }
    
    NSDictionary *dic = @{@"secret":kSecret,@"pid":item.userItem.pid,@"token":item.userItem.token};
    [HttpRequestEngin getCoinmarketList:dic successBlock:^(id  _Nullable responseObject) {
        NSError *error;
        RespondModel *apiModel = [[RespondModel alloc] initObjectsWithDictionary:responseObject objectClass:[CoinMarketModel class] objectError:&error];
        
        if (apiModel.success && apiModel.objects) {
            self.sourceArray = apiModel.objects;
            dispatch_async(dispatch_get_main_queue(), ^{
                [self.tableView reloadData];
                if (self.sourceArray.count <= 0) {
                    self.imageView.hidden = NO;
                } else {
                    self.imageView.hidden = YES;
                }
            });
        } else {
            
            ErrorModel *model = apiModel.error;
            ErrorCodeEvent *event = [[ErrorCodeEvent alloc] initWithError:model];
            [event errorCodeDone:self needLoading:YES];
        }
        
    } failBlock:^(NSError * _Nonnull error) {
        
    } progress:^(NSProgress * _Nonnull loadprogress) {
        
    }];
}



- (void)configureCell:(CoinMarketCell2 *)cell atIndexPath:(NSIndexPath *)indexPath
{
    cell.model = [self.sourceArray objectAtIndex:indexPath.row];
}

#pragma mark-
#pragma mark-----tableView delegate---

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.sourceArray.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 155;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    CoinMarketCell2 *cell = [tableView dequeueReusableCellWithIdentifier:kMarketListCellIdentifier];
    
    [self configureCell:cell atIndexPath:indexPath];
    
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    CoinMarketModel *model = [self.sourceArray objectAtIndex:indexPath.row];
    NSURL *url = [NSURL URLWithString:model.action];
    [[UIApplication sharedApplication] openURL:url];
}



@end
