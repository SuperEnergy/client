//
//  MessageVC.m
//  Ees
//
//  Created by xiaodong on 2017/12/10.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import "MessageVC.h"


typedef void(^getMessageListRsp)(NSArray *array,enum RefreshState state);

#define kMessageCellIdentifier @"messageCell"
#define kMaxPageSize  15
#define kMesType   1

@interface MessageVC ()
{
    int _curPage;
    NSTimeInterval _lastInterval;
    NSTimeInterval _refreshLastInterval;
}

@property(nonatomic,weak) IBOutlet UITableView *tableView;
@property(nonatomic,weak) IBOutlet UIImageView *imageView;

@property (nonatomic, strong) NoticeCell *prototypeCell;

@end

@implementation MessageVC

#pragma mark-
#pragma mark-----overide---

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    
    NSTimeInterval curInterval = [[NSDate date] timeIntervalSince1970];
    int hour = 2;
    if (curInterval - _lastInterval >= hour*60*60) {
        [self getMessageList:nil];
    }
    _lastInterval = curInterval;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.imageView.hidden = YES;
    _sourceArray = [NSMutableArray new];
    _curPage = 1;
    
    
    
    self.view.backgroundColor = kViewBackgroundColor;
    
    __unsafe_unretained UITableView *tableView = self.tableView;
    // 下拉刷新
    tableView.mj_header= [MJRefreshNormalHeader headerWithRefreshingBlock:^{
        NSTimeInterval curInterval = [[NSDate date] timeIntervalSince1970];
        if (curInterval - _refreshLastInterval >= 10) {
            _curPage = 1;
            [self.sourceArray removeAllObjects];
            [self getMessageList:^(NSArray *array, enum RefreshState state) {
                [tableView.mj_header endRefreshing];
                [tableView.mj_footer resetNoMoreData];
            }];
        } else {
            [self.tableView.mj_header endRefreshing];
        }
        _refreshLastInterval = curInterval;
    }];

    // 设置自动切换透明度(在导航栏下面自动隐藏)
    tableView.mj_header.automaticallyChangeAlpha = YES;

    // 上拉刷新
    tableView.mj_footer = [MJRefreshBackNormalFooter footerWithRefreshingBlock:^{
        _curPage++;
        [self getMessageList:^(NSArray *array, enum RefreshState state) {
            if (array.count < kMaxPageSize) {
                [tableView.mj_footer endRefreshingWithNoMoreData];
            } else {
                [tableView.mj_footer endRefreshing];
            }
        }];
    }];
    
    
    [self.tableView registerNib:[UINib nibWithNibName:@"NoticeCell" bundle:nil] forCellReuseIdentifier:kMessageCellIdentifier];
    self.prototypeCell = [self.tableView dequeueReusableCellWithIdentifier:kMessageCellIdentifier];
    self.tableView.backgroundColor = kViewBackgroundColor;
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.tableView.contentInset = kTabelViewEdgeInsets;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark-
#pragma mark-----private---

- (void)getMessageList:(getMessageListRsp)rsp
{
    NSString *pid = [UserInfoManager sharedInstance].userItem.pid;
    NSNumber *typeNum = [NSNumber numberWithInt:kMesType];
    NSNumber *pageNum = [NSNumber numberWithInt:_curPage];
    NSNumber *sizeNum = [NSNumber numberWithInt:kMaxPageSize];
    NSString *token = [UserInfoManager sharedInstance].userItem.token;
    
    NSUserDefaults *userDef = [NSUserDefaults standardUserDefaults];
    NSInteger version = [userDef integerForKey:kMessageVersion];
    if (_curPage > 1) {
        version = 0;
    }
    NSString *strVersion = [NSString stringWithFormat:@"%ld",version];
    
    NSDictionary *dic = @{@"secret":kSecret,@"pid":pid,@"type":typeNum,@"page":pageNum,@"size":sizeNum,@"token":token,@"version":strVersion};
        [HttpRequestEngin noticeListWithParam:dic successBlock:^(id  _Nullable responseObject) {
        RespondModel *resModel = [[RespondModel alloc] initObjectsWithDictionary:responseObject objectClass:[NoticeModel class] objectError:nil];
        if (resModel.success) {
            NSArray *mesArrays = resModel.objects;
            if (mesArrays.count > 0) {
                [self.sourceArray addObjectsFromArray:mesArrays];
            }
            
            dispatch_async(dispatch_get_main_queue(), ^{
                [self.tableView reloadData];
                if (self.sourceArray.count <= 0) {
                    self.imageView.hidden = NO;
                } else {
                    self.imageView.hidden = YES;
                }
                if (rsp) {
                    rsp(mesArrays,RefreshState_Success);
                }
            });
            
            if (_curPage == 1) {
                BOOL writeSuccess = [FileTool writeToPlistFile:kMessageFileName dic:responseObject];
                NSLog(@"write message respond %d",writeSuccess);
                [userDef setInteger:resModel.version forKey:kMessageVersion];
                [userDef synchronize];
            }
            
        } else {
            ErrorModel *model = resModel.error;
            if ([model.code isEqualToString:@"CODE_SYNC_EQUAL"]) {
                NSDictionary *dic = [FileTool readFromPlistFile:kMessageFileName];
                RespondModel *resModel = [[RespondModel alloc] initObjectsWithDictionary:dic objectClass:[NoticeModel class] objectError:nil];
                if (resModel.success && resModel.objects) {
                    self.sourceArray = [NSMutableArray arrayWithArray:resModel.objects];
                    
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [self.tableView reloadData];
                        if (self.sourceArray.count <= 0) {
                            self.imageView.hidden = NO;
                        } else {
                            self.imageView.hidden = YES;
                        }
                        
                        if (rsp) {
                            rsp(nil,RefreshState_NoNewData);
                        }
                    });
                }
            } else {
                ErrorCodeEvent *event = [[ErrorCodeEvent alloc] initWithError:model];
                [event errorCodeDone:self needLoading:NO];

                dispatch_async(dispatch_get_main_queue(), ^{
                    if (rsp) {
                        rsp(nil,RefreshState_Fail);
                    }
                });
            }
            
        }
        
        
    } failBlock:^(NSError * _Nonnull error) {
        
    } progress:^(NSProgress * _Nonnull uploadProgress) {
        
    }];
}

- (CGFloat)cellContentViewWith
{
    CGFloat width = [UIScreen mainScreen].bounds.size.width;
    
    // 适配ios7横屏
    if ([UIApplication sharedApplication].statusBarOrientation != UIInterfaceOrientationPortrait && [[UIDevice currentDevice].systemVersion floatValue] < 8) {
        width = [UIScreen mainScreen].bounds.size.height;
    }
    return width;
}

- (void)configureCell:(NoticeCell *)cell atIndexPath:(NSIndexPath *)indexPath
{
    cell.model = [self.sourceArray objectAtIndex:indexPath.row];
}


#pragma mark-
#pragma mark-----tableview delegate---

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
    NoticeCell *cell = self.prototypeCell;
    cell.contentView.translatesAutoresizingMaskIntoConstraints = NO;
    [self configureCell:cell atIndexPath:indexPath]; //必须先对Cell中的数据进行配置使动态计算时能够知道根据Cell内容计算出合适的高度
    
    /*------------------------------重点这里必须加上contentView的宽度约束不然计算出来的高度不准确-------------------------------------*/
    CGFloat contentViewWidth = [self cellContentViewWith];
    NSLayoutConstraint *widthFenceConstraint = [NSLayoutConstraint constraintWithItem:cell.contentView attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:contentViewWidth];
    [cell.contentView addConstraint:widthFenceConstraint];
    // Auto layout engine does its math
    CGFloat fittingHeight = [cell.contentView systemLayoutSizeFittingSize:UILayoutFittingCompressedSize].height;
    [cell.contentView removeConstraint:widthFenceConstraint];
    /*-------------------------------End------------------------------------*/
    
    CGFloat cellHeight = fittingHeight + 2 * 1 / [UIScreen mainScreen].scale; //必须加上上下分割线的高度
    
    return cellHeight;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NoticeCell *cell = [tableView dequeueReusableCellWithIdentifier:kMessageCellIdentifier];
    
    [self configureCell:cell atIndexPath:indexPath];
    
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    
    return cell;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    
}



@end
