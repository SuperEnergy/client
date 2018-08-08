//
//  LineChartCell.h
//  Ees
//
//  Created by xiaodong on 2018/3/28.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface LineChartCell : UITableViewCell

@property(nonatomic,weak) UIViewController *supperVC;

- (void)updateNetWorkData;

@end
