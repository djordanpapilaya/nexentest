//
//  NSBundle+plist.m
//  NexenSDK
//
//  Created by Axel Jonckheere on 14/07/17.
//
//

#import "NSBundle+plist.h"

@implementation NSBundle (plist)

+ (NSString *)getStringFromMainPlistWithKey:(NSString *)key {
    NSBundle* mainBundle = [NSBundle mainBundle];
    return (NSString *) [mainBundle objectForInfoDictionaryKey:key];
}

@end
