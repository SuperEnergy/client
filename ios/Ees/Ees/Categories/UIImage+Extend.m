//
//  UIImage.m
//  Ees
//
//  Created by KCMac on 2018/1/5.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import "UIImage+Extend.h"

@implementation UIImage (Extend)

//截取部分图像
-(UIImage*)getSubImage:(CGRect)rect
{
    CGImageRef subImageRef = CGImageCreateWithImageInRect(self.CGImage, rect);
    CGRect smallBounds = CGRectMake(0, 0, CGImageGetWidth(subImageRef), CGImageGetHeight(subImageRef));
    
    UIGraphicsBeginImageContext(smallBounds.size);
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextDrawImage(context, smallBounds, subImageRef);
    UIImage* smallImage = [UIImage imageWithCGImage:subImageRef];
    UIGraphicsEndImageContext();
    
    return smallImage;
}


-(UIImage*)getSubImageWithImageView:(UIImageView *)imageview
{
    CGSize hwdSize = imageview.frame.size;
    CGSize imageSize = self.size;
    CGRect rect;
    
    float scale;
    scale =(float)hwdSize.height/hwdSize.width;
    if (imageSize.height < imageSize.width *scale) {
        float height = imageSize.height;
        scale = (float)hwdSize.width/hwdSize.height;
        float width = height *scale;
        rect = CGRectMake((imageSize.width-width)/2, 0, width, height);
    } else {
        float width = imageSize.width;
        scale =(float)hwdSize.height/hwdSize.width;
        float height = width * scale;
        rect = CGRectMake(0, (imageSize.height-height)/2, width, height);
    }
    
    return [self getSubImage:rect];
}


@end
