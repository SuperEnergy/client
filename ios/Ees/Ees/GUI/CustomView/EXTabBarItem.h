//
//  EXTabBarItem.h
//  Ees
//
//  Created by xiaodong on 2017/12/15.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface EXTabBarItem : UITabBarItem


/**
 * 显示小图标
 * 需要在哪个item上面显示小图标时，就用那个item调用
 */
- (void)showBadge;

/**
 * 隐藏小图标
 * 需要隐藏哪个item上面小图标时，就用那个item调用
 */
- (void)hidenBadge;


@end
