//
//  PromotionCell.m
//  Ees
//
//  Created by xiaodong on 2018/3/27.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import "PromotionCell.h"

@interface PromotionCell ()

@property(nonatomic,weak) IBOutlet UIView *cellBgView;
@property(nonatomic,weak) IBOutlet UIImageView *iconView;
@property(nonatomic,weak) IBOutlet UILabel *titleLabel;

@end



@implementation PromotionCell

- (void)awakeFromNib {
    [super awakeFromNib];
    
    self.titleLabel.textColor = kPromotionColor;
    self.titleLabel.text = @"我要推广赚取收益!";
    self.button.imageEdgeInsets = kImageButtonEdgeInsets;
    //[self.button setTitle:@"快捷复制" forState:UIControlStateNormal];
    //[self.button setTitle:@"快捷复制" forState:UIControlStateSelected];
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
