package com.mandaptak.android.Models;


public class LocationPreference {

    String locationName;
    int locationType;
    String locatinoId;
    Boolean selected=false;

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
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

    public String getLocatinoId() {
        return locatinoId;
    }

    public void setLocatinoId(String locatinoId) {
        this.locatinoId = locatinoId;
    }
}
