package com.mandaptak.android.Matches;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mandaptak.android.Models.MatchesModel;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.java.com.mindscapehq.android.raygun4android.RaygunClient;
import me.iwf.photopicker.utils.ImageModel;
import me.iwf.photopicker.utils.Prefs;

public class ViewProfilePage extends AppCompatActivity {
  private TextView slideName, slideHeight, slideReligion, slideDesignation, slideTraits;
  private TextView salary, industry, education, weight, currentLocation, viewFullProfile;
  private ArrayList<ImageModel> userProfileImages = new ArrayList<>();
  private ImageButton slideLike;
  private TwoWayView profileImages;
  private RelativeLayout slidingLayout;
  String parseObjectId;
  Common mApp;
  Context context;
  MatchesModel model = new MatchesModel();
  public final static int REQUEST_CODE = 11;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.view_profile);
    context = ViewProfilePage.this;
    mApp = (Common) context.getApplicationContext();
    init();
    if (getIntent().hasExtra("profile")) {
      model = (MatchesModel) getIntent().getSerializableExtra("profile");
      parseObjectId = model.getProfileId();
    } else {
      this.finish();
    }
    try {
      getSupportActionBar().hide();
    } catch (Exception ignored) {
    }
    getParseData();
    setTraits();
    viewFullProfile.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

      }
    });
  }

  private void init() {
    slidingLayout = (RelativeLayout) findViewById(R.id.sliding_layout);
    slideDesignation = (TextView) findViewById(R.id.slide_designation);
    slideHeight = (TextView) findViewById(R.id.slide_height);
    slideName = (TextView) findViewById(R.id.slide_name);
    slideReligion = (TextView) findViewById(R.id.slide_religion);
    slideTraits = (TextView) findViewById(R.id.slide_traits_match);
    slideLike = (ImageButton) findViewById(R.id.slide_like);
    profileImages = (TwoWayView) findViewById(R.id.list);
    currentLocation = (TextView) findViewById(R.id.current_location);
    weight = (TextView) findViewById(R.id.weight);
    industry = (TextView) findViewById(R.id.industry);
    slideDesignation = (TextView) findViewById(R.id.slide_designation);
    salary = (TextView) findViewById(R.id.salary);
    slideLike = (ImageButton) findViewById(R.id.slide_like);
    education = (TextView) findViewById(R.id.education);
    viewFullProfile = (TextView) findViewById(R.id.view_full_profile);
  }

  private void getParseData() {
    if (mApp.isNetworkAvailable(context)) {
      if (parseObjectId != null) {
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Profile");
        parseQuery.include("industryId");
        parseQuery.include("currentLocation");
        parseQuery.include("religionId");
        parseQuery.include("education1.degreeId");
        parseQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        parseQuery.getInBackground(parseObjectId, new GetCallback<ParseObject>() {
          @Override
          public void done(ParseObject parseObject, ParseException e) {
            if (e == null) {
              showData(parseObject);
              getAllPhotos(parseObject);
            } else {
              e.printStackTrace();
            }
          }
        });
      }
    }

  }

  private void showData(ParseObject object) {
    try {
      String temp = object.getString("name") != null ? object.getString("name") : "";
      slideName.setText(temp);
      temp = object.getString("designation") != null ? object.getString("designation") : "";
      slideDesignation.setText(temp);
      slideHeight.setText("" + object.getInt("age"));
      slideReligion.setText(object.getParseObject("religionId").getString("name"));
      slideTraits.setText(object.getString("name"));
      salary.setText("" + object.getLong("package"));
      slideDesignation.setText(object.getString("designation"));
      education.setText(object.getParseObject("education1").getParseObject("degreeId").getString("name"));
      weight.setText(object.getInt("weight") + " KG");
      industry.setText(object.getParseObject("industryId").getString("name"));
      currentLocation.setText(object.getParseObject("currentLocation").getString("name"));
    } catch (Exception e) {

    }
  }

  void setTraits() {
    try {
      HashMap<String, Object> params = new HashMap<>();
      if (ParseObject.createWithoutData("Profile", Prefs.getProfileId(context)).fetchIfNeeded().getString("gender").equalsIgnoreCase("Male")) {
        params.put("boyProfileId", Prefs.getProfileId(context));
        params.put("girlProfileId", parseObjectId);
      } else {
        params.put("boyProfileId", parseObjectId);
        params.put("girlProfileId", Prefs.getProfileId(context));
      }

      ParseCloud.callFunctionInBackground("matchKundli", params, new FunctionCallback<Object>() {
        @Override
        public void done(Object o, ParseException e) {
          if (e == null) {
            if (o != null) {
              slideTraits.setText(String.valueOf(o) + " Traits Matching");
            }
          } else {
            e.printStackTrace();
            RaygunClient.Send(new Throwable(e.getMessage() + " traits_function"));
          }
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void getAllPhotos(final ParseObject parseProfileObject) {
    ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Photo");
    parseQuery.whereEqualTo("profileId", parseProfileObject);
    parseQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
    parseQuery.findInBackground(new FindCallback<ParseObject>() {
      @Override
      public void done(List<ParseObject> list, ParseException e) {
        if (e == null) {
          if (list != null && list.size() > 0) {
            userProfileImages.clear();
            for (ParseObject model : list) {
              try {
                ImageModel imageModel = new ImageModel();
                ParseFile file = model.getParseFile("file");
                imageModel.setLink(file.getUrl());
                imageModel.setIsPrimary(false);
                imageModel.setParseObject(model.getObjectId());
                userProfileImages.add(imageModel);
              } catch (Exception e1) {
                e1.printStackTrace();
              }
            }
//            profileImages.setAdapter(new UserImagesAdapter(context, ViewProfilePage.this, userProfileImages));

          } else {
            //This has to be handled proper this happens when there is no entry in the photo
            //table for this profile.
            Log.e("viewProfile", parseProfileObject.getObjectId());
          }
        } else {
          //this also has to be handled as per condition till then wait...
          e.printStackTrace();
          Log.e("viewProfile", "handle the el prt for excp");

        }
      }
    });
  }

  public void previewPhoto(Intent intent) {
    startActivityForResult(intent, REQUEST_CODE);
  }
}
