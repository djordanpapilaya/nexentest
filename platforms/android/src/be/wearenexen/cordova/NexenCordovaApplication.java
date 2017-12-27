package be.wearenexen.cordova;

import android.util.Log;

import be.wearenexen.NexenApplication;
import be.wearenexen.cordova.utils.ResourceUtils;

public class NexenCordovaApplication extends NexenApplication {

    private static final String TAG = NexenCordovaApplication.class.getSimpleName();

    public static final String APP_NAME_KEY = "wan_app_name";
    public static final String APP_PASSWORD_KEY = "wan_app_password";

    @Override
    public void onCreate() {
        super.onCreate();

        // Check if credentials exist
        if (!ResourceUtils.doesStringResourceExist(this, APP_NAME_KEY) || !ResourceUtils.doesStringResourceExist(this, APP_PASSWORD_KEY)) {
            throw new RuntimeException("The WeAreNexen SDK cannot work without an app_name and app_password, see docs for more details.");
        }

        Log.d(TAG, "Started the " + TAG);
    }

}
