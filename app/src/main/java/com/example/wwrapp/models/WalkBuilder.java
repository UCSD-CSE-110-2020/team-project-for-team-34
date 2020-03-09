package com.example.wwrapp.models;

/**
 * Builder for walk class
 */
public class WalkBuilder {
    private Walk walk;

    public WalkBuilder() {
        walk = new Walk();
    }

    public Walk getWalk() {
        return walk;
    }

    public WalkBuilder setSteps(long steps) {
        walk.setSteps(steps);
        return this;
    }

    public WalkBuilder setMiles(double miles) {
        walk.setMiles(miles);
        return this;
    }

    public WalkBuilder setDate(String date) {
        walk.setDate(date);
        return this;
    }

    public WalkBuilder setDuration(String duration) {
        walk.setDuration(duration);
        return this;
    }
}
