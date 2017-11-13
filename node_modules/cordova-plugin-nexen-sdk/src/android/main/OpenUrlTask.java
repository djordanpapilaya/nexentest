package be.wearenexen.cordova;

import android.content.Context;
import android.support.annotation.NonNull;

public class OpenUrlTask {

    private String mUrl;

    public OpenUrlTask(@NonNull String url) {
        mUrl = url;
    }

    public void persist(@NonNull Context context) {
        SettingsManager settingsManager = new SettingsManager(context);
        settingsManager.save(SettingsManager.Setting.HAS_URL_TASK_KEY, true);
        settingsManager.save(SettingsManager.Setting.URL_PERSIST_KEY, mUrl);
    }

    public void delete(@NonNull Context context) {
        SettingsManager settingsManager = new SettingsManager(context);
        settingsManager.remove(SettingsManager.Setting.HAS_URL_TASK_KEY);
        settingsManager.remove(SettingsManager.Setting.URL_PERSIST_KEY);
    }

    public static OpenUrlTask restore(@NonNull Context context) {
        SettingsManager settingsManager = new SettingsManager(context);
        return new OpenUrlTask(settingsManager.fetch(SettingsManager.Setting.URL_PERSIST_KEY, ""));
    }

    public static boolean isPending(@NonNull Context context) {
        SettingsManager settingsManager = new SettingsManager(context);
        return settingsManager.fetch(SettingsManager.Setting.HAS_URL_TASK_KEY, false);
    }

    public String getUrl() {
        return mUrl;
    }
}
