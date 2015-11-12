package com.mandaptak.android.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("City")
public class City extends ParseObject {


  public City() {
    super();
  }

  public String getName() {
    return getString("name");
  }

  public void setName(String value) {
    put("name", value);
  }


}
