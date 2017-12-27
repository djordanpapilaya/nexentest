
#import <Foundation/Foundation.h>
@class NXProximityZoneContent;

@protocol NXProximityZone <NSObject>
@property(nonatomic, copy) NSString * identifier;
@property(nonatomic,copy) NSString * name;
@property(nonatomic,copy) NSString * refId;
@property(nonatomic,readonly) NXProximityZoneContent * content;
@property (nonatomic, readonly) NSInteger contentType;
@end
