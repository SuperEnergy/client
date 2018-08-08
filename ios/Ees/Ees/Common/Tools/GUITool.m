//
//  GUITool.m
//  Ees
//
//  Created by KCMac on 2017/12/20.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import "GUITool.h"

@implementation GUITool

+ (BOOL)isEmpty:(UITextField *)target
{
    BOOL result = NO;
    NSString *text = target.text;
    if ((text.length <= 0) || text == nil) {
        return YES;
    }
    return result;
}

+ (NSInteger)textLeng:(UITextField *)textField
{
    NSInteger leng = textField.text.length;
    return leng;
}



//Fund cell
+ (NSString *)fundSourceFromSubType:(NSString *)subtype sourceLabel:(UILabel *)sourceLabel {
    ConfigManager *configMg = [ConfigManager sharedInstance];
    
    NSString *res = [configMg.fundSubTypeNames.valueDic objectForKey:subtype];
    if ([subtype isEqualToString:SUBTYPE_TRRANSFER_OUT]) {
        sourceLabel.textColor = [UIColor orangeColor];
    } else if ([subtype isEqualToString:SUBTYPE_TRRANSFER_IN]) {
        sourceLabel.textColor = kCycleBackgroundColor;
    }
    
    if (res == nil) {
        res = @"其他";
    }
    
    return res;
}


//order
+ (NSString *)orderState:(int)status {
    NSString *res;
    switch (status) {
        case 1:
            res = @"未付款";
            break;
        case 2:
            res = @"支付成功";
            break;
        case 3:
            res = @"已取消";
            break;
        default:
            break;
    }
    return res;
}

+ (UIImage *)createNonInterpolatedUIImageFormCIImage:(CIImage *)image withSize:(CGFloat) size {
    CGRect extent = CGRectIntegral(image.extent);
    CGFloat scale = MIN(size/CGRectGetWidth(extent), size/CGRectGetHeight(extent));
    // 1.创建bitmap;
    size_t width = CGRectGetWidth(extent) * scale;
    size_t height = CGRectGetHeight(extent) * scale;
    CGColorSpaceRef cs = CGColorSpaceCreateDeviceGray();
    CGContextRef bitmapRef = CGBitmapContextCreate(nil, width, height, 8, 0, cs, (CGBitmapInfo)kCGImageAlphaNone);
    CIContext *context = [CIContext contextWithOptions:nil];
    CGImageRef bitmapImage = [context createCGImage:image fromRect:extent];
    CGContextSetInterpolationQuality(bitmapRef, kCGInterpolationNone);
    CGContextScaleCTM(bitmapRef, scale, scale);
    CGContextDrawImage(bitmapRef, extent, bitmapImage);
    // 2.保存bitmap到图片
    CGImageRef scaledImage = CGBitmapContextCreateImage(bitmapRef);
    CGContextRelease(bitmapRef);
    CGImageRelease(bitmapImage);
    return [UIImage imageWithCGImage:scaledImage];
}

@end
