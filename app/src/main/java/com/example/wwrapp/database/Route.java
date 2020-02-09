package com.example.wwrapp.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.time.LocalDateTime;

/**
 * Represents a route that the user has walked
 */
@Entity(tableName = "route_table")
@TypeConverters(LocalDateTimeConverter.class)

public class Route {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String routeName;
    private String startingPoint;

    private LocalDateTime date;
    private String duration;
    private int steps;
    private double miles;

    public Route(String routeName, String startingPoint, LocalDateTime date, String duration, int steps, double miles) {
        this.routeName = routeName;
        this.startingPoint = startingPoint;
        this.date = date;
        this.duration = duration;
        this.steps = steps;
        this.miles = miles;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(String startingPoint) {
        this.startingPoint = startingPoint;
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

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public double getMiles() {
        return miles;
    }

    public void setMiles(double miles) {
        this.miles = miles;
    }


}
