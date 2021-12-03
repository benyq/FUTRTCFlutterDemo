//
//  FUBaseComsumer.m
//  BeautifyExample
//
//  Created by Chen on 2021/4/25.
//  Copyright © 2021 Agora. All rights reserved.
//

#import "FUBaseViewModel.h"
#import <FURenderKit/FURenderKit.h>

@implementation FUBaseViewModel
+ (instancetype)instanceViewModel {
    FUBaseViewModel *consumer = [[[self class] alloc] init];
    consumer.switchIsOn = YES;
    return consumer;
}

//加载对应子类的provider
- (void)loadProvider:(FUBaseNodeModelProvider *)provider {
    
}

//通过索引设置
- (void)consumerWithDataIndex:(int)index viewModelBlock:(ViewModelBlock _Nullable)ViewModelBlock {
    NSArray *dataSource = (NSArray *)self.provider.dataSource;
    if (index < dataSource.count) {
        [self consumerWithData:dataSource[index] viewModelBlock:ViewModelBlock];
    } else {
        NSLog(@"%@,%s: 数组越界",self,__func__);
    }
}

- (void)consumerWithData:(id)model viewModelBlock:(ViewModelBlock _Nullable)ViewModelBlock {
    NSLog(@"抽象类空实现, 需要具体业务类处理业务");
}

//加载到FURenderKit 渲染
- (void)addToRenderLoop {
 
}

//移除
- (void)removeFromRenderLoop {
    
}

//开始生效
- (void)startRender {
    
}

//不生效
- (void)stopRender {
    
}

- (void)resetMaxFacesNumber {
}

@end
