package com.mandaptak.android.Agent;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.mandaptak.android.Adapter.AgentProfilesAdapter;
import com.mandaptak.android.Models.AgentProfileModel;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.mandaptak.android.Views.ExtendedEditText;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AgentActivity extends AppCompatActivity {
  Common mApp;
  Context context;
  ListView profileList;
  AgentProfilesAdapter adapter;
  ArrayList<AgentProfileModel> profileModels = new ArrayList<>();
  TextView addProfile, availableCredits;
  int creditBalance = 0;
  ProgressBar progressBar;
  private HashMap<String, Object> params = new HashMap<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_agent);
    context = this;
    mApp = (Common) getApplicationContext();
    try {
      getSupportActionBar().setTitle("Representative Home");
    } catch (Exception ignored) {
    }
    progressBar = (ProgressBar) findViewById(R.id.progress);
    profileList = (ListView) findViewById(R.id.list);
    addProfile = (TextView) findViewById(R.id.add_button);
    availableCredits = (TextView) findViewById(R.id.available_credits);
    addProfile.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (creditBalance >= 20) {
          if (mApp.isNetworkAvailable(context)) {
            final View permissionDialog = View.inflate(context, R.layout.add_user_dialog, null);
            final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setView(permissionDialog);
            final ExtendedEditText etNumber = (ExtendedEditText) permissionDialog.findViewById(R.id.number);
            AppCompatButton giveButton = (AppCompatButton) permissionDialog.findViewById(R.id.give_button);
            final Spinner relations = (Spinner) permissionDialog.findViewById(R.id.relations);
            relations.setAdapter(ArrayAdapter.createFromResource(context,
                R.array.relation_array, R.layout.location_list_item));
            etNumber.setPrefix("+91");
            giveButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {

                String mobileNumber = etNumber.getText().toString();
                if (!mobileNumber.equals("")) {
                  if (mobileNumber.length() == 10) {
                    alertDialog.dismiss();
                    if (mApp.isNetworkAvailable(context)) {
                      mApp.show_PDialog(context, "Creating User...");
                      HashMap<String, Object> params = new HashMap<>();
                      params.put("mobile", mobileNumber);
                      params.put("relation", relations.getSelectedItem());
                      params.put("agentId", ParseUser.getCurrentUser().getObjectId());
                      ParseCloud.callFunctionInBackground("addNewUserForAgent", params, new FunctionCallback<Object>() {
                        @Override
                        public void done(Object o, ParseException e) {
                          mApp.dialog.dismiss();
                          if (e == null) {
                            getProfiles();
                          } else {
                            mApp.showToast(context, e.getMessage());
                            e.printStackTrace();
                          }
                        }
                      });
                    }
                  } else {
                    mApp.showToast(context, "Invalid Mobile Number");
                  }
                } else {
                  mApp.showToast(context, "Enter Mobile Number");
                }
              }
            });
            alertDialog.show();
          } else {
            mApp.showToast(context, "Internet connection required");
          }
        } else {
          mApp.showToast(context, "Insufficient Balance");
        }
      }
    });
    adapter = new AgentProfilesAdapter(AgentActivity.this, profileModels);
    profileList.setAdapter(adapter);
    if (mApp.isNetworkAvailable(context)) {
      getProfiles();
    }
  }

  public void getProfiles() {
    profileList.setVisibility(View.GONE);
    progressBar.setVisibility(View.VISIBLE);
    ParseQuery<ParseObject> balanceQuery = new ParseQuery<>("UserCredits");
    balanceQuery.whereEqualTo("userId", ParseUser.getCurrentUser());
    balanceQuery.getFirstInBackground(new GetCallback<ParseObject>() {
      @Override
      public void done(ParseObject parseObject, ParseException e) {
        if (e == null) {
          creditBalance = parseObject.getInt("credits");
        }
        availableCredits.setText(creditBalance + " Credits");
      }
    });
    ParseQuery<ParseObject> query = new ParseQuery<>("UserProfile");
    query.include("profileId");
    query.whereEqualTo("userId", ParseUser.getCurrentUser());
    query.whereEqualTo("relation", "Agent");
    query.findInBackground(new FindCallback<ParseObject>() {
      @Override
      public void done(List<ParseObject> list, ParseException e) {
        if (e == null) {
          if (list.size() > 0) {
            profileModels.clear();
            for (ParseObject parseObject : list) {
              try {
                final ParseObject profileObject = parseObject.getParseObject("profileId");
                boolean isComplete = profileObject.getBoolean("isComplete");
                final AgentProfileModel agentProfileModel = new AgentProfileModel();
                agentProfileModel.setProfileObject(profileObject);
                if (isComplete) {
                  SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
                  String date = sdf.format(profileObject.getUpdatedAt());
                  agentProfileModel.setCreateDate(date);
                  agentProfileModel.setIsActive(profileObject.getBoolean("isActive"));
                  agentProfileModel.setImageUrl(profileObject.getParseFile("profilePic").getUrl());
                  agentProfileModel.setName(profileObject.getString("name"));
                  agentProfileModel.setIsComplete(true);
                } else {
                  SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
                  String date = sdf.format(profileObject.getUpdatedAt());
                  agentProfileModel.setCreateDate(date);
                  agentProfileModel.setIsActive(profileObject.getBoolean("isActive"));
                  agentProfileModel.setImageUrl("android.resource://com.mandaptak.android/drawable/com_facebook_profile_picture_blank_square");
                  agentProfileModel.setName(profileObject.getParseUser("userId").fetchIfNeeded().getUsername());
                  agentProfileModel.setIsComplete(false);
                }
                profileModels.add(agentProfileModel);
              } catch (Exception e1) {
                e1.printStackTrace();
              }
            }
            adapter.notifyDataSetChanged();
          } else {
            profileList.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
          }
        } else {
          profileList.setVisibility(View.GONE);
          progressBar.setVisibility(View.GONE);
          mApp.showToast(context, e.getMessage());
          e.printStackTrace();
        }
        profileList.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
      }
    });
  }
}
