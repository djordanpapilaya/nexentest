
#import <Foundation/Foundation.h>
@class NXPrivateBeacon;
@class UILocalNotification;
@class NXSettingsManager;
@class NXLocation;
#import "NXBeaconSDK.h"
#define CONSTANT_MAX_RANGE @"6"

@import UIKit;
#import "NXProximityZone.h"
#define NXProximityManagerTriggerContentNotification @"NXProximityManagerTriggerContentNotification"

#define NXProximityManagerLocationsUpdatedNotification @"NXProximityManagerLocationsUpdatedNotification"
#define NXProximityManagerProximityZonesForLocationUpdatedNotification @"NXProximityManagerProximityZonesForLocationUpdatedNotification"

#define NSProximityLocalTotificationIdentifierKey @"beaconIdentifier"
#define NSProximityLocalTotificationContTypeKey @"contentType"


@interface NXProximityManager : NSObject
@property(nonatomic,readonly) NXSettingsManager * settingsManager;
@property(nonatomic,readonly) NSArray<NXLocation *> * locations;
//@property(nonatomic,readonly) NSMutableArray *currentBeaconIDS;
@property(nonatomic,readonly) BOOL started;
@property(nonatomic, readonly) BOOL starting;
@property (nonatomic, readonly) NSArray <NXProximityZone> *enteredZones;

//@property (nonatomic, strong) NSMutableArray *geofenceArray;
//@property (nonatomic) BOOL verboseContextHubLogging;
+(void)enableLogging:(BOOL)enable;
-(void)retry;
+(BOOL)isNotificationForDwellingProximityZone:(UILocalNotification *)notification;
+(NXLocation *)locationFromNotification:(NSNotification *)notification;
+(NSError *)errorFromNotification:(NSNotification *)notification;
+(NSObject<NXProximityZone> *)proximityZoneFromNotification:(NSNotification *)notification;

-(id)initWithUsername:(NSString *)username
             password:(NSString *)password
             url:(NSURL *)url
             locale:(NSLocale *)locale;

-(void)setLastLocationsRefreshDate:(NSDate *)lastLocationsRefreshDate;
-(void)startLookingForNewGeoFence;
-(void)setErrorHappened:(BOOL )errorHappened;
-(BOOL )errorHappened;
-(void)start:(void (^)(NSError *))completionBlock;
-(void)reset; // you cannot reuse the instance after calling this!

-(void)performFetchWithCompletionHandler:(void (^)(UIBackgroundFetchResult result))completionHandler;
-(void)handleEventsForBackgroundURLSession:(NSString *)identifier completionHandler:(void (^)())completionHandler;

-(void)incrementLoyaltyForProximityZone:(NSObject<NXProximityZone> *)zone;
-(NSObject<NXProximityZone> *)proximityZoneForIdentifier:(NSString *)identifier andContentType:(NSInteger)contType;

-(void)resetGeoFencing;
-(void)refreshLocations:(NSTimer *)timer;
-(void)reloadBeacons;
/**
 * run local notification for provided zone content
 *
 * zone id, and content type could be found in userInfo dictionary
 */
- (void)showNotificationForProximityZone:(NSObject<NXProximityZone> *)zone;

@end
