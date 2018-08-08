//
//  CoinMarketCell.h
//  Ees
//
//  Created by xiaodong on 2018/3/27.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface CoinMarketCell : UITableViewCell

@property(nonatomic,weak) UIViewController *supperVC;

- (void)updateNetWorkData;
@end
