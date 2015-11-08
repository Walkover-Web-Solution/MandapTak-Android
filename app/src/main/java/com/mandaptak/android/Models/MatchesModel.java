package com.mandaptak.android.Models;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public class MatchesModel implements Serializable {

  private String url;
  private String name;
  private String religion;
  private String work;
  private String userId;
  private String profileId;
}
