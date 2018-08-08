//
//  InitMediaCallback.h
//  ALBBMediaService
//
//  Created by XuPeng on 16/9/8.
//  Copyright © 2016年 Alipay. All rights reserved.
//

#ifndef InitMediaCallback_h
#define InitMediaCallback_h

/**
 *  初始化成功回调
 */
typedef void (^InitMediaSuccess)();

/**
 *  初始化失败回调
 *
 *  @param error 错误信息
 */
typedef void (^InitMediaFailure)(NSError *error);


#endif /* InitMediaCallback_h */
