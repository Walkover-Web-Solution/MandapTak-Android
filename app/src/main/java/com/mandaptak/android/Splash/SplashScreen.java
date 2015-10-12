package com.mandaptak.android.Splash;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mandaptak.android.Agent.AgentActivity;
import com.mandaptak.android.EditProfile.EditProfileActivity;
import com.mandaptak.android.Login.LoginActivity;
import com.mandaptak.android.Main.MainActivity;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.newrelic.agent.android.NewRelic;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import main.java.com.mindscapehq.android.raygun4android.RaygunClient;
import me.iwf.photopicker.utils.Prefs;

public class SplashScreen extends AppCompatActivity {
  Common mApp;
  Context context;
  ParseUser user;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash_screen);
    RaygunClient.Init(getApplicationContext());
    RaygunClient.AttachExceptionHandler();

    NewRelic.withApplicationToken(
        "AA6833492f59e6f89eb326bf32f91ae9dc50062702"
    ).start(this.getApplication());

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
      if (mApp.isNetworkAvailable(context)) {
        try {
          new AsyncTask<Void, Void, Void>() {
            String role = "User";

            @Override
            protected Void doInBackground(Void... voids) {
              try {
                role = user.fetch().getParseObject("roleId").fetch().getString("name");
              } catch (Exception e) {
                e.printStackTrace();
              }
              return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
              super.onPostExecute(aVoid);
              if (role.equalsIgnoreCase("User")) {
                ParseQuery<ParseObject> query = new ParseQuery<>("Profile");
                query.getInBackground(Prefs.getProfileId(context), new GetCallback<ParseObject>() {
                  @Override
                  public void done(ParseObject parseObject, ParseException e) {
                    if (e == null) {
                      try {
                        if (parseObject.getBoolean("isActive")) {
                          if (parseObject.getBoolean("isComplete")) {
                            startActivity(new Intent(SplashScreen.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_NO_ANIMATION));
                            SplashScreen.this.finish();
                          } else {
                            Intent intent = new Intent(SplashScreen.this, EditProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            intent.putExtra("firstStart", true);
                            startActivity(intent);
                            SplashScreen.this.finish();
                          }
                        } else {
                          ParseUser.logOut();
                          com.mandaptak.android.Utils.Prefs.getPrefs(context).edit().clear().commit();
                          mApp.showToast(context, "Account Deactivated: Contact Agent");
                          startActivity(new Intent(SplashScreen.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_NO_ANIMATION));
                          SplashScreen.this.finish();
                        }
                      } catch (Exception e1) {
                        e1.printStackTrace();
                        ParseUser.logOut();
                        com.mandaptak.android.Utils.Prefs.getPrefs(context).edit().clear().commit();
                        mApp.showToast(context, "Account Deactivated: Contact Agent");
                        startActivity(new Intent(SplashScreen.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        SplashScreen.this.finish();
                      }
                    } else if (e.getCode() == 209) {
                      //INVALID SESSION TOKEN
                      ParseUser.logOut();
                      com.mandaptak.android.Utils.Prefs.getPrefs(context).edit().clear().commit();
                      startActivity(new Intent(SplashScreen.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_NO_ANIMATION));
                      SplashScreen.this.finish();
                    } else {
                      e.printStackTrace();
                      mApp.showToast(context, "Connection Error");
                    }
                  }
                });
              } else if (role.equalsIgnoreCase("Agent")) {
                ParseQuery<ParseObject> query = new ParseQuery<>("Profile");
                query.getInBackground(Prefs.getProfileId(context), new GetCallback<ParseObject>() {
                  @Override
                  public void done(ParseObject parseObject, ParseException e) {
                    if (e == null) {
                      if (parseObject.getBoolean("isActive")) {
                        startActivity(new Intent(SplashScreen.this, AgentActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        SplashScreen.this.finish();
                      } else {
                        ParseUser.logOut();
                        com.mandaptak.android.Utils.Prefs.getPrefs(context).edit().clear().commit();
                        mApp.showToast(context, "Account Deactivated: Contact Administrator");
                        startActivity(new Intent(SplashScreen.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        SplashScreen.this.finish();
                      }
                    } else if (e.getCode() == 209) {
                      //INVALID SESSION TOKEN
                      ParseUser.logOut();
                      com.mandaptak.android.Utils.Prefs.getPrefs(context).edit().clear().commit();
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
                com.mandaptak.android.Utils.Prefs.getPrefs(context).edit().clear().commit();
                mApp.showToast(context, "Admin Comming Soon");
                startActivity(new Intent(SplashScreen.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_NO_ANIMATION));
                SplashScreen.this.finish();
              }
            }
          }.execute();

        } catch (Exception e) {
          //INVALID SESSION TOKEN
          ParseUser.logOut();
          com.mandaptak.android.Utils.Prefs.getPrefs(context).edit().clear().commit();
          startActivity(new Intent(SplashScreen.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_NO_ANIMATION));
          SplashScreen.this.finish();
          e.printStackTrace();
        }
      }
    }
  }
}
