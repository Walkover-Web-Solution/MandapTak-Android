package com.mandaptak.android.Splash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mandaptak.android.Agent.AgentActivity;
import com.mandaptak.android.EditProfile.EditProfileActivity;
import com.mandaptak.android.Login.LoginActivity;
import com.mandaptak.android.Main.MainActivity;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONObject;

import me.iwf.photopicker.utils.Prefs;

public class SplashScreen extends AppCompatActivity {
    Common mApp;
    Context context;
    ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        context = this;
        mApp = (Common) getApplicationContext();
        try {
            getSupportActionBar().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
        user = ParseUser.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(SplashScreen.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_NO_ANIMATION));
            SplashScreen.this.finish();
        } else {
            try {
                if (user.fetchIfNeeded().getParseObject("roleId").fetchIfNeeded().getString("name").equals("User")) {
                    if (mApp.isNetworkAvailable(context)) {
                        ParseQuery<ParseObject> query = new ParseQuery<>("Profile");
                        query.getInBackground(Prefs.getProfileId(context), new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject parseObject, ParseException e) {
                                if (e == null) {
                                    try {
                                        if (parseObject.fetchIfNeeded().getBoolean("isActive")) {
                                            if (parseObject.fetchIfNeeded().getBoolean("isComplete")) {
                                                startActivity(new Intent(SplashScreen.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                                SplashScreen.this.finish();
                                            } else {
                                                startActivity(new Intent(SplashScreen.this, EditProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                                SplashScreen.this.finish();
                                            }
//                                            validateProfile(parseObject);
                                        } else {
                                            ParseUser.logOut();
                                            mApp.showToast(context, "Account Deactivated: Contact Agent");
                                            startActivity(new Intent(SplashScreen.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                            SplashScreen.this.finish();
                                        }
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                        ParseUser.logOut();
                                        mApp.showToast(context, "Account Deactivated: Contact Agent");
                                        startActivity(new Intent(SplashScreen.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                        SplashScreen.this.finish();
                                    }
                                } else if (e.getCode() == 209) {
                                    //INVALID SESSION TOKEN
                                    ParseUser.logOut();
                                    startActivity(new Intent(SplashScreen.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                    SplashScreen.this.finish();
                                } else {
                                    e.printStackTrace();
                                    mApp.showToast(context, "Connection Error");
                                }
                            }
                        });
                    } else {
                        mApp.showToast(context, "Internet Connection Required");
                    }
                } else if (user.fetchIfNeeded().getParseObject("roleId").fetchIfNeeded().getString("name").equals("Agent")) {
                    ParseQuery<ParseObject> query = new ParseQuery<>("Profile");
                    query.getInBackground(Prefs.getProfileId(context), new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            if (e == null) {
                                try {
                                    if (parseObject.fetchIfNeeded().getBoolean("isActive")) {
                                        startActivity(new Intent(SplashScreen.this, AgentActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                        SplashScreen.this.finish();
                                    } else {
                                        ParseUser.logOut();
                                        mApp.showToast(context, "Account Deactivated: Contact Administrator");
                                        startActivity(new Intent(SplashScreen.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                        SplashScreen.this.finish();
                                    }
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                    ParseUser.logOut();
                                    mApp.showToast(context, "Account Deactivated: Contact Administrator");
                                    startActivity(new Intent(SplashScreen.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                    SplashScreen.this.finish();
                                }
                            } else if (e.getCode() == 209) {
                                //INVALID SESSION TOKEN
                                ParseUser.logOut();
                                startActivity(new Intent(SplashScreen.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                SplashScreen.this.finish();
                            } else {
                                e.printStackTrace();
                                mApp.showToast(context, "Connection Error");
                            }
                        }
                    });
                } else {
                    ParseUser.logOut();
                    mApp.showToast(context, "Admin Comming Soon");
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_NO_ANIMATION));
                    SplashScreen.this.finish();
                }
            } catch (Exception e) {
                //INVALID SESSION TOKEN
                ParseUser.logOut();
                startActivity(new Intent(SplashScreen.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_NO_ANIMATION));
                SplashScreen.this.finish();
                e.printStackTrace();
            }
        }
    }

    private void validateProfile(final ParseObject parseObject) {
        if (checkFieldsTab1(parseObject)) {
            if (checkFieldsTab2(parseObject)) {
                if (checkFieldsTab3(parseObject)) {
                    if (!parseObject.containsKey("profilePic") || parseObject.get("profilePic").equals(JSONObject.NULL)) {
                        startActivity(new Intent(SplashScreen.this, EditProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        SplashScreen.this.finish();
                    } else {
                        startActivity(new Intent(SplashScreen.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        SplashScreen.this.finish();
                    }
                } else {
                    startActivity(new Intent(SplashScreen.this, EditProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_NO_ANIMATION));
                    SplashScreen.this.finish();
                }
            } else {
                startActivity(new Intent(SplashScreen.this, EditProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_NO_ANIMATION));
                SplashScreen.this.finish();
            }
        } else {
            startActivity(new Intent(SplashScreen.this, EditProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK));
            SplashScreen.this.finish();
        }
    }

    private boolean checkFieldsTab1(ParseObject parseObject) {
        if (!parseObject.containsKey("name") || parseObject.get("name").equals(JSONObject.NULL)) {
            return false;
        } else if (!parseObject.containsKey("gender") || parseObject.get("gender").equals(JSONObject.NULL)) {
            return false;
        } else if (!parseObject.containsKey("dob") || parseObject.get("dob").equals(JSONObject.NULL)) {
            return false;
        } else if (!parseObject.containsKey("tob") || parseObject.get("tob").equals(JSONObject.NULL)) {
            return false;
        } else if (!parseObject.containsKey("currentLocation") || parseObject.get("currentLocation").equals(JSONObject.NULL)) {
            return false;
        } else if (!parseObject.containsKey("placeOfBirth") || parseObject.get("placeOfBirth").equals(JSONObject.NULL)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean checkFieldsTab2(ParseObject parseObject) {
        if (!parseObject.containsKey("height") || parseObject.get("height").equals(JSONObject.NULL)) {
            return false;
        } else if (!parseObject.has("weight") || parseObject.get("weight").equals(JSONObject.NULL)) {
            return false;
        } else if (!parseObject.has("religionId") || parseObject.get("religionId").equals(JSONObject.NULL)) {
            return false;
        } else if (!parseObject.has("casteId") || parseObject.get("casteId").equals(JSONObject.NULL)) {
            return false;
        } else if (!parseObject.containsKey("manglik") || parseObject.get("manglik").equals(JSONObject.NULL)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean checkFieldsTab3(ParseObject parseObject) {
        if (!parseObject.containsKey("workAfterMarriage") || parseObject.get("workAfterMarriage").equals(JSONObject.NULL)) {
            return false;
        } else if (!parseObject.has("package") || parseObject.get("package").equals(JSONObject.NULL)) {
            return false;
        } else if (!parseObject.has("designation") || parseObject.get("designation").equals(JSONObject.NULL)) {
            return false;
        } else if (!parseObject.has("placeOfWork") || parseObject.get("placeOfWork").equals(JSONObject.NULL)) {
            return false;
        } else if (!parseObject.has("industryId") || parseObject.get("industryId").equals(JSONObject.NULL)) {
            return false;
        } else if (!parseObject.containsKey("education1") || parseObject.get("education1").equals(JSONObject.NULL)) {
            return false;
        } else {
            return true;
        }
    }
}
