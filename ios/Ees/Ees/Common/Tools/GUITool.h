//
//  GUITool.h
//  Ees
//
//  Created by KCMac on 2017/12/20.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface GUITool : NSObject

+ (BOOL)isEmpty:(UITextField *)target;
+ (NSInteger)textLeng:(UITextField *)textField;



//Fund cell
+ (NSString *)fundSourceFromSubType:(NSString *)subtype sourceLabel:(UILabel *)sourceLabel;

//order
+ (NSString *)orderState:(int)status;
+ (UIImage *)createNonInterpolatedUIImageFormCIImage:(CIImage *)image withSize:(CGFloat) size;

@end
