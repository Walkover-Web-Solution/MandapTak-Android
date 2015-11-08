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
public class UndoModel implements Serializable {

  private ParseObject profileParseObject = null;
  private int actionPerformed = -1;
}
