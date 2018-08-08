//
//  ForgetPasswordVC.h
//  Ees
//
//  Created by xiaodong on 2017/12/12.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef enum : NSUInteger {
    PageTypeForgetPassword,
    PageTypeModifyPassword,
} PageType;

@interface ForgetPasswordVC : SecondLevelBaseVC

@property(nonatomic,assign)PageType type;

@end
