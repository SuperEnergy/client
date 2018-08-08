//
//  NoticeMainVC.m
//  Ees
//
//  Created by xiaodong on 2017/12/10.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import "NoticeMainVC.h"


@interface NoticeMainVC ()

@property(nonatomic,strong) MessageVC *messageVc;//消息
@property(nonatomic,strong) NoticeVC *noticeVc;//公告

@property(nonatomic,strong) EXTabBarItem *tabBarItem;

@end

@implementation NoticeMainVC

#pragma mark-
#pragma mark------overide----

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [[UIApplication sharedApplication] setApplicationIconBadgeNumber:0];
    
    if ([ConfigManager sharedInstance].isChangeSkin) {
        UIImage *selectImage = [UIImage imageNamed:@"tab_msg_press_skin.png"];
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
    self.title = @"消息";
    
    self.tabBarItem = (EXTabBarItem *)self.navigationController.tabBarItem;
    
    self.tabedSlideView.baseViewController = self;
    self.tabedSlideView.tabItemNormalColor = [UIColor lightGrayColor];
    self.tabedSlideView.tabItemSelectedColor = kLoginBtnBGColor;
    self.tabedSlideView.tabbarTrackColor = kLoginBtnBGColor;
    //self.tabedSlideView.tabbarBackgroundImage = [UIImage imageNamed:@"tabbarBk"];
    self.tabedSlideView.tabbarBottomSpacing = 0;
    
    DLTabedbarItem *item1 = [DLTabedbarItem itemWithTitle:@"消息" image:nil selectedImage:nil];
    DLTabedbarItem *item2 = [DLTabedbarItem itemWithTitle:@"公告" image:nil selectedImage:nil];

    self.tabedSlideView.tabbarItems = @[item1, item2];
    [self.tabedSlideView buildTabbar];
    self.tabedSlideView.selectedIndex = 0;
    
    // Do any additional setup after loading the view.
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


#pragma mark-
#pragma mark-------public

- (NSMutableArray *)getLastMessage {
    NSMutableArray *resArray = [NSMutableArray new];
    if (self.messageVc.sourceArray.count > 0) {
        [resArray addObject:[self.messageVc.sourceArray objectAtIndex:0]];
    }
    if (self.noticeVc.sourceArray.count > 0) {
        [resArray addObject:[self.noticeVc.sourceArray objectAtIndex:0]];
    }
    
    [resArray sortUsingComparator:^NSComparisonResult(id obj1, id obj2) {
        
        NoticeModel *notice1 = obj1;
        NoticeModel *notice2 = obj2;
        
        double a = [notice1.createTime doubleValue];
        double b = [notice2.createTime doubleValue];
        
        if (a < b) {
            return NSOrderedDescending;
        } else if (a > b) {
            return NSOrderedAscending;
        } else {
            return NSOrderedSame;
        }
    }];
    
    return resArray;
}

- (void)noticeIndexChange:(int)type {
    
    if (type == 2) {
        self.tabedSlideView.selectedIndex = 1;
    } else {
        self.tabedSlideView.selectedIndex = 0;
    }
}

#pragma mark-
#pragma mark-------DLTabedSlideView delegate
- (NSInteger)numberOfTabsInDLTabedSlideView:(DLTabedSlideView *)sender{
    return 2;
}

- (UIViewController *)DLTabedSlideView:(DLTabedSlideView *)sender controllerAt:(NSInteger)index{
    switch (index) {
        case 0:
        {
            UIStoryboard *board = [UIStoryboard storyboardWithName:@"Main" bundle:nil];
            MessageVC *ctrl = [board instantiateViewControllerWithIdentifier:@"MessageVC"];
            self.messageVc = ctrl;
            return ctrl;
        }
        case 1:
        {
            UIStoryboard *board = [UIStoryboard storyboardWithName:@"Main" bundle:nil];
            NoticeVC *ctrl = [board instantiateViewControllerWithIdentifier:@"NoticeVC"];
            self.noticeVc = ctrl;
            return ctrl;
        }
            
        default:
            return nil;
    }
}


@end
