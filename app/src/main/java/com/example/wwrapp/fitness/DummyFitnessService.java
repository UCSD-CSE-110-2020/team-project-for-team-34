package com.example.wwrapp.fitness;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A Dummy Fitness Service that does nothing. Use this for testing UI screens to avoid the
 * Google Fitness Service sign-in.
 */
public class DummyFitnessService extends Service implements IFitnessService, IFitnessSubject {
    private static final String TAG = "DummyFitnessService";

    private boolean mIsRunning = true;
    private long mSteps;
    private List<IFitnessObserver> mFitnessObservers;

    private final IBinder mBinder = new LocalService();

    public DummyFitnessService() {
        Log.d(TAG, "In constructor of DummyFitnessService");
        mFitnessObservers = new ArrayList<>();
    }


    final class DummyThread implements Runnable {
        int startId;

        public DummyThread(int startId) {
            this.startId = startId;
        }

        @Override
        public void run() {
            synchronized (this) {
                while (mIsRunning) {
                    // Increment steps
                    try {
                        wait(1000);
                        mSteps += 10;
                        notifyObservers();
                        Log.d(TAG, "Step count is now " + mSteps);
                    }
                    catch (InterruptedException e) {
                        Log.d(TAG, e.getMessage());
                    }
                }
                stopSelf(startId);
            }
        }
    }

    @Nullable
    @android.support.annotation.Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    class LocalService extends Binder {
        public DummyFitnessService getService() {
            return DummyFitnessService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "In method onStartCommand()");

        // Start the step-tracking thread
        Thread thread = new Thread(new DummyThread(startId));
        thread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "In method onDestroy()");
        super.onDestroy();
    }

    @Override
    public int getRequestCode() {
        return 0;
    }

    @Override
    public void setup() {
        Log.d(TAG, "In method setup()");
    }

    @Override
    public void updateStepCount() {
        Log.d(TAG, "In method updateStepCount");
    }

    @Override
    public void registerObserver(IFitnessObserver fitnessObserver) {
        mFitnessObservers.add(fitnessObserver);
    }

    @Override
    public void removeObserver(IFitnessObserver fitnessObserver) {
        mFitnessObservers.remove(fitnessObserver);
    }

    @Override
    public void notifyObservers() {
        for (IFitnessObserver observer : mFitnessObservers) {
            observer.update(mSteps);
        }
    }
}
