package com.example.wwrapp.services;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.example.wwrapp.fitness.IFitnessObserver;
import com.example.wwrapp.fitness.IFitnessService;
import com.example.wwrapp.fitness.IFitnessSubject;
import com.example.wwrapp.utils.WWRConstants;

import java.util.ArrayList;
import java.util.List;

public class DummyFitnessServiceWrapper implements IFitnessService, IFitnessObserver, IFitnessSubject {
    private static final String TAG = "DummyWrapper";

    // Whether this wrapper is active
    private boolean mIsActive;
    private Context mContext;

    // Step count
    private long mSteps;
    private List<IFitnessObserver> mFitnessObservers;

    private DummyFitnessService mFitnessService;
    // Whether this wrapper is bound to its service
    private boolean mIsBound;


    public DummyFitnessServiceWrapper(Context context) {
        mIsActive = true;
        mContext = context;
        mFitnessObservers = new ArrayList<>();

        // Recover the steps from SharedPreferences
        SharedPreferences stepsSharedPreferences =
                context.getSharedPreferences(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_FILE_NAME, Context.MODE_PRIVATE);
        mSteps = stepsSharedPreferences.getLong(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_KEY, 0);
        Log.d(TAG, "Retrieved mSteps is " + mSteps);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "In method onServiceConnected()");
            DummyFitnessService.LocalService localService = (DummyFitnessService.LocalService) service;
            mFitnessService = localService.getService();
            mIsBound = true;
            mFitnessService.registerObserver(DummyFitnessServiceWrapper.this);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "In method onServiceDisconnected()");
            mIsActive = false;
            mIsBound = false;
        }
    };

    public void startDummyService() {
        Log.d(TAG, "In method startDummyService()");

        // Only start the service if binding hasn't already happened
        if (!mIsBound) {
            Log.d(TAG, "Binding service");
            Intent intent = new Intent(mContext, DummyFitnessService.class);
            mContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            mContext.startService(intent);
        }
    }

    public void stopDummyService() {
        Log.d(TAG, "In method stopDummyService()");

        // Only stop the service if binding already happened
        if (mIsBound) {
            Log.d(TAG, "Unbinding service");
            Intent intent = new Intent(mContext, DummyFitnessService.class);
            mContext.unbindService(serviceConnection);
            mContext.stopService(intent);
            mIsBound = false;
        }
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
    public void update(long steps) {
        Log.d(TAG, "In method update(), steps is = " + steps);
        // Add the steps from the service simulating the walking to the total
        mSteps += steps;
        notifyObservers();
    }

    @Override
    public void registerObserver(IFitnessObserver fitnessObserver) {
        Log.d(TAG, "Registered an observer =" + fitnessObserver.toString());
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

    public boolean isActive() {
        return mIsActive;
    }
}
