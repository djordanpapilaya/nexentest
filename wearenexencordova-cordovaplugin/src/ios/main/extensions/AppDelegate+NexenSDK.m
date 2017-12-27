//
//  AppDelegate+NexenSDK.h
//  NexenSDK
//
//  Created by Axel Jonckheere on 17/07/17.
//
//

#import "AppDelegate+NexenSDK.h"
#import <objc/runtime.h>
#import "BeaconManager.h"
#import "NSBundle+plist.h"

@implementation AppDelegate (NexenSDK)

NSString* const NXDidReceiveLocalNotification = @"NXDidReceiveLocalNotification";

#pragma mark - Load method
// Its dangerous to override a method from within a category.
// Instead we will use method swizzling. we set this up in the load call.
+ (void)load {
    // What we do here is change the init method of the AppDelegate with our own init method.
    // Our init method will be called first, even before the PhoneGap plugin is actually initialized
    [AppDelegate swizzleWithOriginalMethod:@selector(init) withNewSelector:@selector(init_Swizzled) addOriginalMethod:NO];
    [AppDelegate swizzleWithOriginalMethod:@selector(application:performFetchWithCompletionHandler:) withNewSelector:@selector(application:performFetchWithCompletionHandler_Swizzled:) addOriginalMethod:NO];
    [AppDelegate swizzleWithOriginalMethod:@selector(application:handleEventsForBackgroundURLSession:completionHandler:) withNewSelector:@selector(application:handleEventsForBackgroundURLSession_Swizzled:completionHandler:) addOriginalMethod:NO];
    [AppDelegate swizzleWithOriginalMethod:@selector(application:didFinishLaunchingWithOptions:) withNewSelector:@selector(application:didFinishLaunchingWithOptions_Swizzled:) addOriginalMethod:NO];
    [AppDelegate swizzleWithOriginalMethod:@selector(application:didReceiveLocalNotification:) withNewSelector:@selector(application:didReceiveLocalNotification_Swizzled:) addOriginalMethod:YES];
}

#pragma mark - Swizzler

+ (void)swizzleWithOriginalMethod:(SEL)originalSelector withNewSelector:(SEL)newSelector addOriginalMethod:(BOOL)shouldAddOriginalMethod {

    if (shouldAddOriginalMethod) {
        class_addMethod(self, originalSelector, (IMP) defaultMethodIMP, "v@:");
    }

    Method original, swizzled;
    original = class_getInstanceMethod(self, originalSelector);
    swizzled = class_getInstanceMethod(self, newSelector);
    method_exchangeImplementations(original, swizzled);
}

void defaultMethodIMP (id self, SEL _cmd) { /* nothing to do here */ }

#pragma mark - Swizzled implementations

- (AppDelegate *)init_Swizzled {

    // Load name and pass from plist
    NSString *name = [NSBundle getStringFromMainPlistWithKey:@"WeAreNexenAppName"];
    NSString *password = [NSBundle getStringFromMainPlistWithKey:@"WeAreNexenAppPassword"];

    // Bootstrap the beaconmanager if it hasn't been bootstrapped yet
    if (![[BeaconManager sharedInstance] hasBeenBootstrapped]) {
        [[BeaconManager sharedInstance] bootstrapWithName:name withPassword:password];
    }

    return [self init_Swizzled];
}

- (void)application:(UIApplication *)application performFetchWithCompletionHandler_Swizzled:(void(^)(UIBackgroundFetchResult result))completionHandler {
    [[[BeaconManager sharedInstance] proximityManager] performFetchWithCompletionHandler:completionHandler];

    [self application:application performFetchWithCompletionHandler_Swizzled:completionHandler];
}

- (void)application:(UIApplication *)application handleEventsForBackgroundURLSession_Swizzled:(NSString *)identifier completionHandler:(void(^)())completionHandler {
    [[[BeaconManager sharedInstance] proximityManager] handleEventsForBackgroundURLSession:identifier completionHandler:completionHandler];

    [self application:application handleEventsForBackgroundURLSession_Swizzled:identifier completionHandler:completionHandler];
}

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions_Swizzled:(NSDictionary *)launchOptions {
    if ([UIApplication instancesRespondToSelector:@selector(registerUserNotificationSettings:)]){
        [application registerUserNotificationSettings:[UIUserNotificationSettings settingsForTypes:UIUserNotificationTypeAlert|UIUserNotificationTypeBadge|UIUserNotificationTypeSound categories:nil]];
    }

    return [self application:application didFinishLaunchingWithOptions_Swizzled:launchOptions];
}

- (void)application:(UIApplication *)application didReceiveLocalNotification_Swizzled:(UILocalNotification *)notification {

    // Only trigger if a user has opened the notification
    if (application.applicationState != UIApplicationStateInactive && application.applicationState != UIApplicationStateBackground) {
        return;
    }

    // Broadcast an event that the user has opened a notification
    [[NSNotificationCenter defaultCenter] postNotificationName:NXDidReceiveLocalNotification object:notification];

    [self application:application didReceiveLocalNotification_Swizzled:notification];
}

@end
