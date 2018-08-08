//
//  NSString.h
//  Ees
//
//  Created by KCMac on 2018/1/5.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSString (RegExp)


- (BOOL)isValidPhoneNumber;
- (BOOL)isValidPassword;
- (BOOL)isValidVerificationCode;
- (BOOL)isEmpty;
- (BOOL)isValidCard;
- (BOOL)isValidTransferCount;


@end
