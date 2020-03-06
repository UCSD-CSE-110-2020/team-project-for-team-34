package com.example.wwrapp.models;

import java.util.List;

/**
 * Builder pattern for Route objects
 */
public class RouteBuilder {
    private Route route; // route to build

    public RouteBuilder() {
        // Initialize an empty route: fields will be added later
        route = new Route();
    }

    /**
     * Returns the built Route
     * @return the built Route
     */
    public Route getRoute() {
        return route;
    }

    public RouteBuilder setRouteName(String name) {
        route.setRouteName(name);
        return this;
    }

    public RouteBuilder setStartingPoint(String startingPoint) {
        route.setStartingPoint(startingPoint);
        return this;
    }

    public RouteBuilder setSteps(long steps) {
        route.setSteps(steps);
        return this;
    }

    public RouteBuilder setMiles(double miles) {
        route.setMiles(miles);
        return this;
    }

    public RouteBuilder setFavorite(boolean favorite) {
        route.setFavorite(favorite);
        return this;
    }

    public RouteBuilder setNotes(String notes) {
        route.setNotes(notes);
        return this;
    }

    public RouteBuilder setTags(List<String> tags) {
        route.setTags(tags);
        return this;
    }

    public RouteBuilder setDuration(String duration) {
        route.setDuration(duration);
        return this;
    }
}
