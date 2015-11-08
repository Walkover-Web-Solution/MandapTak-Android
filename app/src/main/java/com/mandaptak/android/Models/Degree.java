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
public class Degree {
  String degreeName;
  Boolean selected;
  ParseObject degreeObj;
}
