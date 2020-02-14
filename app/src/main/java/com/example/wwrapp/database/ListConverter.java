package com.example.wwrapp.database;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;


/**
 * Converts Lists of objects for use in the Room database
 * Credits go to: https://medium.com/@amit.bhandari/storing-java-objects-other-than-primitive-types-in-room-database-11e45f4f6d22
 */
public class ListConverter {

    @TypeConverter
    public static List<String> fromString(String str) {
        Type listType = new TypeToken<List<String>>(){}.getType();
        return new Gson().fromJson(str, listType);
    }

    @TypeConverter
    public static String fromList(List<String> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
