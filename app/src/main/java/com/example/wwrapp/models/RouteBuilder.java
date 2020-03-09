package com.example.wwrapp.models;

import java.util.List;
import java.util.Map;

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
    public RouteBuilder setDateOfLastWalk(String dateOfLastWalk) {
        route.setDateOfLastWalk(dateOfLastWalk);
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

    public RouteBuilder setOwnerName(String name) {
        route.setOwnerName(name);
        return this;
    }


    public RouteBuilder setOwnerEmail(String email) {
        route.setOwnerEmail(email);
        return this;
    }

    public RouteBuilder setWalkers(Map<String, Walk> walkers) {
        route.setWalkers(walkers);
        return this;
    }

    public RouteBuilder setFavoriters(List<String> favoriters) {
        route.setFavoriters(favoriters);
        return this;
    }

    public RouteBuilder setDurationOfLastWalk(String duration) {
        route.setDurationOfLastWalk(duration);
        return this;
    }

}
