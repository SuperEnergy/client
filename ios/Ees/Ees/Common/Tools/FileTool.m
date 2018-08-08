//
//  FileTool.m
//  Ees
//
//  Created by xiaodong on 2018/1/8.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import "FileTool.h"

@implementation FileTool

+ (BOOL)writeToPlistFile:(NSString*)filename dic:(NSDictionary *)dic {
    NSData * data = [NSKeyedArchiver archivedDataWithRootObject:dic];
    NSArray * paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString * documentsDirectory = [paths objectAtIndex:0];
    NSString * path = [documentsDirectory stringByAppendingPathComponent:filename];
    BOOL didWriteSuccessfull = [data writeToFile:path atomically:YES];
    return didWriteSuccessfull;
}

+ (NSDictionary*)readFromPlistFile:(NSString*)filename {
    NSArray * paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString * documentsDirectory = [paths objectAtIndex:0];
    NSString * path = [documentsDirectory stringByAppendingPathComponent:filename];
    NSData * data = [NSData dataWithContentsOfFile:path];
    return  [NSKeyedUnarchiver unarchiveObjectWithData:data];
}

+ (BOOL)deleteFile:(NSString*)filename {
    NSFileManager* fileManager=[NSFileManager defaultManager];
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,NSUserDomainMask, YES);
    NSString * documentsDirectory = [paths objectAtIndex:0];
    NSString * path = [documentsDirectory stringByAppendingPathComponent:filename];
    BOOL isExist = [fileManager fileExistsAtPath:path];
    if (!isExist) {
        return NO;
    } else {
        BOOL isDelete = [fileManager removeItemAtPath:path error:nil];
        if (isDelete) {
            return YES;
        } else {
            return NO;
        }
    }
    return NO;
}

@end
