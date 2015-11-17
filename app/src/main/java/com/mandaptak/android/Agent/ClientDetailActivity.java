package com.mandaptak.android.Agent;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mandaptak.android.Adapter.ClientDetailsAdapter;
import com.mandaptak.android.Models.PermissionModel;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class ClientDetailActivity extends AppCompatActivity {
  ListView permissionList;
  Common mApp;
  Context context;
  String profileObject;
  TextView availableCredits, empty_view;
  ArrayList<PermissionModel> permissionModels;
  ClientDetailsAdapter clientDetailsAdapter;
  ProgressBar progressBar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_client_detail);
    context = this;
    mApp = (Common) getApplicationContext();
    permissionList = (ListView) findViewById(R.id.permissions_list);
    availableCredits = (TextView) findViewById(R.id.available_credits);
    progressBar = (ProgressBar) findViewById(R.id.progress);
    empty_view = (TextView) findViewById(R.id.empty);
    permissionModels = new ArrayList<>();
    if (getIntent().getExtras() != null) {
      profileObject = (String) getIntent().getExtras().getSerializable("data");
      try {
        getSupportActionBar().setTitle((String) getIntent().getExtras().getSerializable("name"));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      } catch (Exception ignored) {
        ignored.printStackTrace();
      }
      String userId = (String) getIntent().getExtras().getSerializable("userId");
      ParseQuery<ParseObject> balanceQuery = new ParseQuery<>("UserCredits");
      balanceQuery.whereEqualTo("userId", ParseUser.createWithoutData(ParseUser.class, userId));
      balanceQuery.getFirstInBackground(new GetCallback<ParseObject>() {
        @Override
        public void done(ParseObject parseObject, ParseException e) {
          if (e == null) {
            availableCredits.setText(" Balance: " + parseObject.getInt("credits") + " Credits");
          }
        }
      });
    } else {
      this.finish();
    }
    clientDetailsAdapter = new ClientDetailsAdapter(ClientDetailActivity.this, permissionModels);
    permissionList.setAdapter(clientDetailsAdapter);
    getExistingPermissions();
  }

  public void getExistingPermissions() {
    permissionList.setVisibility(View.GONE);
    progressBar.setVisibility(View.VISIBLE);
    try {
      ParseQuery<ParseObject> query = new ParseQuery<>("UserProfile");
      query.whereEqualTo("profileId", ParseObject.createWithoutData("Profile", profileObject));
      String[] relaion = {"Bachelor", "Agent"};
      query.whereNotContainedIn("relation", Arrays.asList(relaion));
      query.include("userId");
      query.findInBackground(new FindCallback<ParseObject>() {
        @Override
        public void done(List<ParseObject> list, ParseException e) {
          if (e == null) {
            progressBar.setVisibility(View.GONE);
            if (list.size() > 0) {
              permissionModels.clear();
              permissionList.setVisibility(View.VISIBLE);
              empty_view.setVisibility(View.GONE);
              for (final ParseObject item : list) {
                final ParseUser user = item.getParseUser("userId");
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.US);
                String date = sdf.format(item.getCreatedAt());
                PermissionModel permissionModel = new PermissionModel();
                permissionModel.setBalance(0);
                String relation = item.getString("relation");
                permissionModel.setRelation(relation);
                permissionModel.setDate("Permission given on: " + date);
                permissionModel.setNumber(user.getUsername());
                permissionModel.setUserId(user.getObjectId());
                permissionModel.setProfileId(profileObject);
                permissionModels.add(permissionModel);
                clientDetailsAdapter.notifyDataSetChanged();
              }
            } else {
              permissionList.setVisibility(View.GONE);
              empty_view.setVisibility(View.VISIBLE);
            }
          } else {
            permissionList.setVisibility(View.GONE);
            empty_view.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            e.printStackTrace();
          }
        }
      });
    } catch (Exception e) {
      mApp.dialog.dismiss();
      e.printStackTrace();
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      onBackPressed();
    }
    return super.onOptionsItemSelected(item);
  }
}
