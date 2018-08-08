//
//  Macros.h
//  Ees
//
//  Created by xiaodong on 2017/12/11.
//  Copyright © 2017年 xiaodong. All rights reserved.
//
//定义全局常量，或者是全局的宏

#ifndef Macros_h
#define Macros_h



//color
#define kColorWithHex(hexValue) [UIColor colorWithRed:((float)((hexValue & 0xFF0000) >> 16)) / 255.0 green:((float)((hexValue & 0xFF00) >> 8)) / 255.0 blue:((float)(hexValue & 0xFF)) / 255.0 alpha:1.0f]
#define kColorWithHexWithAlpha(hexValue,alpha) [UIColor colorWithRed:((float)((hexValue & 0xFF0000) >> 16)) / 255.0 green:((float)((hexValue & 0xFF00) >> 8)) / 255.0 blue:((float)(hexValue & 0xFF)) / 255.0 alpha:alpha]


//textfiled
#define kTextFieldBackgroundColor     kColorWithHex(0xf2f2f2)
#define kTextFieldTextColor           kColorWithHex(0x666666)

//nav
#define kNavigationTextColor         [UIColor blackColor]
#define kNavigationTextFont          [UIFont systemFontOfSize:18.0]
#define kNavBarTintColor             [UIColor blackColor]


//绿色button
#define kLoginBtnBGColor   [ConfigManager sharedInstance].isChangeSkin ? kColorWithHex(0x02c407):kColorWithHex(0x02c407)
#define kTabBarItemTextColor [ConfigManager sharedInstance].isChangeSkin ? [UIColor redColor]:kColorWithHex(0x02c407)

#define kLoginBtnBGHighlightColor  kColorWithHex(0x99eeeeee)
#define kLoginBtnFont      [UIFont systemFontOfSize:17.0]
#define kLoginBtnTitleColor [UIColor whiteColor]

//页面背景self.view(有2种背景色)
#define kViewBackgroundColor  kColorWithHex(0xaaeeeeee)
#define kViewBackgroundColor2  [UIColor whiteColor]

#define kCycleBackgroundColor  ([ConfigManager sharedInstance].isChangeSkin ? kColorWithHex(0x99CC00):kColorWithHex(0x99CC00))


#define kBlackColorAlpha      kColorWithHex(0x77000000)

//system color
#define kWhiteColor  [UIColor whiteColor]
#define kClearColor  [UIColor clearColor]
#define kLightGrayColor [UIColor lightGrayColor]

#define kPromotionColor  kColorWithHex(0xff1296db)
#define kGreenColor      kColorWithHex(0xFF02C407)


/*
 UIButton 高度统一为 40
 UITextField 的背景图统一高度为40
 UITextField 的高度统一为30
 UITextField 字体大小统一15
 小Icon统一为16*16
 获取验证码的UIButton字体大小统一是14
 数字键盘统一为 keyboardType = UIKeyboardTypeNumberPad
 首页Icon统一为24*24
 
 */


#define kImageButtonEdgeInsets  UIEdgeInsetsMake(8, 8, 8, 8)
#define kImageButtonEdgeInsets2  UIEdgeInsetsMake(9, 9, 9, 9)
#define kTabelViewEdgeInsets  UIEdgeInsetsMake(0, 0, 10, 0)

#define kDevicesScale ([UIScreen mainScreen].bounds.size.height/667)
#define kIsIphone5Below ([UIScreen mainScreen].bounds.size.height < 568?YES:NO)
#define kIsIphone6Below ([UIScreen mainScreen].bounds.size.height < 667?YES:NO)
#define kIs320WidthScreen ([UIScreen mainScreen].bounds.size.width <= 320?YES:NO)
#define kIsIpad  UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad
#define kIsIphone  UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone
#define kDeviceScreenWidth   [UIScreen mainScreen].bounds.size.width






#endif /* Macros_h */
