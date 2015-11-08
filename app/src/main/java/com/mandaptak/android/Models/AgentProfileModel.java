package com.mandaptak.android.Models;

import com.parse.ParseObject;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public class AgentProfileModel implements Serializable {

  private String name;
  private String imageUrl;
  private String number;
  private boolean isActive;
  private boolean isComplete;
  private String createDate;
  private ParseObject profileObject;
}
