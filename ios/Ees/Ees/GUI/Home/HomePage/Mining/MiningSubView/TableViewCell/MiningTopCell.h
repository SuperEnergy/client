//
//  MiningTopCell.h
//  Ees
//
//  Created by KCMac on 2018/3/27.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol MiningTopCellDelegate <NSObject>

- (void)exCapacityDidClick;
- (void)exSpeedRateDidClick;

- (void)clickConnectPeripheral;

@end



@interface MiningTopCell : UITableViewCell

@property(nonatomic,assign) id<MiningTopCellDelegate>delegate;
@property(nonatomic,weak) UIViewController *supperVC;

- (void)topCellUpdateTopCellSubView;
- (void)topCellUpdateUserMineInfo;
- (void)topCellUpdateMinePercent;
- (void)topCellUpdateMiningState;
- (void)topCellUpdateBatteryLevel;
- (void)topCellUdateMiningData;
- (void)topCellBatteryChangeState:(int)state;

@end
