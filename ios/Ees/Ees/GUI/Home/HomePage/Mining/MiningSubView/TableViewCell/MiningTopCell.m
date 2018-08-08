//
//  MiningTopCell.m
//  Ees
//
//  Created by KCMac on 2018/3/27.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import "MiningTopCell.h"

@interface MiningTopCell ()<BeforeMiningDelegate,DeviceModelDelegate>

@property(nonatomic,weak)IBOutlet UIView *cellBgView;
@property(nonatomic,strong) UIImageView *yearImageView;
@property(nonatomic,weak) IBOutlet UIView *greenBGView;
@property(nonatomic,weak) IBOutlet UIView *submitBgView;
@property(nonatomic,weak) IBOutlet UIView *bottomBGView;


//buttonBGView subview
@property(nonatomic,weak) IBOutlet UILabel *maxQtyLimitLabel;
@property(nonatomic,weak) IBOutlet UILabel *speedRateLabel;
@property(nonatomic,weak) IBOutlet UILabel *percentLabel;
@property(nonatomic,weak) IBOutlet UIView *exCapacityBgView;
@property(nonatomic,weak) IBOutlet UIView *exSpeedRateBgView;


//greenBGView subview
@property(nonatomic,strong) UserMineView *userMineView;
@property(nonatomic,strong) StartMiningView *startView;
@property(nonatomic,strong) MiningAnimationView *miningView;
@property(nonatomic,assign) BOOL isClearRsp;

@end



@implementation MiningTopCell

- (void)layoutSubviews {
    [super layoutSubviews];
    self.startView.supperVC = self.supperVC;
    self.miningView.supperVC = self.supperVC;
}


- (void)awakeFromNib {
    [super awakeFromNib];
    
    [self showNewYearImage];
    self.cellBgView.backgroundColor = kCycleBackgroundColor;
    self.greenBGView.backgroundColor = [UIColor clearColor];
    
    //usreMineView
    NSArray *userMineNib = [[NSBundle mainBundle] loadNibNamed:@"UserMineView" owner:nil options:nil];
    self.userMineView = [userMineNib lastObject];
    [self.greenBGView addSubview:self.userMineView];
    [self.userMineView makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.greenBGView.leading);
        make.trailing.equalTo(self.greenBGView.trailing);
        make.top.equalTo(self.greenBGView.top);
        make.height.equalTo(@40);
    }];
    
    UserMineManager *mg = [UserMineManager sharedInstance];
    NSString *userMingName = mg.currentUserMine.name;
    if (userMingName == nil) {
        userMingName = @"获取收集器失败";
    }
    self.userMineView.titleLabel.text = [NSString stringWithFormat:@"收集器:%@>",userMingName];
    
    //startView
    NSArray *startViewNib = [[NSBundle mainBundle] loadNibNamed:@"StartMiningView" owner:nil options:nil];
    self.startView = [startViewNib lastObject];
    self.startView.supperVC = self.supperVC;
    self.startView.delegate = self;
    self.startView.backgroundColor = [UIColor clearColor];
    [self.greenBGView addSubview:self.startView];
    [self.startView makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.greenBGView).offset(40);
        make.bottom.equalTo(self.greenBGView).offset(-40);
        make.width.equalTo(self.startView.height);
        make.centerX.equalTo(self.greenBGView);
    }];
    
    //gif
    NSArray *miningViewNib = [[NSBundle mainBundle] loadNibNamed:@"MiningAnimationView" owner:nil options:nil];
    self.miningView = [miningViewNib lastObject];
    self.miningView.supperVC = self.supperVC;
    self.miningView.backgroundColor = [UIColor clearColor];
    [self.greenBGView addSubview:self.miningView];
    [self.miningView makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.greenBGView);
        make.bottom.equalTo(self.greenBGView);
        make.width.equalTo(self.miningView.height);
        make.centerX.equalTo(self.greenBGView);
    }];
    self.miningView.hidden = YES;
    [self.greenBGView bringSubviewToFront:self.userMineView];
    
    //submit
    [self.submitBgView.layer setCornerRadius:CGRectGetHeight([self.submitBgView bounds]) / 2];
    self.submitBgView.layer.masksToBounds = YES;
    self.submitBgView.hidden = YES;
    [self.greenBGView bringSubviewToFront:self.submitBgView];
    
    
    UITapGestureRecognizer *gesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(shoppingTap)];
    [self.exCapacityBgView addGestureRecognizer:gesture];
    
    UITapGestureRecognizer *gesture3 = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(shopping2Tap)];
    [self.exSpeedRateBgView addGestureRecognizer:gesture3];
    
    
    UITapGestureRecognizer *gesture2 = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(submitPress)];
    [self.submitBgView addGestureRecognizer:gesture2];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(userMineSwitch:)
                                                 name:kUserMineDidSwitch object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(userMineRefresh:)
                                                 name:kUserMineDidRefresh object:nil];
    
    self.isClearRsp = NO;
    
}


- (void)userMineSwitch:(NSNotification *)notification
{
    UserMineManager *mg = [UserMineManager sharedInstance];
    self.userMineView.titleLabel.text = [NSString stringWithFormat:@"收集器:%@>",mg.currentUserMine.name];
    [self.miningView updateAnimationQtyLimit];
    [self topCellUpdateBatteryLevel];
}

- (void)userMineRefresh:(NSNotification *)notification
{
    UserMineManager *mg = [UserMineManager sharedInstance];
    self.userMineView.titleLabel.text = [NSString stringWithFormat:@"收集器:%@>",mg.currentUserMine.name];
}




- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}


#pragma mark-
#pragma mark---UI Event---
- (void)shoppingTap {
    if ([self.delegate respondsToSelector:@selector(exCapacityDidClick)]) {
        [self.delegate exCapacityDidClick];
    }
}

- (void)shopping2Tap {
    if ([self.delegate respondsToSelector:@selector(exSpeedRateDidClick)]) {
        [self.delegate exSpeedRateDidClick];
    }
}


- (void)submitPress
{
    UserMineManager *mineMg = [UserMineManager sharedInstance];
    NSInteger total = [mineMg.currentUserMine.mineTotal integerValue];
    if (([mineMg.currentUserMine.submitStatus intValue] != 1) && ([mineMg.currentUserMine.mineType.type intValue] != 1)) {
        total = total + [mineMg.currentUserMine.submitTotal integerValue];
    }
    
    double speedRate = [mineMg.currentUserMine.speedRate doubleValue];
    if (speedRate - 1.0 > 0) {
        total = total*speedRate;
    }
    
    if (total == 0) {
        [LoadingTool tipView:self.supperVC.view title:@"您当前收集量为0，没有能量可收取" image:nil];
    } else {
        NSNumber *maxQyt = mineMg.currentUserMine.maxQtyLimit;
        long max = [maxQyt longValue];
        NSString *str = [NSString stringWithFormat:@"今日收取上限%ldmAh\n 总收取量%ldmAh \n 确认收取?",max,total];
        [AlertTool alertShowTwoAction:self.supperVC title:@"收取" context:str okTitle:kStringValue_ok cancelTitle:@"取消" okHandler:^(UIAlertAction *action) {
            
            if ([mineMg.currentUserMine.mineType.type intValue] == 1) {
                [self submitMining];
            } else {
                DeviceModel *devModel = [[DeviceManager sharedInstance] getDeviceWithMac:mineMg.currentUserMine.mac];
                
                if (devModel.peripheral.state == CBPeripheralStateConnected) {
                    devModel.delegate = self;
                    [LoadingTool beginLoading:self.supperVC.view title:@"数据提交中..."];
                    [devModel sendClearBatteryCommond];
                    self.isClearRsp = NO;
                    [NSTimer scheduledTimerWithTimeInterval:10.0 target:self selector:@selector(deylay) userInfo:nil repeats:NO];
                } else {
                    [AlertTool alertShowTwoAction:self.supperVC  title:@"提示" context:@"收取失败, 设备处于未连接状态, 请连接设备后重试" okTitle:@"连接" cancelTitle:@"取消" okHandler:^(UIAlertAction *action) {
                        if ([self.delegate respondsToSelector:@selector(clickConnectPeripheral)]) {
                            [self.delegate clickConnectPeripheral];
                        }
                    } cancelHandler:nil];
                }
            }
            
        } cancelHandler:nil];
    }
}


#pragma mark-
#pragma mark----private----

- (void)submitMining
{
    UserInfoManager *item = [UserInfoManager sharedInstance];
    if (!item.userItem) {
        return;
    }
    self.submitBgView.userInteractionEnabled = NO;
    
    UserMineManager *mineMg = [UserMineManager sharedInstance];
    NSInteger total = [mineMg.currentUserMine.mineTotal integerValue];
    if (([mineMg.currentUserMine.submitStatus intValue] != 1) && ([mineMg.currentUserMine.mineType.type intValue] != 1)) {
        total = total + [mineMg.currentUserMine.submitTotal integerValue];
    }
    NSLog(@"-----submit total %ld ---",total);
    NSString *count = [NSString stringWithFormat:@"%ld",total];
    NSString *countBase32 = [count base32String];
    NSData *inputData = [countBase32 dataUsingEncoding:NSUTF8StringEncoding];
    NSString *countBase64 = [inputData base64EncodedString];
    
    NSString *res = [EncryptTool codeWithRandomString:countBase64];
    
    NSDictionary *dic = @{@"secret":kSecret,@"pid":item.userItem.pid,@"token":item.userItem.token,@"usermine_id":mineMg.currentUserMine.id,@"qty":res};
    [HttpRequestEngin submitMiningWithParam:dic successBlock:^(id  _Nullable responseObject) {
        NSError *error;
        RespondModel *apiModel = [[RespondModel alloc] initObjectWithDictionary:responseObject objectClass:[MiningModel class] objectError:&error];
        if (apiModel.success && apiModel.object) {
            MiningModel *model = apiModel.object;
            
            mineMg.currentUserMine.mineStatus = model.status;
            mineMg.currentUserMine.mineTotal = @0;
            mineMg.currentUserMine.mineTotal = @0;
            mineMg.currentUserMine.submitStatus = @1;
            mineMg.currentUserMine.submitTotal = @0;
            
            NSString *mac = mineMg.currentUserMine.mac;
            DeviceModel *devModel = [[DeviceManager sharedInstance] getDeviceWithMac:mac];
            [devModel sendClearBatteryCommond];
            dispatch_async(dispatch_get_main_queue(), ^{
                [self updateSubmitBgState];
                [AlertTool alertShowDefault:self.supperVC context:@"收取成功,收益将在凌晨0-2点进行区块结算,结算完成立即到账"];
            });
        } else {
            if ([mineMg.currentUserMine.mineType.type intValue] != 1) {
                mineMg.currentUserMine.submitStatus = @0;
                NSInteger total = [mineMg.currentUserMine.mineTotal integerValue];
                total = total + [mineMg.currentUserMine.submitTotal integerValue];
                mineMg.currentUserMine.submitTotal = [NSNumber numberWithInteger:total];
                mineMg.currentUserMine.mineTotal = @0;
                NSString *currentDate = [TimeTool getCurrentDateString];
                mineMg.currentUserMine.submitDate = currentDate;
            }
            
            ErrorModel *model = apiModel.error;
            ErrorCodeEvent *event = [[ErrorCodeEvent alloc] initWithError:model];
            [event errorCodeDone:self needLoading:NO];
            [AlertTool alertShowDefault:self.supperVC context:model.message];
        }
        [self topCellUpdateMiningState];
        self.submitBgView.userInteractionEnabled = YES;
    } failBlock:^(NSError * _Nonnull error) {
        if ([mineMg.currentUserMine.mineType.type intValue] != 1) {
            mineMg.currentUserMine.submitStatus = @0;
            NSInteger total = [mineMg.currentUserMine.mineTotal integerValue];
            total = total + [mineMg.currentUserMine.submitTotal integerValue];
            mineMg.currentUserMine.submitTotal = [NSNumber numberWithInteger:total];
            mineMg.currentUserMine.mineTotal = @0;
            NSString *currentDate = [TimeTool getCurrentDateString];
            mineMg.currentUserMine.submitDate = currentDate;
        }
        self.submitBgView.userInteractionEnabled = YES;
        [AlertTool alertShowDefault:self.supperVC context:@"收取失败"];
    } progress:^(NSProgress * _Nonnull loadprogress) {
        
    }];
}

- (void)connectSuccess:(CBPeripheral *)periphral row:(NSInteger)row {
    UserMineModel *currMine = [UserMineManager sharedInstance].currentUserMine;
    NSString *mac = currMine.mac;
    DeviceModel *devModel = [[DeviceManager sharedInstance] getDeviceWithMac:mac];
    devModel.delegate = self;
    [devModel sendClearBatteryCommond];
}

- (void)clearSuccessRsp {
    self.isClearRsp = YES;
    [LoadingTool finishLoading:self.supperVC.view title:@"提交成功" success:YES];
    [self submitMining];
}

- (void)deylay {
    if (!self.isClearRsp) {
        DeviceModel *devModel = [[DeviceManager sharedInstance] getDeviceWithMac:[UserMineManager sharedInstance].currentUserMine.mac];
        if (devModel.peripheral.state == CBPeripheralStateConnected) {
            [devModel sendClearBatteryCommond];
            [self submitMining];
        } else {
            [LoadingTool failLoading:self.supperVC.view title:@"提交失败"];
        }
        
    }
    
}

- (void)showNewYearImage {
    if ([ConfigManager sharedInstance].isChangeSkin) {
        if (!self.yearImageView) {
            self.yearImageView = [[UIImageView alloc] initWithFrame:CGRectZero];
            self.yearImageView.image = [UIImage imageNamed:@"cover_bg.jpg"];
            [self.cellBgView insertSubview:self.yearImageView atIndex:0];
            [self.yearImageView makeConstraints:^(MASConstraintMaker *make) {
                make.leading.equalTo(self.cellBgView.leading);
                make.trailing.equalTo(self.cellBgView.trailing);
                make.top.equalTo(self.cellBgView.top);
                make.bottom.equalTo(self.cellBgView.bottom);
            }];
        }
    }
}

- (void)updateSubmitBgState
{
    UserMineManager *userMg = [UserMineManager sharedInstance];
    int state = [userMg.currentUserMine.mineStatus intValue];
    
    if (state == 2 || state == 0)
    {
        self.submitBgView.hidden = YES;
    }
    else
    {
        ConfigManager *configMg = [ConfigManager sharedInstance];
        NSInteger hour = [TimeTool getCurrenDateForHour];
        NSString *valueStr = configMg.submitMiningConfig.value;
        NSArray *valueArr = [valueStr componentsSeparatedByString:@","];
        if (valueArr.count >= 2) {
            NSString *beginStr = [valueArr objectAtIndex:0];
            NSString *endStr = [valueArr objectAtIndex:1];
            int begin = [beginStr intValue];
            int end = [endStr intValue];
            if ((hour >= begin) && (hour <= end)) {
                self.submitBgView.hidden = NO;
            } else {
                self.submitBgView.hidden = YES;
            }
        } else {
            self.submitBgView.hidden = YES;
        }
    }
    
}

#pragma mark-
#pragma mark----public----
- (void)topCellUpdateTopCellSubView {
    [self showNewYearImage];
    [self updateSubmitBgState];
}

- (void)topCellUpdateUserMineInfo {
    UserMineManager *mineMg = [UserMineManager sharedInstance];
    long max = [mineMg.currentUserMine.maxQtyLimit longValue];
    self.maxQtyLimitLabel.text = [NSString stringWithFormat:@"%ld",max];
    self.speedRateLabel.text = [NSString stringWithFormat:@"%.1lf",[mineMg.currentUserMine.speedRate doubleValue]];
    [self updateSubmitBgState];
    
    
    int total = [[UserMineManager sharedInstance].currentUserMine.mineTotal intValue];
    [self.miningView updateAnimationBatteryTotal:(int)total];
}


- (void)topCellUpdateMinePercent {
    UserMineManager *mineMg = [UserMineManager sharedInstance];
    double speedRate = [mineMg.currentUserMine.speedRate doubleValue];
    long max = [mineMg.currentUserMine.maxQtyLimit longValue];
    
    NSInteger total =  [mineMg.currentUserMine.mineTotal integerValue];
    if (([mineMg.currentUserMine.mineType.type intValue] != 1) && ([mineMg.currentUserMine.submitStatus intValue] != 1)) {
        total = total+[mineMg.currentUserMine.submitTotal integerValue];
    }
    
    if (speedRate - 1.0 > 0) {
        total = total*speedRate;
    }
    
    float percent = ((float)total/max)*100;
    self.percentLabel.text = [NSString stringWithFormat:@"%.1f%%",percent];
    [self updateSubmitBgState];
}

- (void)topCellUpdateMiningState {
    
    UserMineModel *model = [UserMineManager sharedInstance].currentUserMine;
    int state = [model.mineStatus intValue];

    
    if (state == 0) {
        self.startView.hidden = NO;
        self.miningView.hidden = YES;
        self.submitBgView.hidden = YES;
        [self.miningView stopAnimation];
    } else if (state == 1) {
        self.startView.hidden = YES;
        self.miningView.hidden = NO;
        self.submitBgView.hidden = NO;
        [self.miningView startAnimation];
    } else if (state == 2) {
        self.startView.hidden = NO;
        self.miningView.hidden = YES;
        self.submitBgView.hidden = YES;
        [self.miningView stopAnimation];
    } else if (state == 3) {
        self.startView.hidden = NO;
        self.miningView.hidden = YES;
        self.submitBgView.hidden = YES;
        [self.miningView stopAnimation];
    }
    
    [self updateSubmitBgState];
    [self.startView updateCircleMiningState:(int)state];
}

- (void)topCellUdateMiningData {
    [self updateAppUserMineData];
}


- (void)updateAppUserMineData {
    
    UserMineModel *appMine = [UserMineManager sharedInstance].appUserMine;
    NSUserDefaults *userDef = [NSUserDefaults standardUserDefaults];
    NSString *lastDate = [userDef objectForKey:kMiningDate];
    NSString *currentDate = [TimeTool getCurrentDateString];
    if (![currentDate isEqualToString:lastDate]) {
        [userDef setObject:nil forKey:kBatteryRecordList];
        appMine.mineTotal = @0;
        appMine.mineStatus = @0;
    }
    NSLog(@"miningMainUpdateMiningData currentDate:%@ lastDate:%@",currentDate,lastDate);
    [userDef setObject:currentDate forKey:kMiningDate];
    [userDef synchronize];
}


- (void)updateAppUserMineBatteryLevel {
    int mAh = [DeviceTool batteryCurrentCapacity];
    NSLog(@"miningMainUpdateBatteryLevel 电量为:%d",mAh);
    if (mAh <= 0) {
        return;
    }
    UserMineModel *appMine = [UserMineManager sharedInstance].appUserMine;
    NSUserDefaults *userDef = [NSUserDefaults standardUserDefaults];
    NSInteger total = [appMine.mineTotal integerValue];
    
    if ([[UserMineManager sharedInstance].appUserMine.mineStatus intValue] == 1) {
        NSMutableArray *tempArray;
        NSArray *array = [userDef objectForKey:kBatteryRecordList];
        if (array) {
            tempArray = [NSMutableArray arrayWithArray:array];
        } else {
            tempArray = [NSMutableArray new];
        }
        
        int last = 0;
        if (tempArray.count > 0) {
            NSNumber *tNum = [tempArray objectAtIndex:tempArray.count-1];
            last = [tNum intValue];
            NSLog(@"miningMainUpdateBatteryLevel last:%d",[tNum intValue]);
            if ((mAh-last) < 0) {
                total = total+(last-mAh);
            }
        }
        NSLog(@"miningMainUpdateBatteryLevel total:%ld",(long)total);
        
        NSNumber *tempNum = [NSNumber numberWithInt:mAh];
        [tempArray addObject:tempNum];
        
        
        [userDef setObject:tempArray forKey:kBatteryRecordList];
        [userDef synchronize];
        
        appMine.mineTotal = [NSNumber numberWithInteger:total];
    }
}

- (void)topCellUpdateBatteryLevel
{
    [self updateAppUserMineBatteryLevel];
    
    int total = [[UserMineManager sharedInstance].currentUserMine.mineTotal intValue];
    
    [self.miningView updateAnimationBatteryTotal:(int)total];
    [self updateSubmitBgState];
}

- (void)topCellBatteryChangeState:(int)state {
    [self.miningView updateAnimationBatteryWarningState:state];
}


#pragma mark-
#pragma mark-----BeforeMiningDelegate
- (void)miningStateDidChange:(BOOL)start {
    [self topCellUdateMiningData];
    if (start) {
        [self.miningView startAnimation];
        [self topCellUpdateBatteryLevel];
        self.startView.hidden = YES;
        self.miningView.hidden = NO;
    } else {
        [self.miningView stopAnimation];
        self.startView.hidden = NO;
        self.miningView.hidden = YES;
    }
}
@end
