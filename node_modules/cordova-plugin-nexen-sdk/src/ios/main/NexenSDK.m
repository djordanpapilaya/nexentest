//
//  NexenSDK.m
//  NexenSDK
//
//  Created by Axel Jonckheere on 14/07/17.
//
//

#import "NexenSDK.h"
#import "BeaconManager.h"
#import "AppDelegate+NexenSDK.h"

@implementation NexenSDK {
    CLLocationManager *_locationManager;
    CBCentralManager *_btManager;
}

#pragma mark - Init

-(void)pluginInitialize {
    [super pluginInitialize];

    _locationManager = [CLLocationManager new];
    _btManager = [[CBCentralManager alloc] initWithDelegate:self queue:nil];
    _callbackIds = [NSMutableDictionary new];

    [self subscribe];
    [NXProximityManager enableLogging:YES];
    _locationManager.delegate = self;
}

#pragma mark - Calls exposed for Cordova

- (void)startScanning:(CDVInvokedUrlCommand*)command {

    // Store the callbackID
    [_callbackIds setObject:command.callbackId forKey:NXCordovaStartCallbackKey];

    // Parse the options
    [self parseOptions:[command.arguments objectAtIndex:0]];

    // Permission was denied, we will not be able to scan...
    if ([CLLocationManager authorizationStatus] == kCLAuthorizationStatusDenied) {
        CDVPluginResult *errorResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"No permission granted"];
        [self.commandDelegate sendPluginResult:errorResult callbackId:command.callbackId];
        return;
    }

    // Ask location permission
    if ([CLLocationManager authorizationStatus] == kCLAuthorizationStatusNotDetermined) {
        [_locationManager requestAlwaysAuthorization];
    } else {
        _locationManager = nil; // We only need the locationmanager for asking permissions in this class
        [self startAfterPermissionsGranted];
    }
}

- (void)startAfterPermissionsGranted {
    [self.commandDelegate runInBackground:^{

        // Apply options
        [[BeaconManager sharedInstance] setTags:_tags];
        [[BeaconManager sharedInstance] disableCategories:_disabledCategories];

        // Start scanning for beacons
        [[BeaconManager sharedInstance] start:^(NSError * _Nonnull error) {
            CDVPluginResult *result;

            // Send an error result
            if (error) {
                result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"An error occurred"];
                [self.commandDelegate sendPluginResult:result callbackId:[_callbackIds objectForKey:NXCordovaStartCallbackKey]];
                return;
            }

            // Send the result
            result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
            [self.commandDelegate sendPluginResult:result callbackId:[_callbackIds objectForKey:NXCordovaStartCallbackKey]];

            NSLog(@"The NexenSDK started scanning");
        }];
    }];
}

- (void)stopScanning:(CDVInvokedUrlCommand*)command {

    [self.commandDelegate runInBackground:^{

        // Store the callbackID
        [_callbackIds setObject:command.callbackId forKey:NXCordovaStopCallbackKey];

        // Stop scanning for beacons
        [[BeaconManager sharedInstance] stop];

        // Send the result
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

    }];
}

- (void)onProximityZoneNotification:(CDVInvokedUrlCommand*)command {

    // Store the callbackID
    [_callbackIds setObject:command.callbackId forKey:NXCordovaOnProximityZoneNotificationCallbackKey];
}

- (void)onLocationsLoaded:(CDVInvokedUrlCommand*)command {

    // Store the callbackID
    [_callbackIds setObject:command.callbackId forKey:NXCordovaOnLocationsLoadedCallbackKey];
}

- (void)onPushNotificationTappedForZone:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{

        // Store the callbackID
        [_callbackIds setObject:command.callbackId forKey:NXCordovaOnProximityZoneNotificationCallbackKey];

        // Get the ID passed by Cordova
        NSString *beaconId = [command.arguments objectAtIndex:0];
        NSInteger contentTypeId = (NSInteger) [command.arguments objectAtIndex:1];

        NSObject<NXProximityZone> *zone = [[[BeaconManager sharedInstance] proximityManager] proximityZoneForIdentifier:beaconId andContentType:contentTypeId];

        // If the zone was not found return an error result
        if (!zone) {
            CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Zone does not exist"];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            return;
        }

        // Increment the loyalty
        [[[BeaconManager sharedInstance] proximityManager] incrementLoyaltyForProximityZone:zone];

        // Send the result
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

- (void)onOpenUrl:(CDVInvokedUrlCommand*)command {
    // Store the callbackID
    [_callbackIds setObject:command.callbackId forKey:NXCordovaOnOpenUrlCallbackKey];
}

#pragma mark - Subscribe/unsubscribe

- (void)subscribe {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onNXProximityManagerLocationsUpdatedNotification:) name:NXProximityManagerLocationsUpdatedNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onNXProximityManagerTriggerContentNotification:) name:NXProximityManagerTriggerContentNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onNXDidReceiveLocalNotification:) name:NXDidReceiveLocalNotification object:nil];
}

- (void)unsubscribe {
    [[NSNotificationCenter defaultCenter] removeObserver:self name:NXProximityManagerLocationsUpdatedNotification object:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:NXProximityManagerTriggerContentNotification object:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:NXDidReceiveLocalNotification object:nil];
}

#pragma mark - Beacon Notifications

- (void)onNXProximityManagerLocationsUpdatedNotification:(NSNotification *)notification {

    NSMutableArray *result = [NSMutableArray new];
    NXProximityManager *proximityManager =(NXProximityManager *) notification.object;

    for (NXLocation *location in proximityManager.locations) {
        NSDictionary *locationEntry = [[NSDictionary alloc] initWithObjectsAndKeys:
                                  [[NSNumber alloc]initWithFloat:location.latitude], @"latitude",
                                  [[NSNumber alloc]initWithFloat:location.longitude], @"longitude",
                                  location.identifier, @"id",
                                  location.name, @"name",
                                  nil];

        [result addObject:locationEntry];
    }

    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:result];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:YES]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:[_callbackIds objectForKey:NXCordovaOnLocationsLoadedCallbackKey]];
}

- (void)onNXProximityManagerTriggerContentNotification:(NSNotification *)notification {

    NSObject<NXProximityZone> *zone = [NXProximityManager proximityZoneFromNotification:notification];
    [zone.content trigger];

    if (_shouldShowNotification) {
        [[[BeaconManager sharedInstance] proximityManager] showNotificationForProximityZone:zone];
    }

    NSDictionary *result = [[NSDictionary alloc] initWithObjectsAndKeys:
                            zone.identifier, @"id",
                            zone.name, @"name",
                            zone.refId, @"refId",
                            zone.content.identifier, @"contentIdentifier",
                            nil];

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:result];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:YES]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:[_callbackIds objectForKey:NXCordovaOnProximityZoneNotificationCallbackKey]];
}

#pragma mark - Local Notification tapped event
- (void)onNXDidReceiveLocalNotification:(NSNotification *)notification {

    UILocalNotification *localNotification = (UILocalNotification *)notification.object;

    // Get metadata from the notification
    NSString *identifier = localNotification.userInfo[NSProximityLocalTotificationIdentifierKey ];
    NSNumber *contentType = localNotification.userInfo[NSProximityLocalTotificationContTypeKey];

    // Increment loyalty for the beacon
    NSObject<NXProximityZone> *zone = [[[BeaconManager sharedInstance] proximityManager] proximityZoneForIdentifier:identifier andContentType:[contentType integerValue]];
    [[[BeaconManager sharedInstance] proximityManager] incrementLoyaltyForProximityZone:zone];

    // Send an URL to Cordova so it can be opened in the app
    if ([zone.content isKindOfClass:[NXHTMLProximityZoneContent class]]) {
        NXHTMLProximityZoneContent *zoneContent = (NXHTMLProximityZoneContent *) zone.content;
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:zoneContent.pageUrl];
        [pluginResult setKeepCallback:[NSNumber numberWithBool:YES]];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:[_callbackIds objectForKey:NXCordovaOnOpenUrlCallbackKey]];
    }
}

#pragma mark - CLLocationManager Delegate

-(void)locationManager:(CLLocationManager *)manager didChangeAuthorizationStatus:(CLAuthorizationStatus)status {
    if (status == kCLAuthorizationStatusAuthorizedAlways) {
        [self startAfterPermissionsGranted];
    } else if (status == kCLAuthorizationStatusDenied) {
        CDVPluginResult *errorResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"No permission granted"];
        [self.commandDelegate sendPluginResult:errorResult callbackId:[_callbackIds objectForKey:NXCordovaStartCallbackKey]];
        return;
    }
}

#pragma mark - CBCentralManager Delegate

- (void)centralManagerDidUpdateState:(CBCentralManager *)central {
    // No code needed here, iOS will ask for BT permission as long as CoreBluetooth is bootstrapped
}

#pragma mark - Helpers

- (void)parseOptions:(NSDictionary *)options {

    if ([options objectForKey:@"notifications"]) {
        NSDictionary *notificationsOptions = [options objectForKey:@"notifications"];
        if ([notificationsOptions objectForKey:@"showNotifications"]) {
            NSNumber *value = (NSNumber *) [notificationsOptions objectForKey:@"showNotifications"];
            _shouldShowNotification = [value boolValue];
        }
    }

    if ([options objectForKey:@"disabledCategories"]) {
        _disabledCategories = [options objectForKey:@"disabledCategories"];
    }

    if ([options objectForKey:@"tags"]) {
        _tags = [options objectForKey:@"tags"];
    }
}

#pragma mark - dealloc

-(void)dealloc {
    [self unsubscribe];
}

@end
