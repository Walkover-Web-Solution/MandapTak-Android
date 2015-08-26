package com.mandaptak.android.Models;

import com.parse.ParseObject;

public class LocationPreference {

    private String locationName;
    private int locationType;
    private Boolean selected = false;
    private ParseObject parseObject;
    private ParseObject country;
    private ParseObject state;
    private ParseObject city;

    public LocationPreference(String locationName, int locationType, Boolean selected, ParseObject parseObject, ParseObject country, ParseObject state, ParseObject city) {
        this.locationName = locationName;
        this.locationType = locationType;
        this.selected = selected;
        this.parseObject = parseObject;
        this.country = country;
        this.state = state;
        this.city = city;
    }

    public LocationPreference() {
    }

    public ParseObject getCountry() {
        return country;
    }

    public void setCountry(ParseObject country) {
        this.country = country;
    }

    public Boolean isSelected() {
        return selected;
    }

    public void setIsSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public int getLocationType() {
        return locationType;
    }

    public void setLocationType(int locationType) {
        this.locationType = locationType;
    }

    public ParseObject getParseObject() {
        return parseObject;
    }

    public void setParseObject(ParseObject parseObject) {
        this.parseObject = parseObject;
    }

    public ParseObject getState() {
        return state;
    }

    public void setState(ParseObject state) {
        this.state = state;
    }

    public ParseObject getCity() {
        return city;
    }

    public void setCity(ParseObject city) {
        this.city = city;
    }
}
