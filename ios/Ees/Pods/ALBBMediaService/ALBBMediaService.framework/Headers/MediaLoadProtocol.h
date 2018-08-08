//
//  MediaLoadProtocol.h
//  ALBBMediaService
//
//  Created by XuPeng on 16/9/10.
//  Copyright © 2016年 Alipay. All rights reserved.
//

#import <Foundation/Foundation.h>
@class TFEImageTransferOptions;
@class TFEFile;
@class TFELoadSession;
@class TFELoadOptions;
@class TFELoadNotification;

/**
 *  多媒体SDK下载接口，对外提供多媒体空间图片下载的相关方法
 */
@protocol MediaLoadProtocol <NSObject>

/**
 *  获取通过变形转换之后的url Deprecated
 *
 *  @param options 图片处理选项
 *
 *  @return 变形转换之后的url
 */
- (NSString *)getTransferedURL:(TFEImageTransferOptions *)options error:(NSError **)error __deprecated;


/**
 *  获取通过变形转换之后的url
 *
 *  @param file 文件信息
 *  @param options 图片处理选项
 *
 *  @return 变形转换之后的url
 */
- (NSString *)getTransferredURL:(TFEFile *)file options:(TFEImageTransferOptions *)options;

/**
 *  同步加载
 *
 *  @param url   图片url
 *  @param error 发生的错误
 *
 *  @return TFELoadSession
 */
- (TFELoadSession *)load:(NSString *)url error:(NSError **)error;

/**
 *  同步加载
 *
 *  @param url 图片url
 *  @param options 选项
 *  @param error 发生的错误
 *
 *  @return TFELoadSession
 */
- (TFELoadSession *)load:(NSString *)url options:(TFELoadOptions *)options error:(NSError **)error;


/**
 *  异步加载
 *
 *  @param url      url
 *  @param notifications 异步加载完的回调
 *
 *  @return UIImage
 */
- (NSString *)asynLoad:(NSString *)url notifications:(TFELoadNotification *)notifications;


/**
 *  异步加载
 *
 *  @param url      url
 *  @param notifications 异步加载完的回调
 *  @param options 选项
 *
 *  @return UIImage
 */
- (NSString *)asynLoad:(NSString *)url notifications:(TFELoadNotification *)notifications options:(TFELoadOptions *)options;

@end
