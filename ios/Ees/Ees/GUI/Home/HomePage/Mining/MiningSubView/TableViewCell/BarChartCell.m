//
//  BarChartCell.m
//  Ees
//
//  Created by xiaodong on 2018/3/28.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import "BarChartCell.h"
#import "ZFChart.h"

#define kPaddingWidth   0
#define kGroupWidth   25

@interface BarChartCell ()<ZFGenericChartDataSource, ZFBarChartDelegate>

@property(nonatomic,weak) IBOutlet UIView *cellBgView;
@property (nonatomic, strong) ZFBarChart * barChart;
@property (nonatomic,weak) IBOutlet UILabel *lastestDayLabel;

@property (nonatomic, assign) NSTimeInterval timeInterval;
@property (nonatomic, strong) NSMutableArray *valueArray;
@property (nonatomic, strong) NSMutableArray *dateArray;

@end


@implementation BarChartCell

- (void)awakeFromNib {
    [super awakeFromNib];
    
    _valueArray = [NSMutableArray new];
    _dateArray = [NSMutableArray new];
    
    self.lastestDayLabel.text = kStringValue_lastestDayMinings;
    [self createEnergy];
    [self queryLastestDayMinings];
    
    UITapGestureRecognizer *gesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapGesture)];
    [self.cellBgView addGestureRecognizer:gesture];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(userMineSwitch:)
                                                 name:kUserMineDidSwitch object:nil];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)userMineSwitch:(NSNotification *)notification
{
    [self queryLastestDayMinings];
}


#pragma mark-
#pragma mark------public---
- (void)updateNetWorkData {
    NSUserDefaults *userDef = [NSUserDefaults standardUserDefaults];
    NSString *lastDate = [userDef objectForKey:kMiningReportDate];
    NSString *currentDate = [TimeTool getCurrentDateString];
    if (![currentDate isEqualToString:lastDate]) {
        [self queryLastestDayMinings];
    }
}


#pragma mark-
#pragma mark----private---

- (NSMutableArray *)createBarCharData {
    
    NSMutableArray *array = [NSMutableArray new];
    
    for (int i = 1; i <= 7; i++) {
        NSString *strDay = [TimeTool dateBeforeWithDay:8-i];
        NSMutableDictionary *dic = [NSMutableDictionary new];
        [dic setObject:@"0" forKey:@"strvalue"];
        [dic setObject:strDay forKey:@"strdate"];
        [array addObject:dic];
    }
    return array;
}


- (void)queryLastestDayMinings
{
    NSDate *date = [NSDate date];
    NSTimeInterval currentTime= [date timeIntervalSince1970];
    if (currentTime - self.timeInterval < kQueryInterval) {
        return;
    }
    self.timeInterval = currentTime;
    
    UserInfoManager *item = [UserInfoManager sharedInstance];
    UserMineManager *mineMg = [UserMineManager sharedInstance];
    if (!item.userItem || !mineMg.currentUserMine) {
        return;
    }
    
    [self.valueArray removeAllObjects];
    [self.dateArray removeAllObjects];
    
    NSUserDefaults *userDef = [NSUserDefaults standardUserDefaults];
    NSInteger version = [userDef integerForKey:kLastestDayMiningsVersion];
    NSString *strVersion = [NSString stringWithFormat:@"%ld",(long)version];
    
    NSDictionary *dic = @{@"secret":kSecret,@"pid":item.userItem.pid,@"token":item.userItem.token,@"usermine_id":mineMg.currentUserMine.id,@"day":@(7),@"version":strVersion};
    [HttpRequestEngin miningReportWithParam:dic successBlock:^(id  _Nullable responseObject) {
        NSError *error;
        RespondModel *apiModel = [[RespondModel alloc] initObjectsWithDictionary:responseObject objectClass:[MiningModel class] objectError:&error];
        
        NSMutableArray *array = [self createBarCharData];
        if (apiModel.success && apiModel.objects) {
            for (int i = 0; i < apiModel.objects.count; i++) {
                MiningModel *model = [apiModel.objects objectAtIndex:i];
                double startTime = [model.startTime doubleValue]/1000;
                NSString *strDate = [TimeTool getDateStringWithTimestamp:startTime andFormatter:@"dd"];
                NSString *strValue = [NSString stringWithFormat:@"%ld",[model.actualQty longValue]];
                for (int i = 0; i <array.count; i++) {
                    NSMutableDictionary *dic = [array objectAtIndex:i];
                    NSString *strdate = [dic objectForKey:@"strdate"];
                    if ([strDate isEqualToString:strdate]) {
                        [dic setObject:strValue forKey:@"strvalue"];
                        break;
                    }
                }
            }
            
            for (int i = 0; i <array.count; i++) {
                NSMutableDictionary *dic = [array objectAtIndex:i];
                NSString *strdate = [dic objectForKey:@"strdate"];
                NSString *strvalue = [dic objectForKey:@"strvalue"];
                [self.valueArray addObject:strvalue];
                [self.dateArray addObject:[NSString stringWithFormat:@"%@日",strdate]];
            }
            dispatch_async(dispatch_get_main_queue(), ^{
                [self.barChart strokePath];
            });
            
            BOOL writeSuccess = [FileTool writeToPlistFile:kMiningFileName dic:responseObject];
            NSLog(@"write funds respond %d",writeSuccess);
            [userDef setInteger:apiModel.version forKey:kLastestDayMiningsVersion];
            NSString *currentDate = [TimeTool getCurrentDateString];
            [userDef setObject:currentDate forKey:kMiningReportDate];
            [userDef synchronize];
            
        } else {
            ErrorModel *model = apiModel.error;
            if ([model.code isEqualToString:@"CODE_SYNC_EQUAL"]) {
                NSDictionary *dic = [FileTool readFromPlistFile:kMiningFileName];
                RespondModel *apiModel = [[RespondModel alloc] initObjectsWithDictionary:dic objectClass:[MiningModel class] objectError:&error];
                if (apiModel.success && apiModel.objects) {
                    for (int i = 0; i < apiModel.objects.count; i++) {
                        MiningModel *model = [apiModel.objects objectAtIndex:i];
                        double startTime = [model.startTime doubleValue]/1000;
                        NSString *strDate = [TimeTool getDateStringWithTimestamp:startTime andFormatter:@"dd"];
                        NSString *strValue = [NSString stringWithFormat:@"%ld",[model.actualQty longValue]];
                        for (int i = 0; i <array.count; i++) {
                            NSMutableDictionary *dic = [array objectAtIndex:i];
                            NSString *strdate = [dic objectForKey:@"strdate"];
                            if ([strDate isEqualToString:strdate]) {
                                [dic setObject:strValue forKey:@"strvalue"];
                                break;
                            }
                        }
                    }
                    
                    for (int i = 0; i <array.count; i++) {
                        NSMutableDictionary *dic = [array objectAtIndex:i];
                        NSString *strdate = [dic objectForKey:@"strdate"];
                        NSString *strvalue = [dic objectForKey:@"strvalue"];
                        [self.valueArray addObject:strvalue];
                        [self.dateArray addObject:[NSString stringWithFormat:@"%@日",strdate]];
                    }
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [self.barChart strokePath];
                    });
                }
            } else {
                ErrorCodeEvent *event = [[ErrorCodeEvent alloc] initWithError:model];
                [event errorCodeDone:self needLoading:NO];
            }
            
        }
        
    } failBlock:^(NSError * _Nonnull error) {
        
    } progress:^(NSProgress * _Nonnull loadprogress) {
        
    }];
}


- (void)createEnergy
{
    CGRect screenRect = [UIScreen mainScreen].bounds;
    float hight = 40*screenRect.size.width/75;
    CGRect barRect = CGRectMake(0, 0, screenRect.size.width-10, hight);
    self.barChart = [[ZFBarChart alloc] initWithFrame:barRect];
    self.barChart.dataSource = self;
    self.barChart.delegate = self;
    self.barChart.topicLabel.hidden = YES;
    self.barChart.isShadow = NO;
    self.barChart.xAxisColor = [UIColor groupTableViewBackgroundColor];
    self.barChart.yAxisColor = [UIColor groupTableViewBackgroundColor];
    self.barChart.separateColor = [UIColor groupTableViewBackgroundColor];
    self.barChart.axisLineValueColor = [UIColor grayColor];
    self.barChart.axisLineNameColor = [UIColor grayColor];
    //self.barChart.unit = @"人";
    //    self.barChart.isAnimated = NO;
    //self.barChart.isResetAxisLineMinValue = YES;
    //self.barChart.isResetAxisLineMaxValue = YES;
    self.barChart.isShowAxisLineValue = NO;
    self.barChart.valueLabelPattern = kPopoverLabelPatternBlank;
    self.barChart.isShowXLineSeparate = NO;
    self.barChart.isShowYLineSeparate = YES;
    //    self.barChart.topicLabel.textColor = ZFWhite;
    self.barChart.unitColor = ZFWhite;
    //    self.barChart.xAxisColor = ZFWhite;
    //    self.barChart.yAxisColor = ZFWhite;
    //    self.barChart.xAxisColor = ZFClear;
    //    self.barChart.yAxisColor = ZFClear;
    //    self.barChart.axisLineNameColor = ZFWhite;
    //    self.barChart.axisLineValueColor = ZFWhite;
    //    self.barChart.backgroundColor = ZFPurple;
    self.barChart.isShowAxisArrows = NO;
    self.barChart.separateLineStyle = kLineStyleRealLine;
    self.barChart.isMultipleColorInSingleBarChart = YES;
    //    self.barChart.separateLineDashPhase = 0.f;
    //    self.barChart.separateLineDashPattern = @[@(5), @(5)];
    
    [self.cellBgView insertSubview:self.barChart atIndex:0];
    [self.barChart makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.cellBgView.leading);
        make.trailing.equalTo(self.cellBgView.trailing);
        make.top.equalTo(self.cellBgView.top);
        make.bottom.equalTo(self.cellBgView.bottom);
    }];
    [self.barChart strokePath];
}

#pragma mark-
#pragma mark------UI Event------

- (void)tapGesture
{
    if (self.valueArray.count == 0) {
        [self queryLastestDayMinings];
    }
}


#pragma mark - ZFGenericChartDataSource

- (NSArray *)valueArrayInGenericChart:(ZFGenericChart *)chart{
    return self.valueArray;
}

- (NSArray *)nameArrayInGenericChart:(ZFGenericChart *)chart{
    return self.dateArray;
}


#define kBarColor  kColorWithHex(0x33B5E5)
#define kBarColor2  kColorWithHex(0xAA66CC)
#define kBarColor3  kColorWithHex(0x99CC00)
#define kBarColor4  kColorWithHex(0xFFBB33)
#define kBarColor5  kColorWithHex(0xFF4444)
#define kBarColor6  kColorWithHex(0x33B5E5)
#define kBarColor7  kColorWithHex(0xAA66CC)

- (NSArray *)colorArrayInGenericChart:(ZFGenericChart *)chart{
    //return @[ZFMagenta];
    
    return @[kBarColor2, kBarColor3, kBarColor4, kBarColor5, kBarColor6, kBarColor7,kBarColor];
}

- (CGFloat)axisLineMaxValueInGenericChart:(ZFGenericChart *)chart{
    return 32;
}

- (CGFloat)axisLineMinValueInGenericChart:(ZFGenericChart *)chart{
    return 1;
}

//y轴(普通图表) 或 x轴(横向图表) 数值显示的段数(若不设置，默认5段)
- (NSUInteger)axisLineSectionCountInGenericChart:(ZFGenericChart *)chart{
    return 10;
}

//- (NSInteger)axisLineStartToDisplayValueAtIndex:(ZFGenericChart *)chart{
//    return -7;
//}

- (void)genericChartDidScroll:(UIScrollView *)scrollView{
    NSLog(@"当前偏移量 ------ %f", scrollView.contentOffset.x);
}

#pragma mark - ZFBarChartDelegate

//bar宽度(若不设置，默认为25.f)
- (CGFloat)barWidthInBarChart:(ZFBarChart *)barChart{
    return kGroupWidth;
}

- (CGFloat)paddingForGroupsInBarChart:(ZFBarChart *)barChart{
    float width = (SCREEN_WIDTH-90-6*kGroupWidth)/6;
    return width;
}

//- (id)valueTextColorArrayInBarChart:(ZFGenericChart *)barChart{
//    return ZFBlue;
//}


//- (NSArray *)gradientColorArrayInBarChart:(ZFBarChart *)barChart{
//    ZFGradientAttribute * gradientAttribute = [[ZFGradientAttribute alloc] init];
//    //gradientAttribute.colors = @[(id)kCycleBackgroundColor.CGColor, (id)kCycleBackgroundColor.CGColor];
//    //gradientAttribute.colors = @[(id)kBarColor2.CGColor];
//    gradientAttribute.locations = @[@(0.5), @(0.99)];
//
//    return [NSArray arrayWithObjects:gradientAttribute, nil];
//}

//- (void)barChart:(ZFBarChart *)barChart didSelectBarAtGroupIndex:(NSInteger)groupIndex barIndex:(NSInteger)barIndex bar:(ZFBar *)bar popoverLabel:(ZFPopoverLabel *)popoverLabel{
//    NSLog(@"第%ld组========第%ld个",(long)groupIndex,(long)barIndex);
//
//    //可在此处进行bar被点击后的自身部分属性设置,可修改的属性查看ZFBar.h
//    bar.barColor = kCycleBackgroundColor;
//    bar.isAnimated = NO;
//    bar.opacity = 1.0;
//    [bar strokePath];
//
//    //可将isShowAxisLineValue设置为NO，然后执行下句代码进行点击才显示数值
//    //    popoverLabel.hidden = NO;
//}

- (void)barChart:(ZFBarChart *)barChart didSelectPopoverLabelAtGroupIndex:(NSInteger)groupIndex labelIndex:(NSInteger)labelIndex popoverLabel:(ZFPopoverLabel *)popoverLabel{
    NSLog(@"第%ld组========第%ld个",(long)groupIndex,(long)labelIndex);
    
    //可在此处进行popoverLabel被点击后的自身部分属性设置
    //    popoverLabel.textColor = ZFSkyBlue;
    //    [popoverLabel strokePath];
}


@end
