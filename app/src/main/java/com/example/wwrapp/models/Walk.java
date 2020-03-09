package com.example.wwrapp.models;

import java.io.Serializable;

/**
 * Represents a walk walked by the user
 */
public class Walk implements Serializable {
    private long steps;
    private double miles;
    private String date;
    private String duration;

    public Walk() {
    }

    public long getSteps() {
        return steps;
    }

    public void setSteps(long steps) {
        this.steps = steps;
    }

    public double getMiles() {
        return miles;
    }

    public void setMiles(double miles) {
        this.miles = miles;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Walk:\n");
        stringBuilder.append("Steps: ");
        stringBuilder.append(this.getSteps());
        stringBuilder.append("\nMiles: ");
        stringBuilder.append(this.getMiles());
        stringBuilder.append("\nDate: ");
        stringBuilder.append(this.getDate());
        stringBuilder.append("\nDuration: ");
        stringBuilder.append(this.getDuration());
        return stringBuilder.toString();
    }
}
