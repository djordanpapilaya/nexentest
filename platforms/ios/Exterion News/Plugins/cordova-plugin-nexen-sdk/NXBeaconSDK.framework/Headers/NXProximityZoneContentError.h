
#import <Foundation/Foundation.h>

@interface NXProximityZoneContentError : NSError
+(NXProximityZoneContentError *)errorWithDescription:(NSString *)description uuid:(NSString *)UUIDString;
@end
