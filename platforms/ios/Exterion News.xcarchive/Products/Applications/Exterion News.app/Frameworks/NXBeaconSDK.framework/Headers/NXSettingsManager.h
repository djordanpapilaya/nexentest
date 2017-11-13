
#import <Foundation/Foundation.h>
#import "NXProximityZoneContentCategory.h"

@class NXNetwork;
@class NXProximityZoneContentCategory;

typedef void(^NXSettingsManagerCompletionBlock)(NSError * error);

@interface NXSettingsManager : NSObject
@property(nonatomic,readonly) NSArray<NXProximityZoneContentCategory *> * contentCategories;
@property (nonatomic, readonly) NSArray<NXProximityZoneContentCategory *> *disabledCategories;
@property (nonatomic, readonly) NSDictionary *tags;

-(id)initWithNetwork:(NXNetwork *)network;

-(void)refresh:(NXSettingsManagerCompletionBlock)completionBlock;

-(void)newContentLoadedWithType:(NSInteger)type content:(id)content;
-(void)newContentLoadErrorWithType:(NSInteger)type andError:(NSError *)error;

-(BOOL)contentCategoryEnabled:(NXProximityZoneContentCategory *)category;
-(BOOL)contentCategoryEnabledForIdentifier:(NSString *)categoryIdentifier;
-(void)enableContentCategory:(NXProximityZoneContentCategory *)category enabled:(BOOL)enabled;

- (void)setTag:(NSString *)tagName withValue:(NSString *)value;
- (void)deleteTag:(NSString *)tagName;
- (void)clearTags;

@end
