//
//  HomePageMainVC.m
//  Ees
//
//  Created by xiaodong on 2017/12/10.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import "HomePageMainVC.h"



@interface HomePageMainVC ()<RegisterMachVCDelegate,MiningMainVCDelegate,ActivityPopDelegate>

@property(nonatomic,weak) IBOutlet  UIView *registerMachView;
@property(nonatomic,weak) IBOutlet  UIView *miningMainView;

@property(nonatomic,strong) UIView *statusBar;

@property(nonatomic,strong) RegisterMachVC *registerMachVC;
@property(nonatomic,strong) MiningMainVC *miningMainVC;

@property(nonatomic,strong) EXTabBarItem *tabBarItem;

@end

@implementation HomePageMainVC

#pragma mark-
#pragma mark——overide—

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [[UserInfoManager sharedInstance] refreshUserInfo:^(BOOL success) {
        [self updateHomePageMainUI];
    }];
    if ([ConfigManager sharedInstance].isChangeSkin) {
        UIImage *selectImage = [UIImage imageNamed:@"tab_main_press_skin.png"];
        self.tabBarItem.selectedImage = [selectImage imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];
        [self.tabBarItem showBadge];
    }
    
    UIColor *color = kTabBarItemTextColor;
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
    [[UITabBarItem appearance] setTitleTextAttributes:[NSDictionary dictionaryWithObjectsAndKeys:color,UITextAttributeTextColor, nil] forState:UIControlStateSelected];
#pragma clang diagnostic pop
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    if ([ConfigManager sharedInstance].isChangeSkin) {
        [self.tabBarItem hidenBadge];
    }
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.title = @"首页";
    
    self.tabBarItem = (EXTabBarItem *)self.navigationController.tabBarItem;
    for (UIViewController *vc in self.childViewControllers) {
        if ([vc isKindOfClass:[RegisterMachVC class]]) {
            self.registerMachVC = (RegisterMachVC *)vc;
            self.registerMachVC.delegate = self;
        } else if ([vc isKindOfClass:[MiningMainVC class]]) {
            self.miningMainVC = (MiningMainVC *)vc;
            self.miningMainVC.delegate = self;
        } else {
            
        }
    }
    
    [self.miningMainVC miningMainUpdateMiningData];
    [self popActivity];
    
    self.statusBar = [[UIView alloc] initWithFrame:CGRectZero];
    self.statusBar.backgroundColor = kCycleBackgroundColor;
    if ([ConfigManager sharedInstance].isChangeSkin) {
        self.statusBar.backgroundColor = [UIColor redColor];
    }
    [self.view addSubview:self.statusBar];
    [self.statusBar makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.view.leading);
        make.trailing.equalTo(self.view.trailing);
        make.top.equalTo(self.view.top);
        make.height.equalTo(@(20));
    }];
    
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(appBecomeActive:)
                                                 name:UIApplicationDidBecomeActiveNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(appEnterBackground:)
                                                 name:UIApplicationDidEnterBackgroundNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(significantTimeChange:)
                                                 name:UIApplicationSignificantTimeChangeNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(batteryLevelDidChange:)
                                                 name:UIDeviceBatteryLevelDidChangeNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(batteryStateDidChange:)
                                                 name:UIDeviceBatteryStateDidChangeNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(miningStatusDidChange:)
                                                 name:kMiningStatusDidChange object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(userStatusDidChange:)
                                                 name:kUserStateDidChange object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(maxQtyLimitDidChange:)
                                                 name:kMaxQtyLimitDidChange object:nil];
    
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


#pragma mark-
#pragma mark——private—

- (void)updateHomePageMainUI
{
    self.miningMainView.hidden = NO;
    self.registerMachView.hidden = YES;
    
#if 0
    UserInfoManager *item = [UserInfoManager sharedInstance];
    if ([item.userItem.rna_Status intValue] == 1) {
        self.miningMainView.hidden = NO;
        self.registerMachView.hidden = YES;
    } else {
        self.miningMainView.hidden = YES;
        self.registerMachView.hidden = NO;
        [self.registerMachVC updateRegisterMachUserState:item];
    }
#endif
    
}

- (void)popActivity
{
    UserInfoManager *item = [UserInfoManager sharedInstance];
    if (!item.userItem) {
        return;
    }
    NSDictionary *dic = @{@"secret":kSecret,@"pid":item.userItem.pid,@"token":item.userItem.token};
    [HttpRequestEngin activityListWithParam:dic successBlock:^(id  _Nullable responseObject) {
        NSError *error;
        RespondModel *apiModel = [[RespondModel alloc] initObjectWithDictionary:responseObject objectClass:[NoticeModel class] objectError:&error];
        if (apiModel.success && apiModel.object) {
            NoticeModel *model = apiModel.object;
            
            dispatch_async(dispatch_get_main_queue(), ^{
                NSURL *url = [NSURL URLWithString:model.poster];
                UIStoryboard *board = [UIStoryboard storyboardWithName:@"Common" bundle:nil];
                ActivityPopVC *vc = [board instantiateViewControllerWithIdentifier:@"ActivityPopVC"];
                vc.delegate = self;
                vc.model = model;
                [vc.coverView sd_setImageWithURL:url
                                  placeholderImage:nil
                                         completed:^(UIImage * _Nullable image, NSError * _Nullable error, SDImageCacheType cacheType, NSURL * _Nullable imageURL) {
                                             if (image) {
                                                 dispatch_async(dispatch_get_main_queue(), ^{
                                                     UIImage *newImage = [image getSubImageWithImageView:vc.coverView];
                                                     vc.coverView.image = newImage;
                                                 });
                                             }
                                         }];
                vc.modalPresentationStyle = UIModalPresentationOverFullScreen;// 窗口
                [self.navigationController presentViewController:vc animated:YES completion:^{
                    vc.view.superview.backgroundColor = [UIColor clearColor];// 背景色透明
                }];
            });
        } else {
            ErrorModel *model = apiModel.error;
            ErrorCodeEvent *event = [[ErrorCodeEvent alloc] initWithError:model];
            [event errorCodeDone:self needLoading:NO];
        }
        
        
    } failBlock:^(NSError * _Nonnull error) {
        
    } progress:^(NSProgress * _Nonnull loadprogress) {
        
    }];
}


#pragma mark-
#pragma mark——NSNotification—

- (void)appBecomeActive:(NSNotification *)notification
{
    NSLog(@"app进入前端");
    [self.miningMainVC miningMainUpdateBatteryLevel];
    [self.miningMainVC miningMainUpdateMiningData];
    [self.miningMainVC miningMainAppBecomeActive];
}


- (void)appEnterBackground:(NSNotification *)notification
{
    [self.miningMainVC miningMainUpdateBatteryLevel];
    NSLog(@"app进入后端");
}

- (void)significantTimeChange:(NSNotification *)notification
{
    //重要的时间变化（新的一天开始或时区变化）
    [self.miningMainVC miningMainUpdateMiningData];
}

- (void)batteryLevelDidChange:(NSNotification *)notification
{
    [self.miningMainVC miningMainUpdateBatteryLevel];
    [self.miningMainVC miningMainUpdateMiningData];
}

- (void)batteryStateDidChange:(NSNotification *)notification
{
    NSArray *stateArray = [NSArray arrayWithObjects:@"未开启监视电池状态",@"电池未充电状态",@"电池充电状态",@"电池充电完成",nil];
    NSLog(@"电池状态：%@", [stateArray objectAtIndex:[[UIDevice currentDevice] batteryState]]);
    //[LoadingTool tipView:self.view title:[stateArray objectAtIndex:[[UIDevice currentDevice] batteryState]] image:nil];
    [self.miningMainVC miningMainBatteryChangeState:[[UIDevice currentDevice] batteryState]];
    
    [self.miningMainVC miningMainUpdateBatteryLevel];
    [self.miningMainVC miningMainUpdateMiningData];
}

- (void)miningStatusDidChange:(NSNotification *)notification
{
    [self.miningMainVC miningMainUpdateMiningState];
    
    [self.miningMainVC miningMainUpdateBatteryLevel];
    [self.miningMainVC miningMainUpdateMiningData];
}

//实名认证完后 更新用户信息
- (void)userStatusDidChange:(NSNotification *)notification
{
    UserInfoManager *item = [UserInfoManager sharedInstance];
    [self.registerMachVC updateRegisterMachUserState:item];
}

- (void)maxQtyLimitDidChange:(NSNotification *)notification {
    [self.miningMainVC miningMainUpdateUserMineMaxQtyLimit];
}

#pragma mark-
#pragma mark------Delegate--------
- (void)realNameDidClick
{
    [AlertTool alertShowTwoAction:self title:kStringValue_tip context:@"实名认证后即可免费领取能量收集器\n立即开启认证吧" okTitle:@"确定" cancelTitle:@"取消" okHandler:^(UIAlertAction *action) {
        UIStoryboard *board = [UIStoryboard storyboardWithName:@"Main" bundle:nil];
        RealnameVC *ctrl = [board instantiateViewControllerWithIdentifier:@"RealnameVC"];
        [self.navigationController pushViewController:ctrl animated:YES];
    } cancelHandler:nil];
}

//scrollview scroll
- (void)statusBarDidChangeColor:(UIColor *)color
{
    self.statusBar.backgroundColor = color;
}


- (void)activityPopDidClick:(NoticeModel *)model {
    
    UIStoryboard *board = self.storyboard;
    NoticeDetailVC *vc = [board instantiateViewControllerWithIdentifier:@"NoticeDetailVC"];
    if (model.link && ![model.link isEqualToString:@""]) {
        vc.link = model.link;
        vc.isLinkAction = YES;
    } else {
        vc.isLinkAction = NO;
    }
    vc.title = model.title;
    vc.noticeId = model.id;
    [self.navigationController pushViewController:vc animated:YES];
}


@end
