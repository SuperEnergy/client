//
//  WebViewVC.m
//  Ees
//
//  Created by xiaodong on 2017/12/31.
//  Copyright © 2017年 xiaodong. All rights reserved.
//

#import "WebViewVC.h"

@interface WebViewVC ()

@property(nonatomic,weak) IBOutlet UIWebView *webView;
//返回按钮
@property (nonatomic, strong) UIBarButtonItem *backItem;
//关闭按钮
@property (nonatomic, strong) UIBarButtonItem *closeItem;

@end

@implementation WebViewVC

#pragma mark-
#pragma mark——overide—

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.automaticallyAdjustsScrollViewInsets = NO;
    
    NSURL *telURL = [NSURL URLWithString:self.url];
    [self.webView loadRequest:[NSURLRequest requestWithURL:telURL]];
    
    if (self.isLinkAction) {
        //右边按钮
        UIButton *rightBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        rightBtn.frame = CGRectMake(0, 0, 24, 24);
        [rightBtn setBackgroundImage:[UIImage imageNamed:@"ic_browser.png"] forState:UIControlStateNormal];
        [rightBtn addTarget:self action:@selector(openBrowser) forControlEvents:UIControlEventTouchUpInside];
        UIBarButtonItem *rightItem = [[UIBarButtonItem alloc] initWithCustomView:rightBtn];
        
        self.navigationItem.rightBarButtonItems = @[rightItem];
    }
    
    
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)webViewDidFinishLoad:(UIWebView *)webView
{
    self.title = [webView stringByEvaluatingJavaScriptFromString:@"document.title"];
}

#pragma mark - 添加关闭按钮

- (void)addLeftButton
{
    self.navigationItem.leftBarButtonItem = self.backItem;
}


- (void)createNavigation {
    [self addLeftButton];
}


//点击返回的方法
- (void)backNative
{
    //判断是否有上一层H5页面
    if ([self.webView canGoBack]) {
        //如果有则返回
        [self.webView goBack];
        //同时设置返回按钮和关闭按钮为导航栏左边的按钮
        self.navigationItem.leftBarButtonItems = @[self.backItem, self.closeItem];
    } else {
        [self closeNative];
    }
}

//关闭H5页面，直接回到原生页面
- (void)closeNative
{
    [self.navigationController popViewControllerAnimated:YES];
}

- (UIBarButtonItem *)backItem
{
    if (!_backItem) {
        _backItem = [[UIBarButtonItem alloc] init];
        UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
        [btn setBackgroundImage:[UIImage imageNamed:@"back_icon.png"] forState:UIControlStateNormal];
        [btn addTarget:self action:@selector(backNative) forControlEvents:UIControlEventTouchUpInside];
        btn.contentEdgeInsets = UIEdgeInsetsMake(0, -5, 0, 0);
        btn.frame = CGRectMake(0, 0, 24, 24);
        _backItem.customView = btn;
    }
    return _backItem;
}

- (UIBarButtonItem *)closeItem
{
    if (!_closeItem) {
        _closeItem = [[UIBarButtonItem alloc] initWithTitle:@"关闭" style:UIBarButtonItemStylePlain target:self action:@selector(closeNative)];
        _closeItem.tintColor = [UIColor grayColor];
    }
    return _closeItem;
}


#pragma mark-
#pragma mark-----private---

- (void)openBrowser {
    NSURL *url = [NSURL URLWithString:self.url];
    [[UIApplication sharedApplication] openURL:url];
}

@end
