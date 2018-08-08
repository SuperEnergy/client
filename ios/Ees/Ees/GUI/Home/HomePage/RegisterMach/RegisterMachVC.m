//
//  RegisterMachVC.m
//  Ees
//
//  Created by KCMac on 2017/12/19.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import "RegisterMachVC.h"

@interface RegisterMachVC ()

@property(nonatomic,weak) IBOutlet UIScrollView *bgView;
@property(nonatomic,weak) IBOutlet UIView *topBGView;
@property(nonatomic,weak) IBOutlet UIView *roundView;
@property(nonatomic,weak) IBOutlet NSLayoutConstraint *roundTopConstraint;
@property(nonatomic,weak) IBOutlet NSLayoutConstraint *roundBottonConstraint;

@property(nonatomic,weak) IBOutlet UILabel *reginLabel;
@property(nonatomic,weak) IBOutlet NSLayoutConstraint *reginCenterYConstraint;

@property(nonatomic,weak) IBOutlet UIView *rnaStatusBGView;
@property(nonatomic,weak) IBOutlet UILabel *rnaRemarkLabel;
@property(nonatomic,weak) IBOutlet NSLayoutConstraint *textBGTopConstraint;

@property(nonatomic,weak) IBOutlet UIView *textBGView;
@property(nonatomic,weak) IBOutlet UILabel *textLabel;




@end

@implementation RegisterMachVC

#pragma mark-
#pragma mark——overide—

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    
    [self setRoundViewCornerRadius];
    [self updateRegisterMachUserState:[UserInfoManager sharedInstance]];
}


- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.automaticallyAdjustsScrollViewInsets = NO;
    self.bgView.contentInset = UIEdgeInsetsMake(-20, 0, -30, 0);
    self.textLabel.text = [self readTxt];
    
    self.rnaStatusBGView.hidden = YES;
    self.roundView.userInteractionEnabled = NO;
    
    self.view.backgroundColor = kViewBackgroundColor;
    self.topBGView.backgroundColor = kCycleBackgroundColor;
    
    if (kIsIphone6Below) {
        self.roundTopConstraint.constant = 30;
        self.roundBottonConstraint.constant = 30;
        if (kIsIphone5Below) {
            self.roundTopConstraint.constant = 15;
            self.roundBottonConstraint.constant = 15;
        }
    } else {
        self.roundTopConstraint.constant = 45;
        self.roundBottonConstraint.constant = 45;
    }
    
    [self setRoundViewCornerRadius];
    
    CGFloat fontSize = 20.0*kDevicesScale;
    self.reginLabel.font = [UIFont systemFontOfSize:fontSize];
    self.reginCenterYConstraint.constant = 0;
    
    UITapGestureRecognizer *gesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapGesture)];
    [self.roundView addGestureRecognizer:gesture];
    
    UITapGestureRecognizer *gesture2 = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapGesture)];
    [self.rnaStatusBGView addGestureRecognizer:gesture2];
    
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


#pragma mark-
#pragma mark——private—

- (void)setRoundViewCornerRadius {
    [self.roundView.layer setCornerRadius:CGRectGetHeight([self.roundView bounds]) / 2];
    self.roundView.layer.masksToBounds = YES;
    self.roundView.layer.borderWidth = 5;//边框width
    self.roundView.layer.borderColor = [[UIColor whiteColor] CGColor];//边框color
}


- (NSString *)readTxt
{
    NSString *path = [[NSBundle mainBundle] pathForResource:@"Instructions" ofType:@"txt"];
    NSString *content = [[NSString alloc] initWithContentsOfFile:path encoding:NSUTF8StringEncoding error:nil];
    return content;
}

- (void)realClick
{
    if ([self.delegate respondsToSelector:@selector(realNameDidClick)]) {
        [self.delegate realNameDidClick];
    }
}


#pragma mark-
#pragma mark——Event—

- (void)tapGesture
{
    UserInfoManager *item = [UserInfoManager sharedInstance];
    if ([item.userItem.rna_Status intValue] == 0 || [item.userItem.rna_Status intValue] ==-1)
    {
        [self realClick];
    }
}


#pragma mark-
#pragma mark——public—
- (void)updateRegisterMachUserState:(UserInfoManager *)item
{
    self.textBGTopConstraint.constant = -self.rnaStatusBGView.frame.size.height;
    self.rnaStatusBGView.hidden = YES;
    //item.userItem.rna_Status = 0;
    if ([item.userItem.rna_Status intValue] == 0) {
        self.reginLabel.text = @"免费领取";
        self.roundView.userInteractionEnabled = YES;
        self.reginCenterYConstraint.constant = 0;
    } else if ([item.userItem.rna_Status intValue]== -1) {
        self.reginLabel.text = @"重新\n实名认证";
        self.rnaRemarkLabel.text = [NSString stringWithFormat:@"实名认证被驳回,驳回原因:%@",item.userItem.rna_remarks];
        self.roundView.userInteractionEnabled = YES;
        self.textBGTopConstraint.constant = 0;
        self.rnaStatusBGView.hidden = NO;
        self.reginCenterYConstraint.constant = 10;
    } else if ([item.userItem.rna_Status intValue] == 2) {
        self.reginLabel.text = @"实名认证\n审核中";
        self.roundView.userInteractionEnabled = NO;
        self.reginCenterYConstraint.constant = 10;
    } else if ([item.userItem.rna_Status intValue] == 3) {
        self.reginLabel.text = @"实名认证\n审核中";
        self.roundView.userInteractionEnabled = NO;
        self.reginCenterYConstraint.constant = 10;
    }
}


#pragma mark-
#pragma mark------UIScrollView Delegate-----

- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    
    float y = scrollView.contentOffset.y+20;
    UIView *currentView = self.bgView;
    if ((y > self.topBGView.frame.origin.y) && (y < self.topBGView.frame.origin.y + self.topBGView.frame.size.height)) {
        currentView =  self.topBGView;
    }
    
    if ([self.delegate respondsToSelector:@selector(statusBarDidChangeColor:)]) {
        if ([ConfigManager sharedInstance].isChangeSkin) {
            [self.delegate statusBarDidChangeColor:[UIColor redColor]];
        } else {
            [self.delegate statusBarDidChangeColor:currentView.backgroundColor];
        }
    }
}


@end
