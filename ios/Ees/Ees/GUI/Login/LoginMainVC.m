//
//  LoginMainVC.m
//  Ees
//
//  Created by xiaodong on 2017/12/11.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import "LoginMainVC.h"


@interface LoginMainVC ()
@property(nonatomic,weak) IBOutlet UITextField *userText;
@property(nonatomic,weak) IBOutlet UITextField *passwordText;
@property(nonatomic,weak) IBOutlet UIView *userTextBackView;
@property(nonatomic,weak) IBOutlet UIView *passwordBackView;

@property(nonatomic,weak) IBOutlet BackColorButton *loginBtn;
@property(nonatomic,weak) IBOutlet BackColorButton *fastLoginBtn;
@property(nonatomic,weak) IBOutlet BackColorButton *registerBtn;
@property(nonatomic,weak) IBOutlet BackColorButton *resetPassBtn;

@property(nonatomic,copy) loginSuccessBlock successBlock;
@property(nonatomic,copy) loginFailBlock failBlock;

@end

@implementation LoginMainVC



#pragma mark-
#pragma mark——overide—

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"登录";
    
    self.userTextBackView.backgroundColor = kTextFieldBackgroundColor;
    self.passwordBackView.backgroundColor = kTextFieldBackgroundColor;
    
    self.userText.textColor = kTextFieldTextColor;
    self.userText.keyboardType = UIKeyboardTypeNumberPad;
    
    self.passwordText.textColor = kTextFieldTextColor;
    self.passwordText.keyboardType = UIKeyboardTypeDefault;
    
    
    self.loginBtn.backgroundColor = kLoginBtnBGColor;
    self.loginBtn.titleLabel.font = kLoginBtnFont;
    self.loginBtn.titleLabel.textColor = kLoginBtnTitleColor;
    
    [self.loginBtn setBackgroundColor:kLoginBtnBGColor forState:UIControlStateNormal];
    [self.loginBtn setBackgroundColor:kLoginBtnBGHighlightColor forState:UIControlStateHighlighted];
    [self.loginBtn setTitle:kStringValue_login forState:UIControlStateNormal];
    [self.loginBtn setTitle:kStringValue_login forState:UIControlStateHighlighted];
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark-
#pragma mark——public—
- (void)setLoginBlock:(loginSuccessBlock)successBlock failBlock:(loginFailBlock)failBlock
{
    self.successBlock = successBlock;
    self.failBlock = failBlock;
}


#pragma mark-
#pragma mark—— Event —

- (IBAction)loginPress:(id)sender
{
    NSString *userName = self.userText.text;
    NSString *password = self.passwordText.text;
    
    if (![userName isValidPhoneNumber]) {
        [AlertTool alertShowDefault:self context:@"请输入正确的手机号码"];
        return;
    }
    
    if (![password isValidPassword]) {
        [AlertTool alertShowDefault:self context:@"请输入正确的密码"];
        return;
    }
    
    [LoadingTool beginLoading:self.navigationController.view title:@"登录中..."];
    
    NSString *passwordBase32 = [password base32String];
    
    NSData *passwordBase32Data = [passwordBase32 dataUsingEncoding:NSUTF8StringEncoding];
    
    //encode
    NSString *passwordBase64 = [passwordBase32Data base64EncodedString];
    
    NSString *encry = [EncryptTool codeWithRandomString:passwordBase64];
    
    
    NSMutableDictionary *dic = [[NSMutableDictionary alloc] init];
    [dic setObject:kSecret forKey:@"secret"];
    [dic setObject:userName forKey:@"pid"];
    [dic setObject:encry forKey:@"password"];
    
    __weak typeof(self) weakSelf = self;
    [HttpRequestEngin loginWithParam:dic successBlock:^(id  _Nullable responseObject) {
        NSError *error;
        RespondModel *apiModel = [[RespondModel alloc] initObjectWithDictionary:responseObject objectClass:[UserModel class] objectError:&error];
        
        if (apiModel.success) {
            [LoadingTool finishLoading:self.navigationController.view title:@"登录成功" success:YES];
    
            if (apiModel.object) {
                UserInfoManager *userItem = [UserInfoManager sharedInstance];
                [userItem saveUserInfo:responseObject];
                
                if (self.enterType == LoginEnterRootView) {
                    weakSelf.successBlock(apiModel.object);
                } else {
                    [self dismissViewControllerAnimated:YES completion:nil];
                }
            }
        } else {
            ErrorModel *model = apiModel.error;
            ErrorCodeEvent *event = [[ErrorCodeEvent alloc] initWithError:model];
            [event errorCodeDone:self needLoading:YES];
        }
    } failBlock:^(NSError * _Nonnull error) {
        [LoadingTool failLoading:weakSelf.navigationController.view title:@"网络连接错误"];
    } progress:^(NSProgress * _Nonnull upload) {
        
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
#pragma mark—— textField delegate—

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return YES;
}

- (void)textFieldDidEndEditing:(UITextField *)textField
{
    [textField resignFirstResponder];
}


#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
    
    if ([[segue destinationViewController] isKindOfClass:[FastLoginVC class]]) {
        FastLoginVC *vc = [segue destinationViewController];
        [vc setFastLoginBlock:^(BOOL success, NSDictionary *dic) {
            if (self.enterType == LoginEnterRootView) {
                self.successBlock(dic);
            } else {
                [self dismissViewControllerAnimated:YES completion:nil];
            }
        }];
    }
}


@end
