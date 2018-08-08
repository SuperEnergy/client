//
//  ForgetPasswordVC.m
//  Ees
//
//  Created by xiaodong on 2017/12/12.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import "ForgetPasswordVC.h"

@interface ForgetPasswordVC ()
{
    int timeCount;
}
@property(nonatomic,weak) IBOutlet UITextField *mobileNumberText;
@property(nonatomic,weak) IBOutlet UITextField *codeText;
@property(nonatomic,weak) IBOutlet UITextField *passwordText;
@property(nonatomic,weak) IBOutlet UITextField *password2Text;

@property(nonatomic,weak) IBOutlet UIView *mobileBgView;
@property(nonatomic,weak) IBOutlet UIView *codeBgView;
@property(nonatomic,weak) IBOutlet UIView *passwordBgView;
@property(nonatomic,weak) IBOutlet UIView *passwordBgView2;

@property(nonatomic,weak) IBOutlet UIButton *codeBtn;
@property(nonatomic,weak) IBOutlet UIButton *resetBtn;

@property(nonatomic,weak) IBOutlet NSLayoutConstraint *passwordTopConstraint;
@property(nonatomic,strong) NSTimer *timer;

@end

@implementation ForgetPasswordVC

#pragma mark-
#pragma mark——overide—

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.mobileBgView.backgroundColor = kTextFieldBackgroundColor;
    self.codeBgView.backgroundColor = kTextFieldBackgroundColor;
    self.passwordBgView.backgroundColor = kTextFieldBackgroundColor;
    self.passwordBgView2.backgroundColor = kTextFieldBackgroundColor;
    
    self.mobileNumberText.textColor = kTextFieldTextColor;
    self.codeText.textColor = kTextFieldTextColor;
    self.passwordText.textColor = kTextFieldTextColor;
    self.password2Text.textColor = kTextFieldTextColor;
    
    self.mobileNumberText.keyboardType = UIKeyboardTypeNumberPad;
    self.codeText.keyboardType = UIKeyboardTypeNumberPad;
    
    
    self.resetBtn.backgroundColor = kLoginBtnBGColor;
    self.resetBtn.titleLabel.font = kLoginBtnFont;
    self.resetBtn.titleLabel.textColor = kLoginBtnTitleColor;
    
    self.codeBtn.backgroundColor = kViewBackgroundColor;
    self.codeBtn.titleLabel.textColor = kLoginBtnTitleColor;
    self.codeBtn.enabled = NO;
    
    if (self.type == PageTypeForgetPassword) {
        self.title = @"忘记密码";
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(textFieldDidChangeValue:)
                                                     name:UITextFieldTextDidChangeNotification
                                                   object:self.mobileNumberText];
    } else if (self.type == PageTypeModifyPassword) {
        self.title = @"修改密码";
        self.mobileBgView.hidden = YES;
        self.passwordTopConstraint.constant = -40;
        self.codeBtn.backgroundColor = kLoginBtnBGColor;
        self.codeBtn.enabled = YES;
        self.mobileNumberText.text = [UserInfoManager sharedInstance].userItem.pid;
    }
    
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)dealloc {
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}


#pragma mark-
#pragma mark——Event—

- (IBAction)codePress:(id)sender
{
    NSString *mobileNum = self.mobileNumberText.text;
    
    if (![mobileNum isValidPhoneNumber]) {
        [AlertTool alertShowDefault:self context:@"请输入正确的手机号码"];
        return;
    }
    
    NSDictionary *dic = @{@"pid":mobileNum};
    [HttpRequestEngin authcodeForResetWithParam:dic successBlock:^(id  _Nullable responseObject) {
        
        RespondModel *apiModel = [[RespondModel alloc] initNumberObjectWithDictionary:responseObject];
        if ([apiModel.object boolValue]) {
            
        } else {
            if (apiModel.error) {
                ErrorModel *eror = apiModel.error;
                [LoadingTool tipView:self.navigationController.view title:eror.message image:nil];
            }
        }

    } failBlock:^(NSError * _Nonnull error) {
        [LoadingTool tipView:self.navigationController.view title:@"网络连接失败" image:nil];
    } progress:^(NSProgress * _Nonnull loadprogress) {
        
    }];
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

- (IBAction)resetPasswordPress:(id)sender
{
    NSString *pid = self.mobileNumberText.text;
    NSString *code = self.codeText.text;
    NSString *password = self.passwordText.text;
    NSString *password2 = self.password2Text.text;
    
    if (![pid isValidPhoneNumber]) {
        [AlertTool alertShowDefault:self context:@"请输入正确的手机号码"];
        return;
    }
    
    if (![code isValidVerificationCode]) {
        [AlertTool alertShowDefault:self context:@"请输入正确的验证码"];
        return;
    }
    if (![password isValidPassword]) {
        [AlertTool alertShowDefault:self context:@"请输入正确的密码"];
        return;
    }
    if (![password2 isValidPassword]) {
        [AlertTool alertShowDefault:self context:@"请输入正确的确认密码"];
        return;
    }
    
    if (![password isEqualToString:password2])
    {
        [AlertTool alertShowDefault:self context:@"两次密码不一样"];
        return;
    }
    
    NSString *passwordBase32 = [password base32String];
    NSData *passwordBase32Data = [passwordBase32 dataUsingEncoding:NSUTF8StringEncoding];
    NSString *passwordBase64 = [passwordBase32Data base64EncodedString];
    NSString *encry = [EncryptTool codeWithRandomString:passwordBase64];
    
    NSDictionary *dic = @{@"authcode":code,@"pid":pid,@"password":encry};
    [LoadingTool beginLoading:self.navigationController.view title:@"正在重置密码..."];
    
    [HttpRequestEngin resetPasswordWithParam:dic successBlock:^(id  _Nullable responseObject) {
        RespondModel *apiModel = [[RespondModel alloc] initNumberObjectWithDictionary:responseObject];
        if ([apiModel.object boolValue]) {
            [LoadingTool finishLoading:self.navigationController.view title:@"重置密码成功" success:YES];
        } else {
            ErrorModel *model = apiModel.error;
            ErrorCodeEvent *event = [[ErrorCodeEvent alloc] initWithError:model];
            [event errorCodeDone:self needLoading:YES];
        }
        
    } failBlock:^(NSError * _Nonnull error) {
        [LoadingTool failLoading:self.navigationController.view title:@"网络连接失败"];
    } progress:^(NSProgress * _Nonnull uploadProgress) {
        
    }];
}

#pragma mark-
#pragma mark——TextField delegate—

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return YES;
}

- (void)textFieldDidChangeValue:(NSNotification *)notification
{
    UITextField *textField = (UITextField *)[notification object];
    if (textField.tag == 1000) {
        NSString *text = textField.text;
        if ([text isValidPhoneNumber]) {
            self.codeBtn.enabled = YES;
            self.codeBtn.backgroundColor = kLoginBtnBGColor;
        } else {
            self.codeBtn.enabled = NO;
            self.codeBtn.backgroundColor = kViewBackgroundColor;
        }
    }
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
