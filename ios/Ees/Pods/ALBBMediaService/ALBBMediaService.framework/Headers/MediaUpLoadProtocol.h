//
//  MediaUpLoadProtocol.h
//  ALBBMediaService
//
//  Created by XuPeng on 16/9/10.
//  Copyright © 2016年 Alipay. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TFEUploadNotification.h"
@class TFEUploadParameters;
@class TFEUploadOptions;

/**
 *  多媒体SDK上传文件的接口，对外提供向多媒体空间上传文件相关的方法
 */
@protocol MediaUpLoadProtocol <NSObject>

/**
 *  直接上传接口
 *
 *  @param data         文件data
 *  @param ns           space
 *  @param fileName     服务器上存储的文件名
 *  @param dir          服务器上存储的路径
 *  @param progress     上传进度
 *  @param success      上传成功通知
 *  @param upload       上传失败通知
 *
 *  @return 任务唯一标识
 */
- (NSString *)uploadByData:(NSData *)data
                     space:(NSString *)space
                  fileName:(NSString *)fileName
                       dir:(NSString *)dir
                  progress:(TFEUploadProgress)progress
                   success:(TFEUploadSuccess)success
                    failed:(TFEUploadFailed)failed;

/**
 * 上传接口
 * @param parameters
 * @param notification
 * @return uniqueId
 */
- (NSString *)upload:(TFEUploadParameters *)parameters notification:(TFEUploadNotification *)notification;

/**
 * 上传接口
 * @param parameters
 * @param options
 * @param notification
 * @return uniqueId
 */
- (NSString *)upload:(TFEUploadParameters *)parameters options:(TFEUploadOptions *)options notification:(TFEUploadNotification *)notification;

/**
 *  取消所有任务
 */
- (void)cancelAllUploads;

/**
 *  取消特定任务
 *
 *  @param localUniqueIdentifier    本地上传id，用于标识本地一个任务， 使用TaeFile.localUniqueIdentifier, 或者使用upload的返回参数
 */
- (void)cancelUploadByUniqueId:(NSString *)uniqueIdentifier;

@end
