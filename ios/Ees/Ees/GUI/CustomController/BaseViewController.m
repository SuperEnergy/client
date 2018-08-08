//
//  BaseViewController.m
//  Ees
//
//  Created by xiaodong on 2017/12/11.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import "BaseViewController.h"
#import "AFNetworking.h"


@interface BaseViewController ()

@end

@implementation BaseViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self netWorkMonitor];
    
     [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillChangeFrameNotification:) name:UIKeyboardWillChangeFrameNotification object:nil];
    // Do any additional setup after loading the view.
}

-(void)keyboardWillChangeFrameNotification:(NSNotification *)note{
    return;
    //取出键盘动画的时间(根据userInfo的key----UIKeyboardAnimationDurationUserInfoKey)
    CGFloat duration = [note.userInfo[UIKeyboardAnimationDurationUserInfoKey] floatValue];
    
    //取得键盘最后的frame(根据userInfo的key----UIKeyboardFrameEndUserInfoKey = "NSRect: {{0, 227}, {320, 253}}";)
    CGRect keyboardFrame = [note.userInfo[UIKeyboardFrameEndUserInfoKey] CGRectValue];
    
    //计算控制器的view需要平移的距离
    CGFloat transformY = keyboardFrame.origin.y - self.view.frame.size.height;
    
    //执行动画
    [UIView animateWithDuration:duration animations:^{
        //平移
        self.view.transform = CGAffineTransformMakeTranslation(0, transformY);
    }];
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)netWorkMonitor
{
    //通过AFNetworkReachabilityManager 可以用来检测网络状态的变化
    AFNetworkReachabilityManager *reachManager = [AFNetworkReachabilityManager sharedManager];
    [reachManager startMonitoring];
    [reachManager setReachabilityStatusChangeBlock:^(AFNetworkReachabilityStatus status) {
        switch (status) {
            case AFNetworkReachabilityStatusUnknown: {
                UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"网络状态"
                                                                    message:@"网络异常"
                                                                   delegate:nil
                                                          cancelButtonTitle:kStringValue_ok
                                                          otherButtonTitles:nil];
                [alertView show];
                break;
            }
            case AFNetworkReachabilityStatusNotReachable: {
                UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"网络状态"
                                                                    message:@"网络未连接"
                                                                   delegate:nil
                                                          cancelButtonTitle:kStringValue_ok
                                                          otherButtonTitles:nil];
                [alertView show];
                break;
            }
            case AFNetworkReachabilityStatusReachableViaWWAN: {
                //self.title = @"WWAN连接";
                break;
            }
            case AFNetworkReachabilityStatusReachableViaWiFi: {
                //self.title = @"WIFI连接";
                break;
            }
            default: {
                break;
            }
        }
    }];
}


- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    [self.view endEditing:YES];
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
