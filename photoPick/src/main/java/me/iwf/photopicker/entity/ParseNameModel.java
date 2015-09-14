package me.iwf.photopicker.entity;

import java.io.Serializable;

public class ParseNameModel implements Serializable {
    private String name;
    private String className;
    private String parseObjectId;

    public ParseNameModel(String name, String className, String parseObjectId) {
        this.name = name;
        this.className = className;
        this.parseObjectId = parseObjectId;
    }

    public ParseNameModel() {
    }

    public String getParseObjectId() {
        return parseObjectId;
    }

    public void setParseObjectId(String parseObjectId) {
        this.parseObjectId = parseObjectId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
