package com.mandaptak.android.Agent;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.mandaptak.android.Adapter.AgentProfilesAdapter;
import com.mandaptak.android.Models.AgentProfileModel;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
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
                    mApp.show_PDialog(context, "Creating User...");
                    try {
                        HashMap<String, Object> params = new HashMap<>();
                        params.put("mobile", "9856852415");
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
        query.include("profileId");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        profileModels.clear();
                        for (ParseObject parseObject : list) {
                            try {
                                AgentProfileModel agentProfileModel = new AgentProfileModel();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                                String date = sdf.format(parseObject.fetchIfNeeded().getParseObject("profileId").fetchIfNeeded().getCreatedAt());
                                agentProfileModel.setCreateDate(date);
                                agentProfileModel.setIsActive(parseObject.fetchIfNeeded().getParseObject("profileId").fetchIfNeeded().getBoolean("isActive"));
                                agentProfileModel.setImageUrl(parseObject.fetchIfNeeded().getParseObject("profileId").fetchIfNeeded().getParseFile("profilePic").getUrl());
                                agentProfileModel.setName(parseObject.fetchIfNeeded().getParseObject("profileId").fetchIfNeeded().getString("name"));
                                profileModels.add(agentProfileModel);
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
