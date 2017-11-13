package be.wearenexen.cordova;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@StringDef({
    PluginAction.START_SCANNING,
    PluginAction.STOP_SCANNING,
    PluginAction.PROXIMITY_NOTIFICATION,
    PluginAction.LOCATIONS_LOADED,
    PluginAction.NOTIFICATION_TAPPED_FOR_ZONE,
    PluginAction.OPEN_URL
})
@Retention(RetentionPolicy.SOURCE)
public @interface PluginAction {
    String START_SCANNING = "startScanning";
    String STOP_SCANNING = "stopScanning";
    String PROXIMITY_NOTIFICATION = "onProximityZoneNotification";
    String LOCATIONS_LOADED = "onLocationsLoaded";
    String NOTIFICATION_TAPPED_FOR_ZONE = "onPushNotificationTappedForZone";
    String OPEN_URL = "onOpenUrl";
}
