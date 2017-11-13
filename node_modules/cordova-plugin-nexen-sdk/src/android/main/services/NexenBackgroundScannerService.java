package be.wearenexen.cordova.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.List;

import be.wearenexen.NexenApplication;
import be.wearenexen.NexenServiceInterface;
import be.wearenexen.cordova.BeaconManager;
import be.wearenexen.cordova.NexenCordovaApplication;
import be.wearenexen.cordova.PushHandlerActivity;
import be.wearenexen.cordova.options.Options;
import be.wearenexen.cordova.utils.ResourceUtils;
import be.wearenexen.network.APIError;
import be.wearenexen.network.response.beacon.BaseNexenZone;
import be.wearenexen.network.response.beacon.Location;
import be.wearenexen.network.response.beacon.NexenBeaconZone;

public class NexenBackgroundScannerService extends Service implements NexenServiceInterface {

    public static final String OPTIONS_KEY = "be.wearenexen.cordova.OPTIONS_KEY";

    private final IBinder mBinder = new LocalBinder();

    private NexenServiceInterface mClientReceiver;  // Will send the callbacks to any subscribing clients (activities)
    private Options mOptions;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.getExtras() != null) {
            // Started via app
            parseStartIntent(intent);
        } else {
            // Started when app closes
            restoreOptions();
        }

        if (mOptions.allowsBackgroundScanning()) {
            startScanning();
        }

        Log.d("NEXEN", "Started with the following options: " + mOptions.toString());

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Stop scanning
        BeaconManager.getInstance().stop();
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
    }

    public void startScanning() {
        // Bootstrap the BeaconManager
        if (!BeaconManager.getInstance().hasBeenBootstrapped()) {
            String appName = ResourceUtils.gracefulGetString(getApplicationContext(), NexenCordovaApplication.APP_NAME_KEY);
            String appPassword = ResourceUtils.gracefulGetString(getApplicationContext(), NexenCordovaApplication.APP_PASSWORD_KEY);
            BeaconManager.getInstance().bootstrap(getApplication(), this, appName, appPassword);
        }

        // Start scanning
        BeaconManager.getInstance().start();

        // Cache the options
        cacheOptions();

        Log.d("NEXEN", "Started scanning");
    }

    public void setCallback(NexenServiceInterface nexenServiceInterface) {
        mClientReceiver = nexenServiceInterface;
    }

    @Override
    public void onProximityZonesLoadedFromCache(Collection<NexenBeaconZone> collection) {
        if (mClientReceiver != null) {
            mClientReceiver.onProximityZonesLoadedFromCache(collection);
        }
    }

    @Override
    public void onProximityZoneNotification(BaseNexenZone baseNexenZone) {
        showNotification(baseNexenZone);

        if (mClientReceiver != null) {
            mClientReceiver.onProximityZoneNotification(baseNexenZone);
        }
    }

    @Override
    public void onRequirePermissions(Runnable runnable) {
        if (mClientReceiver != null) {
            mClientReceiver.onRequirePermissions(runnable);
        }
    }

    @Override
    public void onLocationsLoaded(List<Location> list, APIError apiError) {
        if (mClientReceiver != null) {
            mClientReceiver.onLocationsLoaded(list, apiError);
        }
    }

    private void cacheOptions() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(OPTIONS_KEY, Context.MODE_PRIVATE);
        prefs.edit().putString("options", mOptions.getAsJsonObject().toString()).apply();
        Log.d("NEXEN", "Cached the following options: " + mOptions.getAsJsonObject().toString());
    }

    private void restoreOptions() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(OPTIONS_KEY, Context.MODE_PRIVATE);
        String optionsAsJson = prefs.getString("options", "");
        try {
            JSONObject parsedOptions = new JSONObject(optionsAsJson);
            mOptions = new Options(parsedOptions);
            Log.d("NEXEN", "Restored the following options: " + parsedOptions.toString());
        } catch (JSONException e) {
            mOptions = new Options();
        }
    }

    /**
     * Parses the intent options for the intent that started the service.
     */
    private void parseStartIntent(Intent intent) {

        if (intent == null) {
            return;
        }

        String options = intent.getStringExtra(OPTIONS_KEY);
        if (options == null) {
            return;
        }

        try {
            JSONObject parsedOptions = new JSONObject(options);
            mOptions = new Options(parsedOptions);
            Log.d("NEXEN", "Received the following options: " + parsedOptions.toString());
        } catch (JSONException e) {
            mOptions = new Options();
        }
    }

    private void showNotification(BaseNexenZone baseNexenZone) {
        if (mOptions.shouldShowNotifications()) {
            int iconRes = mOptions.getNotificationOptions().getIconRes(getApplicationContext());
            int textColor = mOptions.getNotificationOptions().getTextColor();
            NexenApplication.getInstance().showNotificationForProximityZone(baseNexenZone, PushHandlerActivity.class, iconRes, null, textColor);
        }
    }

    public class LocalBinder extends Binder {
        public NexenBackgroundScannerService getServiceInstance() {
            return NexenBackgroundScannerService.this;
        }
    }
}
