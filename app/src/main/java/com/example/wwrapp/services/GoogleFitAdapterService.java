package com.example.wwrapp.services;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.wwrapp.activities.HomeScreenActivity;
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
                        updateStepCount();
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
    public int getRequestCode() {
        return 0;
    }

    @Override
    public void setup() {
        Log.d(TAG, "In method setup()");

        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .build();
        mAccount = GoogleSignIn.getAccountForExtension(mActivity,fitnessOptions);
        if (!GoogleSignIn.hasPermissions(HomeScreenActivity.account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    mActivity, // your activity
                    WWRConstants.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    HomeScreenActivity.account,
                    fitnessOptions);
        } else {
            updateStepCount();
            startRecording();
        }
    }

    private void startRecording() {
        if (mAccount == null) {
            return;
        }

        Fitness.getRecordingClient(mActivity, mAccount)
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Successfully subscribed!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "There was a problem subscribing.");
                    }
                });
    }

    @Override
    public void updateStepCount() {
        Log.d(TAG, "In method updateStepCount");
        if (mAccount == null) {
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
                                Log.d(TAG, "total is: " + total);

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

                                Log.d(TAG, "Total steps: " + total);
                                System.out.println("Total steps: " + total);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "There was a problem getting the step count.", e);
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
