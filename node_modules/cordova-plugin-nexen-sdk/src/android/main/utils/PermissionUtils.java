package be.wearenexen.cordova.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

public class PermissionUtils {

    private static boolean hasPermission(Context context, String permission) {
        return context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasRequiredSDKPermissions(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || hasLocationPermission(context);
    }

    public static String[] getSDKPermissions() {
        return new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
        };
    }

    public static boolean hasBluetoothPermission(Context context) {
        return hasPermission(context, Manifest.permission.BLUETOOTH) && hasPermission(context, Manifest.permission.BLUETOOTH_ADMIN);
    }

    public static boolean hasLocationPermission(Context context) {
        return hasPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) && hasPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    public static boolean hasReadPhoneStatePermission(Context context) {
        return hasPermission(context, Manifest.permission.READ_PHONE_STATE);
    }

    public static boolean hasWriteExternalStoragePermission(Context context) {
        return hasPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

}
