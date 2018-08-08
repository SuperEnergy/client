//
//  NoticeMainVC.h
//  Ees
//
//  Created by xiaodong on 2017/12/10.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "DLTabedSlideView.h"

@interface NoticeMainVC : FirstLevelBaseVC<DLTabedSlideViewDelegate>

@property (weak, nonatomic) IBOutlet DLTabedSlideView *tabedSlideView;

- (void)noticeIndexChange:(int)type;
- (NSMutableArray *)getLastMessage;

@end
