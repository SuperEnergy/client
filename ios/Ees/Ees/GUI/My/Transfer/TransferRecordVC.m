//
//  TransferRecordVC.m
//  Ees
//
//  Created by xiaodong on 2017/12/27.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import "TransferRecordVC.h"

typedef void(^getFundListRsp)(NSArray *array,enum RefreshState state);

#define kFundCellIdentifier @"fundCell2"
#define kMaxPageSize  15
#define kFundType   3

@interface TransferRecordVC ()

@property(nonatomic,weak) IBOutlet UITableView *tableView;
@property(nonatomic,strong) NSMutableArray *sourceArray;
@property(nonatomic,weak) IBOutlet UIImageView *imageView;

@end

@implementation TransferRecordVC
{
    int _curPage;
}

#pragma mark-
#pragma mark——overide—

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"EES转账明细";
    self.automaticallyAdjustsScrollViewInsets = NO;
    _sourceArray = [NSMutableArray new];
    _curPage = 1;
    [self getFundList:nil];
    
    self.view.backgroundColor = kViewBackgroundColor;
    self.imageView.hidden = YES;
    
    
    __unsafe_unretained UITableView *tableView = self.tableView;
    // 下拉刷新
    tableView.mj_header= [MJRefreshNormalHeader headerWithRefreshingBlock:^{
        _curPage = 1;
        [self.sourceArray removeAllObjects];
        [self getFundList:^(NSArray *array, enum RefreshState state) {
            [tableView.mj_header endRefreshing];
            [tableView.mj_footer resetNoMoreData];
        }];
        
    }];
    
    // 设置自动切换透明度(在导航栏下面自动隐藏)
    tableView.mj_header.automaticallyChangeAlpha = YES;
    
    // 上拉刷新
    tableView.mj_footer = [MJRefreshBackNormalFooter footerWithRefreshingBlock:^{
        _curPage++;
        [self getFundList:^(NSArray *array, enum RefreshState state) {
            if (array.count < kMaxPageSize) {
                [tableView.mj_footer endRefreshingWithNoMoreData];
            } else {
                [tableView.mj_footer endRefreshing];
            }
        }];
    }];
    
    [self.tableView registerNib:[UINib nibWithNibName:@"FundCell2" bundle:nil] forCellReuseIdentifier:kFundCellIdentifier];
    
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
#pragma mark——private—

- (void)getFundList:(getFundListRsp)rsp
{
    NSString *pid = [UserInfoManager sharedInstance].userItem.pid;
    NSNumber *typeNum = [NSNumber numberWithInt:kFundType];
    NSNumber *pageNum = [NSNumber numberWithInt:_curPage];
    NSNumber *sizeNum = [NSNumber numberWithInt:kMaxPageSize];
    NSString *token = [UserInfoManager sharedInstance].userItem.token;
    NSDictionary *dic = @{@"secret":kSecret,@"pid":pid,@"type":typeNum,@"page":pageNum,@"size":sizeNum,@"token":token};
    [HttpRequestEngin fundListWithParam:dic successBlock:^(id  _Nullable responseObject) {
        RespondModel *resModel = [[RespondModel alloc] initObjectsWithDictionary:responseObject objectClass:[FundModel class] objectError:nil];
        if (resModel.success) {
            NSArray *fundArrays = resModel.objects;
            if (fundArrays.count > 0) {
                [self.sourceArray addObjectsFromArray:fundArrays];
            }
            
            dispatch_async(dispatch_get_main_queue(), ^{
                [self.tableView reloadData];
                if (self.sourceArray.count <= 0) {
                    self.imageView.hidden = NO;
                } else {
                    self.imageView.hidden = YES;
                }
                if (rsp) {
                    rsp(fundArrays,RefreshState_Success);
                }
            });
        } else {
            ErrorModel *model = resModel.error;
            ErrorCodeEvent *event = [[ErrorCodeEvent alloc] initWithError:model];
            [event errorCodeDone:self needLoading:YES];
            
            dispatch_async(dispatch_get_main_queue(), ^{
                if (rsp) {
                    rsp(nil,RefreshState_Fail);
                }
            });
        }
        
        
    } failBlock:^(NSError * _Nonnull error) {
        
    } progress:^(NSProgress * _Nonnull uploadProgress) {
        
    }];
}


- (void)configureCell:(FundCell2 *)cell atIndexPath:(NSIndexPath *)indexPath
{
    cell.model = [self.sourceArray objectAtIndex:indexPath.row];
    cell.isShowBalance = NO;
}


#pragma mark-
#pragma mark——tableView delegate—

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
    return 150;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    FundCell2 *cell = [tableView dequeueReusableCellWithIdentifier:kFundCellIdentifier];
    
    [self configureCell:cell atIndexPath:indexPath];
    
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    
    return cell;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    
}


/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
