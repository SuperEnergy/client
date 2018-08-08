//
//  AppDelegate.h
//  Ees
//
//  Created by xiaodong on 2017/12/10.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface AppDelegate : UIResponder <UIApplicationDelegate>

@property (strong, nonatomic) UIWindow *window;

- (void)swithToLogin;
- (void)switchTabBar:(int)index mesType:(int)mesType;
- (NSMutableArray *)getLastMessage;

@end

