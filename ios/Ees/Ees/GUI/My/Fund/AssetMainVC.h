//
//  AssetMainVC.h
//  Ees
//
//  Created by xiaodong on 2017/12/11.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "DLTabedSlideView.h"

@interface AssetMainVC : SecondLevelBaseVC<DLTabedSlideViewDelegate>
@property (weak, nonatomic) IBOutlet DLTabedSlideView *tabedSlideView;
@end
