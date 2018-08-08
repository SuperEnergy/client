//
//  ShoppingVC.m
//  Ees
//
//  Created by xiaodong on 2018/1/14.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import "ShoppingVC.h"


#define kGoodsCellIdentifier @"GoodsCell"


@interface ShoppingVC ()<ShoppingCellDelege>

@property(nonatomic,weak) IBOutlet UILabel *titleLabel;
@property(weak,nonatomic) IBOutlet UIView *lineView;
@property(nonatomic,weak) IBOutlet UITableView *tableView;
@property(nonatomic,strong) ShoppingHeaderView *tableHeaderView;
@property(nonatomic,weak) IBOutlet ShoppingFooterView *footerView;
@property(nonatomic,weak) IBOutlet UIImageView *imageView;
@property (nonatomic, strong) GoodsCell *prototypeCell;

@property(nonatomic,strong) NSMutableArray *sourceArray;




@end

@implementation ShoppingVC

#pragma mark-
#pragma mark-----overide--

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.footerView updateLedgerFund];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    if ([self.subtype intValue] == 0) {
        self.titleLabel.text = @"能量桶扩容";
    } else {
        self.titleLabel.text = @"收集器加速";
    }
    
    _sourceArray = [NSMutableArray new];
    [self getGoodsList];
    
    
    //self.automaticallyAdjustsScrollViewInsets = NO;
    
    self.lineView.backgroundColor = kColorWithHex(0xe6e6e6);
    self.imageView.hidden = YES;
    self.view.backgroundColor = kWhiteColor;
    
    [self.tableView registerNib:[UINib nibWithNibName:@"GoodsCell" bundle:nil] forCellReuseIdentifier:kGoodsCellIdentifier];
    self.prototypeCell = [self.tableView dequeueReusableCellWithIdentifier:kGoodsCellIdentifier];
    self.tableView.backgroundColor = kViewBackgroundColor;
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone ;
    
    CGRect headerRect = CGRectMake(0, 0, self.tableView.frame.size.width, 100);
    self.tableView.tableHeaderView = [[UIView alloc] initWithFrame:headerRect];
    NSArray *nibContents = [[NSBundle mainBundle] loadNibNamed:@"ShoppingHeaderView" owner:nil options:nil];
    self.tableHeaderView = [nibContents lastObject];
    self.tableHeaderView.frame = headerRect;
    self.tableHeaderView.subtype = self.subtype;
    [self.tableHeaderView setModel:[UserMineManager sharedInstance].currentUserMine];
    [self.tableView.tableHeaderView addSubview:self.tableHeaderView];
    
    [self.footerView updateOrderStatus:@"0.0ees"];
    self.footerView.backgroundColor = kViewBackgroundColor;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


#pragma mark-
#pragma mark-----private---

- (double)getTotalMoney {
    double result = 0.0;
    for (int i = 0; i < self.sourceArray.count; i++) {
        ExGoodsModel *exGoods = [self.sourceArray objectAtIndex:i];
        result+=exGoods.eesAmount;
    }
    return result;
}

- (long)getTotalVolume {
    long result = 0;
    for (int i = 0; i < self.sourceArray.count; i++) {
        ExGoodsModel *exGoods = [self.sourceArray objectAtIndex:i];
        int goodVolume = [exGoods.goods.volume intValue];
        long total = goodVolume*exGoods.goodsNum;
        result+=total;
    }
    return result;
}

- (int)getTotalNum {
    int result = 0;
    for (int i = 0; i < self.sourceArray.count; i++) {
        ExGoodsModel *exGoods = [self.sourceArray objectAtIndex:i];
        result+=exGoods.goodsNum;
    }
    return result;
}

- (NSMutableArray *)getOrderArray {
    NSMutableArray *result = [NSMutableArray new];
    for (int i = 0; i < self.sourceArray.count; i++) {
        ExGoodsModel *exGoods = [self.sourceArray objectAtIndex:i];
        if (exGoods.goodsNum > 0) {
            NSMutableDictionary *dic = [[NSMutableDictionary alloc] init];
            [dic setObject:exGoods.goods.id forKey:@"goods_id"];
            [dic setObject:[NSNumber numberWithInt:exGoods.goodsNum] forKey:@"qty"];
            [result addObject:dic];
        }
    }
    return result;
}


- (void)getGoodsList {
    NSString *pid = [UserInfoManager sharedInstance].userItem.pid;
    NSString *token = [UserInfoManager sharedInstance].userItem.token;
    NSNumber *typeNum = [UserMineManager sharedInstance].currentUserMine.mineType.type;
    
    NSNumber *pageNum = [NSNumber numberWithInt:1];
    NSNumber *sizeNum = [NSNumber numberWithInt:1000];
    
    NSDictionary *dic = @{@"secret":kSecret,@"pid":pid,@"type":typeNum,@"page":pageNum,@"size":sizeNum,@"token":token,@"subtype":self.subtype};
    
    [HttpRequestEngin goodsListWithParam:dic successBlock:^(id  _Nullable responseObject) {
        RespondModel *resModel = [[RespondModel alloc] initObjectsWithDictionary:responseObject objectClass:[GoodsModel class] objectError:nil];
        if (resModel.success && resModel.objects) {
            NSArray *tempArray = resModel.objects;
            [self.sourceArray removeAllObjects];
            for (int i = 0; i < tempArray.count; i++) {
                GoodsModel *goods = [tempArray objectAtIndex:i];
                ExGoodsModel *exGoods = [[ExGoodsModel alloc] init];
                exGoods.goods = goods;
                exGoods.goodsNum = 0;
                exGoods.eesAmount = 0.0;
                [self.sourceArray addObject:exGoods];
            }
            
            
            dispatch_async(dispatch_get_main_queue(), ^{
                [self.tableView reloadData];
                if (self.sourceArray.count <= 0) {
                    self.imageView.hidden = NO;
                } else {
                    self.imageView.hidden = YES;
                }
            });
            
        } else {
            ErrorModel *model = resModel.error;
            ErrorCodeEvent *event = [[ErrorCodeEvent alloc] initWithError:model];
            [event errorCodeDone:self needLoading:YES];
        }
        
        
    } failBlock:^(NSError * _Nonnull error) {
        
    } progress:^(NSProgress * _Nonnull uploadProgress) {
        
    }];
}


- (void)orderBook {
    NSString *pid = [UserInfoManager sharedInstance].userItem.pid;
    NSString *token = [UserInfoManager sharedInstance].userItem.token;
    NSString *userMineId = [UserMineManager sharedInstance].currentUserMine.id;
    NSMutableArray *orderArray = [self getOrderArray];
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:orderArray options:NSJSONWritingPrettyPrinted error:nil];
    NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    
    if (orderArray.count <= 0) {
        [LoadingTool tipView:self.view title:@"请选择商品数量" image:nil];
        return;
    }
    
    NSDictionary *dic = @{@"secret":kSecret,@"pid":pid,@"usermine_id":userMineId,@"orderjson":jsonString,@"token":token};
    
    [HttpRequestEngin orderBookWithParam:dic successBlock:^(id  _Nullable responseObject) {
        RespondModel *resModel = [[RespondModel alloc] initObjectsWithDictionary:responseObject objectClass:[GoodsOrderModel class] objectError:nil];
        if (resModel.success && resModel.objects) {
            NSArray *orderArray = resModel.objects;
            NSMutableString *orderIds = [[NSMutableString alloc] init];
            NSMutableString *goodsNames = [[NSMutableString alloc] init];
            
            for (int i = 0; i < orderArray.count; i++) {
                GoodsOrderModel *goodsOrder = [orderArray objectAtIndex:i];
                
                NSString *name = goodsOrder.goods.name;
                int qty = [goodsOrder.qty intValue];
                double eesAmount = [goodsOrder.eesAmount doubleValue];
                
                if (i != orderArray.count-1) {
                    [orderIds appendString:[NSString stringWithFormat:@"%@,",goodsOrder.id]];
                } else {
                    [orderIds appendString:[NSString stringWithFormat:@"%@",goodsOrder.id]];
                }
                [goodsNames appendString:[NSString stringWithFormat:@"%@\n",name]];
                NSString *strAmount = [DecimalNumberTool moneyFormatFormDouble:eesAmount];
                [goodsNames appendString:[NSString stringWithFormat:@"数量: %d  合计: %@\n",qty,strAmount]];
            }
            
            [AlertTool alertShowTwoActionTextAlignmentLeft:self title:@"确认订单" context:goodsNames okTitle:@"付款" cancelTitle:@"取消" okHandler:^(UIAlertAction *action) {
                [self payOrder:orderIds];
            } cancelHandler:nil];
        } else {
            ErrorModel *model = resModel.error;
            ErrorCodeEvent *event = [[ErrorCodeEvent alloc] initWithError:model];
            [event errorCodeDone:self needLoading:YES];
        }
        
        
    } failBlock:^(NSError * _Nonnull error) {
        NSLog(@"---%@",[error description]);
    } progress:^(NSProgress * _Nonnull uploadProgress) {
        
    }];
}


- (void)payOrder:(NSString *)orderIds {
    
    NSString *pid = [UserInfoManager sharedInstance].userItem.pid;
    NSString *token = [UserInfoManager sharedInstance].userItem.token;
    
    NSDictionary *dic = @{@"secret":kSecret,@"pid":pid,@"order_ids":orderIds,@"token":token};
    
    [LoadingTool beginLoading:self.view title:@"支付中..."];
    [HttpRequestEngin orderPayWithParam:dic successBlock:^(id  _Nullable responseObject) {
        RespondModel *resModel = [[RespondModel alloc] initObjectWithDictionary:responseObject objectClass:[GoodsOrderModel class] objectError:nil];
        if (resModel.success) {
            
            [[UserMineManager sharedInstance] refreshUserMineList:^(UserMineModel *usermine) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.tableHeaderView setModel:[UserMineManager sharedInstance].currentUserMine];
                    [[NSNotificationCenter defaultCenter] postNotificationName:kMaxQtyLimitDidChange object:nil];
                });
            }];
            [UserInfoManager sharedInstance].popVC = self;
            [[UserInfoManager sharedInstance] refreshLedger:^(BOOL success) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.footerView updateLedgerFund];
                });
            }];
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(2.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                NSString *strTitle = nil;
                if ([self.subtype intValue] == 0) {
                    strTitle = @"扩容成功";
                } else {
                    strTitle = @"加速成功";
                }
                [LoadingTool finishLoading:self.view title:strTitle success:YES];
                [AlertTool alertShowOKAction:self title:@"提示" context:strTitle buttonTitle:@"确定" handler:^(UIAlertAction *action) {
                    [self dismissViewControllerAnimated:YES completion:nil];
                }];
            });
        } else {
            ErrorModel *model = resModel.error;
            ErrorCodeEvent *event = [[ErrorCodeEvent alloc] initWithError:model];
            [event errorCodeDone:self needLoading:YES];
        }
        
        
    } failBlock:^(NSError * _Nonnull error) {
        [LoadingTool failLoading:self.view title:@"付款失败"];
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

- (void)configureCell:(GoodsCell *)cell atIndexPath:(NSIndexPath *)indexPath
{
    cell.delegate = self;
    cell.subtype = self.subtype;
    cell.model = [self.sourceArray objectAtIndex:indexPath.row];
}

#pragma mark-
#pragma mark-----Event ---

- (IBAction)back:(id)sender {
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (IBAction)orderPress:(id)sender {
    [self orderBook];
}

#pragma mark-
#pragma mark-----tableView delegate---

-(CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 10;
}


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
    GoodsCell *cell = self.prototypeCell;
    cell.contentView.translatesAutoresizingMaskIntoConstraints = NO;
    [self configureCell:cell atIndexPath:indexPath];
    CGFloat contentViewWidth = [self cellContentViewWith];
    NSLayoutConstraint *widthFenceConstraint = [NSLayoutConstraint constraintWithItem:cell.contentView attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:contentViewWidth];
    [cell.contentView addConstraint:widthFenceConstraint];
    
    CGFloat fittingHeight = [cell.contentView systemLayoutSizeFittingSize:UILayoutFittingCompressedSize].height;
    [cell.contentView removeConstraint:widthFenceConstraint];
    
    CGFloat cellHeight = fittingHeight + 2 * 1 / [UIScreen mainScreen].scale;
    
    return cellHeight;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    GoodsCell *cell = [tableView dequeueReusableCellWithIdentifier:kGoodsCellIdentifier];
    
    [self configureCell:cell atIndexPath:indexPath];
    
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    
    return cell;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    //[self detailDidClick:indexPath.row];
}


#pragma mark-
#pragma mark------delegate
- (double)getAllShopTotalMoney:(double)curTotalMoney {
    double result = [self getTotalMoney];
    result += curTotalMoney;
    return result;
}

- (long)getAllShopTotalVolume:(int)curTotalVolume {
    long result = [self getTotalVolume];
    result+=curTotalVolume;
    return result;
}


- (void)shoppingTotalDidChange {
    double totalMoney = [self getTotalMoney];
    NSString *str = [DecimalNumberTool moneyFormatFormDouble:totalMoney];
    [self.footerView updateOrderStatus:str];
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
