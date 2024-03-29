package com.mandaptak.android.FullProfile;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mandaptak.android.R;
import com.mandaptak.android.Utils.CommonUtils;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class BasicProfileInfo extends Fragment {
  CommonUtils mApp;
  private TextView gender, datePicker, timepicker, displayName;
  private TextView placeOfBirth, currentLocation;
  private View rootView;
  private String newName, newGender;
  private Calendar newTOB, newDOB;
  private ParseObject newPOB, newCurrentLocation;
  private Context context;
  private String parseObjectId;
  protected Boolean isVisible = false;

  public BasicProfileInfo() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    Log.e("oncreateView", "" + this.isVisible);
    init(inflater, container);
    if (mApp.isNetworkAvailable())
      getParseData();
    return rootView;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getActivity().getIntent() != null)
      parseObjectId = getActivity().getIntent().getStringExtra("parseObjectId");
    else
      getActivity().finish();
  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    this.isVisible = isVisibleToUser;
    Log.e("setvisisblity hint", "" + this.isVisible);
  }

  private void init(LayoutInflater inflater, ViewGroup container) {
    context = getActivity();
    mApp = new CommonUtils(context);
    newDOB = Calendar.getInstance();
    newTOB = Calendar.getInstance();
    newDOB.setTimeZone(TimeZone.getTimeZone("UTC"));
    newTOB.setTimeZone(TimeZone.getTimeZone("UTC"));
    rootView = inflater.inflate(R.layout.basic_profile_info, container, false);
    gender = (TextView) rootView.findViewById(R.id.gender);
    datePicker = (TextView) rootView.findViewById(R.id.date_of_birth);
    timepicker = (TextView) rootView.findViewById(R.id.time_of_birth);
    placeOfBirth = (TextView) rootView.findViewById(R.id.place_of_birth);
    currentLocation = (TextView) rootView.findViewById(R.id.current_location);
    displayName = (TextView) rootView.findViewById(R.id.display_name);
  }

  private void getParseData() {
    Log.e("basic profile", parseObjectId);
    if (mApp.isNetworkAvailable())
      if (parseObjectId != null) {
        mApp.show_PDialog("Loading..");
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Profile");
        parseQuery.include("currentLocation.Parent.Parent");
        parseQuery.include("placeOfBirth.Parent.Parent");
        parseQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        parseQuery.getInBackground(parseObjectId, new GetCallback<ParseObject>() {
          @Override
          public void done(ParseObject parseObject, ParseException e) {
            if (e == null) {
              populateUserInfo(parseObject);
            } else {
              e.printStackTrace();
            }
            mApp.dialog.dismiss();
          }
        });
      } else {
        getActivity().finish();
      }
  }

  protected void populateUserInfo(ParseObject parseObject) {
    try {
      newName = parseObject.getString("name");
      newGender = parseObject.getString("gender");
      newDOB.setTime(parseObject.getDate("dob"));
      newTOB.setTime(parseObject.getDate("tob"));
      newCurrentLocation = parseObject.getParseObject("currentLocation");
      newPOB = parseObject.getParseObject("placeOfBirth");
      if (newName != null) {
        displayName.setText(newName);
      }
      if (newGender != null) {
        gender.setText(newGender);
      }
      if (newDOB != null) {
        DateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String subdateStr = df.format(newDOB.getTime());
        datePicker.setText(subdateStr);
      }
      if (newTOB != null) {
        DateFormat df = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String subdateStr = df.format(newTOB.getTime());
        timepicker.setText(subdateStr);
      }
      if (newPOB != null) {
        placeOfBirth.setText(newPOB.getString("name")
            + ", " + newPOB.getParseObject("Parent").getString("name") + ", " + newPOB.getParseObject("Parent").getParseObject("Parent").getString("name"));
      }
      if (newCurrentLocation != null) {
        currentLocation.setText(newCurrentLocation.getString("name")
            + ", " + newCurrentLocation.getParseObject("Parent").getString("name") + ", " + newCurrentLocation.getParseObject("Parent").getParseObject("Parent").getString("name"));
      }
    } catch (Exception e1) {
      e1.printStackTrace();
    }
  }
}
