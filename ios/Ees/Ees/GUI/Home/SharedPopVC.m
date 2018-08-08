//
//  SharedPopVC.m
//  Ees
//
//  Created by xiaodong on 2018/7/31.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import "SharedPopVC.h"

@interface SharedPopVC ()

@property(nonatomic,weak) IBOutlet UIImageView *imageView;
@property(nonatomic,weak) IBOutlet UIImageView *imageView2;

@end

@implementation SharedPopVC

- (void)viewWillAppear:(BOOL)animated  {
    [super viewWillAppear:animated];
    self.tabBarController.tabBar.hidden = YES;
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    self.tabBarController.tabBar.hidden = NO;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.title = @"分享";
    UIImage *codeImage = [self createCodePic];
    ConfigModel *config = [ConfigManager sharedInstance].eesShareTemplet;
    NSArray *array = config.valueArray;
    NSString *strUrl = @"http://eeschain.com/site/img/share.jpg";

    if (array.count == 1) {
        strUrl = [array objectAtIndex:0];
    }
    if (array.count >= 2) {
        int index = [self getRandomNumber:0 to:(int)(array.count-1)];
        strUrl = [array objectAtIndex:index];
    }
    NSLog(@"---------share url   :%@",strUrl);
    if (strUrl) {
        NSURL *url = [NSURL URLWithString:strUrl];
        [self.imageView sd_setImageWithURL:url completed:^(UIImage * _Nullable image2, NSError * _Nullable error, SDImageCacheType cacheType, NSURL * _Nullable imageURL) {
            if (image2) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    
                    UIImage *img = codeImage;
                    CGImageRef imgRef = img.CGImage;
                    CGFloat w = CGImageGetWidth(imgRef);
                    CGFloat h = CGImageGetHeight(imgRef);
                    
                    //以1.png的图大小为底图
                    UIImage *img1 = image2;
                    CGImageRef imgRef1 = img1.CGImage;
                    CGFloat w1 = CGImageGetWidth(imgRef1);
                    CGFloat h1 = CGImageGetHeight(imgRef1);
                    CGFloat space = 50;
                    //以1.png的图大小为画布创建上下文
                    UIGraphicsBeginImageContext(CGSizeMake(w1, h1));
                    [img1 drawInRect:CGRectMake(0, 0, w1, h1)];//先把1.png 画到上下文中
                    [img drawInRect:CGRectMake(w1-w-space, h1-h-space, w, h)];//再把小图放在上下文中
                    UIImage *resultImg = UIGraphicsGetImageFromCurrentImageContext();//从当前上下文中获得最终图片
                    UIGraphicsEndImageContext();//关闭上下文
                    
                    self.imageView.image = resultImg;
                    
                    self.imageView.transform = CGAffineTransformMakeScale(1.0, 1.0);
                    [UIView animateWithDuration:0.6 animations:^{
                        self.imageView.transform = CGAffineTransformMakeScale(0.8, 0.8);
                    } completion:^(BOOL finished) {
                        UserInfoManager *userMg = [UserInfoManager sharedInstance];
                        ConfigManager *configMg = [ConfigManager sharedInstance];
                        NSString *str = configMg.shareDescConfig.value;
                        NSString *url = configMg.shareUrlConfig.value;
                        NSString *strUrl = [NSString stringWithFormat:@"%@%@",url,userMg.userItem.id];
                        NSString *shareContext = [NSString stringWithFormat:@"%@%@",str,strUrl];
                        
                        NSArray *activityItems = @[resultImg];
                        UIActivityViewController * activityCtl = [[UIActivityViewController alloc]initWithActivityItems:activityItems applicationActivities:nil];
                        if ([activityCtl respondsToSelector:@selector(popoverPresentationController)])
                        {
                            activityCtl.popoverPresentationController.sourceView = self.view;
                        }
                        [self presentViewController:activityCtl animated:YES completion:nil];
//                        UIActivityViewControllerCompletionWithItemsHandler myBlock = ^(NSString *activityType,BOOL completed,NSArray *returnedItems,NSError *activityError)
//                        {
//                            NSLog(@"activityType :%@", activityType);
//                            if (completed)
//                            {
//                                UIPasteboard *pasteboard = [UIPasteboard generalPasteboard];
//                                pasteboard.string = shareContext;
//                                [LoadingTool tipView:self.view title:@"分享文字内容已复制" image:nil];
//                            }
//                        };
                        //activityCtl.completionWithItemsHandler = myBlock;
                        UIPasteboard *pasteboard = [UIPasteboard generalPasteboard];
                        pasteboard.string = shareContext;
                        [LoadingTool tipView:self.view title:@"分享文字内容已复制" image:nil];
                    }];
                    
                });
            }
        }];
    }
    
    self.view.userInteractionEnabled = YES;
    //self.view.backgroundColor = [UIColor grayColor];
    UITapGestureRecognizer *gesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(hideTapGesture)];
    [self.view addGestureRecognizer:gesture];
    
    // Do any additional setup after loading the view.
}

- (void)hideTapGesture {
    //[self dismissViewControllerAnimated:YES completion:nil];
    [self.navigationController popViewControllerAnimated:YES];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(int)getRandomNumber:(int)from to:(int)to {
    return (int)(from + (arc4random() % (to - from + 1)));
}

- (UIImage *)createCodePic {
    UserInfoManager *userMg = [UserInfoManager sharedInstance];
    ConfigManager *configMg = [ConfigManager sharedInstance];
    
    NSString *url = configMg.shareUrlConfig.value;
    NSString *strUrl = [NSString stringWithFormat:@"%@%@",url,userMg.userItem.id];
    
    // 1. 实例化二维码滤镜
    CIFilter *filter = [CIFilter filterWithName:@"CIQRCodeGenerator"];
    // 2. 恢复滤镜的默认属性
    [filter setDefaults];
    
    // 3. 将字符串转换成NSData
    NSString *urlStr = strUrl;//测试二维码地址,次二维码不能支付,需要配合服务器来二维码的地址(跟后台人员配合)
    NSData *data = [urlStr dataUsingEncoding:NSUTF8StringEncoding];
    // 4. 通过KVO设置滤镜inputMessage数据
    [filter setValue:data forKey:@"inputMessage"];
    
    // 5. 获得滤镜输出的图像
    CIImage *outputImage = [filter outputImage];
    return [GUITool createNonInterpolatedUIImageFormCIImage:outputImage withSize:200];//重绘二维码,使其显示清晰
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
