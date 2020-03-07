package com.example.wwrapp.fitness;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.wwrapp.activities.HomeScreenActivity;
import com.example.wwrapp.utils.TimeMachine;
import com.example.wwrapp.utils.WWRConstants;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * A mock fitness service
 * Credits: https://www.logicbig.com/how-to/java-8-date-time-api/millis-to-date.html
 */
public class MockFitnessService extends Service implements IFitnessService, IFitnessSubject {
    private static final String TAG = "MockFitnessService";

    // How much the updateStepCount method increases the step count
    private static final int STEP_COUNT_INCREMENT = 10;
    private static final int MILLISECONDS_PER_SECOND = 1000;
    private static final int RESET_STEP_COUNT = 0;

    // Initialize the step count only once
    private static long sDailyStepCount = 0;
    private static LocalDateTime sCurrentDateTime;
    private static List<IFitnessObserver> sFitnessObservers;

    private final IBinder mBinder = new LocalBinder();
    private Thread stepCountThread;
    private Thread elapsedTimeThread;
    private boolean isRunning;
    private boolean readMockedDate = false;
    private long elapsedMillisecondsSinceMockTimeSet = 0;

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
//                        Log.d(TAG, "Step count before update: " + MockFitnessService.this.sDailyStepCount);
                        wait(TIMEOUT);

                        LocalDateTime currentDate;
                        if (HomeScreenActivity.IS_MOCKING) {
                            ZoneId zoneId = ZoneId.systemDefault(); // or: ZoneId.of("Europe/Oslo");
                            long startTime = sCurrentDateTime.atZone(zoneId).toEpochSecond();
                            startTime *= MILLISECONDS_PER_SECOND;
                            long currentTime = startTime + elapsedMillisecondsSinceMockTimeSet;
                            currentDate =
                                    LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTime), ZoneId.systemDefault());
                            Log.d(TAG, "start mock time is: " + startTime);
                            Log.d(TAG, "elapsed time in fitness service class is: " + elapsedMillisecondsSinceMockTimeSet);

                            Log.d(TAG, "current mock time is: " + currentTime);


                        } else {
                            currentDate = LocalDateTime.now();
                        }

                        // Reset the step count and time for a new day
                        if (!areSameDay(sCurrentDateTime, currentDate)) {
                            sDailyStepCount = 0;
                            sCurrentDateTime = currentDate;
                        }

                        MockFitnessService.this.updateStepCount();
//                        Context context = MockFitnessApplication.getAppContext();
//                        SharedPreferences sharedPreferences =
//                                context.getSharedPreferences(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_FILE_NAME, MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//                        String formatDateTime = sCurrentDateTime.format(formatter);
//                        Log.d(TAG,"time is " + formatDateTime);
//
//                        int dayOfYear = sCurrentDateTime.getDayOfYear();
//                        int nextSecondDayOfYear = sCurrentDateTime.plusSeconds(1).getDayOfYear();
//                        if( dayOfYear == nextSecondDayOfYear ) {
//                            // Save the daily steps
//                            editor.putLong(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_KEY, MockFitnessService.sDailyStepCount);
//                            editor.apply();
//                        }
//                        else {
//                            // Reset the daily steps
//                            editor.putLong(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_KEY, MockFitnessService.RESET_STEP_COUNT);
//                            editor.apply();
//                        }
//                        sCurrentDateTime = sCurrentDateTime.plusSeconds(1);
////                        Log.d(TAG, "Step count after update: " + MockFitnessService.this.sDailyStepCount);

                        // Inform observers of updated steps
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

    final class ElapsedTimeThread implements Runnable {
        private static final long TIMEOUT = 1000;
        private int startId;

        public ElapsedTimeThread(int startId) {
            this.startId = startId;
        }

        @Override
        public void run() {
            synchronized (ElapsedTimeThread.this) {
                while (isRunning) {
                    try {
                        wait(TIMEOUT);
                        elapsedMillisecondsSinceMockTimeSet += MILLISECONDS_PER_SECOND;
                        Log.d(TAG, "elapsed time in thread class is: " + elapsedMillisecondsSinceMockTimeSet);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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

        // Check if any mocking steps/time were provided
        setup();

        // One-time initialization of thread
        if (stepCountThread == null) {
            isRunning = true;
            stepCountThread = new Thread(new StepCountThread(startId));
            stepCountThread.start();
            elapsedTimeThread = new Thread(new ElapsedTimeThread(startId));
            elapsedTimeThread.start();
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
        // Get the current date. This number may be different than what the fitness
        // service has if the mock screen set time was used
        SharedPreferences timeSharedPreferences = getSharedPreferences
                (WWRConstants.SHARED_PREFERENCES_SYSTEM_TIME_FILE_NAME, MODE_PRIVATE);
        long currentTime = timeSharedPreferences.getLong(WWRConstants.SHARED_PREFERENCES_SYSYTEM_TIME_KEY, -1);

        // Set the mock time, if there was one
        if (isMockTime(currentTime) && !readMockedDate) {
            Instant instant = Instant.ofEpochMilli(currentTime);
            LocalDateTime mockDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
            TimeMachine.useFixedClockAt(mockDateTime);
            sCurrentDateTime = TimeMachine.now();
            readMockedDate = true;

            Log.d(TAG, "Mock date time is: " + sCurrentDateTime.toString());
        } else {
            // Use the real current time
            sCurrentDateTime = LocalDateTime.now();
        }

        // Get the total daily steps. This number may be different than what the
        // fitness service has if the mock screen add steps button was used earlier.
        SharedPreferences stepsSharedPreferences =
                getSharedPreferences(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_FILE_NAME, MODE_PRIVATE);
        long totalDailySteps = stepsSharedPreferences.getLong(WWRConstants.SHARED_PREFERENCES_TOTAL_STEPS_KEY, 0);
        MockFitnessService.this.setDailyStepCount(totalDailySteps);
    }

    @Override
    public void updateStepCount() {
        // Arbitrary step count increment
        // If the calendar day has changed, reset the step count
        if (!areSameDay(sCurrentDateTime, LocalDateTime.now()))
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

    public static boolean isMockTime(long time) {
        return time != WWRConstants.NO_MOCK_TIME;
    }

    /**
     * Returns true if the two date objects are on the same calendar day, false otherwise.
     *
     * @param before
     * @param after
     * @return
     */
    public static boolean areSameDay(LocalDateTime before, LocalDateTime after) {
        int dayDiff = Math.abs(after.getDayOfYear() - before.getDayOfYear());
        return dayDiff > 0;
    }
}
