package com.mandaptak.android.Agent;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
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

  private void importUser() {
    String[] numbers = new String[]{
        "9430457922",
        "9425128104",
        "9414017496",
        "9423397303",
        "9425108425",
        "9425405290",
        "9415032068",
        "9713714076",
        "9425136151",
        "9479871875",
        "9826011746",
        "9926235926",
        "8966902907",
        "9460067402",
        "9826535624",
        "9630902182",
        "8827321964",
        "9479703088",
        "9754544313",
        "9713404089",
        "9406606576",
        "9993094204",
        "9425087424",
        "9406812030",
        "9827311766",
        "8712101861",
        "9425964640",
        "8889955589",
        "9300747767",
        "9768011333",
        "9425065399",
        "9829511564",
        "9413473608",
        "9414621035",
        "9784172100",
        "9785197064",
        "9694598859",
        "9425052011",
        "9926495099",
        "9669100180",
        "9425408757",
        "9977498136",
        "9560822003",
        "9425082157",
        "9826980260",
        "9406613790",
        "9826283235",
        "9752228721",
        "9826016126",
        "9424387512",
        "9993454241",
        "9926273510",
        "9425959552",
        "9827739567",
        "8989678645",
        "8125316003",
        "9405026936",
        "9424072742",
        "9669004546",
        "9424500218",
        "9015484424",
        "9584262667",
        "8823888890",
        "8818884408",
        "8305209150",
        "9406600351",
        "9893703390",
        "7389150348",
        "9619574221",
        "9425044336",
        "9407125973",
        "8878123827",
        "9414568960",
        "9826468789",
        "8007087317",
        "9406523223",
        "9098027534",
        "9826287387",
        "9425044988",
        "9424091333",
        "9893002110",
        "9425345552",
        "9893893831",
        "7798881214",
        "9827536580",
        "9584343444",
        "8522016152",
        "9571202989",
        "9753704444",
        "9595304751",
        "9424066377",
        "9826052424",
        "8965914476",
        "9406650970",
        "9460573628",
        "9926628868",
        "9630338898",
        "9920098209",
        "8806660705",
        "9425666796",
        "8983419353",
        "9300933856",
        "8302524158",
        "9301383446",
        "9987597293",
        "9962563840",
        "9993067355",
        "9926183343",
        "8097274771",
        "9980945001",
        "9039531235",
        "9039568628",
        "9424657711",
        "7387409952"};


    for (String num : numbers) {
      params.clear();
      params.put("mobile", num);
      params.put("relation", "Bachelor");
      params.put("agentId", "2oqPh4Ljnl");
      try {
        Object addNewUserForAgent = ParseCloud.callFunction("addNewUserForAgent", params);

      } catch (ParseException e) {
        e.printStackTrace();
      }
//      ParseCloud.callFunctionInBackground("addNewUserForAgent", params, new FunctionCallback<Object>() {
//        @Override
//        public void done(Object o, ParseException e) {
//          mApp.dialog.dismiss();
//          if (e == null) {
//            getProfiles();
//          } else {
//            mApp.showToast(context, e.getMessage());
//            e.printStackTrace();
//          }
//        }
//      });


    }
  }
}
