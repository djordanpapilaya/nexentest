package be.wearenexen.cordova;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import be.wearenexen.cordova.services.NexenBackgroundScannerService;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, NexenBackgroundScannerService.class);
        context.startService(startServiceIntent);
    }

}
