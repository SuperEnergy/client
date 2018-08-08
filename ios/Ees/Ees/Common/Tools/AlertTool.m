//
//  AlertTool.m
//  Ees
//
//  Created by xiaodong on 2018/1/9.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import "AlertTool.h"

@implementation AlertTool

+ (void)alertShowOKAction:(UIViewController *)target title:(NSString *)title context:(NSString *)context buttonTitle:(NSString *)buttonTitle handler:(void (^ __nullable)(UIAlertAction *action))handler
{
    NSString *context2 = [NSString stringWithFormat:@"\n%@",context];
    UIAlertController *alertController = [UIAlertController alertControllerWithTitle:title message:context2 preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *okAction = [UIAlertAction actionWithTitle:buttonTitle style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        if (handler) {
            handler(action);
        }
    }];
    [okAction setValue:kLoginBtnBGColor forKey:@"titleTextColor"];
    [alertController addAction:okAction];
    [target presentViewController:alertController animated:YES completion:nil];
}

+ (void)alertShowTwoAction:(UIViewController *)target title:(NSString *)title context:(NSString *)context okTitle:(NSString *)okTitle cancelTitle:(NSString *)cancelTitle okHandler:(void (^ __nullable)(UIAlertAction *action))okHandler cancelHandler:(void (^ __nullable)(UIAlertAction *action))cancelHandler
{
    NSString *context2 = [NSString stringWithFormat:@"\n%@",context];
    UIAlertController *alertController = [UIAlertController alertControllerWithTitle:title message:context2 preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *okAction = [UIAlertAction actionWithTitle:okTitle style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        if (okHandler) {
            okHandler(action);
        }
    }];
    UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:cancelTitle style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
        if (cancelHandler) {
            cancelHandler(action);
        }
    }];
    [okAction setValue:kLoginBtnBGColor forKey:@"titleTextColor"];
    [cancelAction setValue:kColorWithHex(0x999999) forKey:@"titleTextColor"];
    [alertController addAction:okAction];
    [alertController addAction:cancelAction];
    [target presentViewController:alertController animated:YES completion:nil];
}

+ (void)alertShowTwoActionTextAlignmentLeft:(UIViewController *)target title:(NSString *)title context:(NSString *)context okTitle:(NSString *)okTitle cancelTitle:(NSString *)cancelTitle okHandler:(void (^ __nullable)(UIAlertAction *action))okHandler cancelHandler:(void (^ __nullable)(UIAlertAction *action))cancelHandler {
    NSString *context2 = [NSString stringWithFormat:@"\n%@",context];
    UIAlertController *alertController = [UIAlertController alertControllerWithTitle:title message:context2 preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *okAction = [UIAlertAction actionWithTitle:okTitle style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        if (okHandler) {
            okHandler(action);
        }
    }];
    UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:cancelTitle style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
        if (cancelHandler) {
            cancelHandler(action);
        }
    }];
    
    UIView *subView1 = alertController.view.subviews[0];
    UIView *subView2 = subView1.subviews[0];
    UIView *subView3 = subView2.subviews[0];
    UIView *subView4 = subView3.subviews[0];
    UIView *subView5 = subView4.subviews[0];
    UILabel *messageLabel = subView5.subviews[1];
    messageLabel.textAlignment = NSTextAlignmentLeft;
    
    [okAction setValue:kLoginBtnBGColor forKey:@"titleTextColor"];
    [cancelAction setValue:kColorWithHex(0x999999) forKey:@"titleTextColor"];
    [alertController addAction:okAction];
    [alertController addAction:cancelAction];
    [target presentViewController:alertController animated:YES completion:nil];
}


+ (void)alertShowDefault:(UIViewController *)target context:(NSString *)context
{
    NSString *context2 = [NSString stringWithFormat:@"\n%@",context];
    UIAlertController *alertController = [UIAlertController alertControllerWithTitle:kStringValue_tip message:context2 preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *okAction = [UIAlertAction actionWithTitle:kStringValue_ok style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        
    }];
    [okAction setValue:kLoginBtnBGColor forKey:@"titleTextColor"];
    [alertController addAction:okAction];
    [target presentViewController:alertController animated:YES completion:nil];
}




@end
