//
//  NoticeTipCell.m
//  Ees
//
//  Created by xiaodong on 2018/3/27.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import "NoticeTipCell.h"

@interface NoticeTipCell ()

@property(nonatomic,weak) IBOutlet UIView *cellBgView;
@property(nonatomic,weak) IBOutlet UILabel *titleLabel;
@property(nonatomic,weak) IBOutlet UIImageView *imageview1;
@property(nonatomic,weak) IBOutlet UIButton *closeBtn;

@end


@implementation NoticeTipCell

- (void)awakeFromNib {
    [super awakeFromNib];
    
    self.closeBtn.imageEdgeInsets = kImageButtonEdgeInsets;
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}


#pragma mark-
#pragma mark-----Button---
- (IBAction)closeMessage:(id)sender
{
    if (self.model) {
        NSString *noticeId = self.model.id;
        NSUserDefaults *userDef = [NSUserDefaults standardUserDefaults];
        [userDef setObject:@"isRead" forKey:noticeId];
        [userDef synchronize];
    }
    
    if ([self.delegate respondsToSelector:@selector(noticeStateDidChange)]) {
        [self.delegate noticeStateDidChange];
    }
}


-(void)setModel:(NoticeModel *)model {
    
    _model = model;
    dispatch_async(dispatch_get_main_queue(), ^{
        self.titleLabel.text = model.title;
        NSURL *url = [NSURL URLWithString:model.cover];
        [self.imageview1 sd_setImageWithURL:url
                          placeholderImage:[UIImage imageNamed:@"login_icon.png"]
                                 completed:^(UIImage * _Nullable image, NSError * _Nullable error, SDImageCacheType cacheType, NSURL * _Nullable imageURL) {
                                     if (image) {
                                         dispatch_async(dispatch_get_main_queue(), ^{
                                             UIImage *newImage = [image getSubImageWithImageView:self.imageview1];
                                             if (newImage) {
                                                 self.imageview1.image = newImage;
                                             } else {
                                                 self.imageview1.image = image;
                                             }
                                         });
                                     }
                                 }];
    });
    
    
}



@end
