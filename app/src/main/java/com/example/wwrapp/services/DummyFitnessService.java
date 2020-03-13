package com.example.wwrapp.services;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.wwrapp.fitness.IFitnessObserver;
import com.example.wwrapp.fitness.IFitnessService;
import com.example.wwrapp.fitness.IFitnessSubject;
import com.example.wwrapp.utils.WWRConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * A Dummy Fitness Service that does nothing. Use this for testing UI screens to avoid the
 * Google Fitness Service sign-in.
 */
public class DummyFitnessService extends Service implements IFitnessService, IFitnessSubject {
    private static final String TAG = "DummyFitnessService";

    // If the thread is running
    // TODO: use atomic boolean
    private boolean mIsRunning;

    // Total step count
    private long mStepCountIncrement;
    private List<IFitnessObserver> mFitnessObservers;

    private final IBinder mBinder = new LocalService();

    public DummyFitnessService() {
        Log.d(TAG, "In constructor of DummyFitnessService");
        mStepCountIncrement = WWRConstants.DUMMY_FITNESS_SERVICE_STEP_COUNT_INCREMENT;
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
                        notifyObservers();
                        Log.d(TAG, "Step count is now " + mStepCountIncrement);
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
        mIsRunning = true;
        Thread thread = new Thread(new DummyThread(startId));
        thread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "In method onDestroy()");
        // Tell the thread to stop
        mIsRunning = false;
        super.onDestroy();
    }



    @Override
    public void setup() {
        Log.d(TAG, "In method setup()");
    }

    @Override
    public void startFitnessService(Activity activity) {
        Log.d(TAG, "startFitnessService: ");
    }

    @Override
    public void stopFitnessService() {
        Log.d(TAG, "stopFitnessService: ");

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
            observer.update(mStepCountIncrement);
        }
    }
}
