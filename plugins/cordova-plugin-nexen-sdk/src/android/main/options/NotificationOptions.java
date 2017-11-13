package be.wearenexen.cordova.options;

import android.util.Log;
import android.graphics.Color;
import android.content.Context;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationOptions {

    private static final String TAG = NotificationOptions.class.getSimpleName();

    private String mSmallNotificationName;
    private int mTextColor;
    private boolean mShouldShowNotifications;

    public NotificationOptions() {
        this(null);
    }

    public NotificationOptions(JSONObject data) {
        mTextColor = Color.BLACK;
        if (data != null) {
            parseData(data);
        }
    }

    private void parseData(JSONObject data) {

        try {

            if (data.has("showNotifications")) {
                mShouldShowNotifications = data.getBoolean("showNotifications");
            }

            if (data.has("textColor")) {
                String hexColor = data.getString("textColor");
                hexColor = hexColor.startsWith("#") ? hexColor : "#" + hexColor;

                try {
                    mTextColor = Color.parseColor(hexColor);
                } catch (IllegalArgumentException e) {
                    mTextColor = Color.BLACK;
                }
            }

            if (data.has("smallIconName")) {
                mSmallNotificationName = data.getString("smallIconName");
            }

        } catch (JSONException e) {
            Log.e(TAG, "Got en error whilst parsing the options", e);
        }
    }

    public boolean shouldShowNotifications() {
        return mShouldShowNotifications;
    }

    public int getIconRes(Context context) {
        if (TextUtils.isEmpty(mSmallNotificationName)) {
            return getDefaultIconRes(context);
        }

        int icon = context.getResources().getIdentifier(mSmallNotificationName, "drawable", context.getPackageName());
        return icon == 0 ? getDefaultIconRes(context) : icon;
    }

    public int getDefaultIconRes(Context context) {
        return context.getApplicationInfo().icon;
    }

    public int getTextColor() {
        return mTextColor;
    }

    @Override
    public String toString() {
        return "NotificationOptions{" +
                "mSmallNotificationName='" + mSmallNotificationName + '\'' +
                ", mTextColor=" + mTextColor +
                ", mShouldShowNotifications=" + mShouldShowNotifications +
                '}';
    }
}
