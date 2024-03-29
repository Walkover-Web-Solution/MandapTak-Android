package com.mandaptak.android.Login;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import com.mandaptak.android.Layer.LayerImpl;
import com.mandaptak.android.R;
import com.mandaptak.android.Splash.SplashScreen;
import com.mandaptak.android.Utils.Common;
import com.mandaptak.android.Utils.IncomingSms;
import com.mandaptak.android.Views.ExtendedEditText;
import com.mandaptak.android.Views.TypefaceTextView;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.HashMap;
import java.util.List;

import me.iwf.photopicker.utils.Prefs;

public class LoginActivity extends AppCompatActivity {
  static ExtendedEditText etNumber;
  static Boolean sendOtp = true;
  AppCompatButton loginButton;
  Context context;
  Common mApp;
  String mobileNumber, mobileNumberParam;
  TypefaceTextView label;
  ImageView numberImage;
  IncomingSms incomingSms;

  public static void setCode(String code) {
    if (etNumber != null && !sendOtp)
      etNumber.setText(code);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    incomingSms = new IncomingSms();
    context = this;
    mApp = (Common) context.getApplicationContext();
    try {
      getSupportActionBar().hide();
    } catch (Exception ignored) {
    }
    loginButton = (AppCompatButton) findViewById(R.id.login_button);
    etNumber = (ExtendedEditText) findViewById(R.id.number);
    label = (TypefaceTextView) findViewById(R.id.label_number);
    numberImage = (ImageView) findViewById(R.id.number_image);
    etNumber.setPrefix("+91 ");
    ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
    pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
    CirclePageIndicator titleIndicator = (CirclePageIndicator) findViewById(R.id.circles);
    titleIndicator.setViewPager(pager);
    loginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(final View view) {
        mobileNumber = etNumber.getText().toString();
        if (!mobileNumber.equals("")) {
          if (sendOtp && mobileNumber.length() > 9) {
            mobileNumberParam = mobileNumber;
            sendOtpOnGivenNumber(mobileNumberParam);
          } else if (!sendOtp && mobileNumber.length() == 4) {
            verifyOtpForGivenNumber(mobileNumber);
          }
        } else
          mApp.showToast(context, "Invalid input");
      }
    });
    etNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_GO) {
          mobileNumber = etNumber.getText().toString();
          if (!mobileNumber.equals("")) {
            if (sendOtp && mobileNumber.length() > 9) {
              mobileNumberParam = mobileNumber;
              sendOtpOnGivenNumber(mobileNumberParam);
            } else if (!sendOtp && mobileNumber.length() == 4) {
              verifyOtpForGivenNumber(mobileNumber);
            }
          } else
            mApp.showToast(context, "Invalid input");
        }
        return true;
      }
    });
    etNumber.setText(mApp.getNumber());

    IntentFilter intentFilter = new IntentFilter(
        "android.provider.Telephony.SMS_RECEIVED");
    registerReceiver(incomingSms, intentFilter);
  }

  @Override
  protected void onDestroy() {
    unregisterReceiver(incomingSms);
    super.onDestroy();
  }

  @Override
  public void onBackPressed() {
    if (!sendOtp) {
      loginButton.setText("LOGIN");
      etNumber.setText("");
      etNumber.setHint("xxx-xxx-xxxx");
      etNumber.setPrefix("+91 ");
      etNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
      numberImage.setImageResource(R.drawable.ic_call_white);
      label.setText("Enter your number below to get access");
      sendOtp = true;
      etNumber.setText(mApp.getNumber());
    } else
      super.onBackPressed();
  }

  private void verifyOtpForGivenNumber(String code) {
    mApp.show_PDialog(context, "Verifying...");
    try {
      HashMap<String, Object> params = new HashMap<>();
      params.put("mobile", mobileNumberParam);
      params.put("otp", code);
      ParseCloud.callFunctionInBackground("verifyNumber", params, new FunctionCallback<Object>() {
        @Override
        public void done(Object o, ParseException e) {
          mApp.dialog.dismiss();
          if (e == null) {
            getUserLogin(o.toString());
          } else {
            mApp.showToast(context, e.getMessage());
            e.printStackTrace();
          }
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void sendOtpOnGivenNumber(String mobileNumber) {
    if (mApp.isNetworkAvailable(context)) {
      mApp.show_PDialog(context, "Sending Verification Code...");
      try {
        HashMap<String, Object> params = new HashMap<>();
        params.put("mobile", mobileNumber);
        ParseCloud.callFunctionInBackground("sendOtp", params, new FunctionCallback<Object>() {
          @Override
          public void done(Object o, ParseException e) {
            mApp.dialog.dismiss();
            if (e == null) {
              etNumber.setText("");
              numberImage.setImageResource(R.drawable.ic_code_verify_white);
              sendOtp = false;
              etNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
              etNumber.setHint("xxxx");
              etNumber.setPrefix("");
              loginButton.setText("VERIFY");
              label.setText("Enter the verification code you received");
            } else {
              mApp.showToast(context, "Please contact your nearest agent");
              e.printStackTrace();
            }
          }
        });
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private void getUserLogin(String password) {
    if (mApp.isNetworkAvailable(context)) {
      mApp.show_PDialog(context, "Logging in...");
      ParseUser.logInInBackground(mobileNumberParam, password, new LogInCallback() {
        public void done(final ParseUser user, ParseException e) {
          if (user != null) {
            try {
              ParseInstallation installation = ParseInstallation.getCurrentInstallation();
              installation.put("user", user);
              installation.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                  if (e == null) {
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("userId", user.getObjectId());
                    ParseCloud.callFunctionInBackground("deleteDuplicateInstallations", params);
                  }
                }
              });
              String role = user.fetchIfNeeded().getParseObject("roleId").fetchIfNeeded().getString("name");
              switch (role) {
                case "User": {
                  ParseQuery<ParseObject> query = new ParseQuery<>("UserProfile");
                  query.whereEqualTo("userId", user);
                  query.whereNotEqualTo("relation", "Agent");
                  query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                      if (e == null) {
                        int size = list.size();
                        ParseObject parseObject = null;
                        boolean hasPrimary = false;
                        for (int i = 0; i < size; i++) {
                          if (list.get(i).getBoolean("isPrimary")) {
                            parseObject = list.get(i);
                            hasPrimary = true;
                          } else if (!hasPrimary && (size - 1) == i) {
                            parseObject = list.get(i);
                          }
                        }
                        if (parseObject != null) {
                          if (!LayerImpl.isAuthenticated()) {
                            LayerImpl.authenticateUser();
                          }
                          Prefs.setProfileId(context, parseObject.getParseObject("profileId").getObjectId());
                          startActivity(new Intent(LoginActivity.this, SplashScreen.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_NO_ANIMATION));
                          LoginActivity.this.finish();
                        } else {
                          ParseUser.logOut();
                          mApp.showToast(context, "Error fetching profile");
                        }
                      } else {
                        ParseUser.logOut();
                        e.printStackTrace();
                        mApp.showToast(context, e.getMessage());
                      }
                      mApp.dialog.dismiss();
                    }
                  });
                  break;
                }
                case "Agent": {
                  ParseQuery<ParseObject> query = new ParseQuery<>("UserProfile");
                  query.whereEqualTo("userId", user);
                  query.whereEqualTo("isPrimary", true);
                  query.getFirstInBackground(new GetCallback<ParseObject>() {
                                               @Override
                                               public void done(ParseObject parseObject, ParseException e) {
                                                 mApp.dialog.dismiss();
                                                 if (e == null) {
                                                   if (!LayerImpl.isAuthenticated()) {
                                                     LayerImpl.authenticateUser();
                                                   }
                                                   Prefs.setProfileId(context, parseObject.getParseObject("profileId").getObjectId());
                                                   startActivity(new Intent(LoginActivity.this, SplashScreen.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                                   LoginActivity.this.finish();
                                                 } else {
                                                   ParseUser.logOut();
                                                   e.printStackTrace();
                                                   mApp.showToast(context, "Login Error");
                                                 }
                                               }
                                             }
                  );
                  break;
                }
                default:
                  ParseUser.logOut();
                  e.printStackTrace();
                  mApp.showToast(context, "Admin View: Coming Soon");
                  break;
              }
            } catch (ParseException e1) {
              e1.printStackTrace();
            }
          } else {
            mApp.dialog.dismiss();
            e.printStackTrace();
            mApp.showToast(context, e.getMessage());
          }
        }
      });
    }
  }

  private class MyPagerAdapter extends FragmentPagerAdapter {

    public MyPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int pos) {
      switch (pos) {
        case 0:
          return new WelcomeScreen1();
        case 1:
          return new WelcomeScreen2();
        case 2:
          return new WelcomeScreen3();
        case 3:
          return new WelcomeScreen4();
        case 4:
          return new WelcomeScreen5();
        default:
          return new WelcomeScreen1();
      }
    }

    @Override
    public int getCount() {
      return 5;
    }
  }
}
