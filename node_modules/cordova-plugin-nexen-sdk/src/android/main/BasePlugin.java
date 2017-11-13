package be.wearenexen.cordova;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;


public class BasePlugin extends CordovaPlugin {

    private static CordovaWebView sWebView;
    private static boolean sForeground = false;

    private Intent mIntent;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        boolean result = super.execute(action, args, callbackContext);
        sWebView = webView;
        return result;
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        sForeground = true;
    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);

        sForeground = false;
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);

        sForeground = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        sForeground = false;
        sWebView = null;
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        mIntent = intent;
    }

    protected Context getApplicationContext() {
        return getApplication().getApplicationContext();
    }

    protected Application getApplication() {
        return (Application) getContext().getApplicationContext();
    }

    protected Context getContext() {
        return cordova.getActivity();
    }

    public @Nullable Intent getLastReceivedIntent() {
        return mIntent;
    }

    public static boolean isInForeground() {
        return sForeground;
    }

    public static boolean isActive() {
        return sWebView != null;
    }
}
