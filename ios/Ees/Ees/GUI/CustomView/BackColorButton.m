//
//  BackColorButton.m
//  Ees
//
//  Created by xiaodong on 2018/1/14.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import "BackColorButton.h"

@implementation BackColorButton
{
    NSMutableDictionary *_colors;
}


- (instancetype)initWithCoder:(NSCoder *)aDecoder {
    if (self = [super initWithCoder:aDecoder]) {
        _colors = [NSMutableDictionary dictionary];
    }
    return self;
}
- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        _colors = [NSMutableDictionary dictionary];
    }
    return self;
}

- (void)setBackgroundColor:(UIColor *)backgroundColor forState:(UIControlState)state
{
    // If it is normal then set the standard background here
    if(state == UIControlStateNormal)
    {
        [super setBackgroundColor:backgroundColor];
    }
    // Store the background colour for that state
    [_colors setValue:backgroundColor forKey:[self keyForState:state]];
}
- (UIColor *)backgroundColorForState:(UIControlState)state
{
    return [_colors valueForKey:[self keyForState:state]];
}
- (void)setHighlighted:(BOOL)highlighted
{
    // Do original Highlight
    [super setHighlighted:highlighted];
    // Highlight with new colour OR replace with orignial
    NSString *highlightedKey = [self keyForState:UIControlStateHighlighted];
    UIColor *highlightedColor = [_colors valueForKey:highlightedKey];
    if (highlighted && highlightedColor) {
        [super setBackgroundColor:highlightedColor];
    } else {
        // 由于系统在调用setSelected后，会再触发一次setHighlighted，故做如下处理，否则，背景色会被最后一次的覆盖掉。
        if ([self isSelected]) {
            NSString *selectedKey = [self keyForState:UIControlStateSelected];
            UIColor *selectedColor = [_colors valueForKey:selectedKey];
            [super setBackgroundColor:selectedColor];
        } else {
            NSString *normalKey = [self keyForState:UIControlStateNormal];
            [super setBackgroundColor:[_colors valueForKey:normalKey]];
        }
    }
}
- (void)setSelected:(BOOL)selected
{
    // Do original Selected
    [super setSelected:selected];
    // Select with new colour OR replace with orignial
    NSString *selectedKey = [self keyForState:UIControlStateSelected];
    UIColor *selectedColor = [_colors valueForKey:selectedKey];
    if (selected && selectedColor) {
        [super setBackgroundColor:selectedColor];
    } else {
        NSString *normalKey = [self keyForState:UIControlStateNormal];
        [super setBackgroundColor:[_colors valueForKey:normalKey]];
    }
}
- (NSString *)keyForState:(UIControlState)state
{
    return [NSString stringWithFormat:@"state_%d", state];
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
