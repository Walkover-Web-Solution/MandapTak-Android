package com.mandaptak.android.Models;

import com.parse.ParseObject;

public class AgentProfileModel {
  private String name;
  private String imageUrl;
  private boolean isActive;
  private boolean isComplete;
  private String createDate;
  private ParseObject profileObject;

  public AgentProfileModel() {
  }

  public ParseObject getProfileObject() {
    return profileObject;
  }

  public void setProfileObject(ParseObject profileObject) {
    this.profileObject = profileObject;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public boolean isActive() {
    return isActive;
  }

  public void setIsActive(boolean isActive) {
    this.isActive = isActive;
  }

  public String getCreateDate() {
    return createDate;
  }

  public void setCreateDate(String createDate) {
    this.createDate = createDate;
  }

  public boolean isComplete() {
    return isComplete;
  }

  public void setIsComplete(boolean isComplete) {
    this.isComplete = isComplete;
  }
}
