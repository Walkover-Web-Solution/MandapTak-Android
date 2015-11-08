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
public class Location {

  private String city;
  private ParseObject cityObject;
  private String state;
  private String country;
}
