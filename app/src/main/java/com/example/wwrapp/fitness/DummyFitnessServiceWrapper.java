package com.example.wwrapp.fitness;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DummyFitnessServiceWrapper implements IFitnessService,
        IFitnessObserver, IFitnessSubject {
    private static final String TAG = "Wrapper";
    private Context mContext;
    private long mSteps;
    private List<IFitnessObserver> mFitnessObservers;
    private DummyFitnessService mFitnessService;
    private boolean mIsBound;

    public DummyFitnessServiceWrapper(Context context) {
        mContext = context;
        mFitnessObservers = new ArrayList<>();
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
            mIsBound = false;
        }
    };

    public void startDummyService() {
        Log.d(TAG, "In method startDummyService()");
        if (!mIsBound) {
            Log.d(TAG, "Binding service");
            Intent intent = new Intent(mContext, DummyFitnessService.class);
            mContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            mContext.startService(intent);
        }
    }

    public void stopDummyService() {
        Log.d(TAG, "In method stopDummyService()");
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
}
