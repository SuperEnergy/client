//
//  AssetMainVC.m
//  Ees
//
//  Created by xiaodong on 2017/12/11.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import "AssetMainVC.h"


@interface AssetMainVC ()

@end

@implementation AssetMainVC

#pragma mark-
#pragma mark——overide—

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self.navigationController setNavigationBarHidden:NO];
    self.tabBarController.tabBar.hidden = YES;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"资产明细";
    
    self.tabedSlideView.baseViewController = self;
    self.tabedSlideView.tabItemNormalColor = [UIColor lightGrayColor];
    self.tabedSlideView.tabItemSelectedColor = kLoginBtnBGColor;
    self.tabedSlideView.tabbarTrackColor = kLoginBtnBGColor;
    //self.tabedSlideView.tabbarBackgroundImage = [UIImage imageNamed:@"tabbarBk"];
    self.tabedSlideView.tabbarBottomSpacing = 0;
    
    DLTabedbarItem *item1 = [DLTabedbarItem itemWithTitle:@"能量收益" image:nil selectedImage:nil];
    DLTabedbarItem *item2 = [DLTabedbarItem itemWithTitle:@"所有明细" image:nil selectedImage:nil];
    
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
#pragma mark——tabelView delegate—

- (NSInteger)numberOfTabsInDLTabedSlideView:(DLTabedSlideView *)sender{
    return 2;
}

- (UIViewController *)DLTabedSlideView:(DLTabedSlideView *)sender controllerAt:(NSInteger)index{
    switch (index) {
        case 0:
        {
            UIStoryboard *board = [UIStoryboard storyboardWithName:@"Main" bundle:nil];
            MiningAssetRecordVC *ctrl = [board instantiateViewControllerWithIdentifier:@"MiningAssetRecordVC"];
            return ctrl;
        }
            break;
        case 1:
        {
            UIStoryboard *board = [UIStoryboard storyboardWithName:@"Main" bundle:nil];
            TotalAssetRecordVC *ctrl = [board instantiateViewControllerWithIdentifier:@"TotalAssetRecordVC"];
            return ctrl;
        }
            break;
        
        default:
            return nil;
    }
}


@end
