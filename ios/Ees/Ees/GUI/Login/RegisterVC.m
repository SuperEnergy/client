//
//  RegisterVC.m
//  Ees
//
//  Created by xiaodong on 2017/12/12.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import "RegisterVC.h"


@interface RegisterVC ()
{
    int timeCount;
}
@property(nonatomic,weak) IBOutlet UIView *nameBgView;
@property(nonatomic,weak) IBOutlet UIView *mobileBgView;
@property(nonatomic,weak) IBOutlet UIView *codeBgView;
@property(nonatomic,weak) IBOutlet UIView *passwordBgView;
@property(nonatomic,weak) IBOutlet UIView *inviteCodeBgView;

@property(nonatomic,weak) IBOutlet UITextField *nameText;
@property(nonatomic,weak) IBOutlet UITextField *mobileText;
@property(nonatomic,weak) IBOutlet UITextField *codeText;
@property(nonatomic,weak) IBOutlet UITextField *passwordText;
@property(nonatomic,weak) IBOutlet UITextField *inviteCodeText;

@property(nonatomic,weak) IBOutlet UIButton *codeBtn;
@property(nonatomic,weak) IBOutlet UIButton *registerBtn;
@property(nonatomic,strong) NSTimer *timer;

@end

@implementation RegisterVC

#pragma mark-
#pragma mark——overide—

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"注册";
    
    self.nameBgView.backgroundColor = kTextFieldBackgroundColor;
    self.mobileBgView.backgroundColor = kTextFieldBackgroundColor;
    self.codeBgView.backgroundColor = kTextFieldBackgroundColor;
    self.passwordBgView.backgroundColor = kTextFieldBackgroundColor;
    self.inviteCodeBgView.backgroundColor = kTextFieldBackgroundColor;
    
    self.nameText.textColor = kTextFieldTextColor;
    self.mobileText.textColor = kTextFieldTextColor;
    self.codeText.textColor = kTextFieldTextColor;
    self.passwordText.textColor = kTextFieldTextColor;
    self.inviteCodeText.textColor = kTextFieldTextColor;
    
    self.mobileText.keyboardType = UIKeyboardTypeNumberPad;
    self.codeText.keyboardType = UIKeyboardTypeNumberPad;
    
    
    self.registerBtn.backgroundColor = kLoginBtnBGColor;
    self.registerBtn.titleLabel.font = kLoginBtnFont;
    self.registerBtn.titleLabel.textColor = kLoginBtnTitleColor;
    
    self.codeBtn.backgroundColor = kViewBackgroundColor;
    self.codeBtn.titleLabel.textColor = kLoginBtnTitleColor;
    self.codeBtn.enabled = NO;
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(textFieldDidChangeValue:)
                                                 name:UITextFieldTextDidChangeNotification
                                               object:self.mobileText];
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
    NSString *mobileNum = self.mobileText.text;
    if (![mobileNum isValidPhoneNumber]) {
        [AlertTool alertShowDefault:self context:@"请输入正确的手机号码"];
        return;
    }

    NSDictionary *dic = @{@"pid":mobileNum};
    [HttpRequestEngin authcodeForRegisterWithParam:dic successBlock:^(id  _Nullable responseObject) {
        
        RespondModel *apiModel = [[RespondModel alloc] initNumberObjectWithDictionary:responseObject];
        if ([apiModel.object boolValue]) {
            
        } else {
            if (apiModel.error) {
                ErrorModel *eror = apiModel.error;
                [LoadingTool tipView:self.navigationController.view title:eror.message image:nil];
            } else {
                [LoadingTool tipView:self.navigationController.view title:@"获取验证码失败" image:nil];
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


- (IBAction)registerPress:(id)sender
{
    NSString *name = self.nameText.text;
    NSString *pid = self.mobileText.text;
    NSString *code = self.codeText.text;
    NSString *password = self.passwordText.text;
    NSString *ref = self.inviteCodeText.text;
    
    if ([name isEmpty]) {
        [AlertTool alertShowDefault:self context:@"昵称不能为空"];
        return;
    }
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
//    if (![ref isValidPhoneNumber]) {
//        [GUITool alertShowDefault:self context:@"请输入正确的推荐人手机号码"];
//        return;
//    }
    
    NSDictionary *dic = @{@"authcode":code,@"pid":pid,@"password":password,@"name":name,@"ref":ref,@"from":@"APP"};
    [LoadingTool beginLoading:self.navigationController.view title:@"正在注册..."];
    
    [HttpRequestEngin registerWithParam:dic successBlock:^(id  _Nullable responseObject) {
        RespondModel *apiModel = [[RespondModel alloc] initNumberObjectWithDictionary:responseObject];
        if ([apiModel.object boolValue]) {
            [LoadingTool finishLoading:self.navigationController.view title:@"注册成功" success:YES];
            [self.navigationController popViewControllerAnimated:YES];
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

- (IBAction)protocalPress:(id)sender
{
    UIStoryboard *board = [UIStoryboard storyboardWithName:@"Main" bundle:nil];
    WebViewVC *ctrl = [board instantiateViewControllerWithIdentifier:@"WebViewVC"];
    ctrl.url = kAgreementUrl;
    ctrl.title = @"软件服务条款";
    ctrl.isLinkAction = YES;
    [self.navigationController pushViewController:ctrl animated:YES];
}

#pragma mark-
#pragma mark——TextField delegate—

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return YES;
}

//这里可以通过发送object消息获取注册时指定的UITextField对象
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
