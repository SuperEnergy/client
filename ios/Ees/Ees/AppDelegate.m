//
//  AppDelegate.m
//  Ees
//
//  Created by xiaodong on 2017/12/10.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import "AppDelegate.h"
#import "LoginMainVC.h"
#import "HttpRequestEngin.h"

@interface AppDelegate ()
@property(nonatomic,strong) UIViewController *rootVC;
@end

@implementation AppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Override point for customization after application launch.
    
    __weak typeof(self)weakSelf = self;
    BOOL logined = [UserInfoManager sharedInstance].logined;
    if (!logined) {
        self.rootVC = self.window.rootViewController;
        UIStoryboard *board = [UIStoryboard storyboardWithName:@"Login" bundle:nil];
        LoginMainVC *ctrl = [board instantiateViewControllerWithIdentifier:@"LoginMainVC"];
        [ctrl setLoginBlock:^(NSDictionary *dic) {
            weakSelf.window.rootViewController = weakSelf.rootVC;
            [weakSelf updateUserInfo];
        } failBlock:^{
            
        }];
        EXNavigationController *navCtrol = [[EXNavigationController alloc] initWithRootViewController:ctrl];
        self.window.rootViewController = navCtrol;
        
    } else {
        [self updateUserInfo];
    }
    
    if ([NetTool checkNetWorkPermission] == 1) {
        [AlertTool alertShowDefault:self.window.rootViewController context:@"s"];
    }
    [[ConfigManager sharedInstance] setConfig:application];
    
    if ([UIApplication instancesRespondToSelector:@selector(registerUserNotificationSettings:)]){
        [[UIApplication sharedApplication] registerUserNotificationSettings:[UIUserNotificationSettings settingsForTypes:UIUserNotificationTypeAlert|UIUserNotificationTypeBadge|UIUserNotificationTypeSound categories:nil]];
    }
    
    return YES;
}

- (void)switchTabBar:(int)index mesType:(int)mesType
{
    UITabBarController *tabCtl = (UITabBarController *)self.window.rootViewController;
    [tabCtl setSelectedIndex:index];
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.2 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        NSArray *navArray = tabCtl.viewControllers;
        EXNavigationController *noticeNav = (EXNavigationController *)[navArray objectAtIndex:1];
        NSArray *vcArray = noticeNav.viewControllers;
        NoticeMainVC *noticeVC = (NoticeMainVC *)[vcArray objectAtIndex:0];
        [noticeVC noticeIndexChange:mesType];
    });
}

- (void)swithToLogin
{
    __weak typeof(self)weakSelf = self;
    self.rootVC = self.window.rootViewController;
    UIStoryboard *board = [UIStoryboard storyboardWithName:@"Login" bundle:nil];
    LoginMainVC *ctrl = [board instantiateViewControllerWithIdentifier:@"LoginMainVC"];
    [ctrl setLoginBlock:^(NSDictionary *dic) {
        weakSelf.window.rootViewController = weakSelf.rootVC;
        [weakSelf updateUserInfo];
    } failBlock:^{
        
    }];
    UINavigationController *navCtrol = [[UINavigationController alloc] initWithRootViewController:ctrl];
    self.window.rootViewController = navCtrol;
}


- (NSMutableArray *)getLastMessage {
    UITabBarController *tabCtl = (UITabBarController *)self.window.rootViewController;
    NSArray *navArray = tabCtl.viewControllers;
    EXNavigationController *noticeNav = (EXNavigationController *)[navArray objectAtIndex:1];
    NSArray *vcArray = noticeNav.viewControllers;
    NoticeMainVC *noticeVC = (NoticeMainVC *)[vcArray objectAtIndex:0];
    return [noticeVC getLastMessage];
}


- (void)applicationWillResignActive:(UIApplication *)application {
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
}




- (void)applicationDidEnterBackground:(UIApplication *)application {
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}


- (void)applicationWillEnterForeground:(UIApplication *)application {
    
    
    [self updateUserInfo];
    // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
}


- (void)applicationDidBecomeActive:(UIApplication *)application {
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    [[UserMineManager sharedInstance] clearUserMinesDataWhenDiffDate];
    [[DeviceManager sharedInstance] beginScanPeripherals];
}


- (void)applicationWillTerminate:(UIApplication *)application {
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}


- (void)application:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)notification
{
    [[UIApplication sharedApplication] setApplicationIconBadgeNumber:0];
}

#pragma mark-
#pragma mark---private function
- (void)updateUserInfo
{
    [ConfigManager sharedInstance].popVC = self.rootVC;
    [[ConfigManager sharedInstance] queryConfigList];
    [UserInfoManager sharedInstance].popVC = self.window.rootViewController;
    [[UserInfoManager sharedInstance] refreshUserInfo:^(BOOL success) {
        
    }];
    [[UserInfoManager sharedInstance] refreshLedger:^(BOOL success) {
        
    }];
    
    //UserInfoManager *item = [UserInfoManager sharedInstance];
    [MineManager sharedInstance].popVC = self.rootVC;
    [UserMineManager sharedInstance].popVC = self.rootVC;
    if ([UserMineManager sharedInstance].currentUserMine) {
        
        [[MineManager sharedInstance] queryMiningStatus:[UserMineManager sharedInstance].currentUserMine listRsp:^(UserMineModel *usermine) {
            
        }];
        [[UserMineManager sharedInstance] refreshUserMineList:^(UserMineModel *usermine) {
            
        }];
    } else {
        [[UserMineManager sharedInstance] refreshUserMineList:^(UserMineModel *usermine) {
            [[MineManager sharedInstance] queryMiningStatus:usermine listRsp:^(UserMineModel *usermine) {
                
            }];
        }];
    }
}

@end
