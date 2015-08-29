package com.mandaptak.android.Models;

public class PermissionModel {
    private String number;
    private String relation;
    private String date;

    public PermissionModel(String number, String relation, String date) {
        this.number = number;
        this.relation = relation;
        this.date = date;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
