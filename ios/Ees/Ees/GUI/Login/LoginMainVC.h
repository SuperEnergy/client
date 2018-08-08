//
//  LoginMainVC.h
//  Ees
//
//  Created by xiaodong on 2017/12/11.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import <UIKit/UIKit.h>


enum LoginEnter
{
    LoginEnterRootView = 0,
    LoginEnterOther,
};

typedef void(^loginSuccessBlock)(NSDictionary *dic);
typedef void(^loginFailBlock)(void);


@interface LoginMainVC : FirstLevelBaseVC

@property(nonatomic,assign)enum LoginEnter enterType;


- (void)setLoginBlock:(loginSuccessBlock)successBlock failBlock:(loginFailBlock)failBlock;

@end
