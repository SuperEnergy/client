//
//  PromptCell.m
//  Ees
//
//  Created by xiaodong on 2018/3/27.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import "PromptCell.h"


@interface PromptCell ()

@property(nonatomic,weak) IBOutlet UIView *cellBgView;
@property(nonatomic,weak) IBOutlet UIImageView *submitImageView;
@property(nonatomic,weak) IBOutlet UILabel *submitTipLabel;
@property(nonatomic,strong) NSTimer *timer;
@property(nonatomic,strong) NSDate *zeroDate;

@end

@implementation PromptCell

- (void)awakeFromNib {
    [super awakeFromNib];
    
    
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}


#pragma mark-
#pragma mark-----public----

- (void)switchHomePage {
    self.zeroDate = [TimeTool currentZeroDate];
}


- (void)promptUpdateMiningState {
    
    UserMineManager *mineMg = [UserMineManager sharedInstance];
    
    [self endPromptCountDown];
    
    if ([mineMg.currentUserMine.mineType.type intValue] == 1) {
        if ([mineMg.currentUserMine.mineStatus intValue]== 2 || [mineMg.currentUserMine.mineStatus intValue]== 3) {
            [self beginPromptCountDown];
        }
    }
}

- (void)updatePromptBatteryState:(int)batteryState
{
    if (batteryState == 2 || batteryState == 3) {
        self.submitTipLabel.text = @"充电期间无法进行收集能量";
        self.submitImageView.image = [UIImage imageNamed:@"ic_warnning.png"];
        self.submitTipLabel.textColor = [UIColor redColor];
    }
}


#pragma mark-
#pragma mark----private----

- (void)beginPromptCountDown {
    
    self.submitImageView.image = [UIImage imageNamed:@"ic_time.png"];
    self.submitTipLabel.textColor = [UIColor orangeColor];
    if (!self.zeroDate) {
        self.zeroDate = [TimeTool currentZeroDate];
    }
    if (self.timer) {
        [self.timer invalidate];
        self.timer = nil;
    }
    self.timer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(timerEvent:) userInfo:nil repeats:YES];
    [self.timer fire];
}

- (void)endPromptCountDown {
    if (self.timer) {
        [self.timer invalidate];
        self.timer = nil;
    }
}


- (void)timerEvent:(NSTimer*)theTimer {
    NSDate *nowDate = [NSDate date];
    NSDate *localNowDate = [nowDate dateByAddingTimeInterval:60*60*8];
    NSTimeInterval interval = [self.zeroDate timeIntervalSinceDate:localNowDate];
    
    int hour = interval/3600;
    double hourMod = (long)interval%3600;
    int minute = hourMod/60;
    double MinuteMod = (long)hourMod%60;
    int second = MinuteMod;
    self.submitTipLabel.text = [NSString stringWithFormat:@"距离下次开始时间还剩:%d小时%d分钟%d秒",hour,minute,second];
    if (interval <= 1) {
        [self.timer invalidate];
    }
}
@end
