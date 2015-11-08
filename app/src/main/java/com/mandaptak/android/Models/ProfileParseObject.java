package com.mandaptak.android.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

@ParseClassName("Profile")
public class ProfileParseObject extends ParseObject {

  public ProfileParseObject() {
    super();
  }

  public String getName() {
    return getString("name");
  }

  public void setName(String name) {
    put("name", name);
  }

  public String getGender() {
    return getString("gender");
  }

  public void setGender(String value) {
    put("gender", value);
  }

  public Date getDateOfBirth() {
    return getDate("dob");
  }

  public void setDateOfBirth(Date value) {
    put("dob", value);
  }

  public Date getTimeOfBirth() {
    return getDate("tob");
  }

  public void setTimeOfBirth(Date value) {
    put("tob", value);
  }

  public ParseObject getPlaceOfBirth() {
    return getParseObject("placeOfBirth");
  }

  public void setPlaceOfBirth(ParseObject value) {
    put("placeOfBirth", value);
  }

  public ParseObject getCurrentLocation() {
    return getParseObject("currentLocation");
  }

  public void setCurrentLocation(ParseObject value) {
    put("currentLocation", value);
  }

  public int getWeight() {
    return getInt("weight");
  }

  public void setWeight(int value) {
    put("weight", value);
  }

  public int getHeight() {
    return getInt("height");
  }

  public void setHeight(int value) {
    put("height", value);
  }

  public ParseObject getReligion() {
    return getParseObject("religionId");
  }

  public void setReligion(ParseObject value) {
    put("religionId", value);
  }

  public ParseObject getCaste() {
    return getParseObject("casteId");
  }

  public void setCaste(ParseObject value) {
    put("casteId", value);
  }

  public ParseObject getGotra() {
    return getParseObject("gotraId");
  }

  public void setGotra(ParseObject value) {
    put("gotraId", value);
  }

  public int getManglik() {
    return getInt("manglik");
  }

  public void setManglik(int value) {
    put("manglik", value);
  }

  public ParseObject getIndustry() {
    return getParseObject("industryId");
  }

  public void setIndustry(ParseObject value) {
    put("industryId", value);
  }

  public String getDesignation() {
    return getString("designation");
  }

  public void setDesignation(String value) {
    put("designation", value);
  }

  public String getPlaceOfWork() {
    return getString("placeOfWork");
  }

  public void setPlaceOfWork(String value) {
    put("placeOfWork", value);
  }

  public long getPackage() {
    return getLong("package");
  }

  public void setPackage(long value) {
    put("package", value);
  }

  public ParseObject getEducation1() {
    return getParseObject("education1");
  }

  public void setEducation1(ParseObject value) {
    put("education1", value);
  }

  public ParseObject getEducation2() {
    return getParseObject("education2");
  }

  public void setEducation2(ParseObject value) {
    put("education2", value);
  }

  public ParseObject getEducation3() {
    return getParseObject("education3");
  }

  public void setEducation3(ParseObject value) {
    put("education3", value);
  }

  public int getWorkAfterMarriage() {
    return getInt("workAfterMarriage");
  }

  public void setWorkAfterMarriage(int value) {
    put("workAfterMarriage", value);
  }
}
