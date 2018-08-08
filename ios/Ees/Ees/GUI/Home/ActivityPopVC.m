//
//  ActivityPopVC.m
//  Ees
//
//  Created by xiaodong on 2017/12/25.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import "ActivityPopVC.h"

@interface ActivityPopVC ()



@end

@implementation ActivityPopVC

#pragma mark-
#pragma mark——overide—

- (void)awakeFromNib
{
    [super awakeFromNib];
    self.view.backgroundColor = [UIColor colorWithRed:0.0 green:0.0 blue:0.0 alpha:0.8];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    UITapGestureRecognizer *gesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapGesture)];
    [self.coverView addGestureRecognizer:gesture];
    // Do any additional setup after loading the view.
}

- (void)tapGesture {
    if ([self.delegate respondsToSelector:@selector(activityPopDidClick:)]) {
        [self.delegate activityPopDidClick:self.model];
    }
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark-
#pragma mark——Event—

- (IBAction)close:(id)sender
{
    [self dismissViewControllerAnimated:YES completion:nil];
}
/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
