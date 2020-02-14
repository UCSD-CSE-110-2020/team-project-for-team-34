package com.example.wwrapp.database;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a walk walked by the user
 */
public class Walk implements Serializable {
    private long steps;
    private double miles;
    private LocalDateTime date;
    private String duration;

    public Walk(long steps, double miles, LocalDateTime data, String duration) {
        this.steps = steps;
        this.miles = miles;
        this.date = data;
        this.duration = duration;
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
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
