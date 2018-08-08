//
//  FileTool.h
//  Ees
//
//  Created by xiaodong on 2018/1/8.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface FileTool : NSObject

+ (BOOL)writeToPlistFile:(NSString*)filename dic:(NSDictionary *)dic;
+ (NSDictionary*)readFromPlistFile:(NSString*)filename;
+ (BOOL)deleteFile:(NSString*)filename;

@end
