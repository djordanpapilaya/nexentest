//
//  BeaconManager.m
//  NexenSDK
//
//  Created by Axel Jonckheere on 14/07/17.
//
//

#import "BeaconManager.h"

@implementation BeaconManager

#pragma mark - Init

+ (instancetype)sharedInstance {
    static BeaconManager *beaconManager = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        beaconManager = [[self alloc] init];
    });
    return beaconManager;
}

#pragma mark - Managing the Nexen SDK

- (void)bootstrapWithName:(NSString *)name withPassword:(NSString *)password {
    _username = name;
    _password = password;
    _proximityManager = [[NXProximityManager alloc] initWithUsername:_username password:_password url:nil locale:[NSLocale currentLocale]];
    _hasBeenBootstrapped = YES;
}

- (void)start:(void (^)(NSError *))completionBlock {
    dispatch_sync(dispatch_get_main_queue(),^ {

        if (!_hasBeenBootstrapped) {
            [self bootstrapWithName:_username withPassword:_password];
        }

        [_proximityManager start:completionBlock];
    });
}

- (void)stop {
    dispatch_sync(dispatch_get_main_queue(),^ {
        [_proximityManager reset];
        _hasBeenBootstrapped = NO;
    });
}

#pragma mark - Filters
- (void)disableCategories:(NSArray<NSNumber *>*)categoriesToDisable {
    for (NXProximityZoneContentCategory *category in _proximityManager.settingsManager.contentCategories) {
        for (NSNumber *disabledCategory in categoriesToDisable) {
            if ([category.identifier isEqualToString:[disabledCategory stringValue]]) {
                if ([_proximityManager.settingsManager contentCategoryEnabledForIdentifier: category.identifier]) {
                    [_proximityManager.settingsManager enableContentCategory:category enabled:NO];
                }
            } else {
                if (![_proximityManager.settingsManager contentCategoryEnabledForIdentifier: category.identifier]) {
                    [_proximityManager.settingsManager enableContentCategory:category enabled:YES];
                }
            }
        }
    }
}

- (void)setTags:(NSDictionary *)tags {

    // Clear previously stored tags
    [_proximityManager.settingsManager clearTags];

    // Set all tags in the settings manager
    for (NSString *key in tags) {
        [_proximityManager.settingsManager setTag:key withValue:tags[key]];
    }
}

@end
