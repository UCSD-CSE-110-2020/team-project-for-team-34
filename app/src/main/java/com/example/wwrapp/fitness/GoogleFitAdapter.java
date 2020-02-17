package com.example.wwrapp.fitness;

import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.wwrapp.HomeScreenActivity;
import com.example.wwrapp.WWRConstants;
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

import static android.content.Context.MODE_PRIVATE;

//import com.google.android.material;

public class GoogleFitAdapter implements IFitnessService, IFitnessSubject {
    private final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = System.identityHashCode(this) & 0xFFFF;
    private final String TAG = "GoogleFitAdapter";
    private GoogleSignInAccount account;

    private List<IFitnessObserver> sFitnessObservers;
    private long mStepCount;

    private int offset = 5;

    private HomeScreenActivity activity;

    public GoogleFitAdapter(HomeScreenActivity homeScreenActivity) {
        activity = homeScreenActivity;
        if (sFitnessObservers == null) {
            sFitnessObservers = new ArrayList<>();
        }
    }

    private final IBinder mBinder = new GoogleFitAdapter.LocalBinder();

    public class LocalBinder extends Binder {
        public IFitnessService getService() {
            // return MockFitnessService.this;
            return GoogleFitAdapter.this;
        }
    }

    public void setup() {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .build();


        account = GoogleSignIn.getAccountForExtension(activity, fitnessOptions);
        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    activity, // your activity
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    account,
                    fitnessOptions);
        } else {
            updateStepCount();
            startRecording();
        }
    }

    private void startRecording() {
        if (account == null) {
            return;
        }

        Fitness.getRecordingClient(activity, account)
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


    /**
     * Reads the current daily step total, computed from midnight of the current day on the device's
     * current timezone.
     */
    public void updateStepCount() {
        if (account == null) {
            return;
        }

        Fitness.getHistoryClient(activity, account)
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

                                SharedPreferences saveSteps = activity.getSharedPreferences(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_FILE_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor = saveSteps.edit();

                                // This block is for testing Google Fit when one cannot physically move the phone.
//                                SharedPreferences testSave =
//                                        activity.getSharedPreferences(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_FILE_NAME, MODE_PRIVATE);
//                                long savedSteps = testSave.getLong(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_KEY, -1);
//                                // Testing only
//                                savedSteps += offset;
//                                total = savedSteps;
                                mStepCount = total;
                                activity.update(total);
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
    public int getRequestCode() {
        return GOOGLE_FIT_PERMISSIONS_REQUEST_CODE;
    }

    @Override
    public void registerObserver(IFitnessObserver fitnessObserver) {
        sFitnessObservers.add(fitnessObserver);
    }

    @Override
    public void removeObserver(IFitnessObserver fitnessObserver) {
        sFitnessObservers.remove(fitnessObserver);
    }

    @Override
    public void notifyObservers() {
        for (IFitnessObserver fitnessObserver : sFitnessObservers) {
            fitnessObserver.update(mStepCount);
        }
    }
}
