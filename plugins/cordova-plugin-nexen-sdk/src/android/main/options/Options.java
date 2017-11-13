package be.wearenexen.cordova.options;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Options {

    private static final String TAG = Options.class.getSimpleName();

    private Map<String, String> mTags;
    private Set<Integer> mDisabledCategories;
    private NotificationOptions mNotificationOptions;
    private boolean mAllowBackgroundScanning;
    private JSONObject mOriginalObject;

    public Options() {
        this(null);
    }

    public Options(JSONObject data) {
        mDisabledCategories = new HashSet<>();
        mTags = new HashMap<>();
        mNotificationOptions = new NotificationOptions();

        if (data != null) {
            parseData(data);
        }
    }

    private void parseData(JSONObject data) {
        mOriginalObject = data;

        try {

            if (data.has("notifications")) {
                mNotificationOptions = new NotificationOptions(data.getJSONObject("notifications"));
            }

            if (data.has("allowBackgroundScanning")) {
                mAllowBackgroundScanning = data.getBoolean("allowBackgroundScanning");
            }

            if (data.has("disabledCategories")) {
                JSONArray categories = data.getJSONArray("disabledCategories");
                for (int i = 0; i < categories.length(); i++) {
                    mDisabledCategories.add(categories.getInt(i));
                }
            }

            if (data.has("tags")) {
                JSONArray tags = data.getJSONArray("tags");
                for (int i = 0; i < tags.length(); i++) {
                    JSONObject keyValueSet = tags.getJSONObject(i);
                    String key = keyValueSet.keys().next();
                    mTags.put(key, keyValueSet.getString(key));
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, "Got en error whilst parsing the options", e);
        }
    }

    public NotificationOptions getNotificationOptions() {
        return mNotificationOptions;
    }

    public boolean shouldShowNotifications() {
        return mNotificationOptions != null && mNotificationOptions.shouldShowNotifications();
    }

    public boolean allowsBackgroundScanning() {
        return mAllowBackgroundScanning;
    }

    public Set<Integer> getDisabledCategories() {
        return mDisabledCategories;
    }

    public Map<String, String> getTags() {
        return mTags;
    }

    public JSONObject getAsJsonObject() {
        return mOriginalObject;
    }


    @Override
    public String toString() {
        return "Options{" +
                "mTags=" + mTags +
                ", mDisabledCategories=" + mDisabledCategories +
                ", mNotificationOptions=" + mNotificationOptions +
                ", mAllowBackgroundScanning=" + mAllowBackgroundScanning +
                '}';
    }
}
