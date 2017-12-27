//
//  BeaconManager.h
//  NexenSDK
//
//  Created by Axel Jonckheere on 14/07/17.
//
//

#import <NXBeaconSDK/NXProximityManager.h>

NS_ASSUME_NONNULL_BEGIN

@interface BeaconManager : NSObject

@property (nonatomic) BOOL hasBeenBootstrapped;
@property (nonatomic, strong) NXProximityManager *proximityManager;
@property (nonatomic, strong) NSString *username;
@property (nonatomic, strong) NSString *password;

+ (instancetype)sharedInstance;
- (void)bootstrapWithName:(NSString *)name withPassword:(NSString *)password;
- (void)start:(void (^)(NSError *))completionBlock;
- (void)stop;
- (void)disableCategories:(NSArray<NSNumber *>*)categoriesToDisable;
- (void)setTags:(NSDictionary *)tags;

@end

NS_ASSUME_NONNULL_END
