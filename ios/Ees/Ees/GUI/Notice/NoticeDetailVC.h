//
//  NoticeDetailVC.h
//  Ees
//
//  Created by KCMac on 2017/12/19.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface NoticeDetailVC : SecondLevelBaseVC

@property(nonatomic,strong) NSString *noticeId;
@property(nonatomic,assign) BOOL isLinkAction;
@property(nonatomic,strong) NSString *link;

@end
