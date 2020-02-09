package com.example.wwrapp.database;

import android.util.Log;

import androidx.room.TypeConverter;

import java.time.LocalDateTime;

public class LocalDateTimeConverter {

    private static String TAG = "LocalDateTimeConverter";

    @TypeConverter
    public static LocalDateTime toDate(String dateString) {
        Log.d(TAG, "dateString is:" + dateString);
        if (dateString == null) {
            return null;
        } else {
            return LocalDateTime.parse(dateString);
        }
    }

    @TypeConverter
    public static String toDateString(LocalDateTime date) {
        Log.d(TAG, "date is:" + date);
        if (date == null) {
            return null;
        } else {
            return date.toString();
        }
    }
}
