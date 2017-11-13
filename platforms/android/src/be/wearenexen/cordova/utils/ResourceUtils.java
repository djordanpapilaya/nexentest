package be.wearenexen.cordova.utils;

import android.content.Context;

public class ResourceUtils {

    public static boolean doesStringResourceExist(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "string", context.getPackageName()) > 0;
    }

    public static String gracefulGetString(Context context, String name) {
        int identifier = context.getResources().getIdentifier(name, "string", context.getPackageName());

        if (identifier <= 0) {
            return null;
        }

        return context.getResources().getString(identifier);
    }

}
