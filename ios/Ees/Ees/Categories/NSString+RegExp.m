//
//  NSString.m
//  Ees
//
//  Created by KCMac on 2018/1/5.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import "NSString+RegExp.h"

@implementation NSString (RegExp)

- (BOOL)isValidateByRegex:(NSString *)regex {
    NSPredicate *pre = [NSPredicate predicateWithFormat:@"SELF MATCHES %@",regex];
    NSString *desStr;
    desStr = [self stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
    return [pre evaluateWithObject:desStr];
}

//验证手机号
- (BOOL)isValidPhoneNumber {
    NSString *phone = @"^(0|86|17951)?(13[0-9]|15[012356789]|17[0135678]|18[0-9]|14[579])[0-9]{8}$";
    
    if ([self isValidateByRegex:phone]) {
        return YES;
    }else {
        return NO;
    }
}

- (BOOL)isValidPassword {
    NSString *regEx =[NSString stringWithFormat:@"^[a-zA-Z0-9]{%d,%d}+$", 6, 25];
    return [self isValidateByRegex:regEx];
    return NO;
}

- (BOOL)isValidVerificationCode {
    NSString *regEx = [NSString stringWithFormat:@"^\\d{%@,}$", @"6"];
    return [self isValidateByRegex:regEx];
}

- (BOOL)isEmpty {
    if (self.length == 0) {
        return YES;
    }
    return NO;
}

//身份证号验证
- (BOOL)isValidCard {
    NSString *regEx = @"(^[0-9]{15}$)|([0-9]{17}([0-9]|X)$)";
    return [self isValidateByRegex:regEx];
}

- (BOOL)isValidTransferCount {
    double count = [self doubleValue];
    if (count > 0.0) {
        return YES;
    } else {
        return NO;
    }
}



//备用
//特殊字符
- (BOOL)isSpecialChar {
    NSString *regEx = @"[^%&',;=?$\x22]+";
    return [self isValidateByRegex:regEx];
}

//只能输入数字
- (BOOL)isNumber {
    NSString *regEx = @"^[0-9]*$";
    return [self isValidateByRegex:regEx];
}

//校验只能输入n位的数字
- (BOOL)isNumberWithLength:(NSString *)length {
    NSString *regEx = [NSString stringWithFormat:@"^\\d{%@}$", length];
    return [self isValidateByRegex:regEx];
}

//校验最少输入n位的数字
- (BOOL)isNumberWithLeastLength:(NSString *)leastLength {
    NSString *regEx = [NSString stringWithFormat:@"^\\d{%@,}$", leastLength];
    return [self isValidateByRegex:regEx];
}

//大写字母
- (BOOL)isUpperLetter {
    NSString *regEx = @"^[A-Z]+$";
    return [self isValidateByRegex:regEx];
}

//小写字母
- (BOOL)isLowerLetter {
    NSString *regEx = @"^[a-z]+$";
    return [self isValidateByRegex:regEx];
}

- (BOOL)isValidEmail {
    NSString *regEx = @"[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
    return [self isValidateByRegex:regEx];
}


@end
