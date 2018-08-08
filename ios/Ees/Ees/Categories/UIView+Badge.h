//
//  UIView+DKSBadge.h
//  TestProject
//
//  Created by aDu on 16/9/15.
//  Copyright © 2016年 DuKaiShun. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIView (Badge)

/**
 *  通过创建UIImageView，显示小图标；
 */
@property (nonatomic, strong) UIImageView *badge;

/**
 *  显示小红点
 */
- (void)showBadge;

/**
 *  隐藏小红点
 */
- (void)hidenBadge;

@end
