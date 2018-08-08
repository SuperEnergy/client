//
//  TaskOneCell.m
//  Ees
//
//  Created by KCMac on 2018/3/27.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import "TaskOneCell.h"

@interface TaskOneCell ()

@property(nonatomic,weak)IBOutlet UIView *cellBgView;
@property(nonatomic,weak) IBOutlet UIImageView *iconView;
@property(nonatomic,weak) IBOutlet UILabel *titleLabel;

@end





@implementation TaskOneCell

- (void)awakeFromNib {
    [super awakeFromNib];
    
    self.titleLabel.textColor = kGreenColor;
    self.titleLabel.text = @"任务1:立即推广一个好友赚取300mAh能量桶容量!";
    
    [self.button setTitle:@"立即赚取" forState:UIControlStateNormal];
    [self.button setTitle:@"立即赚取" forState:UIControlStateSelected];
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
