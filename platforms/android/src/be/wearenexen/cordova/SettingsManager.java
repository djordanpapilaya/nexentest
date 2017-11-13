package be.wearenexen.cordova;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.StringDef;

import com.exterion.exterionnews.BuildConfig;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This class can be used to store settings, it exposes the SharedPreferences but prevents common
 * pitfalls such as mistyping the setting name. The settings are also all bundled to the application
 * privately.
 * <p>
 * Example usage inside an Activity
 * <pre>
 *     {@code
 *         SettingsManager settingsManager = new SettingsManager(this);
 *         boolean isFirstRun = settingsManager.fetch(SettingsManager.Setting.FIRST_RUN, true);
 *     }
 * </pre>
 */
public class SettingsManager {

    private static final String GLOBAL_SETTINGS_KEY = BuildConfig.APPLICATION_ID + ".SETTINGS";

    private SharedPreferences mSharedPreferences;

    @StringDef({
            Setting.HAS_STARTED_SCANNING_ONCE,
            Setting.HAS_URL_TASK_KEY,
            Setting.URL_PERSIST_KEY
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Setting {
        String HAS_STARTED_SCANNING_ONCE = BuildConfig.APPLICATION_ID + ".HAS_STARTED_SCANNING_ONCE";
        String HAS_URL_TASK_KEY = BuildConfig.APPLICATION_ID + ".HAS_URL_TASK_KEY";
        String URL_PERSIST_KEY = BuildConfig.APPLICATION_ID + ".URL_PERSIST_KEY";
    }

    public SettingsManager(Context context) {
        mSharedPreferences = context.getSharedPreferences(GLOBAL_SETTINGS_KEY, Context.MODE_PRIVATE);
    }

    public void save(@Setting String key, boolean value) {
        mSharedPreferences.edit().putBoolean(key, value).apply();
    }

    public void save(@Setting String key, String value) {
        mSharedPreferences.edit().putString(key, value).apply();
    }

    public void save(@Setting String key, long value) {
        mSharedPreferences.edit().putLong(key, value).apply();
    }

    public void save(@Setting String key, int value) {
        mSharedPreferences.edit().putInt(key, value).apply();
    }

    public void save(@Setting String key, float value) {
        mSharedPreferences.edit().putFloat(key, value).apply();
    }

    public boolean fetch(@Setting String key, boolean defaultValue) {
        return mSharedPreferences.getBoolean(key, defaultValue);
    }

    public String fetch(@Setting String key, String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }

    public long fetch(@Setting String key, long defaultValue) {
        return mSharedPreferences.getLong(key, defaultValue);
    }

    public int fetch(@Setting String key, int defaultValue) {
        return mSharedPreferences.getInt(key, defaultValue);
    }

    public float fetch(@Setting String key, float defaultValue) {
        return mSharedPreferences.getFloat(key, defaultValue);
    }

    public void remove(@Setting String key) {
        mSharedPreferences.edit().remove(key).apply();
    }

    public void removeAll() {
        mSharedPreferences.edit().clear().apply();
    }

}
