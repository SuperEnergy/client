//
//  MiningAnimationView.m
//  Ees
//
//  Created by KCMac on 2018/3/27.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import "MiningAnimationView.h"

@interface MiningAnimationView ()

@property(nonatomic,weak) IBOutlet UILabel *speedRateMiningLabel;
@property(nonatomic,weak) IBOutlet UILabel *miningLabel;
@property(nonatomic,weak) IBOutlet UILabel *totalLabel;
@property(nonatomic,weak) IBOutlet UIImageView *imageView;
@property(nonatomic,strong) FLAnimatedImageView *gifImageView;

@end





@implementation MiningAnimationView

- (void)awakeFromNib {
    [super awakeFromNib];
    [self setup];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(userMineSwitch:)
                                                 name:kUserMineDidSwitch object:nil];
}

- (void)userMineSwitch:(NSNotification *)notification
{
    
}


#pragma mark-
#pragma mark----private----
- (void)setup {
    self.backgroundColor = [UIColor clearColor];
    UserMineManager *mingeMg = [UserMineManager sharedInstance];
    long max = [mingeMg.currentUserMine.maxQtyLimit longValue];
    self.totalLabel.text = [NSString stringWithFormat:@"最大容量:%ld\n(mAh)",max];
    self.imageView.hidden = YES;
    
    self.gifImageView = [[FLAnimatedImageView alloc] init];
    self.gifImageView.contentMode = UIViewContentModeScaleAspectFill;
    self.gifImageView.clipsToBounds = YES;
    [self addSubview:self.gifImageView];
    [self.gifImageView makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.leading);
        make.trailing.equalTo(self.trailing);
        make.top.equalTo(self.top);
        make.bottom.equalTo(self.bottom);
    }];
}


#pragma mark-
#pragma mark——public—

- (void)updateAnimationBatteryWarningState:(int)state
{
    int usermingType = [[UserMineManager sharedInstance].currentUserMine.mineType.type intValue];
    if (state == 2 || state == 3) {
        if (usermingType != 1) {
            self.imageView.hidden = YES;
        } else {
            self.imageView.hidden = NO;
        }
    } else {
        self.imageView.hidden = YES;
    }
}

- (void)updateAnimationQtyLimit
{
    UserMineManager *mingeMg = [UserMineManager sharedInstance];
    long max = [mingeMg.currentUserMine.maxQtyLimit longValue];
    self.totalLabel.text = [NSString stringWithFormat:@"最大容量:%ld\n(mAh)",max];
}

- (void)updateAnimationBatteryTotal:(int)total
{
    UserMineManager *mingeMg = [UserMineManager sharedInstance];
    double speedRate = [mingeMg.currentUserMine.speedRate doubleValue];
    if (([mingeMg.currentUserMine.mineType.type intValue] != 1) && ([mingeMg.currentUserMine.submitStatus intValue] != 1)) {
        total = total+[mingeMg.currentUserMine.submitTotal integerValue];
    }

    //统一改为白色
    self.miningLabel.textColor = kWhiteColor;
    if (speedRate - 1.0 > 0) {
        self.speedRateMiningLabel.hidden = NO;
        self.speedRateMiningLabel.text = [NSString stringWithFormat:@"%d(mAh)x%@",total,[DecimalNumberTool speedRateFormatFormDouble:speedRate]];
        total = total*speedRate;
        self.miningLabel.text = [NSString stringWithFormat:@"%d",total];
    } else {
        self.speedRateMiningLabel.hidden = YES;
        self.miningLabel.text = [NSString stringWithFormat:@"%d",total];
    }
    
}

- (void)startAnimation
{
    dispatch_async(dispatch_get_main_queue(), ^{
        if (!self.gifImageView.animatedImage) {
            NSURL *url = [[NSBundle mainBundle] URLForResource:@"mininging_animation" withExtension:@"gif"];
            NSData *data = [NSData dataWithContentsOfURL:url];
            FLAnimatedImage *animatedImage = [FLAnimatedImage animatedImageWithGIFData:data];
            self.gifImageView.animatedImage = animatedImage;
        }
        [self.gifImageView startAnimating];
    });
}

- (void)stopAnimation
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.gifImageView stopAnimating];
    });
}


@end
