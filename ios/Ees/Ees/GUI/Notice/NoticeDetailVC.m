//
//  NoticeDetailVC.m
//  Ees
//
//  Created by KCMac on 2017/12/19.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import "NoticeDetailVC.h"

@interface NoticeDetailVC ()

@property(nonatomic,weak) IBOutlet UIWebView *webView;

@end

@implementation NoticeDetailVC


#pragma mark-
#pragma mark-----overide---
- (void)viewDidLoad {
    [super viewDidLoad];
    self.automaticallyAdjustsScrollViewInsets = NO;
    
    if (self.isLinkAction) {
        if (self.link) {
            [self doLinkAction];
            
            //右边按钮
            UIButton *rightBtn = [UIButton buttonWithType:UIButtonTypeCustom];
            rightBtn.frame = CGRectMake(0, 0, 24, 24);
            [rightBtn setBackgroundImage:[UIImage imageNamed:@"ic_browser.png"] forState:UIControlStateNormal];
            [rightBtn addTarget:self action:@selector(openBrowser) forControlEvents:UIControlEventTouchUpInside];
            UIBarButtonItem *rightItem = [[UIBarButtonItem alloc] initWithCustomView:rightBtn];
            
            self.navigationItem.rightBarButtonItems = @[rightItem];
        }
    } else {
        [self getNoticeDdetail];
    }
    
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


#pragma mark-
#pragma mark-----private---

- (void)getNoticeDdetail
{
    NSString *pid = [UserInfoManager sharedInstance].userItem.pid;
    NSString *token = [UserInfoManager sharedInstance].userItem.token;
    
    
    NSDictionary *dic = @{@"secret":kSecret,@"pid":pid,@"id":self.noticeId,@"token":token};
    
    [HttpRequestEngin noticeDetailWithParam:dic successBlock:^(id  _Nullable responseObject) {
        RespondModel *resModel = [[RespondModel alloc] initObjectWithDictionary:responseObject objectClass:[NoticeModel class] objectError:nil];
        if (resModel.success) {
            NoticeModel *model = resModel.object;
            
            
            dispatch_async(dispatch_get_main_queue(), ^{
                 [self.webView loadHTMLString:model.content baseURL:nil];
            });
        } else {
            ErrorModel *model = resModel.error;
            ErrorCodeEvent *event = [[ErrorCodeEvent alloc] initWithError:model];
            [event errorCodeDone:self needLoading:YES];
        }
        
        
    } failBlock:^(NSError * _Nonnull error) {
        
    } progress:^(NSProgress * _Nonnull uploadProgress) {
        
    }];
}

- (void)doLinkAction {
    
    NSURL *url = [self getUrl];
    NSURLRequest* request = [NSURLRequest requestWithURL:url];
    [self.webView loadRequest:request];
}

- (void)openBrowser {
    NSURL *url = [self getUrl];
    [[UIApplication sharedApplication] openURL:url];
}

- (NSURL *)getUrl {
    UserInfoManager *userMg = [UserInfoManager sharedInstance];
    NSString *pidstr = [NSString stringWithFormat:@"pid=%@",userMg.userItem.pid];
    NSString *tokenstr = [NSString stringWithFormat:@"token=%@",userMg.userItem.token];
    NSString *secretstr = [NSString stringWithFormat:@"secret=%@",kSecret];
    NSString *idstr = [NSString stringWithFormat:@"id=%@",userMg.userItem.id];
    
    self.link = [self.link stringByReplacingOccurrencesOfString:@"pid={pid}" withString:pidstr];
    self.link = [self.link stringByReplacingOccurrencesOfString:@"token={token}" withString:tokenstr];
    self.link = [self.link stringByReplacingOccurrencesOfString:@"secret={secret}" withString:secretstr];
    self.link = [self.link stringByReplacingOccurrencesOfString:@"id={id}" withString:idstr];
    NSURL *url = [NSURL URLWithString:self.link];
    return url;
}

@end
