//
//  ALBBMediaService.h
//  ALBBMediaService
//
//  Created by XuPeng on 16/9/8.
//  Copyright © 2016年 Alipay. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "InitMediaCallback.h"
#import "MediaLoadProtocol.h"
#import "MediaUpLoadProtocol.h"

@interface ALBBMediaService : NSObject<MediaLoadProtocol, MediaUpLoadProtocol>

/**
 * 配置是否使用spdy来加载图片
 */
@property(nonatomic) BOOL useSpdy;

/**
 * ALBBMediaService初始化，同步执行
 */
+ (void)syncInit;

/**
 * ALBBMediaService初始化，异步执行
 */
+ (void)asyncInit;

/**
 *  ALBBMediaService初始化，异步执行，结果回调
 *
 *  @param success 初始化成功回调
 *  @param failure 初始化失败回调
 */
+ (void)asyncInit:(InitMediaSuccess)success failure:(InitMediaFailure)failure;

/**
 *  返回单例
 */
+ (instancetype)sharedInstance;

/**
 *  设置是否开启Debug，显示Debug日志
 */
+ (void)setDebug:(BOOL)debug;

/**
 * 设置全局的回调通知
 */
- (void)setGlobalNotification:(TFEUploadNotification *) notify;
@end