//
//  UpdateDeviceCell.m
//  Ees
//
//  Created by xiaodong on 2018/4/13.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import "UpdateDeviceCell.h"

@interface UpdateDeviceCell ()<DeviceModelDelegate>

@property(nonatomic,weak) IBOutlet UIView *cellBgView;
@property(nonatomic,weak) IBOutlet UILabel *titleLabel;
@property(nonatomic,weak) IBOutlet UIButton *refreshBtn;

@end



@implementation UpdateDeviceCell

- (void)awakeFromNib {
    [super awakeFromNib];
    
    self.titleLabel.textColor = kPromotionColor;
    self.titleLabel.text = @"点击\"刷新\"更新设备数据";
    [self.refreshBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.refreshBtn setBackgroundColor:kLoginBtnBGColor];
    [self.refreshBtn setTitle:@"刷新" forState:UIControlStateNormal];
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (IBAction)refresh:(id)sender {
    UserMineModel *currMine = [UserMineManager sharedInstance].currentUserMine;
    NSString *mac = currMine.mac;
    DeviceModel *devModel = [[DeviceManager sharedInstance] getDeviceWithMac:mac];
    devModel.delegate = self;
    [devModel connectPeripheral];
}

- (void)connectSuccess:(CBPeripheral *)periphral row:(NSInteger)row {
    UserMineModel *currMine = [UserMineManager sharedInstance].currentUserMine;
    NSString *mac = currMine.mac;
    DeviceModel *devModel = [[DeviceManager sharedInstance] getDeviceWithMac:mac];
    devModel.delegate = self;
    [devModel sendGetBatteryCommond];
}


- (void)batteryRsp:(unsigned int)total peripheral:(CBPeripheral *)peripheral {
    UserMineModel *model = [UserMineManager sharedInstance].currentUserMine;
    NSString *mac = model.mac;
    DeviceModel *devModel = [[DeviceManager sharedInstance] getDeviceWithMac:mac];
    if (devModel.peripheral == peripheral) {
        model.mineTotal = [NSNumber numberWithInt:total];
        
        [[NSNotificationCenter defaultCenter] postNotificationName:kRefreshUserMineData object:nil];
        [LoadingTool tipView:self.supperVC.view title:@"数据更新成功" image:nil];
    }
}
@end
