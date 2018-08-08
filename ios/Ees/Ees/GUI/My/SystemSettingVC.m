//
//  SystemSettingVC.m
//  Ees
//
//  Created by xiaodong on 2018/2/5.
//  Copyright © 2018年 xiaodong. All rights reserved.
//


#define kCellTitle @"收取提醒"
#define kCellTitle2 @"关于我们"
#define kCellTitle3 @"版本编号"

#define kIconLabelCellHigh  60

#import "SystemSettingVC.h"

#pragma mark-
#pragma mark-----CellItem---

@interface SystemSettingItem : NSObject

@property(nonatomic,strong) NSString *title;
@property(nonatomic,strong) NSString *identifier;
@property(nonatomic,strong) NSString *imageName;
@property(nonatomic,assign) SEL selector;
@property(nonatomic,assign) float high;

@end

@implementation SystemSettingItem

+ (instancetype)settingWithTitle:(NSString *)title identifier:(NSString *)identifier high:(float)high imageName:(NSString *)imageName selector:(SEL)selector
{
    SystemSettingItem *item = [[self class] new];
    item.title = title;
    item.identifier = identifier;
    item.imageName = imageName;
    item.selector = selector;
    item.high = high;
    return item;
}

@end


@interface SystemSettingVC ()

@property(nonatomic,weak) IBOutlet UITableView *tableView;
@property(nonatomic,strong) NSArray<SystemSettingItem *> *sourceArray;

@end

@implementation SystemSettingVC

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.sourceArray =
    @[[SystemSettingItem settingWithTitle:kCellTitle identifier:@"NotificationCell" high:kIconLabelCellHigh imageName:@"ic_notification.png" selector:nil],
      [SystemSettingItem settingWithTitle:kCellTitle2 identifier:@"IconLabelCell" high:kIconLabelCellHigh imageName:@"ic_about.png" selector:@selector(aboutMeAction)],
      [SystemSettingItem settingWithTitle:kCellTitle3 identifier:@"AppVersionCell" high:kIconLabelCellHigh imageName:@"icon.png" selector:nil],
      ];
    
    
    
    self.automaticallyAdjustsScrollViewInsets = NO;
    self.title = @"系统设置";
    self.view.backgroundColor = kWhiteColor;
    
    
    [self.tableView registerNib:[UINib nibWithNibName:@"NotificationCell" bundle:nil] forCellReuseIdentifier:@"NotificationCell"];
    [self.tableView registerNib:[UINib nibWithNibName:@"IconLabelCell" bundle:nil] forCellReuseIdentifier:@"IconLabelCell"];
    [self.tableView registerNib:[UINib nibWithNibName:@"AppVersionCell" bundle:nil] forCellReuseIdentifier:@"AppVersionCell"];
    
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.tableView.backgroundColor = kViewBackgroundColor;
    self.tableView.showsVerticalScrollIndicator = NO;
    self.tableView.contentInset = UIEdgeInsetsMake(-5, 0, 0, 0);
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark-
#pragma mark-----private---



#pragma mark-
#pragma mark-----Event---

- (void)aboutMeAction
{
    UIStoryboard *board = self.storyboard;
    WebViewVC *ctrl = [board instantiateViewControllerWithIdentifier:@"WebViewVC"];
    ctrl.url = kAboutUrl;
    ctrl.title = kCellTitle2;
    ctrl.isLinkAction = YES;
    [self.navigationController pushViewController:ctrl animated:YES];
}

#pragma mark-
#pragma mark-----tabelView delegate---

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
    SystemSettingItem *item = self.sourceArray[indexPath.row];
    return item.high;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    SystemSettingItem *item = self.sourceArray[indexPath.row];
    NSString *identifier = item.identifier;
    NSString *title = item.title;
    NSString *imageName = item.imageName;
    
    
    UITableViewCell *cell;
    UITableViewCellAccessoryType accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    UIColor *color = kClearColor;
    
    if ([title isEqualToString:kCellTitle3]) {
        AppVersionCell *iconCell = [tableView dequeueReusableCellWithIdentifier:identifier];
        iconCell.label.text = title;
        iconCell.iconImageView.image = [UIImage imageNamed:imageName];
        cell = iconCell;
        accessoryType = UITableViewCellAccessoryNone;
    } else if ([title isEqualToString:kCellTitle]) {
        NotificationCell *timeCell = [tableView dequeueReusableCellWithIdentifier:identifier];
        timeCell.label.text = title;
        timeCell.iconImageView.image = [UIImage imageNamed:imageName];
        cell = timeCell;
        accessoryType = UITableViewCellAccessoryNone;
    } else {
        IconLabelCell *iconCell = [tableView dequeueReusableCellWithIdentifier:identifier];
        iconCell.cellTitleLabel.text = title;
        iconCell.iconImageView.image = [UIImage imageNamed:imageName];
        cell = iconCell;
    }
    
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    cell.accessoryType = accessoryType;
    cell.backgroundColor = color;
    cell.contentView.backgroundColor = color;
    
    return cell;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    SystemSettingItem *item = self.sourceArray[indexPath.row];
    
#pragma clang diagnostic push
#pragma clang diagnostic ignored   "-Warc-performSelector-leaks"
    if ([self respondsToSelector:item.selector]) {
        [self performSelector:item.selector];
    }
#pragma clang diagnostic pop
    
}


@end
