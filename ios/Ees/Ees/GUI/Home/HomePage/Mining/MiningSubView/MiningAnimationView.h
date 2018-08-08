//
//  MiningAnimationView.h
//  Ees
//
//  Created by KCMac on 2018/3/27.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface MiningAnimationView : UIView

@property(nonatomic,weak) UIViewController *supperVC;

- (void)updateAnimationBatteryWarningState:(int)state;
- (void)updateAnimationQtyLimit;
- (void)updateAnimationBatteryTotal:(int)total;
- (void)startAnimation;
- (void)stopAnimation;

@end
