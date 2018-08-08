//
//  BackColorButton.h
//  Ees
//
//  Created by xiaodong on 2018/1/14.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface BackColorButton : UIButton

@property (nonatomic, copy) NSString *name;
- (void)setBackgroundColor:(UIColor *)backgroundColor forState:(UIControlState)state;
- (UIColor *)backgroundColorForState:(UIControlState)state;

@end
