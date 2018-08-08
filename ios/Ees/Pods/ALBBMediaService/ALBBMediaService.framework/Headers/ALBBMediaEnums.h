//
//  ALBBMediaEnums.h
//  ALBBMediaService
//
//  Created by XuPeng on 16/9/9.
//  Copyright © 2016年 Alipay. All rights reserved.
//

#ifndef ALBBMediaEnums_h
#define ALBBMediaEnums_h

/** 多媒体SDK的错误码TFEError */
typedef NS_ENUM(NSInteger, TFEError) {
    TFEErrorUnKnown = 0,
    TFEErrorFileNotExist = 1001,
    TFEErrorFileTypeDisallow = 1002,
    TFEErrorFileRetryTimesExceed = 1003,
    TFEErrorMultipartRetryTimesExceed = 1004,
    TFEErrorUploadError = 1005,
    TFEErrorLoadError = 1006,
    TFEErrorFileReadException = 1007,
    TFEErrorAssetsReadException = 1008,
    TFEErrorNetworkNotReachable = 1009,
    TFEErrorNetworkChanged = 1010,
    TFEErrorConnectionTimeout = 1011,
    TFEErrorImageSizeExceed = 1012,
    TFEErrorIllegalArgument = 1013,
    TFEErrorLoadRequestError = 1014,
    TFEErrorSessionError = 1015,
    TFEErrorAuthFailed = 1016,
    TFEErrorTokenExipred = 1017,
    TFEErrorAssetsGlobalDenied = 1018,
    TFEErrorCanceled = 2001,
    
    TFEErrorTokenRequire = 3001,
    TFEErrorContentInvalid = 3002,
    TFEErrorIdOrUploadIdRequire = 3003,
    TFEErrorPartNumberInvalid = 3004,
    TFEErrorPartsInvalid = 3005,
    
    TFEErrorAppKeyNull = 10202,
    TFEErrorInitFailed = 10203
};

/** 多媒体SDK的环境变量TFEEnvironment */
typedef NS_ENUM(NSInteger, TFEEnvironment) {
    TFEEnvironmentDaily = 0,
    TFEEnvironmentPreRelease = 1,
    TFEEnvironmentRelease = 2,
    TFEEnvironmentSandBox = 3
};

/** 多媒体SDK的任务状态码TFETaskStatus */
typedef NS_ENUM(NSInteger, TFETaskStatus) {
    TFETaskStatusReady = 0,
    TFETaskStatusFailed = 1,
    TFETaskStatusCanceled = 2,
    TFETaskStatusRunning = 3,
    TFETaskStatusSuspend = 4,
    TFETaskStatusSuccess = 5
};

#endif /* ALBBMediaEnums_h */
