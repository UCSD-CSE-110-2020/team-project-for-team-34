package com.example.wwrapp.services;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.example.wwrapp.fitness.IFitnessObserver;
import com.example.wwrapp.fitness.IFitnessService;
import com.example.wwrapp.fitness.IFitnessSubject;

import java.util.ArrayList;
import java.util.List;

public class GoogleFitnessServiceWrapper implements IFitnessService, IFitnessSubject, IFitnessObserver {
    private static final String TAG = "GoogleWrapper";

    // Whether this wrapper is active
    private boolean mIsActive;
    private Context mContext;

    // Step count
    private long mSteps;
    private List<IFitnessObserver> mFitnessObservers;

    private boolean mIsBound;
    private GoogleFitAdapterService mFitnessService;

    private Activity mActivity;

    public GoogleFitnessServiceWrapper(Context context) {
        mContext = context;
        mFitnessObservers = new ArrayList<>();
    }

    public Context getContext() {
        return mContext;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "In method onServiceConnected()");
            GoogleFitAdapterService.LocalService localService = (GoogleFitAdapterService.LocalService) service;
            mFitnessService = localService.getService();
            mIsBound = true;
            mFitnessService.registerObserver(GoogleFitnessServiceWrapper.this);

            // Pass in an activity so Google Fit can request sign in
            ((GoogleFitAdapterService) mFitnessService).setActivity(mActivity);
            ((GoogleFitAdapterService) mFitnessService).setup();


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "In method onServiceDisconnected()");
            mIsBound = false;
        }
    };

    public void startGoogleService(Activity activity) {
        // Save the activity
        this.mActivity = activity;
        Log.d(TAG, "In method startDummyService()");
        if (!mIsBound) {
            Log.d(TAG, "Binding service");
            Intent intent = new Intent(mContext, GoogleFitAdapterService.class);
            mContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            mContext.startService(intent);
        }
    }

    public void stopGoogleService() {
        Log.d(TAG, "In method stopDummyService()");
        if (mIsBound) {
            Log.d(TAG, "Unbinding service");
            Intent intent = new Intent(mContext, GoogleFitAdapterService.class);
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
        mSteps = steps;
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
