package be.wearenexen.cordova;

import android.app.Application;
import android.support.annotation.NonNull;

import be.wearenexen.NexenProximityManager;
import be.wearenexen.NexenProximitySetup;
import be.wearenexen.NexenServiceInterface;

public final class BeaconManager {

    private static BeaconManager sInstance;

    private NexenProximityManager mProximityManager;
    private boolean mHasBeenBootstrapped;

    private BeaconManager() {

    }

    public static BeaconManager getInstance() {
        if (sInstance == null) {
            sInstance = new BeaconManager();
        }
        return sInstance;
    }

    public synchronized void start() {
        NexenProximityManager.get().start();
    }

    public synchronized void stop() {
        NexenProximityManager.get().stop();
    }

    public synchronized boolean hasBeenBootstrapped() {
        return mHasBeenBootstrapped;
    }

    public synchronized NexenProximityManager getProximityManager() {
        return mProximityManager;
    }

    public synchronized void bootstrap(@NonNull Application application, @NonNull NexenServiceInterface callbacks, String appName, String appPassword) {

        if (mHasBeenBootstrapped) {
            return;
        }

        mProximityManager = NexenProximityManager.get();
        NexenProximitySetup setup = new NexenProximitySetup(appName, appPassword);
        mProximityManager.configureFor(application, callbacks, setup);
        mHasBeenBootstrapped = true;
    }

}
