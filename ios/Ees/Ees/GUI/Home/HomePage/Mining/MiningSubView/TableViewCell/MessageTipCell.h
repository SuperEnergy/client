//
//  MessageTipCell.h
//  Ees
//
//  Created by xiaodong on 2018/3/27.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol MessageTipCellDelegate <NSObject>

- (void)messageStateDidChange;

@end


@interface MessageTipCell : UITableViewCell

@property(nonatomic,weak) UIViewController *supperVC;
@property (nonatomic, strong) NoticeModel *model;
@property(nonatomic,assign)id<MessageTipCellDelegate>delegate;


@end
