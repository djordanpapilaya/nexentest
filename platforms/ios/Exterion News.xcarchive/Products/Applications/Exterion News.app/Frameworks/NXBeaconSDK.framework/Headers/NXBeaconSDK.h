
#import <UIKit/UIKit.h>
#import <NXBeaconSDK/NXSettingsManager.h>
#import <NXBeaconSDK/NXProximityManager.h>
#import <NXBeaconSDK/NXProximityZone.h>
#import <NXBeaconSDK/NXProximityZoneContent.h>
#import <NXBeaconSDK/NXProximityZoneContentCategory.h>
#import <NXBeaconSDK/NXProximityZoneContentError.h>
#import <NXBeaconSDK/NXProximityZoneType.h>
#import <NXBeaconSDK/NXFollowTrackProximityZoneContent.h>
#import <NXBeaconSDK/NXHTMLProximityZoneContent.h>
#import <NXBeaconSDK/NXTextPushProximityZoneContent.h> 
#import <NXBeaconSDK/NXVideoProximityZoneContent.h>
#import <NXBeaconSDK/NXLocation.h>

FOUNDATION_EXPORT double BeaconSDKVersionNumber;

FOUNDATION_EXPORT const unsigned char BeaconSDKVersionString[];

static NSString const *BDYGeofenceTag = @"geofence-tag";

#define SYSTEM_VERSION_EQUAL_TO(v)                  ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] == NSOrderedSame)
#define SYSTEM_VERSION_GREATER_THAN(v)              ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] == NSOrderedDescending)
#define SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(v)  ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] != NSOrderedAscending)
#define SYSTEM_VERSION_LESS_THAN(v)                 ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] == NSOrderedAscending)
#define SYSTEM_VERSION_LESS_THAN_OR_EQUAL_TO(v)     ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] != NSOrderedDescending)


// In this header, you should import all the public headers of your framework using statements like #import <NXBeaconSDK/PublicHeader.h>


