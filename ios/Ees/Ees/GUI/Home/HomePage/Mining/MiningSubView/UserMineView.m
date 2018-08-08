//
//  UserMineView.m
//  Ees
//
//  Created by xiaodong on 2018/4/7.
//  Copyright © 2018年 xiaodong. All rights reserved.
//

#import "UserMineView.h"


@interface UserMineView ()

@property(nonatomic,weak) IBOutlet UIImageView *imageView;


@end



@implementation UserMineView

- (void)awakeFromNib {
    [super awakeFromNib];
    self.userInteractionEnabled = YES;
    UITapGestureRecognizer *gesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(userMineTapGesture)];
    [self addGestureRecognizer:gesture];
}


- (void)userMineTapGesture {
    [[NSNotificationCenter defaultCenter] postNotificationName:kClickUserMineView object:nil];
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
