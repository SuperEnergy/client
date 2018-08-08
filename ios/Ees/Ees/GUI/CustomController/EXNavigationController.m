//
//  EXNavigationController.m
//  Ees
//
//  Created by xiaodong on 2017/12/24.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import "EXNavigationController.h"

@interface EXNavigationController ()

@end

@implementation EXNavigationController

#pragma mark 一个类只会调用一次
+ (void)initialize
{
    UINavigationBar *navBar = [UINavigationBar appearance];
    //[UIApplication sharedApplication].statusBarStyle = UIStatusBarStyleBlackOpaque;
    // 1.4.设置导航栏的文字
    [navBar setTitleTextAttributes:@{
                                     UITextAttributeTextColor : kNavigationTextColor,
                                     
                                     UITextAttributeTextShadowOffset : [NSValue valueWithUIOffset:UIOffsetZero],
                                     UITextAttributeFont : kNavigationTextFont
                                     }];
    //[navBar setBarTintColor:kNavBarTintColor];
    //[navBar setBackgroundImage:[UIImage imageNamed:@"my_bg.jpg"] forBarMetrics:UIBarMetricsDefault];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
