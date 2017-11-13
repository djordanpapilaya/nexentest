# WeAreNexen Cordova Plugin
## Adding the plugin to your project

To add the plugin to your project, first download the plugin and put it aside your project.
Then add the plugin by running the following command:

    cordova plugin add <path_to_plugin>  --variable APP_PASSWORD="<password>" --variable APP_NAME="<name>" --variable LOCATION_USAGE_DESCRIPTION="This app uses your location to track beacons." --variable BLUETOOTH_USAGE_DESCRIPTION="Please allow to use Bluetooth to start beacon monitoring"

* The app name & password are provided by WeAreNexen and are used to identify your app.
* The location/bluetooth usage description is required by iOS.

## Plugin location

The plugin can be found in `window.plugins.NexenSDK`.

## Plugin methods

### Start
To start scanning the SDK should call the following method:

	plugins.NexenSDK.startScanning(options, (data) => {
        // Subscribe to the plugin callbacks
		this.onLocationsLoaded();
		this.onProximityZoneNotification();
	}, (err) => {
		// Handle error
	});

You should subscribe to onLocationsLoaded and onProximityZoneNotification to receive these callbacks after the call was successful.

This call might throw an error if the permissions were not granted on Android/iOS.

### Stop
This stops the SDK from scanning.

    plugins.NexenSDK.stopScanning((data) => {
		// Do something after stopping the scanning
    }, (err) => {
		// Handle error
    });

Note: Since stopping the NXProximityManager on iOS makes its instance unusable, stopping the SDK on iOS is artificial and will not actually stop scanning.

### OnLocationsLoaded

This callback is called when the locations have been loaded by the SDK.

	plugins.NexenSDK.onLocationsLoaded((data) => {
        // Do something with the data
	}, (err) => {
		// Handle error
	});

It is important to subscribe to this call after starting the SDK was successful.
The data received is the same as in the native SDK's, formatted as a JSON object.

### OnProximityZoneNotification

This callback is called when a notification was triggered.

    plugins.NexenSDK.onProximityZoneNotification((data) => {
        // Do something with the notification
    }, (err) => {
        // Handle error
    });


It is important to subscribe to this call after starting the SDK was successful.
The data received is the same as in the native SDK's, formatted as a JSON object.

### onPushNotificationTappedForZone

This callback can be used to confirm a user has tapped a push notification (or some other sort of interaction with the beacon).

    plugins.NexenSDK.onPushNotificationTappedForZone(beaconId, contentTypeId, (data) => {
        // Call was successful
    }, (err) => {
        // Zone not found, handle error
    });

Note: The contentTypeId parameter is only required for iOS and will be ignored on Android.

### onOpenUrl

This callback is called whenever a push notification was tapped that has HTML content.
The URL is passed onto Cordova.

    plugins.NexenSDK.onOpenUrl((url) => {
        // Handle the URL
    }, (err) => {
        // Handle error
    });

## Plugin options

When starting the plugin the following options payload can be passed:

    var options = {
        disabledCategories: [],
        tags: [],
        allowBackgroundScanning: true,			// Android only
        notifications: {
            showNotifications: true,
            textColor: "#ff9900", 				// Android only
            smallIconName: "notification_icon"	// Android only
        }
    };
