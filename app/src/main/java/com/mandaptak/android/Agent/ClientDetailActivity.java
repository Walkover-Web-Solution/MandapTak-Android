package com.mandaptak.android.Agent;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;
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
import java.util.List;
import java.util.Locale;


public class ClientDetailActivity extends AppCompatActivity {
  ListView permissionList;
  Common mApp;
  Context context;
  String profileObject;
  int creditBalance = 0;
  TextView availableCredits;
  ArrayList<PermissionModel> permissionModels;
  ClientDetailsAdapter clientDetailsAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_client_detail);
    context = this;
    mApp = (Common) getApplicationContext();
    permissionList = (ListView) findViewById(R.id.permissions_list);
    availableCredits = (TextView) findViewById(R.id.available_credits);
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
    }
    clientDetailsAdapter = new ClientDetailsAdapter(ClientDetailActivity.this, permissionModels);
    permissionList.setAdapter(clientDetailsAdapter);
    getExistingPermissions();
  }

  public void getExistingPermissions() {
    mApp.show_PDialog(context, "Loading...");
    try {
      ParseQuery<ParseObject> query = new ParseQuery<>("UserProfile");
      query.whereEqualTo("profileId", ParseObject.createWithoutData("Profile", profileObject));
      query.include("userId");
      query.findInBackground(new FindCallback<ParseObject>() {
        @Override
        public void done(List<ParseObject> list, ParseException e) {
          if (e == null) {
            if (list.size() > 0) {
              permissionModels.clear();
              for (final ParseObject item : list) {
                final ParseUser user = item.getParseUser("userId");
                ParseQuery<ParseObject> balanceQuery = new ParseQuery<>("UserCredits");
                balanceQuery.whereEqualTo("userId", user);
                balanceQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                  @Override
                  public void done(ParseObject parseObject, ParseException e) {
                    if (e == null) {
                      creditBalance = parseObject.getInt("credits");
                      SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.US);
                      String date = sdf.format(item.getCreatedAt());
                      PermissionModel permissionModel = new PermissionModel();
                      permissionModel.setBalance(creditBalance);
                      String relation = item.getString("relation");
                      if (relation.equals(""))
                        permissionModel.setRelation("Bachelor");
                      else
                        permissionModel.setRelation(relation);

                      permissionModel.setDate("Permission given on: " + date);
                      permissionModel.setNumber(user.getUsername());
                      if (user.getUsername().equalsIgnoreCase(ParseUser.getCurrentUser().getUsername())) {
                        if (item.getBoolean("isPrimary")) {
                          availableCredits.setText("Balance: " + creditBalance + " Credits");
                        }
                      } else if (relation.equals("Agent")) {
                        permissionModel.setRelation("Representative");
                      }
                      permissionModels.add(permissionModel);
                      clientDetailsAdapter.notifyDataSetChanged();
                    }
                  }
                });


              }
            }
          } else {
            e.printStackTrace();
          }
          mApp.dialog.dismiss();
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
