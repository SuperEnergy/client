//
//  RealnameVC.m
//  Ees
//
//  Created by KCMac on 2017/12/17.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import "RealnameVC.h"

@interface RealnameVC ()
@property(nonatomic,weak) IBOutlet UITextField *userNameLabel;
@property(nonatomic,weak) IBOutlet UITextField *identityCardLabel;

@property(nonatomic,weak) IBOutlet UIView *userBackView;
@property(nonatomic,weak) IBOutlet UIView *identityCardBackView;

@property(nonatomic,weak) IBOutlet UILabel *inductionsText;
@property(nonatomic,weak) IBOutlet UIButton *nextBtn;

@property(nonatomic,strong) NSString *upLoadToken;

@end

@implementation RealnameVC


#pragma mark-
#pragma mark——overide—

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"实名认证";
    self.inductionsText.text = @"1、姓名、证件号码信息必须真实有效。\n2、上传非真实信息将会被封号。";
    self.inductionsText.textColor = [UIColor redColor];
    self.userBackView.backgroundColor = kTextFieldBackgroundColor;
    self.identityCardBackView.backgroundColor = kTextFieldBackgroundColor;
    
    self.userNameLabel.textColor = kTextFieldTextColor;
    self.identityCardLabel.textColor = kTextFieldTextColor;
    
    self.nextBtn.backgroundColor = kLoginBtnBGColor;
    self.nextBtn.titleLabel.font = kLoginBtnFont;
    self.nextBtn.titleLabel.textColor = kLoginBtnTitleColor;
    
    [self getRealnameToken];
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    NSString *userName = self.userNameLabel.text;
    NSString *idNum = self.identityCardLabel.text;
    
    if ([segue.destinationViewController isKindOfClass:[UploadIDPictureVC class]]) {
        UIViewController *vc = segue.destinationViewController;
        [vc setValue:userName forKey:@"userName"];
        [vc setValue:idNum forKey:@"identityCard"];
        [vc setValue:self.upLoadToken forKey:@"upLoadToken"];
    }
}

#pragma mark-
#pragma mark——private—

- (void)getRealnameToken
{
    UserInfoManager *item = [UserInfoManager sharedInstance];
    NSString *pid = item.userItem.pid;
    NSString *token = item.userItem.token;
    NSDictionary *dic = @{@"secret":kSecret,@"pid":pid,@"token":token};
    [HttpRequestEngin realnameUploadWithParam:dic successBlock:^(id  _Nullable responseObject) {
        RespondModel *apiModel = [[RespondModel alloc] initStringObjectWithDictionary:responseObject];
        if (apiModel.success && apiModel.object) {
            self.upLoadToken = apiModel.object;
        } else {
            ErrorModel *model = apiModel.error;
            ErrorCodeEvent *event = [[ErrorCodeEvent alloc] initWithError:model];
            [event errorCodeDone:self needLoading:YES];
        }
        
        NSLog(@"token %@",self.upLoadToken);
        
    } failBlock:^(NSError * _Nonnull error) {
        
    } progress:^(NSProgress * _Nonnull loadprogress) {
        
    }];
}


#pragma mark-
#pragma mark——Event—

- (IBAction)nextPress:(id)sender
{
    NSString *userName = self.userNameLabel.text;
    NSString *idNum = self.identityCardLabel.text;
    
    if ([userName isEmpty]) {
        [AlertTool alertShowDefault:self context:@"请输入正确的用户名"];
        return;
    }
    if (![idNum isValidCard]) {
        [AlertTool alertShowDefault:self context:@"请输入正确的身份证号码"];
        return;
    }
    
    UIStoryboard *storyboard = self.storyboard;
    UploadIDPictureVC *vc = [storyboard instantiateViewControllerWithIdentifier:@"UploadIDPictureVC"];
    [vc setValue:userName forKey:@"userName"];
    [vc setValue:idNum forKey:@"identityCard"];
    [vc setValue:self.upLoadToken forKey:@"upLoadToken"];
    [self.navigationController pushViewController:vc animated:YES];
}




#pragma mark-
#pragma mark——delegate—

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return YES;
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
