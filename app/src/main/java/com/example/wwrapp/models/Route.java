package com.example.wwrapp.models;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Represents a route that the user has walked
 */
@IgnoreExtraProperties
public class Route implements Serializable {

    public static final String FIELD_NAME = "routeName";
    public static final String FIELD_STARTING_POINT = "startingPoint";
    public static final String FIELD_DATE = "date";
    public static final String FIELD_STEPS = "steps";
    public static final String FIELD_MILES = "miles";
    public static final String FIELD_FAVORITE = "favorite";
    public static final String FIELD_TAGS = "tags";
    public static final String FIELD_NOTES = "notes";
    public static final String FIELD_OWNER_EMAIL = "ownerEmail";
    public static final String FIELD_OWNER_NAME = "ownerName";
    public static final String FIELD_WALKERS = "walkers";


    // Basic information that a Route should store
    private String routeName;
    private String startingPoint;
    private long steps;
    private double miles;
    private String dateOfLastWalk;

    // Additional Route characteristics
    private List<String> tags;
    private boolean isFavorite;
    private String notes;

    // Properties of Routes that relate to users
    private String ownerName;
    private String ownerEmail;
    private Map<String, Walk> walkers; // maps emails of users to their walk stats for this Route
    private List<String> favoriters; // a list of people who have favorited this Route

    // Not required, but informative
    private String durationOfLastWalk;

    /**
     * Empty constructor required for Firestore
     */
    public Route() {
    }


    // Getters/setters for basic info
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

    public void setDurationOfLastWalk(String duration) {
        this.durationOfLastWalk = duration;
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

    public String getDateOfLastWalk() {
        return dateOfLastWalk;
    }

    public void setDateOfLastWalk(String dateOfLastWalk) {
        this.dateOfLastWalk = dateOfLastWalk;
    }

    // Getters/setters for additional info

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Getters/setters for external properties

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Map<String, Walk> getWalkers() {
        return walkers;
    }

    public void setWalkers(Map<String, Walk> walkers) {
        this.walkers = walkers;
    }

    public List<String> getFavoriters() {
        return favoriters;
    }

    public void setFavoriters(List<String> favoriters) {
        this.favoriters = favoriters;
    }

    // Extra getters/setters
    public String getDurationOfLastWalk() {
        return durationOfLastWalk;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Route:\n");
        stringBuilder.append("Route name: ").append(this.getRouteName()).append("\n");
        stringBuilder.append("Route starting point: ").append(this.getStartingPoint()).append("\n");
//        stringBuilder.append("Route date: ").append(this.getDate()).append("\n");
        stringBuilder.append("Route duration: ").append(this.getDurationOfLastWalk()).append("\n");
        stringBuilder.append("Route steps: ").append(this.getSteps()).append("\n");
        stringBuilder.append("Route miles: ").append(this.getMiles()).append("\n");
        stringBuilder.append("Route tags: ");
        List<String> tags = this.getTags();
        if (tags != null) {
            for (String tag : tags) {
                stringBuilder.append(tag).append(",");
            }

        }
        stringBuilder.append("\n");
        stringBuilder.append("Route favorite? ").append(this.isFavorite).append("\n");
        stringBuilder.append("Route notes: ").append(this.getNotes());
        return stringBuilder.toString();
    }
}
