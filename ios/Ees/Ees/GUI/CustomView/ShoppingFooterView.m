//
//  ShoppingFooterView.m
//  Ees
//
//  Created by xiaodong on 2018/1/14.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import "ShoppingFooterView.h"


@interface ShoppingFooterView ()

@property (weak, nonatomic) IBOutlet UILabel *label;
@property (weak, nonatomic) IBOutlet UILabel *label2;
@property (weak, nonatomic) IBOutlet UILabel *label3;
@property (weak, nonatomic) IBOutlet UIButton *button;
@property(weak,nonatomic) IBOutlet UIView *lineView;

@end


@implementation ShoppingFooterView


- (void)awakeFromNib {
    [super awakeFromNib];
    
    UserInfoManager *userInfo = [UserInfoManager sharedInstance];
    LedgerModel *fund = userInfo.ledger;
    NSString *str = [DecimalNumberTool moneyFormatFormDouble:[fund.fund doubleValue]];
    self.label.text = [NSString stringWithFormat:@"可用余额 %@",str];
    self.label.textColor = kColorWithHex(0x808080);
    self.lineView.backgroundColor = kColorWithHex(0xcecece);
    self.label2.text = @"合计:";
    self.label2.textColor = kColorWithHex(0x4c4c4c);
    self.label3.textColor = kColorWithHex(0x4c4c4c);
    [self.button setTitle:@"结算" forState:UIControlStateNormal];
    [self.button setTitle:@"结算" forState:UIControlStateHighlighted];
    [self.button setBackgroundColor:kLoginBtnBGColor];
    
    [self updateOrderStatus:@"0.0ees"];
}

- (void)updateOrderStatus:(NSString *)str {
    self.label3.text = str;
}

- (void)updateLedgerFund {
    UserInfoManager *userInfo = [UserInfoManager sharedInstance];
    LedgerModel *fund = userInfo.ledger;
    NSString *str = [DecimalNumberTool moneyFormatFormDouble:[fund.fund doubleValue]];
    self.label.text = [NSString stringWithFormat:@"可用余额 %@",str];
}
/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
