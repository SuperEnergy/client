//
//  ActivityPopVC.h
//  Ees
//
//  Created by xiaodong on 2017/12/25.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import "BaseViewController.h"

@protocol ActivityPopDelegate <NSObject>

- (void)activityPopDidClick:(NoticeModel *)model;

@end

@interface ActivityPopVC : UIViewController

@property(nonatomic,weak) IBOutlet UIImageView *coverView;
@property(nonatomic,assign) id<ActivityPopDelegate>delegate;
@property(nonatomic,strong) NoticeModel *model;

@end
