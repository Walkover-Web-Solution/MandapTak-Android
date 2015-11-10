package com.mandaptak.android.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Religion")
public class Religion extends ParseObject {

  public String getId() {
    return this.getObjectId();
  }

  public void setObjectId(String value) {
    put("objectId", value);
  }

  public String getReligion() {
    return getString("Religion");
  }

  public void setReligion(String value) {
    put("Religion", value);
  }

  public String getName() {
    return getString("name");
  }

  public void setName(String value) {
    put("name", value);
  }

}
