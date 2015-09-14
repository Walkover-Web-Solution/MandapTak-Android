package me.iwf.photopicker.entity;

import java.io.Serializable;
import java.util.Calendar;

public class Profile implements Serializable {
    private String name;
    private String gender;
    private Calendar dateOfBirth;
    private Calendar timeOfBirth;
    private ParseNameModel placeOfBirth;
    private ParseNameModel currentLocation;
    private int weight = 0;
    private int height = 0;
    private ParseNameModel religion;
    private ParseNameModel caste;
    private ParseNameModel gotra;
    private int manglik = -1;
    private ParseNameModel industry;
    private String designation;
    private String company;
    private long income = -1;
    private ParseNameModel education1;
    private ParseNameModel education2;
    private ParseNameModel education3;
    private int workAfterMarriage = -1;
    private String biodata;
    private long minBudget = -1;
    private long maxBudget = -1;

    public Profile() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Calendar getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Calendar dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Calendar getTimeOfBirth() {
        return timeOfBirth;
    }

    public void setTimeOfBirth(Calendar timeOfBirth) {
        this.timeOfBirth = timeOfBirth;
    }

    public ParseNameModel getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(ParseNameModel placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public ParseNameModel getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(ParseNameModel currentLocation) {
        this.currentLocation = currentLocation;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public ParseNameModel getReligion() {
        return religion;
    }

    public void setReligion(ParseNameModel religion) {
        this.religion = religion;
    }

    public ParseNameModel getCaste() {
        return caste;
    }

    public void setCaste(ParseNameModel caste) {
        this.caste = caste;
    }

    public ParseNameModel getGotra() {
        return gotra;
    }

    public void setGotra(ParseNameModel gotra) {
        this.gotra = gotra;
    }

    public int getManglik() {
        return manglik;
    }

    public void setManglik(int manglik) {
        this.manglik = manglik;
    }

    public ParseNameModel getIndustry() {
        return industry;
    }

    public void setIndustry(ParseNameModel industry) {
        this.industry = industry;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public long getIncome() {
        return income;
    }

    public void setIncome(long income) {
        this.income = income;
    }

    public ParseNameModel getEducation1() {
        return education1;
    }

    public void setEducation1(ParseNameModel education1) {
        this.education1 = education1;
    }

    public ParseNameModel getEducation2() {
        return education2;
    }

    public void setEducation2(ParseNameModel education2) {
        this.education2 = education2;
    }

    public ParseNameModel getEducation3() {
        return education3;
    }

    public void setEducation3(ParseNameModel education3) {
        this.education3 = education3;
    }

    public int getWorkAfterMarriage() {
        return workAfterMarriage;
    }

    public void setWorkAfterMarriage(int workAfterMarriage) {
        this.workAfterMarriage = workAfterMarriage;
    }

    public String getBiodata() {
        return biodata;
    }

    public void setBiodata(String biodata) {
        this.biodata = biodata;
    }

    public long getMinBudget() {
        return minBudget;
    }

    public void setMinBudget(long minBudget) {
        this.minBudget = minBudget;
    }

    public long getMaxBudget() {
        return maxBudget;
    }

    public void setMaxBudget(long maxBudget) {
        this.maxBudget = maxBudget;
    }

}
