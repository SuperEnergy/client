//
//  UploadIDPictureVC.m
//  Ees
//
//  Created by KCMac on 2017/12/17.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import "UploadIDPictureVC.h"

@interface UploadIDPictureVC ()<UIImagePickerControllerDelegate, UINavigationControllerDelegate>

@property(nonatomic,weak) IBOutlet UIScrollView *bgView;
@property(nonatomic,weak) IBOutlet UILabel *inductionsText;
@property(nonatomic,weak) IBOutlet UIImageView *imageView;
@property(nonatomic,weak) IBOutlet UIImageView *imageView2;
@property(nonatomic,weak) IBOutlet UIImageView *imageView3;
@property(nonatomic,strong) UIImagePickerController *imagePickerController;
@property(nonatomic,weak) IBOutlet UIButton *nextBtn;


@property(nonatomic,assign) int selectIndex;//0:选中第一张 1：选中第二张 2：选中第三张
@property(nonatomic,strong) NSString *upLoadToken;
@property(nonatomic,strong) NSString *userName;
@property(nonatomic,strong) NSString *identityCard;
@property(nonatomic,strong) NSMutableArray *imageNameArray;

@end

@implementation UploadIDPictureVC

#pragma mark-
#pragma mark---overide---

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    
}
- (void)viewDidLoad {
    [super viewDidLoad];
    
    UserInfoManager *userMg = [UserInfoManager sharedInstance];
    if ([userMg.userItem.rna_Status intValue] == -1) {
        [AlertTool alertShowTwoAction:self title:@"重要提醒" context:@"您之前上传的认证信息由于不符合规范，已被驳回；若您再次未按规范上传认证信息，系统将予以封号处理，谢谢合作。" okTitle:@"确定" cancelTitle:@"取消" okHandler:^(UIAlertAction *action) {
            
        } cancelHandler:^(UIAlertAction *action) {
            [self.navigationController popToRootViewControllerAnimated:YES];
        }];
    }
    
    self.title = @"实名认证";
    self.inductionsText.text = @"1、证件文字必须清晰可见，点击示例上传对应照片。\n2、必须在证件上贴有 \"EES认证\" 纸条，不能有任何PS痕迹。\n3、上传非证件照片将会被封号。";
    self.inductionsText.textColor = [UIColor redColor];
    self.automaticallyAdjustsScrollViewInsets = NO;
    self.bgView.contentInset = UIEdgeInsetsMake(10, 0, 0, 0);
    
    self.imageView.userInteractionEnabled = YES;
    self.imageView2.userInteractionEnabled = YES;
    self.imageView3.userInteractionEnabled = YES;
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(imageViewTap:)];
    [self.imageView addGestureRecognizer:tap];
    
    UITapGestureRecognizer *tap2 = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(imageViewTap2:)];
    [self.imageView2 addGestureRecognizer:tap2];
    
    UITapGestureRecognizer *tap3 = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(imageViewTap3:)];
    [self.imageView3 addGestureRecognizer:tap3];
    

    self.nextBtn.backgroundColor = kLoginBtnBGColor;
    self.nextBtn.titleLabel.font = kLoginBtnFont;
    self.nextBtn.titleLabel.textColor = kLoginBtnTitleColor;
    
    
    if (!self.upLoadToken) {
        [self getRealnameToken];
    } else {
        NSLog(@"%@",self.upLoadToken);
    }
    NSArray *tempPic = @[@"",@"",@""];
    self.imageNameArray = [NSMutableArray arrayWithArray:tempPic];
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark-
#pragma mark-----Event----

- (void)imageViewTap:(UITapGestureRecognizer *)gesture
{
    self.selectIndex = 0;
    [self actionSheet];
}

- (void)imageViewTap2:(UITapGestureRecognizer *)gesture
{
    self.selectIndex = 1;
    [self actionSheet];
}

- (void)imageViewTap3:(UITapGestureRecognizer *)gesture
{
    self.selectIndex = 2;
    [self actionSheet];
}

- (IBAction)nextPress:(id)sender
{
    UserInfoManager *item = [UserInfoManager sharedInstance];
    NSString *pid = item.userItem.pid;
    NSString *token = item.userItem.token;
    NSString *realname = self.userName;
    NSString *idcardno = self.identityCard;
    NSString *idcardimg1 = self.imageNameArray[0];
    NSString *idcardimg2 = self.imageNameArray[1];
    NSString *idcardimg3 = self.imageNameArray[2];
    if (idcardimg1.length <= 0) {
        [AlertTool alertShowDefault:self context:@"第一张图片上传失败"];
        return;
    }
    if (idcardimg2.length <= 0) {
        [AlertTool alertShowDefault:self context:@"第二张图片上传失败"];
        return;
    }
//    if (idcardimg3.length <= 0) {
//        [AlertTool alertShowDefault:self context:@"第三张图片上传失败"];
//        return;
//    }
    
    NSString *passwordBase32 = [idcardno base32String];
    NSData *passwordBase32Data = [passwordBase32 dataUsingEncoding:NSUTF8StringEncoding];
    NSString *passwordBase64 = [passwordBase32Data base64EncodedString];
    NSString *encry = [EncryptTool codeWithRandomString:passwordBase64];
    
    NSDictionary *dic = @{@"secret":kSecret,@"pid":pid,@"token":token,@"realname":realname,@"idcardno":encry,@"idcardimg1":idcardimg1,@"idcardimg2":idcardimg2};
    [HttpRequestEngin realnameWithParam:dic successBlock:^(id  _Nullable responseObject) {
        RespondModel *apiModel = [[RespondModel alloc] initStringObjectWithDictionary:responseObject];
        if (apiModel.success) {
            
            [AlertTool alertShowOKAction:self title:kStringValue_tip context:@"实名认证信息提交成功，我们会在1-2个工作日内给您处理，请耐心等待" buttonTitle:kStringValue_ok handler:^(UIAlertAction *action) {
                UserInfoManager *userMg = [UserInfoManager sharedInstance];
                [userMg refreshUserInfo:^(BOOL success) {
                    [[NSNotificationCenter defaultCenter] postNotificationName:kUserStateDidChange object:nil];
                }];
                
                [self.navigationController popToRootViewControllerAnimated:YES];
            }];
            
        } else {
            
            ErrorModel *model = apiModel.error;
            ErrorCodeEvent *event = [[ErrorCodeEvent alloc] initWithError:model];
            [event errorCodeDone:self needLoading:YES];
        }
    } failBlock:^(NSError * _Nonnull error) {
        [LoadingTool tipView:self.view title:@"实名认证失败" image:nil];
    } progress:^(NSProgress * _Nonnull loadprogress) {
        
    }];
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
        imagePickerVC.allowsEditing = NO;
        [self presentViewController:imagePickerVC animated:YES completion:nil];
    } else {
        [AlertTool alertShowDefault:self context:@"请在iphone的“设置-隐私-照片”选项中，允许EES访问你的手机相册。"];
    }
}


- (void)getRealnameToken
{
    UserInfoManager *item = [UserInfoManager sharedInstance];
    NSString *pid = item.userItem.pid;
    NSString *token = item.userItem.token;
    NSDictionary *dic = @{@"secret":kSecret,@"pid":pid,@"token":token};
    [HttpRequestEngin realnameUploadWithParam:dic successBlock:^(id  _Nullable responseObject) {
        //NSError *error;
        RespondModel *apiModel = [[RespondModel alloc] initStringObjectWithDictionary:responseObject];
        self.upLoadToken = apiModel.object;
    } failBlock:^(NSError * _Nonnull error) {
        
    } progress:^(NSProgress * _Nonnull loadprogress) {
        
    }];
}


- (void)uploadImageWithData:(NSData *)data image:(UIImage *)image
{
    if (!self.upLoadToken) {
        [AlertTool alertShowDefault:self context:@"token获取失败,请重试。"];
        [self getRealnameToken];
        return;
    }
    ALBBWTUploadDataRequest *request = [ALBBWTUploadDataRequest new];
    request.content = data;
    request.token = self.upLoadToken; //上传必需的凭证token
    NSString *fileName = [NSString stringWithFormat:@"%@_%d",self.identityCard,self.selectIndex];
    request.fileName = fileName; //可选，保存到服务端的文件名
    request.dir = @"/iosDir"; //可选，服务端的路径，以 '/' 开头
    request.customParms = @{@"customFormat": @"png"}; //开发者可以自定义一些参数
    ALBBWantu *albbWantu = [ALBBWantu defaultWantu]; //获取默认顽兔多媒体实例
    [LoadingTool beginLoading:self.view title:@"正在上传..."];
    [albbWantu upload:request
      completeHandler:^(ALBBWTUploadResponse *response, NSError *error) {
          NSLog(@"error: %@", error); //如果上传失败，在这里获取错误信息
          NSLog(@"reuestId: %@", response.requestId); //每一次请求的requestId
          NSLog(@"url: %@", response.url); //上传成功后返回的文件url
          if ([response.url length] > 0) {
              self.imageNameArray[_selectIndex] = response.url;
              dispatch_async(dispatch_get_main_queue(), ^{
                  [LoadingTool finishLoading:self.view title:@"上传成功" success:YES];
                  if (self.selectIndex == 0) {
                      self.imageView.contentMode = UIViewContentModeScaleAspectFit;
                      self.imageView.image = image;
                  } else if (self.selectIndex == 1) {
                      self.imageView2.contentMode = UIViewContentModeScaleAspectFit;
                      self.imageView2.image = image;
                  } else {
                      self.imageView3.contentMode = UIViewContentModeScaleAspectFit;
                      self.imageView3.image = image;
                  }
              });
              
          } else {
              [LoadingTool failLoading:self.view title:@"上传失败，请重新上传"];
          }
          
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


//camera delegate
// 选择图片成功调用此方法
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary<NSString *,id> *)info{
    NSString *mediaType=[info objectForKey:UIImagePickerControllerMediaType];
    //判断资源类型
    if ([mediaType isEqualToString:(NSString *)kUTTypeImage]){
        //如果是图片
        UIImage *image = info[UIImagePickerControllerOriginalImage];
        //压缩图片
        NSData *fileData = UIImageJPEGRepresentation(image, 0.5);
        //上传图片
        [self uploadImageWithData:fileData image:image];
    }
    
    [self dismissViewControllerAnimated:YES completion:nil];
}


// 取消图片选择调用此方法
- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker {
    [self dismissViewControllerAnimated:YES completion:nil];
}

@end
