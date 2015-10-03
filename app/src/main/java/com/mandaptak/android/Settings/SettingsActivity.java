package com.mandaptak.android.Settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mandaptak.android.Adapter.PermissionsAdapter;
import com.mandaptak.android.Main.MainActivity;
import com.mandaptak.android.Models.PermissionModel;
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

import me.iwf.photopicker.utils.Prefs;

public class SettingsActivity extends AppCompatActivity {
    LinearLayout resetButton;
    Common mApp;
    Context context;
    ListView permissionList;
    View permissionFooter;
    PermissionsAdapter permissionsAdapter;
    ArrayList<PermissionModel> permissionModels;
    TextView morePermission, availableCredits;
    boolean isPrimaryUser = false;
    int creditBalance = 0;

    void init() {
        context = this;
        mApp = (Common) getApplicationContext();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        permissionList = (ListView) findViewById(R.id.permissions_list);
        permissionFooter = View.inflate(context, R.layout.permission_list_footer, null);
        resetButton = (LinearLayout) permissionFooter.findViewById(R.id.reset_profiles_button);
        morePermission = (TextView) permissionFooter.findViewById(R.id.more_permission);
        availableCredits = (TextView) findViewById(R.id.available_credits);
        permissionList.addFooterView(permissionFooter);
        permissionList.setFooterDividersEnabled(true);
        permissionModels = new ArrayList<>();
        permissionsAdapter = new PermissionsAdapter(SettingsActivity.this, permissionModels, isPrimaryUser);
        permissionList.setAdapter(permissionsAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
        if (mApp.isNetworkAvailable(context))
            getExistingPermissions();
        morePermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (creditBalance >= 10) {
                    if (mApp.isNetworkAvailable(context)) {
                        final View permissionDialog = View.inflate(context, R.layout.add_permission_dialog, null);
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
                                            mApp.show_PDialog(context, "Giving Permission..");
                                            HashMap<String, Object> params = new HashMap<>();
                                            params.put("mobile", mobileNumber);
                                            params.put("profileId", Prefs.getProfileId(context));
                                            params.put("relation", relations.getSelectedItem());

                                            ParseCloud.callFunctionInBackground("givePermissiontoNewUser", params, new FunctionCallback<Object>() {
                                                @Override
                                                public void done(Object o, ParseException e) {
                                                    mApp.dialog.dismiss();
                                                    if (e == null) {
                                                        getExistingPermissions();
                                                    } else {
                                                        e.printStackTrace();
                                                        mApp.showToast(context, e.getMessage());
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
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mApp.isNetworkAvailable(context)) {
                    mApp.show_PDialog(context, "Resetting..");
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("oid", Prefs.getProfileId(context));
                    ParseCloud.callFunctionInBackground("resetProfiles", params, new FunctionCallback<Object>() {
                        @Override
                        public void done(Object o, ParseException e) {
                            mApp.dialog.dismiss();
                            if (e == null) {
                                onBackPressed();
                            } else {
                                e.printStackTrace();
                                mApp.showToast(context, e.getMessage());
                            }
                        }
                    });
                } else {
                    mApp.showToast(context, "Internet connection required");
                }
            }
        });
    }

    public void getExistingPermissions() {
        mApp.show_PDialog(context, "Loading...");
        ParseQuery<ParseObject> balanceQuery = new ParseQuery<>("UserCredits");
        balanceQuery.whereEqualTo("userId", ParseUser.getCurrentUser());
        balanceQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    creditBalance = parseObject.getInt("credits");
                    availableCredits.setText("Balance: " + creditBalance + " Credits");
                }
            }
        });
        try {

            ParseQuery<ParseObject> query = new ParseQuery<>("UserProfile");
            query.whereEqualTo("profileId", ParseObject.createWithoutData("Profile",Prefs.getProfileId(context)));
            query.include("userId");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        if (list.size() > 0) {
                            permissionModels.clear();
                            for (ParseObject item : list) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.US);
                                String date = sdf.format(item.getCreatedAt());
                                ParseUser user = item.getParseUser("userId");
                                PermissionModel permissionModel = new PermissionModel();
                                String relation = item.getString("relation");
                                if (relation.equals(""))
                                    permissionModel.setRelation("Bachelor");
                                else
                                    permissionModel.setRelation(relation);
                                permissionModel.setDate("Permission given on: " + date);
                                permissionModel.setNumber(user.getUsername());
                                if (user.getUsername().equalsIgnoreCase(ParseUser.getCurrentUser().getUsername())) {
                                    if (item.getBoolean("isPrimary")) {
                                        isPrimaryUser = true;
                                    }
                                    permissionModel.setIsCurrentUser(true);
                                } else if (relation.equals("Agent")) {
                                    permissionModel.setIsCurrentUser(true);
                                } else {
                                    permissionModel.setIsCurrentUser(false);
                                }
//                                    permissionModel.setProfileId(profileObject.getObjectId());
                                permissionModels.add(permissionModel);
                            }
                            if (isPrimaryUser) {
                                morePermission.setVisibility(View.VISIBLE);
                                resetButton.setVisibility(View.VISIBLE);
                            }
                            permissionsAdapter = new PermissionsAdapter(SettingsActivity.this, permissionModels, isPrimaryUser);
                            permissionList.setAdapter(permissionsAdapter);
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
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SettingsActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_CLEAR_TOP));
        this.finish();
    }
}
