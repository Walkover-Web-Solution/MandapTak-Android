package com.mandaptak.android.Agent;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ListView;
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

public class AgentActivity extends AppCompatActivity {
    Common mApp;
    Context context;
    ListView profileList;
    AgentProfilesAdapter adapter;
    ArrayList<AgentProfileModel> profileModels = new ArrayList<>();
    TextView addProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent);
        context = this;
        mApp = (Common) getApplicationContext();
        getSupportActionBar().setTitle("Agent Home");
        profileList = (ListView) findViewById(R.id.list);
        addProfile = (TextView) findViewById(R.id.add_button);
        addProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mApp.isNetworkAvailable(context)) {
                    final View permissionDialog = View.inflate(context, R.layout.add_user_dialog, null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setView(permissionDialog);
                    final ExtendedEditText etNumber = (ExtendedEditText) permissionDialog.findViewById(R.id.number);
                    AppCompatButton giveButton = (AppCompatButton) permissionDialog.findViewById(R.id.give_button);
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
                                        params.put("agentId", ParseUser.getCurrentUser().getObjectId());
                                        ParseCloud.callFunctionInBackground("addNewUserForAgent", params, new FunctionCallback<Object>() {
                                            @Override
                                            public void done(Object o, ParseException e) {
                                                mApp.dialog.dismiss();
                                                if (e == null) {
                                                    getProfiles();
                                                } else {
                                                    mApp.showToast(context, "Try after some time");
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
            }
        });
        if (mApp.isNetworkAvailable(context)) {
            getProfiles();
        }
    }

    void getProfiles() {
        mApp.show_PDialog(context, "Loading Profiles...");
        ParseQuery<ParseObject> query = new ParseQuery<>("UserProfile");
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
                                final ParseObject profileObject = parseObject.fetchIfNeeded().getParseObject("profileId");
                                final boolean isComplete = profileObject.fetchIfNeeded().getBoolean("isComplete");
                                final AgentProfileModel agentProfileModel = new AgentProfileModel();
                                if (isComplete) {
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                                    String date = sdf.format(profileObject.fetchIfNeeded().getUpdatedAt());
                                    agentProfileModel.setCreateDate(date);
                                    agentProfileModel.setIsActive(profileObject.fetchIfNeeded().getBoolean("isActive"));
                                    agentProfileModel.setImageUrl(profileObject.fetchIfNeeded().getParseFile("profilePic").getUrl());
                                    agentProfileModel.setName(profileObject.fetchIfNeeded().getString("name"));
                                    agentProfileModel.setIsComplete(isComplete);
                                    profileModels.add(agentProfileModel);
                                } else {
                                    ParseUser.getQuery().getInBackground(profileObject.fetchIfNeeded().getString("userId"), new GetCallback<ParseUser>() {
                                        @Override
                                        public void done(ParseUser parseUser, ParseException e) {
                                            if (e == null) {
                                                try {
                                                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                                                    String date = sdf.format(profileObject.fetchIfNeeded().getUpdatedAt());
                                                    agentProfileModel.setCreateDate(date);
                                                    agentProfileModel.setIsActive(profileObject.fetchIfNeeded().getBoolean("isActive"));
                                                    agentProfileModel.setImageUrl("android.resource://com.mandaptak.android/drawable/com_facebook_profile_picture_blank_square");
                                                    agentProfileModel.setName(parseUser.fetchIfNeeded().getUsername());
                                                    agentProfileModel.setIsComplete(isComplete);
                                                    profileModels.add(agentProfileModel);
                                                    adapter.notifyDataSetChanged();
                                                } catch (Exception e1) {
                                                    e1.printStackTrace();
                                                }
                                            }
                                        }
                                    });
                                }

                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                        adapter = new AgentProfilesAdapter(context, profileModels);
                        profileList.setAdapter(adapter);
                    }
                }
                mApp.dialog.dismiss();
            }
        });
    }
}
