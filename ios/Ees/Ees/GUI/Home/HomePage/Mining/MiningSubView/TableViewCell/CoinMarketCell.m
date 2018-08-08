//
//  CoinMarketCell.m
//  Ees
//
//  Created by xiaodong on 2018/3/27.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import "CoinMarketCell.h"

@interface CoinMarketCell ()
{
    NSTimeInterval _lastInterval;
}
@property(nonatomic,weak) IBOutlet UIView *cellBgView;
@property(nonatomic,weak) IBOutlet UILabel *marketNameLable;
@property(nonatomic,weak) IBOutlet UILabel *coinEnNameLable;
@property(nonatomic,weak) IBOutlet UILabel *realLabel;
@property(nonatomic,weak) IBOutlet UILabel *lastLabel;
@property(nonatomic,weak) IBOutlet UILabel *priceRMBLabel;
@property(nonatomic,weak) IBOutlet UILabel *upDownLabel;
@property(nonatomic,weak) IBOutlet UILabel *detailLabel;
@property(nonatomic,weak) IBOutlet UIButton *refreshBtn;


@end


@implementation CoinMarketCell

- (void)awakeFromNib {
    [super awakeFromNib];
    
    [self getCoinmarket];
    
    self.marketNameLable.textColor = kPromotionColor;
    [self.refreshBtn setTitle:@"刷新" forState:UIControlStateNormal];
    [self.refreshBtn setTitle:@"刷新" forState:UIControlStateSelected];
    
    
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}


#pragma mark-
#pragma mark-----UI Event
- (IBAction)refresh:(id)sender {
    [self getCoinmarket];
}

#pragma mark-
#pragma mark-----private

- (void)noDataRsp:(BOOL)isShow {
    dispatch_async(dispatch_get_main_queue(), ^{
        self.marketNameLable.hidden = !isShow;
        self.coinEnNameLable.hidden = !isShow;
        self.lastLabel.hidden = !isShow;
        self.priceRMBLabel.hidden = !isShow;
        self.upDownLabel.hidden = !isShow;
        self.detailLabel.hidden = !isShow;
    });
}


- (void)getCoinmarket {
    
    UserInfoManager *item = [UserInfoManager sharedInstance];
    if (!item.userItem) {
        return;
    }
    
    NSDictionary *dic = @{@"secret":kSecret,@"pid":item.userItem.pid,@"token":item.userItem.token};
    [HttpRequestEngin getCoinmarket:dic successBlock:^(id  _Nullable responseObject) {
        NSError *error;
        RespondModel *apiModel = [[RespondModel alloc] initObjectWithDictionary:responseObject objectClass:[CoinMarketModel class] objectError:&error];
        
        if (apiModel.success && apiModel.object) {
            CoinMarketModel *model = apiModel.object;
            MineManager *mineMg = [MineManager sharedInstance];
            mineMg.cointMarket = model;
            dispatch_async(dispatch_get_main_queue(), ^{
                self.marketNameLable.text = model.marketName;
                self.coinEnNameLable.text = @"EES";
                self.realLabel.text = @"实时行情";
                double lastDb = [model.last doubleValue];
                double priceRMBDb = [model.priceRMB doubleValue];
                NSString *priceStr = [DecimalNumberTool moneyRMBFormDouble:priceRMBDb];
                NSString *unit = model.unit;
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
                self.detailLabel.text = model.detail;//[NSString stringWithFormat:@"量(24h)%@"]
            });
        } else {
            
            ErrorModel *model = apiModel.error;
            ErrorCodeEvent *event = [[ErrorCodeEvent alloc] initWithError:model];
            [event errorCodeDone:self.supperVC needLoading:YES];
        }
        
    } failBlock:^(NSError * _Nonnull error) {
        
    } progress:^(NSProgress * _Nonnull loadprogress) {
        
    }];
}

#pragma mark-
#pragma mark------public---
- (void)updateNetWorkData {
    NSTimeInterval curInterval = [[NSDate date] timeIntervalSince1970];
    if (curInterval - _lastInterval >= 5*60) {
        [self getCoinmarket];
    }
    _lastInterval = curInterval;
}

@end
