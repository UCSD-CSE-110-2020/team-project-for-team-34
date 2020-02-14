package com.example.wwrapp.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a route that the user has walked
 */
@Entity(tableName = "route_table")
@TypeConverters({LocalDateTimeConverter.class, ListConverter.class})

public class Route implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    // All the information that a Route should store
    private String routeName;
    private String startingPoint;
    private LocalDateTime date;
    private String duration;
    private long steps;
    private double miles;

    // Additional information a Route can store
    private List<String> tags;
    private boolean isFavorite;
    private String notes;


    /**
     * Constructor with additional information: tags, favorite, notes
     * @param routeName
     * @param startingPoint
     * @param date
     * @param duration
     * @param steps
     * @param miles
     * @param tags
     * @param isFavorite
     * @param notes
     */
    public Route(String routeName, String startingPoint, LocalDateTime date, String duration,
                 long steps, double miles, List<String> tags, boolean isFavorite, String notes) {
        this.routeName = routeName;
        this.startingPoint = startingPoint;
        this.date = date;
        this.duration = duration;
        this.steps = steps;
        this.miles = miles;
        this.tags = tags;
        this.isFavorite = isFavorite;
        this.notes = notes;
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

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Route:\n");
        stringBuilder.append("Route name: ").append(this.getRouteName()).append("\n");
        stringBuilder.append("Route starting point: ").append(this.getStartingPoint()).append("\n");
        stringBuilder.append("Route date: ").append(this.getDate()).append("\n");
        stringBuilder.append("Route duration: ").append(this.getDuration()).append("\n");
        stringBuilder.append("Route steps: ").append(this.getSteps()).append("\n");
        stringBuilder.append("Route miles: ").append(this.getMiles()).append("\n");
        stringBuilder.append("Route tags: ");
        for (String tag : this.getTags()) {
            stringBuilder.append(tag).append(",");
        }
        stringBuilder.append("\n");
        stringBuilder.append("Route favorite? ").append(this.isFavorite).append("\n");
        stringBuilder.append("Route notes: ").append(this.getNotes());

        return stringBuilder.toString();
    }


}
