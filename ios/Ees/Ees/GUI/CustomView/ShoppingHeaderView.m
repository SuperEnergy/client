//
//  ShoppingHeaderView.m
//  Ees
//
//  Created by xiaodong on 2018/1/14.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import "ShoppingHeaderView.h"

@interface ShoppingHeaderView ()

@property (weak, nonatomic) IBOutlet UILabel *label;
@property (weak, nonatomic) IBOutlet UILabel *label2;
@property (weak, nonatomic) IBOutlet UILabel *label3;

@end


@implementation ShoppingHeaderView

- (void)awakeFromNib {
    [super awakeFromNib];
    self.label.textColor = kColorWithHex(0x4c4c4c);
    self.label2.textColor = kColorWithHex(0x808080);
    self.label3.textColor = kColorWithHex(0x808080);
}


-(void)setModel:(UserMineModel *)model {
    if ([self.subtype intValue] == 0) {
        self.label.text = [NSString stringWithFormat:@"%@",model.name];
        self.label2.text = [NSString stringWithFormat:@"能量桶容量:%@mAh",[model.maxQtyLimit stringValue]];
        self.label3.text = [NSString stringWithFormat:@"能量桶容量上限:%@mAh",[model.maxQty stringValue]];
    } else {
        self.label.text = [NSString stringWithFormat:@"%@",model.name];
        self.label2.text = [NSString stringWithFormat:@"收集器加速率: %@",[model.speedRate stringValue]];
        self.label3.text = [NSString stringWithFormat:@"收集器加速率上限: %@",[model.maxSpeedRate stringValue]];
    }
}
/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
