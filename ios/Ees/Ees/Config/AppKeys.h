//
//  AppKeys.h
//  Ees
//
//  Created by xiaodong on 2017/12/11.
//  Copyright © 2017年 xiaodong. All rights reserved.
//
//定义通知，存储UserDefault的key值

#ifndef AppKeys_h
#define AppKeys_h


//Notification key
#define kMiningStatusDidChange  @"MiningStatusDidChange"
#define kUserMineDidRefresh  @"UserMineDidRefresh"
#define kUserStateDidChange  @"UserStateDidChange"
#define kMaxQtyLimitDidChange @"MaxQtyLimitDidChange"
#define kClickUserMineView  @"ClickUserMineView"
#define kUserMineDidSwitch  @"UserMineDidSwitch"
#define kRefreshUserMineData @"RefreshUserMineData"



//NSUserDefault
#define kLoginState   @"LoginState"
#define kBatteryRecordList   @"BatteryRecordList"
#define kMiningDate    @"MiningDate"
#define kFundReportDate    @"FundReportDate"
#define kMiningReportDate  @"MiningReportDate"
#define kLocalNotificationSwitch @"LocalNotificationSwitch"
#define kCurrentUserMineId  @"CurrentUserMineId"


#define kLastestDayFundsVersion  @"LastestDayFundsVersion"
#define kLastestDayMiningsVersion  @"LastestDayMiningsVersion"
#define kMessageVersion @"MessageVersion"
#define kNoticeVersion @"NoticeVersion"
#define kLedgerVersion @"LedgerVersion"

#define kMiningTimes  @"MiningTimes"
#define kIsChangeSkin @"isChangeSkin"
#define kDiscoverVersion @"DiscoverVersion"
#define kUserMineState @"UserMineState"
#define kUserMineTotal @"UserMineTotal"
#define kSettingVersion @"SettingVersion"
#define kSubmitTotal  @"SubmitTotal"
#define kSubmitStatus @"SubmitStatus"
#define kSubmitDate   @"SubmitDate"


//File name
#define kLedgerFileName @"Ledger"
#define kUserFileName @"User"
#define kUserMineFileName @"userMine"
#define kFundFileName @"fund"
#define kMiningFileName @"mining"
#define kMessageFileName @"Message"
#define kNoticeFileName @"Notice"
#define kDiscoverFileName @"Discover"
#define kSettingFileName @"Setting"


#endif /* AppKeys_h */
