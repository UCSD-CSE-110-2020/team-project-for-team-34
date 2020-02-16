package com.example.wwrapp.fitness;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.wwrapp.WWRConstants;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A mock fitness service
 */
public class MockFitnessService extends Service implements IFitnessService, IFitnessSubject{
    private static final String TAG = "MockFitnessService";
    // How much the updateStepCount method increases the step count
    private static final int STEP_COUNT_INCREMENT = 10;

    // Initialize the step count only once
    private static long sDailyStepCount = 0;
    private static LocalDateTime sCurrentDateTime;
    private static List<IFitnessObserver> sFitnessObservers;


    private final IBinder mBinder = new LocalBinder();
    private Thread stepCountThread;
    private boolean isRunning;

    public MockFitnessService() {
        Log.d(TAG, "MockFitnessService constructor called");

        // Initialize the date only once
        if (sCurrentDateTime == null) {
            sCurrentDateTime = LocalDateTime.now();
        }
        Log.d(TAG, "MockFitnessService time is: " + sCurrentDateTime.toString());

        if (sFitnessObservers == null) {
            sFitnessObservers = new ArrayList<>();

        }
    }

    public class LocalBinder extends Binder {
        public IFitnessService getService() {
            // return MockFitnessService.this;
            return MockFitnessApplication.getFitnessService();
        }
    }



    /**
     * Worker to update step count
     */
    final class StepCountThread implements Runnable {
        private static final long TIMEOUT = 1000;

        int startId;

        StepCountThread(int startId) {
            StepCountThread.this.startId = startId;
        }

        @Override
        public void run() {
            synchronized (StepCountThread.this) {
                try {
                    while (isRunning) {
                        // Simulate walking with periodic step count updates
//                        Log.d(TAG, "Waiting ...");
//                        Log.d(TAG, "Step count before update: " + MockFitnessService.this.sDailyStepCount);
                        wait(TIMEOUT);
                        MockFitnessService.this.updateStepCount();
                        // Save the daily steps
                        Context context = MockFitnessApplication.getAppContext();
                        SharedPreferences sharedPreferences =
                                context.getSharedPreferences(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_FILE_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_KEY, MockFitnessService.sDailyStepCount);
                        editor.apply();

//                        Log.d(TAG, "Step count after update: " + MockFitnessService.this.sDailyStepCount);
                        MockFitnessService.this.notifyObservers();
                    }
                } catch (InterruptedException e) {
                    Log.w(TAG, "Interrupted Exception");
                    e.printStackTrace();
                }
                // Stop the service when the thread is stopped
                Log.d(TAG, "Exited while loop in Thread");
                stopSelf(startId);
            }
        }
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "in method onUnbind");
        // isRunning = false;
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "In method onStartCommand");
        // Launch the step counting
        // Log.d(TAG, "Number of fitness observers is: " + sFitnessObservers.size());

        // Update the saved id of the thread about to run so that it can be stopped later

        // Update the step count to be its current value; otherwise, the step count will start from 0
        // Comenting out for now to test static vars
        // setDailyStepCount(intent.getLongExtra(WWRConstants.EXTRA_DAILY_STEPS_KEY, 0));

        // One-time initialization of thread
        if (stepCountThread == null) {
            isRunning = true;
            stepCountThread = new Thread(new StepCountThread(startId));
            stepCountThread.start();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "In method onDestroy");
        super.onDestroy();
    }

    @Override
    public int getRequestCode() {
        return 0;
    }

    @Override
    public void setup() {

    }

    @Override
    public void updateStepCount() {
        // Arbitrary step count increment
        this.setDailyStepCount(this.getDailyStepCount() + STEP_COUNT_INCREMENT);
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
//        Log.d(TAG, "In method notifyObservers");
//        Log.d(TAG, "Number of fitness observers is: " + this.sFitnessObservers.size());
        for (IFitnessObserver fitnessObserver : sFitnessObservers) {
//            Log.d(TAG, "Calling update on a fitness observer ... ");
            fitnessObserver.update(sDailyStepCount);
        }
    }

    public long getDailyStepCount() {
        return sDailyStepCount;
    }

    public void setDailyStepCount(long dailyStepCount) {
        sDailyStepCount = dailyStepCount;
    }

    public LocalDateTime getCurrentDateTime() {
        return sCurrentDateTime;
    }

    public void setCurrentDateTime(LocalDateTime currentDateTime) {
        sCurrentDateTime = currentDateTime;
    }

    /**
     * Returns true if afterDateTime is one full day ahead of beforeDateTime, false otherwise
     * @param beforeDateTime
     * @param afterDateTime
     * @return
     */
    public static boolean compareDays(LocalDateTime beforeDateTime, LocalDateTime afterDateTime) {
return false;
    }
}
