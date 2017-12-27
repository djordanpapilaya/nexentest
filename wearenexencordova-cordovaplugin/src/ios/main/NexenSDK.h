//
//  NexenSDK.h
//  NexenSDK
//
//  Created by Axel Jonckheere on 14/07/17.
//
//

#import <Cordova/CDV.h>
@import CoreLocation;
@import CoreBluetooth;

NS_ASSUME_NONNULL_BEGIN

@interface NexenSDK : CDVPlugin <CLLocationManagerDelegate, CBCentralManagerDelegate>

#define NXCordovaStartCallbackKey @"NXCordovaStartCallbackKey"
#define NXCordovaStopCallbackKey @"NXCordovaStopCallbackKey"
#define NXCordovaOnProximityZoneNotificationCallbackKey @"NXCordovaOnProximityZoneNotificationCallbackKey"
#define NXCordovaOnLocationsLoadedCallbackKey @"NXCordovaOnLocationsLoadedCallbackKey"
#define NXCordovaOnPushNotificationTappedForZoneCallbackKey @"NXCordovaOnPushNotificationTappedForZoneCallbackKey"
#define NXCordovaOnOpenUrlCallbackKey @"NXCordovaOnOpenUrlCallbackKey"

@property (nonatomic) BOOL shouldShowNotification;
@property (nonatomic, strong) NSArray *disabledCategories;
@property (nonatomic, strong) NSDictionary *tags;
@property (nonatomic, strong) NSMutableDictionary *callbackIds;

- (void)startScanning:(CDVInvokedUrlCommand*)command;
- (void)stopScanning:(CDVInvokedUrlCommand*)command;
- (void)onProximityZoneNotification:(CDVInvokedUrlCommand*)command;
- (void)onLocationsLoaded:(CDVInvokedUrlCommand*)command;
- (void)onOpenUrl:(CDVInvokedUrlCommand*)command;
- (void)onPushNotificationTappedForZone:(CDVInvokedUrlCommand*)command;

@end

NS_ASSUME_NONNULL_END
