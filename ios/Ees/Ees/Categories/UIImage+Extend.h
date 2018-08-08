//
//  UIImage.h
//  Ees
//
//  Created by KCMac on 2018/1/5.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface UIImage (Extend)

-(UIImage*)getSubImage:(CGRect)rect;
-(UIImage*)getSubImageWithImageView:(UIImageView *)imageview;

@end
