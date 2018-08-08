//
//  NoticeTipCell.h
//  Ees
//
//  Created by xiaodong on 2018/3/27.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import <UIKit/UIKit.h>


@protocol NoticeTipCellDelegate <NSObject>

- (void)noticeStateDidChange;

@end


@interface NoticeTipCell : UITableViewCell

@property(nonatomic,weak) UIViewController *supperVC;
@property (nonatomic, strong) NoticeModel *model;
@property(nonatomic,assign)id<NoticeTipCellDelegate>delegate;

@end
