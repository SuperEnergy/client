//
//  LoadingTool.h
//  Ees
//
//  Created by xiaodong on 2017/12/14.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface LoadingTool : NSObject

+ (void)beginLoading:(UIView *)superView title:(NSString *)title;
+ (void)loading:(UIView *)superView upload:(NSProgress *)upload;
+ (void)finishLoading:(UIView *)superView title:(NSString *)title success:(BOOL)success;
+ (void)failLoading:(UIView *)superView title:(NSString *)title;
+ (void)tipView:(UIView *)superView title:(NSString *)title image:(UIImage *)image;
@end
