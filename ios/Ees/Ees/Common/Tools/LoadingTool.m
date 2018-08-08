//
//  LoadingTool.m
//  Ees
//
//  Created by xiaodong on 2017/12/14.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import "LoadingTool.h"

@implementation LoadingTool

+ (void)beginLoading:(UIView *)superView title:(NSString *)title
{
    dispatch_async(dispatch_get_main_queue(), ^{
        MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:superView animated:YES];
        hud.label.text = title;
        hud.minSize = CGSizeMake(150.f, 100.f);
    });
}

//进度条
+ (void)loading:(UIView *)superView upload:(NSProgress *)upload
{
    dispatch_async(dispatch_get_main_queue(), ^{
        float progress = (float)upload.completedUnitCount / (float)upload.totalUnitCount;
        NSLog(@"-----------%lf----",progress);
        MBProgressHUD *hud = [MBProgressHUD HUDForView:superView];
        hud.mode = MBProgressHUDModeDeterminate;
        hud.progress = progress;
    });
}

+ (void)finishLoading:(UIView *)superView title:(NSString *)title success:(BOOL)success
{
    dispatch_async(dispatch_get_main_queue(), ^{
        MBProgressHUD *hud = [MBProgressHUD HUDForView:superView];
        UIImage *image = [[UIImage imageNamed:@"Checkmark"] imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
        UIImageView *imageView = [[UIImageView alloc] initWithImage:image];
        hud.customView = imageView;
        hud.mode = MBProgressHUDModeCustomView;
        hud.label.text = title;
        [hud hideAnimated:YES afterDelay:2.f];
    });
}


+ (void)failLoading:(UIView *)superView title:(NSString *)title
{
    dispatch_async(dispatch_get_main_queue(), ^{
        MBProgressHUD *hud = [MBProgressHUD HUDForView:superView];
        hud.mode = MBProgressHUDModeText;
        hud.label.text = @"";
        hud.detailsLabel.text = title;
        [hud hideAnimated:YES afterDelay:3.f];
    });
}


+ (void)tipView:(UIView *)superView title:(NSString *)title image:(UIImage *)image
{
    dispatch_async(dispatch_get_main_queue(), ^{
        MBProgressHUD *hub = [MBProgressHUD showHUDAddedTo:superView animated:YES];
        hub.mode = MBProgressHUDModeCustomView;
        if (image) {
            hub.customView = [[UIImageView alloc] initWithImage:image];
        }
        hub.label.text = title;
        [hub hideAnimated:YES afterDelay:2.0f];
    });
}

@end
