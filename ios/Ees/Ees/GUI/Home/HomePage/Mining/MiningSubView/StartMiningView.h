//
//  StartMiningView.h
//  Ees
//
//  Created by KCMac on 2018/3/27.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol BeforeMiningDelegate <NSObject>

@required
- (void)miningStateDidChange:(BOOL)start;

@end


@interface StartMiningView : UIView

@property(nonatomic,weak) UIViewController *supperVC;
@property(nonatomic,assign)id <BeforeMiningDelegate>delegate;

- (void)updateCircleMiningState:(int)state;

@end
