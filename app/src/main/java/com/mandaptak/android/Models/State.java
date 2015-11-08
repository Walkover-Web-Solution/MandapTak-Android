package com.mandaptak.android.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("State")
public class State extends ParseObject {
  public State() {
    super();
  }

  public String getName() {
    return getString("name");
  }

  public void setName(String value) {
    put("name", value);
  }



}
