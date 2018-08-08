//
//  NetTool.m
//  Ees
//
//  Created by xiaodong on 2018/1/10.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import "NetTool.h"


@import CoreTelephony;
@implementation NetTool

+ (int)checkNetWorkPermission
{
    CTCellularData *cellularData = [[CTCellularData alloc]init];
    CTCellularDataRestrictedState state = cellularData.restrictedState;
    return state;
}

@end
