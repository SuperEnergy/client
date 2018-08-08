//
//  MessageTipCell.m
//  Ees
//
//  Created by xiaodong on 2018/3/27.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import "MessageTipCell.h"

@interface MessageTipCell ()

@property(nonatomic,weak) IBOutlet UIView *cellBgView;
@property(nonatomic,weak) IBOutlet UILabel *messageLabel;
@property(nonatomic,weak) IBOutlet UIButton *closeBtn;

@end





@implementation MessageTipCell

- (void)awakeFromNib {
    [super awakeFromNib];
    
    self.closeBtn.imageEdgeInsets = kImageButtonEdgeInsets;
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
    
    if ([self.delegate respondsToSelector:@selector(messageStateDidChange)]) {
        [self.delegate messageStateDidChange];
    }
}

-(void)setModel:(NoticeModel *)model {
    
    _model = model;
    dispatch_async(dispatch_get_main_queue(), ^{
        self.messageLabel.text = model.title;
    });
    
    
}

@end
