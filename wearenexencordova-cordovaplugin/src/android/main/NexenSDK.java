package be.wearenexen.cordova;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.List;

import be.wearenexen.NexenApplication;
import be.wearenexen.NexenServiceInterface;
import be.wearenexen.cordova.options.Options;
import be.wearenexen.cordova.services.NexenBackgroundScannerService;
import be.wearenexen.cordova.utils.JSONConverterUtils;
import be.wearenexen.cordova.utils.PermissionUtils;
import be.wearenexen.cordova.utils.ServiceUtils;
import be.wearenexen.network.APIError;
import be.wearenexen.network.response.beacon.BaseNexenZone;
import be.wearenexen.network.response.beacon.Location;
import be.wearenexen.network.response.beacon.NexenBeaconZone;

public class NexenSDK extends BasePlugin implements NexenServiceInterface, ServiceConnection {

    private static final String TAG = NexenSDK.class.getSimpleName();

    public static final int PERMISSIONS_ALL_REQUEST_CODE = 0;
    public static final int PERMISSION_DENIED_ERROR = 20;

    private NexenBackgroundScannerService mBeaconService;

    private Options mOptions;

    private CallbackContext mStartCallbackContext;
    private CallbackContext mStopCallbackContext;
    private CallbackContext mProximityNotificationCallbackContext;
    private CallbackContext mLocationsLoadedCallbackContext;
    private CallbackContext mNotificationTappedCallbackContext;
    private CallbackContext mOnOpenUrlCallbackContext;

    @Override
    protected void pluginInitialize() {
        super.pluginInitialize();

        getContext().registerReceiver(mOpenUrlReceiver, new IntentFilter(PushHandlerActivity.OPEN_URL_BROADCAST));
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        switch (action) {
            case PluginAction.START_SCANNING:
                mStartCallbackContext = callbackContext;

                // Parse options and apply them
                mOptions = args != null && args.length() > 0 ? new Options(args.getJSONObject(0)) : new Options();
                NexenApplication.getInstance().getBeaconsFilter().setDisabledCategories(mOptions.getDisabledCategories());
                NexenApplication.getInstance().getBeaconsFilter().setTags(mOptions.getTags());

                if (!hasPermission()) {
                    requestAllPermissions();    // If all permissions were granted, the service will start scanning
                } else {
                    startScanning();
                }

                return true;
            case PluginAction.STOP_SCANNING:
                mStopCallbackContext = callbackContext;
                stopScanning();
                return true;
            case PluginAction.PROXIMITY_NOTIFICATION:
                mProximityNotificationCallbackContext = callbackContext;
                return true;
            case PluginAction.LOCATIONS_LOADED:
                mLocationsLoadedCallbackContext = callbackContext;
                return true;
            case PluginAction.NOTIFICATION_TAPPED_FOR_ZONE:
                mNotificationTappedCallbackContext = callbackContext;
                onNotificationTappedForZone(args.getLong(0));
                return true;
            case PluginAction.OPEN_URL:
                mOnOpenUrlCallbackContext = callbackContext;
                handlePendingOpenUrlTasks();
                return true;
            default:
                return false;
        }
    }

    private void startScanning() {

        // Start the background service to scan
        if (!ServiceUtils.isServiceRunning(getContext(), NexenBackgroundScannerService.class)) {
            getApplicationContext().startService(getBackgroundServiceIntent());
        }
        getApplicationContext().bindService(getBackgroundServiceIntent(), this, Context.BIND_AUTO_CREATE);

        SettingsManager settingsManager = new SettingsManager(getContext());
        settingsManager.save(SettingsManager.Setting.HAS_STARTED_SCANNING_ONCE, true);

        // Perform callback
        Log.d(TAG, "WeAreNexen will start scanning");
        mStartCallbackContext.success();
    }

    private void stopScanning() {
        Log.d(TAG, "WeAreNexen will stop scanning");

        getApplicationContext().unbindService(this);
        getApplicationContext().stopService(getBackgroundServiceIntent());

        mStopCallbackContext.success();
    }

    @Override
    public void onProximityZonesLoadedFromCache(Collection<NexenBeaconZone> collection) {
        // This callback method is not used in the Cordova plugin, we can safely ignore it
        Log.d(TAG, "onProximityZonesLoadedFromCache");
    }

    @Override
    public void onProximityZoneNotification(BaseNexenZone baseNexenZone) {
        Log.d(TAG, "onProximityZoneNotification");

        if (mProximityNotificationCallbackContext == null) {
            return;
        }

        // Parse data to JSON
        JSONObject data;
        try {
            data = JSONConverterUtils.convertBaseNexenZone(baseNexenZone);
        } catch (JSONException e) {
            data = null;
            Log.e(TAG, e.getMessage());
        }

        // Send callback to Cordova
        PluginResult result = new PluginResult(PluginResult.Status.OK, data);
        result.setKeepCallback(true);
        mProximityNotificationCallbackContext.sendPluginResult(result);
    }

    @Override
    public void onLocationsLoaded(List<Location> list, APIError apiError) {
        Log.d(TAG, "onLocationsLoaded");

        if (mLocationsLoadedCallbackContext == null) {
            return;
        }

        PluginResult result;
        JSONArray data;

        // Send error when needed
        if (apiError != null) {
            Log.d(TAG, "APIError: " + apiError.getMessage());
            result = new PluginResult(PluginResult.Status.ERROR, apiError.getMessage());
            result.setKeepCallback(true);
            mLocationsLoadedCallbackContext.sendPluginResult(result);
            return;
        }

        // Parse data to JSON
        try {
            data = JSONConverterUtils.convertLocationCollection(list);
        } catch (JSONException e) {
            data = null;
            Log.e(TAG, e.getMessage());
        }

        // Send result
        result = new PluginResult(PluginResult.Status.OK, data);
        result.setKeepCallback(true);
        mLocationsLoadedCallbackContext.sendPluginResult(result);
    }

    @Override
    public void onRequirePermissions(Runnable finishedCallback) {
    }

    private void onNotificationTappedForZone(long id) {

        if (mNotificationTappedCallbackContext == null) {
            return;
        }

        BaseNexenZone zone = BeaconManager.getInstance().getProximityManager().findGeofenceZone(id);
        if (zone == null) {
            PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "Zone does not exist");
            mNotificationTappedCallbackContext.sendPluginResult(pluginResult);
            return;
        }

        BeaconManager.getInstance().getProximityManager().pushNotificationTappedForZone(zone);
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
        mNotificationTappedCallbackContext.sendPluginResult(pluginResult);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        NexenBackgroundScannerService.LocalBinder binder = (NexenBackgroundScannerService.LocalBinder) iBinder;
        mBeaconService = binder.getServiceInstance();
        mBeaconService.setCallback(this);
        mBeaconService.startScanning();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            if (ServiceUtils.isServiceRunning(getContext(), NexenBackgroundScannerService.class)) {
                getApplicationContext().unbindService(this);
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Tried unbinding the service whilst the service wasn\'t bound in the first place", e);
        }

        getContext().unregisterReceiver(mOpenUrlReceiver);
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {

        if (grantResults.length == 0) {
            return;
        }

        for (int r : grantResults) {
            if (r == PackageManager.PERMISSION_DENIED) {
                mStartCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, PERMISSION_DENIED_ERROR));
                return;
            }
        }
        switch (requestCode) {
            case PERMISSIONS_ALL_REQUEST_CODE:
                startScanning();
                break;
        }
    }

    private void requestAllPermissions() {
        PermissionHelper.requestPermissions(this, PERMISSIONS_ALL_REQUEST_CODE, PermissionUtils.getSDKPermissions());
    }

    public boolean hasPermission() {
        return PermissionUtils.hasRequiredSDKPermissions(getContext());
    }

    private Intent getBackgroundServiceIntent() {
        Intent intent = new Intent(cordova.getActivity(), NexenBackgroundScannerService.class);
        intent.putExtra(NexenBackgroundScannerService.OPTIONS_KEY, mOptions.getAsJsonObject().toString());
        return intent;
    }

    private void handlePendingOpenUrlTasks() {
        if (!OpenUrlTask.isPending(getContext())) {
            return;
        }

        // Get the task
        OpenUrlTask task = OpenUrlTask.restore(getContext());

        // Execute the onOpenUrl
        PluginResult result = new PluginResult(PluginResult.Status.OK, task.getUrl());
        result.setKeepCallback(true);
        mOnOpenUrlCallbackContext.sendPluginResult(result);

        // Delete the task
        task.delete(getContext());
    }

    private BroadcastReceiver mOpenUrlReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (mOnOpenUrlCallbackContext == null) {
                return;
            }

            String url = intent.getExtras().getString(PushHandlerActivity.OPEN_URL_BROADCAST_URL_KEY);
            PluginResult result = new PluginResult(PluginResult.Status.OK, url);
            result.setKeepCallback(true);
            mOnOpenUrlCallbackContext.sendPluginResult(result);
        }
    };
}
