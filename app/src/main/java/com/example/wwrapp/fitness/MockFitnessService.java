package com.example.wwrapp.fitness;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

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

    private long mDailyStepCount;
    private LocalDateTime mCurrentDateTime;
    private final IBinder mBinder = new LocalService();

    private List<IFitnessObserver> fitnessObservers;

    public MockFitnessService() {
        this.mDailyStepCount = 0;
        this.mCurrentDateTime = LocalDateTime.now();
        this.fitnessObservers = new ArrayList<>();
    }

    public class LocalService extends Binder {
        public IFitnessService getService() {
            return MockFitnessApplication.getFitnessService();
        }
    }



    /**
     * Worker to update step count
     */
    final class StepCountThread implements Runnable {
        private int startId;
        private static final long TIMEOUT = 1000;

        StepCountThread(int startId) {
            StepCountThread.this.startId = startId;
        }



        @Override
        public void run() {
            synchronized (StepCountThread.this) {
                try {
                    while (true) {
                        // Simulate walking with periodic step count updates
                        Log.d(TAG, "Waiting ...");
                        Log.d(TAG, "Step count before update: " + MockFitnessService.this.mDailyStepCount);
                        wait(TIMEOUT);
                        MockFitnessService.this.updateStepCount();
                        Log.d(TAG, "Step count after update: " + MockFitnessService.this.mDailyStepCount);
                        MockFitnessService.this.notifyObservers();
                    }
                } catch (InterruptedException e) {
                    Log.w(TAG, "Interrupted Exception");
                    e.printStackTrace();
                }
                // Stop the service when it is stopped
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "In method onStartCommand");
        // Launch the step counting
        Log.d(TAG, "Number of fitness observers is: " + this.fitnessObservers.size());
        Thread thread = new Thread(new StepCountThread(startId));
        thread.start();
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
        this.fitnessObservers.add(fitnessObserver);
    }

    @Override
    public void removeObserver(IFitnessObserver fitnessObserver) {
        this.fitnessObservers.remove(fitnessObserver);
    }

    @Override
    public void notifyObservers() {
        Log.d(TAG, "In method notifyObservers");
//        Log.d(TAG, "Number of fitness observers is: " + this.fitnessObservers.size());
        for (IFitnessObserver fitnessObserver : this.fitnessObservers) {
            Log.d(TAG, "Calling update on a fitness observer ... ");
            fitnessObserver.update(this.mDailyStepCount);
        }
    }

    public long getDailyStepCount() {
        return mDailyStepCount;
    }

    public void setDailyStepCount(long dailyStepCount) {
        this.mDailyStepCount = dailyStepCount;
    }

    public LocalDateTime getCurrentDateTime() {
        return mCurrentDateTime;
    }

    public void setCurrentDateTime(LocalDateTime currentDateTime) {
        this.mCurrentDateTime = currentDateTime;
    }
}
