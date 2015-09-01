package com.mandaptak.android.Models;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.layer.atlas.Atlas;

public class Participant implements Atlas.Participant {
    public String userId;
    public String firstName;
    public String lastName;
    public Bitmap avatarImg;
    public Drawable avtatar;

   /* public Participant(String userId, String firstName, String lastName, Bitmap avatarImg, Drawable avtatar) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatarImg = avatarImg;
        this.avtatar = avtatar;
    }*/

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setAvatarImg(Bitmap avatarImg) {
        this.avatarImg = avatarImg;
    }

    public void setAvtatar(Drawable avtatar) {
        this.avtatar = avtatar;
    }

    public String getId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public Drawable getAvatarDrawable() {
        return null;
    }

    public Bitmap getAvatarImage() {
        return avatarImg;
    }
}