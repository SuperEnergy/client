//
//  CoinMarketCell.m
//  Ees
//
//  Created by xiaodong on 2018/3/27.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import "CoinMarketCell2.h"

@interface CoinMarketCell2 ()

@property(nonatomic,weak) IBOutlet UIView *cellBgView;
@property(nonatomic,weak) IBOutlet UILabel *marketNameLable;
@property(nonatomic,weak) IBOutlet UILabel *coinEnNameLable;
@property(nonatomic,weak) IBOutlet UILabel *realLabel;
@property(nonatomic,weak) IBOutlet UILabel *lastLabel;
@property(nonatomic,weak) IBOutlet UILabel *priceRMBLabel;
@property(nonatomic,weak) IBOutlet UILabel *upDownLabel;
@property(nonatomic,weak) IBOutlet UILabel *detailLabel;



@end


@implementation CoinMarketCell2

- (void)awakeFromNib {
    [super awakeFromNib];
    self.backgroundColor = kClearColor;
    self.marketNameLable.textColor = kPromotionColor;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

-(void)setModel:(CoinMarketModel *)model{
    _model = model;
    self.marketNameLable.text = model.marketName;
    self.coinEnNameLable.text = @"EES";
    self.realLabel.text = @"实时行情";
    double lastDb = [model.last doubleValue];
    double priceRMBDb = [model.priceRMB doubleValue];
    NSString *unit = model.unit;
    NSString *priceStr = [DecimalNumberTool moneyRMBFormDouble:priceRMBDb];
    self.lastLabel.text = [NSString stringWithFormat:@"%lf",lastDb];
    self.priceRMBLabel.text = [NSString stringWithFormat:@"%@%@",unit,priceStr];
    NSString *upDown = model.upDown;
    if ([upDown hasPrefix:@"+"]) {
        self.upDownLabel.textColor = kGreenColor;
    } else {
        self.upDownLabel.textColor = [UIColor redColor];
    }
    NSString *dateStr = @"今日";
    self.upDownLabel.text = [NSString stringWithFormat:@"%@(%@)",upDown,dateStr];
    self.detailLabel.text = model.detail;
}


@end
