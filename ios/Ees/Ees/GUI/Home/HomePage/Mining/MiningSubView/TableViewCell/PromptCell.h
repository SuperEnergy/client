//
//  PromptCell.h
//  Ees
//
//  Created by xiaodong on 2018/3/27.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface PromptCell : UITableViewCell

@property(nonatomic,weak) UIViewController *supperVC;

- (void)updatePromptBatteryState:(int)batteryState;
- (void)promptUpdateMiningState;
- (void)switchHomePage;

@end
