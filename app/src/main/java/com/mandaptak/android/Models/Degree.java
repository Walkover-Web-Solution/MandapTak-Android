package com.mandaptak.android.Models;


import com.parse.ParseObject;

public class Degree {
    String degreeName;
    Boolean selected;
    ParseObject degreeObj;

    public ParseObject getDegreeObj() {
        return degreeObj;
    }

    public void setDegreeObj(ParseObject degreeObj) {
        this.degreeObj = degreeObj;
    }

    public Degree(String name, ParseObject parseObject, Boolean selected) {
        this.degreeName = name;
        this.degreeObj = parseObject;
        this.selected = selected;
    }

    public String getDegreeName() {
        return degreeName;
    }

    public void setDegreeName(String degreeName) {
        this.degreeName = degreeName;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
