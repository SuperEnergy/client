//
//  UIView.m
//  Ees
//
//  Created by KCMac on 2018/1/6.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import "UIView+Extend.h"

@implementation UIView (Extend)

- (UIView *)subViewWithPoint:(CGPoint)point
{
    for (UIView *subview in self.subviews) {
        if(CGRectContainsPoint(subview.bounds, point)) {
            return subview;
        }
            
    }
    return nil;
}

@end
