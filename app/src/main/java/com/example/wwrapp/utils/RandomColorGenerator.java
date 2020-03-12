package com.example.wwrapp.utils;

import android.graphics.Color;

import java.util.Random;

public class RandomColorGenerator {
    static private Random random = new Random();

//    public RandomColorGenerator() {
//        random = new Random();
//    }

    public static int generateRandomNum() {return random.nextInt();}
    public static Color generateRandomColor() {
        return Color.valueOf(random.nextInt());
    }
}
