package com.example.wwrapp.services;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.wwrapp.fitness.IFitnessObserver;
import com.example.wwrapp.fitness.IFitnessService;
import com.example.wwrapp.fitness.IFitnessSubject;
import com.example.wwrapp.utils.WWRConstants;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class GoogleFitAdapterService extends Service implements IFitnessService, IFitnessSubject {
    private static final String TAG = "GoogleFitAdapterService";
    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1;

    private boolean mDoneSettingUp;

    // Whether the inner thread is running
    private boolean mIsRunning;

    // Step count
    private long mSteps;
    private List<IFitnessObserver> mFitnessObservers;

    private final IBinder mBinder = new GoogleFitAdapterService.LocalService();

    // Reference to the activity to request sign-in on
    private Activity mActivity;
    private GoogleSignInAccount mAccount;


    public GoogleFitAdapterService() {
        Log.d(TAG, "In constructor of GoogleFitAdapterService");
        mFitnessObservers = new ArrayList<>();
    }

    public void setActivity(Activity activity) {
        this.mActivity = activity;
    }

    final class GoogleThread implements Runnable {
        int startId;

        public GoogleThread(int startId) {
            this.startId = startId;
        }

        @Override
        public void run() {
            synchronized (this) {
                while (mIsRunning) {
                    // Increment steps
                    try {
                        wait(WWRConstants.WAIT_TIME);
                        Log.d(TAG, "Value of mDoneSettingUp is " + mDoneSettingUp);
                        if (mDoneSettingUp) {
                            updateStepCount();
                        }
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
        public GoogleFitAdapterService getService() {
            return GoogleFitAdapterService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "In method onStartCommand()");

        // Start the step-tracking thread
        mIsRunning = true;
        Thread thread = new Thread(new GoogleThread(startId));
        thread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "In method onDestroy()");

        // Tell the thread to stop running
        mIsRunning = false;
        super.onDestroy();
    }


    @Override
    public void setup() {
        Log.d(TAG, "In method setup()");

        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .build();
        mAccount = GoogleSignIn.getAccountForExtension(mActivity, fitnessOptions);
        if (!GoogleSignIn.hasPermissions(mAccount, fitnessOptions)) {
            Log.d(TAG, "Sign in permissions evaluated to false, requesting permissions now");
            GoogleSignIn.requestPermissions(
                    mActivity, // your activity
                    WWRConstants.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    mAccount,
                    fitnessOptions);
        } else {
            Log.d(TAG, "Calling startRecording");
            startRecording();
        }
    }

    @Override
    public void startFitnessService(Activity activity) {
        Log.d(TAG, "startFitnessService: ");
    }

    @Override
    public void stopFitnessService() {
        Log.d(TAG, "stopFitnessService: ");

    }

    private void startRecording() {
        Log.d(TAG, "startRecording:");
        if (mAccount == null) {
            return;
        }

        if (true) {
            // Check if the Activity Recognition permission is granted
            if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACTIVITY_RECOGNITION)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission not granted");
                // Permission is not granted
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                        GOOGLE_FIT_PERMISSIONS_REQUEST_CODE);
            } else {
                Log.d(TAG, "Permission was granted");
            }
        }

        Fitness.getRecordingClient(mActivity, mAccount)
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Successfully subscribed!");
                        mDoneSettingUp = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "There was a problem subscribing.");
                        Log.i(TAG, "onFailure: " + e.getMessage());
                    }
                });
    }

    /**
     * Helper method to get steps from Google Fit and notify observers
     */
    public void updateStepCount() {
        Log.v(TAG, "In method updateStepCount");
        if (mAccount == null) {
            Log.d(TAG, "updateStepCount: mAccount is null");
            return;
        }

        Fitness.getHistoryClient(mActivity, mAccount)
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {
                            @Override
                            public void onSuccess(DataSet dataSet) {
                                Log.d(TAG, dataSet.toString());
                                long total =
                                        dataSet.isEmpty()
                                                ? 0
                                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                                Log.v(TAG, "total is: " + total);

                                SharedPreferences saveSteps = mActivity.getSharedPreferences(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_FILE_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor = saveSteps.edit();

                                // This block is for testing Google Fit when one cannot physically move the phone.
//                                SharedPreferences testSave =
//                                        activity.getSharedPreferences(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_FILE_NAME, MODE_PRIVATE);
//                                long savedSteps = testSave.getLong(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_KEY, -1);
//                                // Testing only
//                                savedSteps += offset;
//                                total = savedSteps;
                                mSteps = total;
                                notifyObservers();
                                editor.putLong(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_KEY, total);
                                editor.apply();

                                Log.v(TAG, "Total steps: " + total);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "There was a problem getting the step count.", e);
                            }
                        });
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
