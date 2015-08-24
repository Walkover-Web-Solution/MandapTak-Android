package com.mandaptak.android.Models;

import com.parse.ParseObject;

public class ParseNameModel {
    private String name;
    private ParseObject parseObject;

    public ParseNameModel(String name, ParseObject parseObject) {
        this.name = name;
        this.parseObject = parseObject;
    }

    public ParseNameModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ParseObject getParseObject() {
        return parseObject;
    }

    public void setParseObject(ParseObject parseObject) {
        this.parseObject = parseObject;
    }
}
