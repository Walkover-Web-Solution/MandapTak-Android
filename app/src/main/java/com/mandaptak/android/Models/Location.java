package com.mandaptak.android.Models;

import com.parse.ParseObject;

public class Location {
  private String city;
  private ParseObject cityObject;
  private String state;
  private String country;

  public Location(String city, ParseObject cityObject, String state, String country) {
    this.city = city;
    this.cityObject = cityObject;
    this.state = state;
    this.country = country;
  }

  public Location() {
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public ParseObject getCityObject() {
    return cityObject;
  }

  public void setCityObject(ParseObject cityObject) {
    this.cityObject = cityObject;
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
