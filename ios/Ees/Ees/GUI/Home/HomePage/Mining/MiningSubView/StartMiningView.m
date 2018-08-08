//
//  StartMiningView.m
//  Ees
//
//  Created by KCMac on 2018/3/27.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import "StartMiningView.h"

@interface StartMiningView ()

@property(nonatomic,weak) IBOutlet UILabel *label;
@property(nonatomic,weak) IBOutlet UIView *roundView;
@property(nonatomic,weak) IBOutlet NSLayoutConstraint *labelCenterYConstraint;

@end


@implementation StartMiningView


- (void)awakeFromNib {
    [super awakeFromNib];
    [self setup];
}

- (void)layoutSubviews {
    [super layoutSubviews];
    [self.roundView.layer setCornerRadius:CGRectGetHeight([self.roundView bounds]) / 2];
    self.roundView.layer.masksToBounds = YES;
    self.roundView.layer.borderWidth = 5;
    self.roundView.layer.borderColor = [[UIColor whiteColor] CGColor];
}

#pragma mark-
#pragma mark----public---

- (void)updateCircleMiningState:(int)state
{
    NSString *title = @"";
    switch (state) {
        case 0:
            title = @"开始收集";
            self.labelCenterYConstraint.constant = 0;
            self.roundView.userInteractionEnabled = YES;
            break;
        case 1:
            title = @"正在收集";
            self.labelCenterYConstraint.constant = 0;
            self.roundView.userInteractionEnabled = NO;
            break;
        case 2:
            title = @"收取完成\n收益结算中";
            self.labelCenterYConstraint.constant = 10;
            self.roundView.userInteractionEnabled = NO;
            break;
        case 3:
            title = @"收取完成\n收益结算中";
            self.labelCenterYConstraint.constant = 10;
            self.roundView.userInteractionEnabled = NO;
            break;
        default:
            break;
    }
    self.label.text = title;
}


#pragma mark-
#pragma mark----private----
- (void)setup {
    //ui
    [self.roundView.layer setCornerRadius:CGRectGetHeight([self.roundView bounds]) / 2];
    self.roundView.layer.masksToBounds = YES;
    self.roundView.layer.borderWidth = 5;
    self.roundView.layer.borderColor = [[UIColor whiteColor] CGColor];
    
    CGFloat fontSize = 20.0*kDevicesScale;
    self.label.font = [UIFont systemFontOfSize:fontSize];
    self.labelCenterYConstraint.constant = 0;
    
    UITapGestureRecognizer *gesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(roundViewTapGesture)];
    [self.roundView addGestureRecognizer:gesture];
}

- (void)roundViewTapGesture
{
    ConfigManager *configMg = [ConfigManager sharedInstance];
    NSInteger hour = [TimeTool getCurrenDateForHour];
    NSString *valueStr = configMg.startMiningConfig.value;
    NSArray *valueArr = [valueStr componentsSeparatedByString:@","];
    if (valueArr.count >= 2) {
        NSString *beginStr = [valueArr objectAtIndex:0];
        NSString *endStr = [valueArr objectAtIndex:1];
        int begin = [beginStr intValue];
        int end = [endStr intValue];
        if ((hour >= begin) && (hour <= end)) {
            
            [self startMining];
        } else {
            [AlertTool alertShowDefault:self.supperVC context:@"非收集时间段，请明日再来"];
        }
    }
    
}


- (void)startMining
{
    UserInfoManager *item = [UserInfoManager sharedInstance];
    if (!item.userItem) {
        return;
    }
    UserMineManager *mineMg = [UserMineManager sharedInstance];
    if (!mineMg.currentUserMine) {
        return;
    }
    
    self.roundView.userInteractionEnabled = NO;
    
    NSDictionary *dic = @{@"secret":kSecret,@"pid":item.userItem.pid,@"token":item.userItem.token,@"usermine_id":mineMg.currentUserMine.id};
    [HttpRequestEngin startMiningWithParam:dic successBlock:^(id  _Nullable responseObject) {
        NSError *error;
        RespondModel *apiModel = [[RespondModel alloc] initObjectWithDictionary:responseObject objectClass:[MiningModel class] objectError:&error];
        if (apiModel.success && apiModel.object) {
            MiningModel *model = apiModel.object;
            mineMg.currentUserMine.mineStatus = model.status;
            
            if ([self.delegate respondsToSelector:@selector(miningStateDidChange:)]) {
                [self.delegate miningStateDidChange:YES];
            }
            
            NSUserDefaults *userDef = [NSUserDefaults standardUserDefaults];
            NSInteger time = [userDef integerForKey:kMiningTimes];
            if (time <= 5) {
                [AlertTool alertShowDefault:self.supperVC context:@"记得在20点-24点来\"收取\", 忘记收取将会没有收益, 请您关注下时间"];
            }
            time++;
            [userDef setInteger:time forKey:kMiningTimes];
            [userDef synchronize];
        } else {
            ErrorModel *model = apiModel.error;
            ErrorCodeEvent *event = [[ErrorCodeEvent alloc] initWithError:model];
            [event errorCodeDone:self needLoading:NO];
            [AlertTool alertShowDefault:self.supperVC context:model.message];
            if ([self.delegate respondsToSelector:@selector(miningStateDidChange:)]) {
                [self.delegate miningStateDidChange:NO];
            }
        }
        self.roundView.userInteractionEnabled = YES;
        
    } failBlock:^(NSError * _Nonnull error) {
        [LoadingTool tipView:self title:@"网络连接错误!" image:nil];
        if ([self.delegate respondsToSelector:@selector(miningStateDidChange:)]) {
            [self.delegate miningStateDidChange:NO];
        }
        self.roundView.userInteractionEnabled = YES;
        [AlertTool alertShowDefault:self.supperVC context:@"收集失败"];
    } progress:^(NSProgress * _Nonnull loadprogress) {
        
    }];
    
}


@end
