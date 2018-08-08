//
//  UIView+DKSBadge.m
//  TestProject
//
//  Created by aDu on 16/9/15.
//  Copyright © 2016年 DuKaiShun. All rights reserved.
//

#import "UIView+Badge.h"
#import <objc/runtime.h>

static char badgeViewKey;
static NSInteger const pointWidth = 30; //小红点的宽高
static NSInteger const rightRange = -5; //距离控件右边的距离

@implementation UIView (Badge)

- (void)showBadge
{
    if (self.badge == nil) {
        CGRect frame = CGRectMake(CGRectGetWidth(self.frame) + rightRange,-9, pointWidth, pointWidth);
        self.badge = [[UIImageView alloc] initWithFrame:frame];
        self.badge.backgroundColor = [UIColor clearColor];
        self.badge.image = [UIImage imageNamed:@"tab_notif.png"];
        [self addSubview:self.badge];
        [self bringSubviewToFront:self.badge];
    }
}


- (void)hidenBadge
{
    //从父视图上面移除
    [self.badge removeFromSuperview];
    self.badge = nil;
}

#pragma mark - GetterAndSetter

- (UIImageView *)badge
{
    //通过runtime创建一个UIImageview的属性
    return objc_getAssociatedObject(self, &badgeViewKey);
}

- (void)setBadge:(UILabel *)badge
{
    objc_setAssociatedObject(self, &badgeViewKey, badge, OBJC_ASSOCIATION_RETAIN);
}

@end
