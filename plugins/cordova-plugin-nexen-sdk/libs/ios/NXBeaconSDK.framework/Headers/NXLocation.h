
#import <Foundation/Foundation.h>
#import <MapKit/MapKit.h>
@interface NXLocation : NSObject<MKAnnotation>
@property(nonatomic,copy) NSString * identifier;
@property(nonatomic,copy) NSString * name;
@property(nonatomic,assign) float latitude;
@property(nonatomic,assign) float longitude;
@property(nonatomic,assign) NSTimeInterval refreshInterval;
@property(nonatomic,strong) NSDate * zonesSyncedOn;


@end
