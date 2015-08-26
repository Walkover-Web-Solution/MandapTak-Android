package me.iwf.photopicker.utils;

import java.io.Serializable;

public class ImageModel implements Serializable {
    private String link;
    private boolean isPrimary;
    private String parseObjectId;

    public ImageModel(String link, boolean isPrimary, String parseObjectId) {
        this.link = link;
        this.isPrimary = isPrimary;
        this.parseObjectId = parseObjectId;
    }

    public ImageModel() {
    }

    public String getParseObjectId() {
        return parseObjectId;
    }

    public void setParseObject(String parseObjectId) {
        this.parseObjectId = parseObjectId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }
}
