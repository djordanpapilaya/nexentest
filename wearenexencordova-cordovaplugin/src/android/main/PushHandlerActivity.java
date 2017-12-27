package be.wearenexen.cordova;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Collection;
import java.util.List;

import be.wearenexen.NexenProximityManager;
import be.wearenexen.NexenServiceInterface;
import be.wearenexen.cordova.utils.ResourceUtils;
import be.wearenexen.network.APIError;
import be.wearenexen.network.callback.IResponseListener;
import be.wearenexen.network.response.beacon.BaseNexenZone;
import be.wearenexen.network.response.beacon.Location;
import be.wearenexen.network.response.beacon.NexenBeaconZone;
import be.wearenexen.network.response.beacon.content.Content;
import be.wearenexen.network.response.beacon.content.HtmlContent;

import static be.wearenexen.Constants.BEACON_ID_INTENT_DATA_KEY;
import static be.wearenexen.Constants.CONTENT_ID_INTENT_DATA_KEY;
import static be.wearenexen.Constants.INVALID_ID;

public class PushHandlerActivity extends Activity {

    private static String TAG = PushHandlerActivity.class.getSimpleName();

    public static final String OPEN_URL_BROADCAST = "be.wearenexen.cordova.OPEN_URL_BROADCAST";
    public static final String OPEN_URL_BROADCAST_URL_KEY = "be.wearenexen.cordova.OPEN_URL_BROADCAST_URL_KEY";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");

        // Bootstrap the SDK again
        if (!NexenSDK.isActive()) {
            BeaconManager.getInstance().bootstrap(getApplication(), new NexenServiceInterface() {
                @Override
                public void onProximityZonesLoadedFromCache(Collection<NexenBeaconZone> collection) {

                }

                @Override
                public void onProximityZoneNotification(BaseNexenZone baseNexenZone) {

                }

                @Override
                public void onRequirePermissions(Runnable runnable) {

                }

                @Override
                public void onLocationsLoaded(List<Location> list, APIError apiError) {

                }
            }, ResourceUtils.gracefulGetString(this, NexenCordovaApplication.APP_NAME_KEY), ResourceUtils.gracefulGetString(this, NexenCordovaApplication.APP_PASSWORD_KEY));
        }

        // Process the push bundle
        processPushBundle();
    }

    /**
     * Processes the push bundle from the intent data.
     */
    private void processPushBundle() {

        // Verify intent
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }

        // Get metadata
        final long zoneId = intent.getLongExtra(BEACON_ID_INTENT_DATA_KEY, INVALID_ID);
        final long contentId = intent.getLongExtra(CONTENT_ID_INTENT_DATA_KEY, INVALID_ID);

        // Handle beacon trigger
        NexenProximityManager.get().getBeaconZone(zoneId, contentId, callback);
    }

    /**
     * Perform the correct action based upon the content
     *
     * @param zoneContent The content of the beacon/geofence zone
     */
    private void handleContent(@Nullable Content zoneContent) {

        // Launch the main activity if no content was found
        if (zoneContent == null) {
            return;
        }

        // Check if the content is an HTML page
        if (zoneContent instanceof HtmlContent) {
            HtmlContent htmlContent = (HtmlContent) zoneContent;
            if (NexenSDK.isActive()) {
                broadcastOpenUrl(htmlContent.getPageUrl());
            } else {
                OpenUrlTask task = new OpenUrlTask(htmlContent.getPageUrl());
                task.persist(this);
            }
        }
    }

    /**
     * Sends a broadcast, receivers should handle the URL send by this broadcast.
     *
     * @param url The URL to handle
     */
    private void broadcastOpenUrl(String url) {
        Intent intent = new Intent(OPEN_URL_BROADCAST);
        intent.putExtra(OPEN_URL_BROADCAST_URL_KEY, url);
        sendBroadcast(intent);
    }

    /**
     * Force reloading of the launcher activity of the app.
     */
    private void forceMainActivityReload() {
        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(getApplicationContext().getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private IResponseListener<NexenBeaconZone> callback = new IResponseListener<NexenBeaconZone>() {
        @Override
        public void onResponse(NexenBeaconZone zone) {

            // Cancel all notifications
            final NotificationManager notificationManager = (NotificationManager) PushHandlerActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();

            // Handle the beacon
            if (zone != null) {
                BeaconManager.getInstance().getProximityManager().pushNotificationTappedForZone(zone);
                handleContent(zone.getContent());
            }

            // Reboot/re-activate the app
            if (!NexenSDK.isActive()) {
                forceMainActivityReload();
            } else {
                finish();
            }
        }

        @Override
        public void onError(APIError apiError) {
            finish();
        }
    };


}
