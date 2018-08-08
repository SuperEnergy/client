//
//  TransferAccoutsVC.m
//  Ees
//
//  Created by xiaodong on 2017/12/20.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import "TransferAccoutsVC.h"



@interface TransferAccoutsVC ()
{
    int timeCount;
}

@property(nonatomic,weak)IBOutlet UIView *tipBgView;
@property(nonatomic,weak)IBOutlet UILabel *tipLabel;
@property(nonatomic,weak)IBOutlet UITextField *accountText;
@property(nonatomic,weak)IBOutlet UITextField *countText;
@property(nonatomic,weak)IBOutlet UITextField *codeText;

@property(nonatomic,weak)IBOutlet UIView *accBgView;
@property(nonatomic,weak)IBOutlet UIView *countBgView;
@property(nonatomic,weak)IBOutlet UIView *codeBgView;

@property(nonatomic,weak)IBOutlet UILabel *totalLabel;
@property(nonatomic,weak)IBOutlet UILabel *chargeLabel;

@property(nonatomic,weak)IBOutlet UIButton *codeBtn;
@property(nonatomic,weak)IBOutlet UIButton *transferBtn;

@property(nonatomic,strong) NSTimer *timer;
@property(nonatomic,assign) double charge;
@property(nonatomic,assign) float baseCharge;

@end

@implementation TransferAccoutsVC

#pragma mark-
#pragma mark----overide---

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.baseCharge = 1.0;
    self.charge = 0.0;
    
    self.automaticallyAdjustsScrollViewInsets = NO;
    self.tipBgView.backgroundColor = [UIColor clearColor];
    self.tipLabel.textColor = [UIColor blackColor];
    ConfigManager *configMg = [ConfigManager sharedInstance];
    if (configMg.chargeDesc) {
        NSString *chargeDesc = configMg.chargeDesc.value;
        chargeDesc = [chargeDesc stringByReplacingOccurrencesOfString:@"\\n" withString:@"\n"];
        self.tipLabel.text = chargeDesc;
        
    } else {
        self.tipLabel.text = [self readTxt];
    }
    if (configMg.chargeFee) {
        self.baseCharge = [configMg.chargeFee.value floatValue];
    }
    
    self.accBgView.backgroundColor = kTextFieldBackgroundColor;
    self.countBgView.backgroundColor = kTextFieldBackgroundColor;
    self.codeBgView.backgroundColor = kTextFieldBackgroundColor;
    
    self.accountText.textColor = kTextFieldTextColor;
    self.countText.textColor = kTextFieldTextColor;
    self.codeText.textColor = kTextFieldTextColor;
    
    self.accountText.keyboardType = UIKeyboardTypeNumberPad;
    self.countText.keyboardType = UIKeyboardTypeDecimalPad;
    self.codeText.keyboardType = UIKeyboardTypeDefault;
    
    
    self.codeBtn.backgroundColor = kViewBackgroundColor;
    self.codeBtn.titleLabel.textColor = kLoginBtnTitleColor;
    self.codeBtn.enabled = NO;
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(textFieldDidChangeValue:)
                                                 name:UITextFieldTextDidChangeNotification
                                               object:self.accountText];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(textFieldDidChangeValue:)
                                                 name:UITextFieldTextDidChangeNotification
                                               object:self.countText];
    
    self.transferBtn.backgroundColor = kLoginBtnBGColor;
    self.transferBtn.titleLabel.font = kLoginBtnFont;
    self.transferBtn.titleLabel.textColor = kLoginBtnTitleColor;
    
    UserInfoManager *userInfo = [UserInfoManager sharedInstance];
    LedgerModel *fund = userInfo.ledger;
    NSString *str = [DecimalNumberTool moneyFormatFormDouble:[fund.fund doubleValue]];
    self.totalLabel.text = [NSString stringWithFormat:@"可用余额 %@",str];
    self.chargeLabel.textColor = [UIColor orangeColor];
    [self updateCharge];
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark-
#pragma mark-----button---

- (IBAction)codePress:(id)sender
{
    NSString *count = self.countText.text;
    double dCount = [count doubleValue];
    if (dCount < 1) {
        [LoadingTool tipView:self.view title:@"转账数额必须大于1ees" image:nil];
        //[AlertTool alertShowDefault:self context:@"转账数额必须大于1ees"];
        return;
    }
    UserInfoManager *userInfo = [UserInfoManager sharedInstance];
    LedgerModel *fund = userInfo.ledger;
    double total = dCount+self.charge;
    if (total>[fund.fund doubleValue]) {
        //[AlertTool alertShowDefault:self context:@"转账数额超过可用余额"];
        [LoadingTool tipView:self.view title:@"转账数额超过可用余额" image:nil];
        return;
    }
    
    NSString *mobileNum = [UserInfoManager sharedInstance].userItem.pid;
    NSDictionary *dic = @{@"pid":mobileNum,@"token":[UserInfoManager sharedInstance].userItem.token};
    [HttpRequestEngin authcodeForTransferWithParam:dic successBlock:^(id  _Nullable responseObject) {
        
        NSDictionary *errorDic = [responseObject objectForKey:@"error"];
        if (![errorDic isKindOfClass:[NSNull class]]) {
            NSString *mes = [errorDic objectForKey:@"message"];
            [LoadingTool tipView:self.navigationController.view title:mes image:nil];
        }
    } failBlock:^(NSError * _Nonnull error) {
        [LoadingTool tipView:self.navigationController.view title:@"验证码发送错误" image:nil];
    } progress:^(NSProgress * _Nonnull loadprogress) {
        
    }];
    
    self.codeBtn.enabled = NO;
    self.codeBtn.enabled = NO;
    if (self.timer) {
        [self.timer invalidate];
        self.timer = nil;
    }
    
    self.timer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(timerEvent:) userInfo:nil repeats:YES];
    [self.timer fire];
}

- (void)timerEvent:(NSTimer*)theTimer {
    if (timeCount == 60) {
        [self.timer invalidate];
        timeCount = 0;
        self.codeBtn.enabled = YES;
        [self.codeBtn setTitle:@"获取验证码" forState:UIControlStateNormal];
        [self.codeBtn setTitle:@"获取验证码" forState:UIControlStateDisabled];
        self.codeBtn.backgroundColor = kLoginBtnBGColor;
    } else {
        self.codeBtn.enabled = NO;
        NSString *title = [NSString stringWithFormat:@"还有%.2d秒",60-timeCount];
        [self.codeBtn setTitle:title forState:UIControlStateNormal];
        [self.codeBtn setTitle:title forState:UIControlStateDisabled];
        self.codeBtn.backgroundColor = kViewBackgroundColor;
    }
    timeCount++;
}



- (IBAction)transferPress:(id)sender
{
    NSString *otherPid = self.accountText.text;
    NSString *count = self.countText.text;
    NSString *code = self.codeText.text;
    
    if ([otherPid isEqualToString:[UserInfoManager sharedInstance].userItem.pid]) {
        [AlertTool alertShowDefault:self context:@"请输入对方的手机号码"];
        return;
    }
    if (![otherPid isValidPhoneNumber]) {
        [AlertTool alertShowDefault:self context:@"请输入正确的对方手机号码"];
        return;
    }
    
    if (![count isValidTransferCount]) {
        [AlertTool alertShowDefault:self context:@"请输入正确的转账数量"];
        return;
    }
    
    double dCount = [count doubleValue];
    if (dCount < 1) {
        [AlertTool alertShowDefault:self context:@"转账数额必须大于1ees"];
        return;
    }
    UserInfoManager *userInfo = [UserInfoManager sharedInstance];
    LedgerModel *fund = userInfo.ledger;
    double total = dCount+self.charge;
    if (total>[fund.fund doubleValue]) {
        [AlertTool alertShowDefault:self context:@"转账数额超过可用余额"];
        return;
    }
    
    if (code.length != 6) {
        [AlertTool alertShowDefault:self context:@"请输入身份证号码后6位"];
        return;
    }
    
    NSString *str = [self readTxt];
    [AlertTool alertShowTwoActionTextAlignmentLeft:self title:@"风险提醒" context:str okTitle:@"确定" cancelTitle:@"取消" okHandler:^(UIAlertAction *action) {
        NSString *countBase32 = [count base32String];
        NSData *countBase32Data = [countBase32 dataUsingEncoding:NSUTF8StringEncoding];
        NSString *countBase64 = [countBase32Data base64EncodedString];
        NSString *encry = [EncryptTool codeWithRandomString:countBase64];
        
        NSString *codeBase32 = [code base32String];
        NSData *codeBase32Data = [codeBase32 dataUsingEncoding:NSUTF8StringEncoding];
        NSString *codeBase64 = [codeBase32Data base64EncodedString];
        NSString *codeEncry = [EncryptTool codeWithRandomString:codeBase64];
        
        UserInfoManager *item = [UserInfoManager sharedInstance];
        NSDictionary *dic = @{@"secret":kSecret,@"pid":item.userItem.pid,@"to_pid":otherPid,@"token":item.userItem.token,@"qty":encry,@"idcard":codeEncry};
        
        [LoadingTool beginLoading:self.navigationController.view title:@"转账中.."];
        //__weak typeof(self) weakSelf = self;
        [HttpRequestEngin transferIdcardWithParam:dic successBlock:^(id  _Nullable responseObject) {
            
            RespondModel *apiModel = [[RespondModel alloc] initNumberObjectWithDictionary:responseObject];
            NSNumber *number = apiModel.object;
            if ([number boolValue]) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self clearTextField];
                });
                [LoadingTool finishLoading:self.navigationController.view title:@"转账成功" success:YES];
            } else {
                ErrorModel *model = apiModel.error;
                ErrorCodeEvent *event = [[ErrorCodeEvent alloc] initWithError:model];
                [event errorCodeDone:self needLoading:YES];
            }
        } failBlock:^(NSError * _Nonnull error) {
            [LoadingTool finishLoading:self.navigationController.view title:error.description success:NO];
        } progress:^(NSProgress * _Nonnull upload) {
            [LoadingTool loading:self.navigationController.view upload:upload];
        }];
    } cancelHandler:^(UIAlertAction *action) {
        
    }];
}

#pragma mark-
#pragma mark----private

- (NSString *)readTxt
{
    NSString *path = [[NSBundle mainBundle] pathForResource:@"TransferWarning" ofType:@"txt"];
    NSString *content = [[NSString alloc] initWithContentsOfFile:path encoding:NSUTF8StringEncoding error:nil];
    return content;
}

- (void)updateCharge {
    double total = [self.countText.text doubleValue];
    self.charge = total*0.01+self.baseCharge;
    NSString *str = [DecimalNumberTool moneyFormatFormDouble:self.charge];
    NSString *baseStr = [DecimalNumberTool moneyFormatFormDouble:self.baseCharge];
    if (self.baseCharge > 0.0) {
        self.chargeLabel.text = [NSString stringWithFormat:@"手续费(%@+1%@)：%@",baseStr,@"%",str];
    } else {
        self.chargeLabel.text = [NSString stringWithFormat:@"手续费(1%@)：%@",@"%",str];
    }
    
}

- (void)clearTextField {
    self.accountText.text = @"";
    self.countText.text = @"";
    self.codeText.text = @"";
}


#pragma mark-
#pragma mark-----delegate--
- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return YES;
}

#pragma mark-
#pragma mark----notification---
//这里可以通过发送object消息获取注册时指定的UITextField对象
- (void)textFieldDidChangeValue:(NSNotification *)notification
{
    UITextField *textField = (UITextField *)[notification object];
    if (textField.tag == 1000) {
        NSString *text = textField.text;
        if ([text isValidPhoneNumber] && ![text isEqualToString:[UserInfoManager sharedInstance].userItem.pid]) {
            self.codeBtn.enabled = YES;
            self.codeBtn.backgroundColor = kLoginBtnBGColor;
        } else {
            self.codeBtn.enabled = NO;
            self.codeBtn.backgroundColor = kViewBackgroundColor;
        }
    } else if (textField.tag == 999) {
        [self updateCharge];
    }
}


@end
