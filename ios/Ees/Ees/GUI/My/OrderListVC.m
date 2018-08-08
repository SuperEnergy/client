//
//  OrderListVC.m
//  Ees
//
//  Created by xiaodong on 2018/1/15.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import "OrderListVC.h"

typedef void(^getOrderListRsp)(NSArray *array,enum RefreshState state);

#define kOrderListCellIdentifier @"OrderListCell"
#define kMaxPageSize  15


@interface OrderListVC ()

@property(nonatomic,weak) IBOutlet UITableView *tableView;
@property(nonatomic,weak) IBOutlet UIImageView *imageView;
@property(nonatomic,strong) NSMutableArray *sourceArray;
@property (nonatomic, strong) OrderListCell *prototypeCell;

@end

@implementation OrderListVC
{
    int _curPage;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.automaticallyAdjustsScrollViewInsets = NO;
    _sourceArray = [NSMutableArray new];
    _curPage = 1;
    [self getOrderList:nil];
    
    
    self.view.backgroundColor = kViewBackgroundColor;
    self.imageView.hidden = YES;
    
    __unsafe_unretained UITableView *tableView = self.tableView;
    // 下拉刷新
    tableView.mj_header= [MJRefreshNormalHeader headerWithRefreshingBlock:^{
        _curPage = 1;
        [self.sourceArray removeAllObjects];
        [self getOrderList:^(NSArray *array, enum RefreshState state) {
            [tableView.mj_header endRefreshing];
            [tableView.mj_footer resetNoMoreData];
        }];
        
    }];
    
    // 设置自动切换透明度(在导航栏下面自动隐藏)
    tableView.mj_header.automaticallyChangeAlpha = YES;
    
    // 上拉刷新
    tableView.mj_footer = [MJRefreshBackNormalFooter footerWithRefreshingBlock:^{
        _curPage++;
        [self getOrderList:^(NSArray *array, enum RefreshState state) {
            if (array.count < kMaxPageSize) {
                [tableView.mj_footer endRefreshingWithNoMoreData];
            } else {
                [tableView.mj_footer endRefreshing];
            }
        }];
    }];
    
    [self.tableView registerNib:[UINib nibWithNibName:@"OrderListCell" bundle:nil] forCellReuseIdentifier:kOrderListCellIdentifier];
    self.prototypeCell = [self.tableView dequeueReusableCellWithIdentifier:kOrderListCellIdentifier];
    self.tableView.backgroundColor = kViewBackgroundColor;
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.tableView.contentInset = kTabelViewEdgeInsets;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


#pragma mark-
#pragma mark------private 

- (void)getOrderList:(getOrderListRsp)rsp
{
    NSString *pid = [UserInfoManager sharedInstance].userItem.pid;
    NSNumber *pageNum = [NSNumber numberWithInt:_curPage];
    NSNumber *sizeNum = [NSNumber numberWithInt:kMaxPageSize];
    NSString *token = [UserInfoManager sharedInstance].userItem.token;
    
    NSDictionary *dic = @{@"secret":kSecret,@"pid":pid,@"page":pageNum,@"size":sizeNum,@"token":token};
    
    [HttpRequestEngin orderListWithParam:dic successBlock:^(id  _Nullable responseObject) {
        RespondModel *resModel = [[RespondModel alloc] initObjectsWithDictionary:responseObject objectClass:[GoodsOrderModel class] objectError:nil];
        if (resModel.success && resModel.objects) {
            NSArray *orders = resModel.objects;
            if (orders.count > 0) {
                [self.sourceArray addObjectsFromArray:orders];
            }
            
            dispatch_async(dispatch_get_main_queue(), ^{
                [self.tableView reloadData];
                if (self.sourceArray.count <= 0) {
                    self.imageView.hidden = NO;
                } else {
                    self.imageView.hidden = YES;
                }
                
                if (rsp) {
                    rsp(orders,RefreshState_Success);
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


- (CGFloat)cellContentViewWith
{
    CGFloat width = [UIScreen mainScreen].bounds.size.width;
    
    // 适配ios7横屏
    if ([UIApplication sharedApplication].statusBarOrientation != UIInterfaceOrientationPortrait && [[UIDevice currentDevice].systemVersion floatValue] < 8) {
        width = [UIScreen mainScreen].bounds.size.height;
    }
    return width;
}

- (void)configureCell:(OrderListCell *)cell atIndexPath:(NSIndexPath *)indexPath
{
    cell.model = [self.sourceArray objectAtIndex:indexPath.row];
    //cell.row = indexPath.row;
    //cell.delegate = self;
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
    OrderListCell *cell = self.prototypeCell;
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
    OrderListCell *cell = [tableView dequeueReusableCellWithIdentifier:kOrderListCellIdentifier];
    
    [self configureCell:cell atIndexPath:indexPath];
    
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    
    return cell;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    //[self detailDidClick:indexPath.row];
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
