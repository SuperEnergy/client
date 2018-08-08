//
//  LineChartCell.m
//  Ees
//
//  Created by xiaodong on 2018/3/28.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import "LineChartCell.h"

#define kPaddingWidth   0
#define kGroupWidth   25


@interface LineChartCell ()<ZFGenericChartDataSource, ZFLineChartDelegate>

@property(nonatomic,weak) IBOutlet UIView *cellBgView;
@property (nonatomic, strong) ZFLineChart * lineChart;
@property (nonatomic,weak) IBOutlet UILabel *lastestDayLabel;

@property (nonatomic, assign) NSTimeInterval timeInterval;
@property (nonatomic, strong) NSMutableArray *valueArray;
@property (nonatomic, strong) NSMutableArray *dateArray;

@end



@implementation LineChartCell

- (void)awakeFromNib {
    [super awakeFromNib];
    
    _valueArray = [NSMutableArray new];
    _dateArray = [NSMutableArray new];
    
    self.lastestDayLabel.text = kStringValue_lastestDayFunds;
    [self createRevenue];
    [self queryLastestDayFunds];
    
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
    [self queryLastestDayFunds];
}

#pragma mark-
#pragma mark------public---
- (void)updateNetWorkData {
    NSUserDefaults *userDef = [NSUserDefaults standardUserDefaults];
    NSString *lastDate = [userDef objectForKey:kFundReportDate];
    NSString *currentDate = [TimeTool getCurrentDateString];
    if (![currentDate isEqualToString:lastDate]) {
        [self queryLastestDayFunds];
    }
}

#pragma mark-
#pragma mark------private---

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


- (void)queryLastestDayFunds
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
    NSInteger version = [userDef integerForKey:kLastestDayFundsVersion];
    NSString *strVersion = [NSString stringWithFormat:@"%ld",version];
    int type = [mineMg.currentUserMine.mineType.type intValue];
    NSString *typeStr = [NSString stringWithFormat:@"%d",type];
    NSDictionary *dic = @{@"secret":kSecret,@"pid":item.userItem.pid,@"token":item.userItem.token,@"usermine_id":mineMg.currentUserMine.id,@"type":typeStr,@"day":@"7",@"version":strVersion};
    [HttpRequestEngin fundReportWithParam:dic successBlock:^(id  _Nullable responseObject) {
        NSError *error;
        RespondModel *apiModel = [[RespondModel alloc] initObjectsWithDictionary:responseObject objectClass:[FundModel class] objectError:&error];
        
        NSMutableArray *array = [self createBarCharData];
        if (apiModel.success && apiModel.objects) {
            for (int i = 0; i < apiModel.objects.count; i++) {
                FundModel *model = [apiModel.objects objectAtIndex:i];
                
                double timestamp = ([model.createDate doubleValue]-24*60*60*1000)/1000;
                NSString *strDate = [TimeTool getDateStringWithTimestamp:timestamp andFormatter:@"dd"];
                NSString *strValue = [NSString stringWithFormat:@"%lf",[model.qty doubleValue]];
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
                [self.lineChart strokePath];
            });
            
            BOOL writeSuccess = [FileTool writeToPlistFile:kFundFileName dic:responseObject];
            NSLog(@"write funds respond %d",writeSuccess);
            [userDef setInteger:apiModel.version forKey:kLastestDayFundsVersion];
            
            NSString *currentDate = [TimeTool getCurrentDateString];
            [userDef setObject:currentDate forKey:kFundReportDate];
            [userDef synchronize];
            
        } else {
            ErrorModel *model = apiModel.error;
            if ([model.code isEqualToString:@"CODE_SYNC_EQUAL"]) {
                NSDictionary *dic = [FileTool readFromPlistFile:kFundFileName];
                RespondModel *apiModel = [[RespondModel alloc] initObjectsWithDictionary:dic objectClass:[FundModel class] objectError:&error];
                if (apiModel.success && apiModel.objects) {
                    for (int i = 0; i < apiModel.objects.count; i++) {
                        FundModel *model = [apiModel.objects objectAtIndex:i];
                        double timestamp = ([model.createDate doubleValue]-24*60*60*1000)/1000;
                        NSString *strDate = [TimeTool getDateStringWithTimestamp:timestamp andFormatter:@"dd"];
                        NSString *strValue = [NSString stringWithFormat:@"%lf",[model.qty doubleValue]];
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
                        [self.lineChart strokePath];
                    });
                }
            } else {
                ErrorCodeEvent *event = [[ErrorCodeEvent alloc] initWithError:model];
                [event errorCodeDone:self.supperVC needLoading:NO];
            }
            
        }
        
    } failBlock:^(NSError * _Nonnull error) {
        
    } progress:^(NSProgress * _Nonnull loadprogress) {
        
    }];
}


- (void)createRevenue
{
    CGRect screenRect = [UIScreen mainScreen].bounds;
    float hight = 40*screenRect.size.width/75;
    self.lineChart = [[ZFLineChart alloc] initWithFrame:CGRectMake(0, 0, screenRect.size.width, hight)];
    self.lineChart.dataSource = self;
    self.lineChart.delegate = self;
    self.lineChart.xAxisColor = [UIColor groupTableViewBackgroundColor];
    self.lineChart.yAxisColor = [UIColor groupTableViewBackgroundColor];
    self.lineChart.separateColor = [UIColor groupTableViewBackgroundColor];
    self.lineChart.axisLineValueColor = [UIColor grayColor];
    self.lineChart.axisLineNameColor = [UIColor grayColor];
    self.lineChart.shadowColor = [UIColor clearColor];
    //self.lineChart.topicLabel.text = @"xx小学各年级男女人数";
    //self.lineChart.unit = @"人";
    //self.lineChart.isResetAxisLineMaxValue = YES;
    self.lineChart.isShowAxisLineValue = NO;
    //self.lineChart.topicLabel.textColor = ZFPurple;
    //self.lineChart.isResetAxisLineMinValue = YES;
    self.lineChart.isAnimated = YES;
    self.lineChart.valueLabelPattern = kPopoverLabelPatternBlank;
    self.lineChart.isShowXLineSeparate = NO;
    self.lineChart.isShowYLineSeparate = YES;
    self.lineChart.unitColor = ZFWhite;
    self.lineChart.isShowAxisArrows = NO;
    self.lineChart.linePatternType = kLinePatternTypeSharp;
    self.lineChart.lineStyle = kLineStyleRealLine;
    self.lineChart.lineDashPhase = 0.f;
    self.lineChart.lineDashPattern = @[@(5), @(5)];
    self.lineChart.isShowAxisLineValue = NO;
    self.lineChart.xLineNameLabelToXAxisLinePadding = 0.0;
    self.lineChart.valueType = kValueTypeDecimal;
    //    self.lineChart.valueCenterToCircleCenterPadding = 0;
    
    [self.cellBgView insertSubview:self.lineChart atIndex:0];
    [self.lineChart makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.cellBgView.leading);
        make.trailing.equalTo(self.cellBgView.trailing);
        make.top.equalTo(self.cellBgView.top);
        make.bottom.equalTo(self.cellBgView.bottom);
    }];
    //self.view.backgroundColor = [UIColor redColor];
    [self.lineChart strokePath];
}

#pragma mark-
#pragma mark----UI Event
- (void)tapGesture
{
    if (self.valueArray.count == 0) {
        [self queryLastestDayFunds];
    }
}


#pragma mark - ZFGenericChartDataSource

- (NSArray *)valueArrayInGenericChart:(ZFGenericChart *)chart{
    return self.valueArray;
}

- (NSArray *)nameArrayInGenericChart:(ZFGenericChart *)chart{
    return self.dateArray;
}

- (NSArray *)colorArrayInGenericChart:(ZFGenericChart *)chart{
    return @[kCycleBackgroundColor];
}

- (CGFloat)axisLineMaxValueInGenericChart:(ZFGenericChart *)chart{
    return 10;
}

//- (CGFloat)axisLineMinValueInGenericChart:(ZFGenericChart *)chart{
//    return -200;
//}

- (NSUInteger)axisLineSectionCountInGenericChart:(ZFGenericChart *)chart{
    return 10;
}

//- (NSInteger)axisLineStartToDisplayValueAtIndex:(ZFGenericChart *)chart{
//    return -7;
//}

//- (void)genericChartDidScroll:(UIScrollView *)scrollView{
//    NSLog(@"当前偏移量 ------ %f", scrollView.contentOffset.x);
//}

#pragma mark - ZFLineChartDelegate

- (CGFloat)groupWidthInLineChart:(ZFLineChart *)lineChart{
    return kGroupWidth;
}

- (CGFloat)paddingForGroupsInLineChart:(ZFLineChart *)lineChart{
    float width = (SCREEN_WIDTH-90-6*kGroupWidth)/6;
    return width;
}

- (CGFloat)circleRadiusInLineChart:(ZFLineChart *)lineChart{
    return 2.f;
}

- (CGFloat)lineWidthInLineChart:(ZFLineChart *)lineChart{
    return 1.0;
}

- (NSArray *)valuePositionInLineChart:(ZFLineChart *)lineChart{
    return @[@(kChartValuePositionOnTop)];
}

- (NSArray<ZFGradientAttribute *> *)gradientColorArrayInLineChart:(ZFLineChart *)lineChart{
    ZFGradientAttribute * gradientAttribute = [[ZFGradientAttribute alloc] init];
    gradientAttribute.colors = @[(id)kCycleBackgroundColor.CGColor, (id)kCycleBackgroundColor.CGColor, (id)kCycleBackgroundColor.CGColor];
    gradientAttribute.locations = @[@(0.1), @(0.22)];
    gradientAttribute.startPoint = CGPointMake(0.5, 0);
    gradientAttribute.endPoint = CGPointMake(0.5, 1);
    
    return [NSArray arrayWithObjects:gradientAttribute, nil];
}

- (void)lineChart:(ZFLineChart *)lineChart didSelectCircleAtLineIndex:(NSInteger)lineIndex circleIndex:(NSInteger)circleIndex circle:(ZFCircle *)circle popoverLabel:(ZFPopoverLabel *)popoverLabel{
    NSLog(@"第%ld个", (long)circleIndex);
    
    //可在此处进行circle被点击后的自身部分属性设置,可修改的属性查看ZFCircle.h
    //    circle.circleColor = ZFRed;
    //    circle.isAnimated = YES;
    //    circle.opacity = 0.5;
    //    [circle strokePath];
    
    //可将isShowAxisLineValue设置为NO，然后执行下句代码进行点击才显示数值
    //    popoverLabel.hidden = NO;
}

- (void)lineChart:(ZFLineChart *)lineChart didSelectPopoverLabelAtLineIndex:(NSInteger)lineIndex circleIndex:(NSInteger)circleIndex popoverLabel:(ZFPopoverLabel *)popoverLabel{
    NSLog(@"第%ld个" ,(long)circleIndex);
    
    //可在此处进行popoverLabel被点击后的自身部分属性设置
    //    popoverLabel.textColor = ZFGold;
    //    [popoverLabel strokePath];
}




@end
