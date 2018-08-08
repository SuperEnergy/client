//
//  FastLoginVC.m
//  Ees
//
//  Created by xiaodong on 2017/12/12.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import "FastLoginVC.h"

@interface FastLoginVC ()
{
    int timeCount;
}
@property(nonatomic,weak) IBOutlet UITextField *mobileNumberText;
@property(nonatomic,weak) IBOutlet UITextField *codeText;
@property(nonatomic,weak) IBOutlet UIButton *codeBtn;
@property(nonatomic,weak) IBOutlet UIView *mobileBackView;
@property(nonatomic,weak) IBOutlet UIView *codeBackView;
@property(nonatomic,weak) IBOutlet UIButton *loginBtn;
@property(nonatomic,strong) NSTimer *timer;

@property(nonatomic,copy) loginBlock block;

@end

@implementation FastLoginVC



#pragma mark-
#pragma mark——overide—

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"快捷登录";
    
    self.mobileBackView.backgroundColor = kTextFieldBackgroundColor;
    self.codeBackView.backgroundColor = kTextFieldBackgroundColor;
    
    self.mobileNumberText.textColor = kTextFieldTextColor;
    self.codeText.textColor = kTextFieldTextColor;
    self.mobileNumberText.keyboardType = UIKeyboardTypeNumberPad;
    self.codeText.keyboardType = UIKeyboardTypeNumberPad;
    
    self.loginBtn.backgroundColor = kLoginBtnBGColor;
    self.loginBtn.titleLabel.font = kLoginBtnFont;
    self.loginBtn.titleLabel.textColor = kLoginBtnTitleColor;
    
    self.codeBtn.backgroundColor = kViewBackgroundColor;
    self.codeBtn.titleLabel.textColor = kLoginBtnTitleColor;
    self.codeBtn.enabled = NO;
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(textFieldDidChangeValue:)
                                                 name:UITextFieldTextDidChangeNotification
                                               object:self.mobileNumberText];
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
#pragma mark——public—

- (void)setFastLoginBlock:(loginBlock)block
{
    self.block = block;
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
    [HttpRequestEngin authcodeForphoneLoginWithParam:dic successBlock:^(id  _Nullable responseObject) {
        
        RespondModel *apiModel = [[RespondModel alloc] initNumberObjectWithDictionary:responseObject];
        if (apiModel.success) {
            
        } else {
            ErrorModel *model = apiModel.error;
            ErrorCodeEvent *event = [[ErrorCodeEvent alloc] initWithError:model];
            [event errorCodeDone:self needLoading:YES];
        }
        
    } failBlock:^(NSError * _Nonnull error) {
        //[LoadingTool tipView:self.navigationController.view title:error.description image:nil];
    } progress:^(NSProgress * _Nonnull uploadProgress) {
        
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

- (IBAction)loginPress:(id)sender
{
    NSString *pid = self.mobileNumberText.text;
    NSString *code = self.codeText.text;
    
    if (![pid isValidPhoneNumber]) {
        [AlertTool alertShowDefault:self context:@"请输入正确的手机号码"];
        return;
    }
    
    if (![code isValidVerificationCode]) {
        [AlertTool alertShowDefault:self context:@"请输入正确的验证码"];
        return;
    }
    
    
    NSMutableDictionary *dic = [[NSMutableDictionary alloc] init];
    [dic setObject:kSecret forKey:@"secret"];
    [dic setObject:pid forKey:@"pid"];
    [dic setObject:code forKey:@"authcode"];
    
    [LoadingTool beginLoading:self.navigationController.view title:@"登录中..."];
    
    __weak typeof(self) weakSelf = self;
    [HttpRequestEngin phoneLoginWithParam:dic successBlock:^(id  _Nullable responseObject) {
        NSError *error;
        RespondModel *apiModel = [[RespondModel alloc] initObjectWithDictionary:responseObject objectClass:[UserModel class] objectError:&error];
        
        if (apiModel.success) {
            UserInfoManager *userItem = [UserInfoManager sharedInstance];
            [userItem saveUserInfo:responseObject];
            [LoadingTool finishLoading:self.navigationController.view title:@"登录成功" success:YES];
            self.block(YES,responseObject);
        } else {
            
            ErrorModel *model = apiModel.error;
            ErrorCodeEvent *event = [[ErrorCodeEvent alloc] initWithError:model];
            [event errorCodeDone:self needLoading:YES];
        }
    } failBlock:^(NSError * _Nonnull error) {
        [LoadingTool failLoading:weakSelf.navigationController.view title:@"网络连接失败"];
    } progress:^(NSProgress * _Nonnull upload) {
        
    }];
}

- (IBAction)protocalPress:(id)sender
{
    UIStoryboard *board = [UIStoryboard storyboardWithName:@"Main" bundle:nil];
    WebViewVC *ctrl = [board instantiateViewControllerWithIdentifier:@"WebViewVC"];
    ctrl.url = kAgreementUrl;
    ctrl.isLinkAction = YES;
    ctrl.title = @"软件服务条款";
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
