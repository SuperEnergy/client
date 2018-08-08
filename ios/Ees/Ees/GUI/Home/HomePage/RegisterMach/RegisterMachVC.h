//
//  RegisterMachVC.h
//  Ees
//
//  Created by KCMac on 2017/12/19.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol RegisterMachVCDelegate <NSObject>

- (void)realNameDidClick;
- (void)statusBarDidChangeColor:(UIColor *)color;

@end


@interface RegisterMachVC : UIViewController

@property(nonatomic,assign)id <RegisterMachVCDelegate> delegate;
- (void)updateRegisterMachUserState:(UserInfoManager *)item;

@end
