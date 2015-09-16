package com.mandaptak.android.Models;

import android.graphics.drawable.Drawable;

import com.layer.atlas.Atlas;

import java.io.Serializable;

public class Participant implements Atlas.Participant, Serializable {
    public String userId;
    public String firstName;
    public String lastName;

    public String getId() {
        return userId;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public Drawable getAvatarDrawable() {
        return null;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Contact [userId: ").append(userId).append(", firstName: ").append(firstName).append(", lastName: ").append(lastName).append("]");
        return builder.toString();
    }
}