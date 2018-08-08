//
// Created by huamulou on 15-1-20.
// Copyright (c) 2015 alibaba. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TFEUploadPolicy.h"

@interface TFEUploadPolicy ()


+ (instancetype)defaultPolicy;

+ (instancetype)policyWithNamespace:(NSString *)space remoteCall:(TFEUploadRemoteCall *)remoteCall mimeLimit:(NSString *)mimeLimit;

- (NSDictionary *)toDic;



@end