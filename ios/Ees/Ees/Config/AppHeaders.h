//
//  AppHeaders.h
//  Ees
//
//  Created by KCMac on 2017/12/19.
//  Copyright © 2017年 xiaodong. All rights reserved.
//
//存放app所有的头文件
#ifndef AppHeaders_h
#define AppHeaders_h

//config
#import "AppKeys.h"
#import "Macros.h"
#import "StringValue.h"
#import "NetHeader.h"


//Categories
#import "NSString+RegExp.h"
#import "UIImage+Extend.h"
#import "UIView+Extend.h"



//common
#import "TimeTool.h"
#import "LoadingTool.h"
#import "GUITool.h"
#import "MineManager.h"
#import "UserInfoManager.h"
#import "EncryptTool.h"
#import "ErrorCodeEvent.h"
#import "DecimalNumberTool.h"
#import "DeviceTool.h"
#import "FileTool.h"
#import "AlertTool.h"
#import "NetTool.h"


//network
#import "HttpRequestEngin.h"

//model
#import "RespondModel.h"
#import "ErrorModel.h"
#import "NoticeModel.h"
#import "FundModel.h"
#import "UserModel.h"
#import "MiningModel.h"
#import "ConfigModel.h"
#import "MineTypeModel.h"
#import "UserMineModel.h"
#import "LedgerModel.h"
#import "MiningReportModel.h"
#import "ConfigManager.h"
#import "GoodsModel.h"
#import "GoodsOrderModel.h"
#import "ExGoodsModel.h"
#import "DiscoverModel.h"
#import "CoinMarketModel.h"
#import "UserMineManager.h"
#import "DeviceManager.h"
#import "DeviceModel.h"



//customView
#import "NoticeCell.h"
#import "NoticeCell2.h"
#import "LoginInfoCell.h"
#import "AssetInfoCell.h"
#import "IconLabelCell.h"
#import "GeneralizeCell.h"
#import "OneButtonCell.h"
#import "TwoLabelCell.h"
#import "FundCell.h"
#import "FundCell2.h"
#import "AvatarCell.h"
#import "BackColorButton.h"
#import "ShoppingHeaderView.h"
#import "ShoppingFooterView.h"
#import "GoodsCell.h"
#import "OrderListCell.h"
#import "AppVersionCell.h"
#import "NotificationCell.h"
#import "EXTabBarItem.h"
#import "DiscoverListCell.h"
#import "UpdateVersionCell.h"
#import "UserMineView.h"
#import "MiningTopCell.h"
#import "StartMiningView.h"
#import "MiningAnimationView.h"
#import "TaskOneCell.h"
#import "TaskTwoCell.h"
#import "PromotionCell.h"
#import "PromptCell.h"
#import "MessageTipCell.h"
#import "NoticeTipCell.h"
#import "CoinMarketCell.h"
#import "LineChartCell.h"
#import "BarChartCell.h"
#import "CoinMarketCell2.h"
#import "DeviceListCell.h"
#import "UserMineCell.h"
#import "UserMineHeaderView.h"
#import "UpdateDeviceCell.h"
#import "DiscoverListCell2.h"


//custom viewcontroller
#import "BaseViewController.h"
#import "SecondLevelBaseVC.h"
#import "FirstLevelBaseVC.h"
#import "EXNavigationController.h"


//vendor
#define MAS_SHORTHAND
#import "Masonry.h"
#import "JSONModel.h"
#import "MBProgressHUD.h"
#import "AFNetworking.h"
#import <SDWebImage/UIImageView+WebCache.h>
#import "MJRefresh.h"
#import <ALBBMediaService/ALBBWantu.h>
#import <YLGIFImage.h>
#import <YLImageView.h>
#import "ZFChart.h"
#import "MF_Base32Additions.h"
#import "Base64.h"
#import <UMCommon/UMCommon.h>
#import <UMAnalytics/MobClick.h> 
#import <FLAnimatedImage/FLAnimatedImage.h>
#import "BabyBluetooth.h"


//viewcontroller
//login
#import "LoginMainVC.h"
#import "FastLoginVC.h"
#import "RegisterVC.h"
#import "ForgetPasswordVC.h"

//home page
#import "RealnameVC.h"
#import "RegisterMachVC.h"
#import "MiningMainVC.h"
#import "UploadIDPictureVC.h"
#import "ActivityPopVC.h"
#import "ShoppingVC.h"
#import "UserMineListVC.h"
#import "CoinMarketListVC.h"
#import "SharedPopVC.h"


//notice
#import "MessageVC.h"
#import "NoticeVC.h"
#import "NoticeDetailVC.h"
#import "NoticeMainVC.h"

//discover
#import "DiscoverMainVC.h"


//my
#import "AssetMainVC.h"
#import "TotalAssetRecordVC.h"
#import "MiningAssetRecordVC.h"
#import "EditUserInfoVC.h"
#import "TransferAccoutsVC.h"
#import "TransferRecordVC.h"
#import "WebViewVC.h"
#import "OrderListVC.h"
#import "SystemSettingVC.h"


#endif /* AppHeaders_h */
