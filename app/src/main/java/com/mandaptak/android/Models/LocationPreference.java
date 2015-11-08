package com.mandaptak.android.Models;

import com.parse.ParseObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public class LocationPreference {

  private String locationName;
  private int locationType;
  private Boolean selected = false;
  private ParseObject parseObject;
  private ParseObject country;
  private ParseObject state;
  private ParseObject city;
}
