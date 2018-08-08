//
//  FastLoginVC.h
//  Ees
//
//  Created by xiaodong on 2017/12/12.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef void(^loginBlock)(BOOL success,NSDictionary *dic);

@interface FastLoginVC : SecondLevelBaseVC

- (void)setFastLoginBlock:(loginBlock)block;


@end
