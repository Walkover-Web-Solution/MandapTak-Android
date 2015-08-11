package com.mandaptak.android.Models;

public class Location {
    private String placeId;
    private String city;
    private String state;
    private String country;
    private String description;

    public Location(String description, String placeId, String city, String state, String country) {
        this.placeId = placeId;
        this.city = city;
        this.state = state;
        this.country = country;
        this.description = description;
    }

    public Location() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
