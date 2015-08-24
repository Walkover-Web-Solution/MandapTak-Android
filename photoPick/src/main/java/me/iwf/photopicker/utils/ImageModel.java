package me.iwf.photopicker.utils;

import java.io.Serializable;

public class ImageModel implements Serializable {
    private String link;
    private boolean isPrimary;

    public ImageModel(String link, boolean isPrimary) {
        this.link = link;
        this.isPrimary = isPrimary;
    }

    public ImageModel() {
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
