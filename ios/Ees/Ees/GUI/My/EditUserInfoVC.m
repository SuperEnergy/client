//
//  EditUserInfoVC.m
//  Ees
//
//  Created by KCMac on 2017/12/19.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import "EditUserInfoVC.h"
#import "AppDelegate.h"

#define kCellTitle @"昵称"
#define kCellTitle2 @"手机号码"
#define kCellTitle3 @"登录密码"
#define kCellTitle4 @"实名认证"
#define kCellTitle5 @"退出登录"

#define kAvatarCellIdentifier @"AvatarCell"
#define kTwoLabelCellIdentifier @"TwoLabelCell"
#define kButtonCellIdentifier @"OneButtonCell"

#pragma mark-
#pragma mark——Cell Item—


@interface EditUserInfoItem : NSObject

@property(nonatomic,strong) NSString *title;
@property(nonatomic,strong) NSString *identifier;
@property(nonatomic,assign) SEL selector;
@property(nonatomic,assign) float high;



@end

@implementation EditUserInfoItem

+ (instancetype)userItemWith:(NSString *)title identifier:(NSString *)identifier high:(float)high selector:(SEL)selector
{
    EditUserInfoItem *item = [[self class] new];
    item.title = title;
    item.identifier = identifier;
    item.selector = selector;
    item.high = high;
    return item;
}

@end


#pragma mark-
#pragma mark——EditUserInfoVC—

@interface EditUserInfoVC ()<UIImagePickerControllerDelegate, UINavigationControllerDelegate>

@property(nonatomic,weak) IBOutlet UITableView *tableView;
@property(nonatomic,strong) NSArray<EditUserInfoItem *> *sourceArray;
@property(nonatomic,strong) NSString *upLoadToken;
@property(nonatomic,strong) UIImagePickerController *imagePickerController;
@property(nonatomic,strong) UIImageView *avatarView;
@property(nonatomic,strong) NSString *url;

@end

@implementation EditUserInfoVC

#pragma mark-
#pragma mark——overide—

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.title = @"编辑个人资料";
    
    self.sourceArray = @[[EditUserInfoItem userItemWith:kCellTitle identifier:kAvatarCellIdentifier high:100 selector:nil],
        [EditUserInfoItem userItemWith:kCellTitle2 identifier:kTwoLabelCellIdentifier high:70 selector:nil],
        [EditUserInfoItem userItemWith:kCellTitle3 identifier:kTwoLabelCellIdentifier high:70 selector:@selector(resetPassword)],
        [EditUserInfoItem userItemWith:kCellTitle4 identifier:kTwoLabelCellIdentifier high:70 selector:@selector(realName)],
        [EditUserInfoItem userItemWith:kCellTitle5 identifier:kButtonCellIdentifier high:120 selector:@selector(loginOut)]
                         ];
    [self.tableView registerNib:[UINib nibWithNibName:kAvatarCellIdentifier bundle:nil] forCellReuseIdentifier:kAvatarCellIdentifier];
    [self.tableView registerNib:[UINib nibWithNibName:kTwoLabelCellIdentifier bundle:nil] forCellReuseIdentifier:kTwoLabelCellIdentifier];
    [self.tableView registerNib:[UINib nibWithNibName:kButtonCellIdentifier bundle:nil] forCellReuseIdentifier:kButtonCellIdentifier];
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.tableView.backgroundColor = kViewBackgroundColor;
    self.tableView.contentInset = UIEdgeInsetsMake(-5, 0, 0, 0);
    
    self.automaticallyAdjustsScrollViewInsets = NO;
    
    
    [self getAvatarToken];
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


#pragma mark-
#pragma mark——tableview delegate—

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.sourceArray.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    EditUserInfoItem *item = self.sourceArray[indexPath.row];
    return item.high;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    EditUserInfoItem *item = self.sourceArray[indexPath.row];
#pragma clang diagnostic push
#pragma clang diagnostic ignored   "-Warc-performSelector-leaks"
    if ([self respondsToSelector:item.selector]) {
        [self performSelector:item.selector];
    }
#pragma clang diagnostic pop
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    EditUserInfoItem *item = self.sourceArray[indexPath.row];
    NSString *identifier = item.identifier;
    NSString *title = item.title;
    UITableViewCell *cell;
    if ([title isEqualToString:kCellTitle]) {
        AvatarCell *cell2 = [tableView dequeueReusableCellWithIdentifier:identifier];
        cell2.userNameLabel.text = kCellTitle;
        cell2.userNameLabel.font = [UIFont systemFontOfSize:30];
        cell2.userNameLabel.text = [UserInfoManager sharedInstance].userItem.name;
        cell2.model = [UserInfoManager sharedInstance].userItem;
        UITapGestureRecognizer *gesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(avatarTap)];
        [cell2.avatarView addGestureRecognizer:gesture];
        self.avatarView = cell2.avatarView;
        cell = cell2;
    } else if ([title isEqualToString:kCellTitle2]) {
        TwoLabelCell *cell2 = [tableView dequeueReusableCellWithIdentifier:identifier];
        cell2.label.text = kCellTitle2;
        cell2.label2.text = [UserInfoManager sharedInstance].userItem.pid;
        cell = cell2;
    } else if ([title isEqualToString:kCellTitle3]) {
        TwoLabelCell *cell2 = [tableView dequeueReusableCellWithIdentifier:identifier];
        cell2.label.text = kCellTitle3;
        cell2.label2.textColor = kLightGrayColor;
        cell2.label2.text = @"修改密码";
        cell = cell2;
    } else if ([title isEqualToString:kCellTitle4]) {
        TwoLabelCell *cell2 = [tableView dequeueReusableCellWithIdentifier:identifier];
        cell2.label.text = kCellTitle4;
        cell2.label2.textColor = kLightGrayColor;
        int rna_Status = [[UserInfoManager sharedInstance].userItem.rna_Status intValue];
        if ( rna_Status == 0) {
            cell2.label2.text = @"未认证";
        } else if (rna_Status == 1) {
            cell2.label2.text = @"已认证";
        } else if (rna_Status == -1) {
            cell2.label2.text = @"未认证";
        } else if (rna_Status == 2) {
            cell2.label2.text = @"审核中";
        } else if (rna_Status == 3) {
            cell2.label2.text = @"审核中";
        }
        cell = cell2;
    } else if ([title isEqualToString:kCellTitle5]) {
        OneButtonCell *cell2 = [tableView dequeueReusableCellWithIdentifier:identifier];
        [cell2.button setTintColor:[UIColor whiteColor]];
        cell2.button.backgroundColor = kLoginBtnBGColor;
        [cell2.button setTitle:kCellTitle5 forState:UIControlStateNormal];
        [cell2.button setTitle:kCellTitle5 forState:UIControlStateHighlighted];
        [cell2.button addTarget:self action:@selector(loginOut) forControlEvents:UIControlEventTouchUpInside];
        cell = cell2;
    }
    cell.accessoryType = UITableViewCellAccessoryNone;
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    return cell;
}

#pragma mark-
#pragma mark-------Event---

- (void)avatarTap
{
    [self actionSheet];
}

- (void)resetPassword
{
    UIStoryboard *board = [UIStoryboard storyboardWithName:@"Login" bundle:nil];
    ForgetPasswordVC *vc = [board instantiateViewControllerWithIdentifier:@"ForgetPasswordVC"];
    vc.type = PageTypeModifyPassword;
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)realName
{
    UserInfoManager *item = [UserInfoManager sharedInstance];
    if ([item.userItem.rna_Status intValue] == 0 || [item.userItem.rna_Status intValue] ==-1)
    {
        UIStoryboard *board = self.storyboard;
        RealnameVC *vc = [board instantiateViewControllerWithIdentifier:@"RealnameVC"];
        [self.navigationController pushViewController:vc animated:YES];
    }
}

- (void)loginOut
{
    [AlertTool alertShowTwoAction:self title:kStringValue_tip context:@"退出登录后今日能量收集数据将会被清空\n是否继续退出?" okTitle:kStringValue_ok cancelTitle:@"取消" okHandler:^(UIAlertAction *action) {
        
        UserInfoManager *item = [UserInfoManager sharedInstance];
        NSString *pid = item.userItem.pid;
        NSString *token = item.userItem.token;
        NSDictionary *dic = @{@"secret":kSecret,@"pid":pid,@"token":token};
        //[LoadingTool beginLoading:self.navigationController.view title:@"退出登录..."];
        [HttpRequestEngin logoutWithParam:dic successBlock:^(id  _Nullable responseObject) {
            RespondModel *apiModel = [[RespondModel alloc] initNumberObjectWithDictionary:responseObject];
            if (apiModel.success) {
                NSUserDefaults *userDef = [NSUserDefaults standardUserDefaults];
                [userDef setObject:nil forKey:kBatteryRecordList];
                
                [userDef setObject:nil forKey:kFundReportDate];
                [userDef setObject:nil forKey:kMiningReportDate];
                [userDef setInteger:0 forKey:kLastestDayFundsVersion];
                [userDef setInteger:0 forKey:kLastestDayMiningsVersion];
                [userDef setInteger:0 forKey:kMessageVersion];
                [userDef setInteger:0 forKey:kNoticeVersion];
                [userDef setInteger:0 forKey:kSettingVersion];
                
                [userDef synchronize];
                
                
                [[UserInfoManager sharedInstance] removeUserInfo];
                [[UserInfoManager sharedInstance] removeLedgerInfo];
                [[UserMineManager sharedInstance] removeUserMineList];
                
                
                [FileTool deleteFile:kFundFileName];
                [FileTool deleteFile:kMessageFileName];
                [FileTool deleteFile:kMiningFileName];
                [FileTool deleteFile:kNoticeFileName];
                //[FileTool deleteFile:kSettingFileName];
                
                [self.navigationController popToRootViewControllerAnimated:YES];
                AppDelegate *delegate = (AppDelegate *)[UIApplication sharedApplication].delegate;
                [delegate swithToLogin];
            } else {
                ErrorModel *model = apiModel.error;
                ErrorCodeEvent *event = [[ErrorCodeEvent alloc] initWithError:model];
                [event errorCodeDone:self needLoading:YES];
            }
            
        } failBlock:^(NSError * _Nonnull error) {
            //[LoadingTool failLoading:self.view title:@"退出失败..."];
        } progress:^(NSProgress * _Nonnull loadprogress) {
            
        }];
    } cancelHandler:nil];
    
}


#pragma mark-
#pragma mark-----private---

- (void)actionSheet {
    
    UIAlertController *actionSheetController = [UIAlertController alertControllerWithTitle:nil message:nil preferredStyle:UIAlertControllerStyleActionSheet];
    
    UIAlertAction *cameraAction = [UIAlertAction actionWithTitle:@"拍照" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        [self openCamera];
    }];
    UIAlertAction *albumAction = [UIAlertAction actionWithTitle:@"从手机相册选择" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        [self openPhotoLibrary];
    }];
    UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
        
    }];
    
    [actionSheetController addAction:cameraAction];
    [actionSheetController addAction:albumAction];
    [actionSheetController addAction:cancelAction];
    
    [self presentViewController:actionSheetController animated:YES completion:nil];
}

//打开摄像头
- (void)openCamera
{
    if ([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera]) {
        UIImagePickerController * imagePickerVC = [[UIImagePickerController alloc] init];
        imagePickerVC.sourceType = UIImagePickerControllerSourceTypeCamera;
        imagePickerVC.mediaTypes = @[(NSString *)kUTTypeImage];
        imagePickerVC.delegate = self;
        imagePickerVC.allowsEditing = YES;
        imagePickerVC.videoQuality = UIImagePickerControllerQualityType640x480;
        imagePickerVC.cameraCaptureMode = UIImagePickerControllerCameraCaptureModePhoto;
        [self presentViewController:imagePickerVC animated:YES completion:nil];
    } else {
        [AlertTool alertShowDefault:self context:@"请在iphone的“设置-隐私”选项中，允许EES访问你的摄像头和麦克风。"];
    }
}

//打开相册
- (void)openPhotoLibrary {
    if ([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypePhotoLibrary]) {
        UIImagePickerController * imagePickerVC = [[UIImagePickerController alloc] init];
        // 设置资源来源（相册、相机、图库之一）
        imagePickerVC.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
        imagePickerVC.videoQuality = UIImagePickerControllerQualityType640x480;
        imagePickerVC.mediaTypes = @[(NSString *)kUTTypeImage];
        imagePickerVC.delegate = self;
        imagePickerVC.allowsEditing = YES;
        [self presentViewController:imagePickerVC animated:YES completion:nil];
    } else {
        [AlertTool alertShowDefault:self context:@"请在iphone的“设置-隐私-照片”选项中，允许EES访问你的手机相册。"];
    }
}

- (void)getAvatarToken
{
    UserInfoManager *item = [UserInfoManager sharedInstance];
    NSString *pid = item.userItem.pid;
    NSString *token = item.userItem.token;
    NSDictionary *dic = @{@"secret":kSecret,@"pid":pid,@"token":token};
    [HttpRequestEngin avatarWithParam:dic successBlock:^(id  _Nullable responseObject) {
        RespondModel *apiModel = [[RespondModel alloc] initStringObjectWithDictionary:responseObject];
        if (apiModel.success && apiModel.object) {
            self.upLoadToken = apiModel.object;
        } else {
            ErrorModel *model = apiModel.error;
            ErrorCodeEvent *event = [[ErrorCodeEvent alloc] initWithError:model];
            [event errorCodeDone:self needLoading:YES];
        }
        
        //NSLog(@"token %@",self.upLoadToken);
        
    } failBlock:^(NSError * _Nonnull error) {
        
    } progress:^(NSProgress * _Nonnull loadprogress) {
        
    }];
}

- (void)uploadImageWithData:(NSData *)data image:(UIImage *)image
{
    if (!self.upLoadToken) {
        [AlertTool alertShowDefault:self context:@"获取token失败!"];
        [self getAvatarToken];
        return;
    }
    ALBBWTUploadDataRequest *request = [ALBBWTUploadDataRequest new];
    request.content = data;
    request.token = self.upLoadToken; //上传必需的凭证token
    
    //request.fileName = fileName; //可选，保存到服务端的文件名
    request.dir = @"/iosDir"; //可选，服务端的路径，以 '/' 开头
    request.customParms = @{@"customFormat": @"png"}; //开发者可以自定义一些参数
    ALBBWantu *albbWantu = [ALBBWantu defaultWantu]; //获取默认顽兔多媒体实例
    [LoadingTool beginLoading:self.view title:@"正在上传..."];
    [albbWantu upload:request
      completeHandler:^(ALBBWTUploadResponse *response, NSError *error) {
          NSLog(@"url: %@", response.url); //上传成功后返回的文件url
          if ([response.url length] > 0) {
              self.url = response.url ;
              self.avatarView.image = image;
              [LoadingTool finishLoading:self.view title:@"上传成功" success:YES];
              [self uploadAvatar];
          } else {
              [LoadingTool failLoading:self.view title:@"上传失败，请重新上传"];
          }
          
      }];
}

- (void)uploadAvatar {
    UserInfoManager *item = [UserInfoManager sharedInstance];
    NSString *pid = item.userItem.pid;
    NSString *token = item.userItem.token;
    NSDictionary *dic = @{@"secret":kSecret,@"pid":pid,@"token":token,@"name":item.userItem.name,@"avatar":self.url};
    [HttpRequestEngin modifyUserInfoWithParam:dic successBlock:^(id  _Nullable responseObject) {
        RespondModel *apiModel = [[RespondModel alloc] initObjectWithDictionary:responseObject objectClass:[UserModel class] objectError:nil];
        if (apiModel.success && apiModel.object) {
            UserInfoManager *item = [UserInfoManager sharedInstance];
            [item saveUserInfo:responseObject];
        } else {
            ErrorModel *model = apiModel.error;
            ErrorCodeEvent *event = [[ErrorCodeEvent alloc] initWithError:model];
            [event errorCodeDone:self needLoading:YES];
        }
        
        //NSLog(@"token %@",apiModel.object);
        
    } failBlock:^(NSError * _Nonnull error) {
        
    } progress:^(NSProgress * _Nonnull loadprogress) {
        
    }];
}


#pragma mark-
#pragma mark-----UIImagePickerControllerDelegate----
// 选择图片成功调用此方法

- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingImage:(UIImage *)image editingInfo:(nullable NSDictionary<NSString *,id> *)editingInfo {
    NSData *fileData = UIImageJPEGRepresentation(image, 0.5);
    //上传图片
    [self uploadImageWithData:fileData image:image];
}

//适用获取所有媒体资源，只需判断资源类型
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary<NSString *,id> *)info{
    NSString *mediaType=[info objectForKey:UIImagePickerControllerMediaType];
    //判断资源类型
    if ([mediaType isEqualToString:(NSString *)kUTTypeImage]){
        //如果是图片
        UIImage *image = info[UIImagePickerControllerEditedImage];
        //压缩图片
        NSData *fileData = UIImageJPEGRepresentation(image, 0.5);
        //上传图片
        [self uploadImageWithData:fileData image:image];
    }
    
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
