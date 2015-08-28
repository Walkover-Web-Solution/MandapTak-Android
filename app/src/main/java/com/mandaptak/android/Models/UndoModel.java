package com.mandaptak.android.Models;

import com.parse.ParseObject;

import java.io.Serializable;

public class UndoModel implements Serializable {
    private ParseObject profileParseObject;
    private int actionPerformed = -1;

    public UndoModel(ParseObject profileParseObject, int actionPerformed) {
        this.profileParseObject = profileParseObject;
        this.actionPerformed = actionPerformed;
    }

    public UndoModel() {
        this.profileParseObject = null;
        this.actionPerformed = -1;
    }

    public int getActionPerformed() {
        return actionPerformed;
    }

    public void setActionPerformed(int actionPerformed) {
        this.actionPerformed = actionPerformed;
    }

    public ParseObject getProfileParseObject() {
        return profileParseObject;
    }

    public void setProfileParseObject(ParseObject profileParseObject) {
        this.profileParseObject = profileParseObject;
    }
}
