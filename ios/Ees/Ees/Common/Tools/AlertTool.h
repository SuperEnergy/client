//
//  AlertTool.h
//  Ees
//
//  Created by xiaodong on 2018/1/9.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface AlertTool : NSObject

+ (void)alertShowOKAction:(UIViewController *_Nullable)target title:(NSString *)title context:(NSString *)context buttonTitle:(NSString *)buttonTitle handler:(void (^ __nullable)(UIAlertAction *action))handler;
+ (void)alertShowTwoAction:(UIViewController *)target title:(NSString *)title context:(NSString *)context okTitle:(NSString *)okTitle cancelTitle:(NSString *)cancelTitle okHandler:(void (^ __nullable)(UIAlertAction *action))okHandler cancelHandler:(void (^ __nullable)(UIAlertAction *action))cancelHandler;
+ (void)alertShowTwoActionTextAlignmentLeft:(UIViewController *)target title:(NSString *)title context:(NSString *)context okTitle:(NSString *)okTitle cancelTitle:(NSString *)cancelTitle okHandler:(void (^ __nullable)(UIAlertAction *action))okHandler cancelHandler:(void (^ __nullable)(UIAlertAction *action))cancelHandler;
+ (void)alertShowDefault:(UIViewController *)target context:(NSString *)context;



@end
