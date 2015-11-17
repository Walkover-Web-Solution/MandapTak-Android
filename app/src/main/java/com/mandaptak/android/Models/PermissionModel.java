package com.mandaptak.android.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public class PermissionModel {

  private String number;
  private String relation;
  private String date;
  private String profileId;
  private String userId;
  private boolean isCurrentUser;
  private int balance;
}
