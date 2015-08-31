package com.mandaptak.android.Login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mandaptak.android.Main.MainActivity;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.mandaptak.android.Views.TypefaceTextView;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.HashMap;

import me.iwf.photopicker.utils.Prefs;

public class LoginActivity extends AppCompatActivity {
    Button loginButton;
    Context context;
    Common mApp;
    EditText etNumber;
    String mobileNumber, mobileNumberParam;
    Boolean sendOtp = true;
    TypefaceTextView label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        mApp = (Common) context.getApplicationContext();
        try {
            getSupportActionBar().hide();
        } catch (Exception ignored) {
        }
        loginButton = (Button) findViewById(R.id.login_button);
        etNumber = (EditText) findViewById(R.id.number);
        label = (TypefaceTextView) findViewById(R.id.label_number);
        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        CirclePageIndicator titleIndicator = (CirclePageIndicator) findViewById(R.id.circles);
        titleIndicator.setViewPager(pager);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                mobileNumber = etNumber.getText().toString();
                if (!mobileNumber.equals("") && mobileNumber != null) {
                    if (sendOtp && mobileNumber.length() > 9) {
                        mobileNumberParam = mobileNumber;
                        etNumber.setText("");
                        etNumber.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                        sendOtp = false;
                        loginButton.setText("VERIFY");
                        label.setText("Enter the verification code you received");
                        sendOtpOnGivenNumber(mobileNumber);
                    } else if (mobileNumber.length() > 5) {
                        verifyOtpForGivenNumber(mobileNumber);
                    }
                } else
                    mApp.showToast(context, "Invalid input");
                mobileNumberParam = "Arpit";
                getUserLogin("walkover");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onBackPressed() {
        if (!sendOtp) {
            etNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
            loginButton.setText("LOGIN");
            etNumber.setText("");
            label.setText("Enter your number below to get access");
            sendOtp = true;
        } else
            super.onBackPressed();

    }

    private void verifyOtpForGivenNumber(String code) {
        try {
            HashMap<String, Object> params = new HashMap<>();
            params.put("mobile", mobileNumberParam);
            params.put("otp", code);
            ParseCloud.callFunctionInBackground("verifyNumber", params, new FunctionCallback<Object>() {
                @Override
                public void done(Object o, ParseException e) {
                    if (e == null) {
                        getUserLogin(o.toString());
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

    private void sendOtpOnGivenNumber(String mobileNumber) {
        mApp.show_PDialog(context, "Loading...");
        try {
            HashMap<String, Object> params = new HashMap<>();
            params.put("mobile", mobileNumber);
            ParseCloud.callFunctionInBackground("sendOtp", params, new FunctionCallback<Object>() {
                @Override
                public void done(Object o, ParseException e) {
                    mApp.dialog.dismiss();
                    if (null != e) {
                        // mApp.showToast(context, "Contact your nearest agent");
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getUserLogin(String password) {
        ParseUser.logInInBackground(mobileNumberParam, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    ParseQuery<ParseObject> query = new ParseQuery<>("UserProfile");
                    query.whereEqualTo("userId", user);
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            if (e == null) {
                                Prefs.setProfileId(context, parseObject.getParseObject("profileId").getObjectId());
                                startActivity(new Intent(LoginActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                LoginActivity.this.finish();
                            } else {
                                ParseUser.logOut();
                                e.printStackTrace();
                                mApp.showToast(context, "Login Error");
                            }
                        }
                    });

                } else {
                    Log.e("Login", "" + e);
                    mApp.showToast(context, "Invalid ID/Password");
                }
            }
        });
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {
                case 0:
                    return WelcomeScreen1.newInstance();
                case 1:
                    return WelcomeScreen2.newInstance();
                case 2:
                    return WelcomeScreen3.newInstance();
                case 3:
                    return WelcomeScreen4.newInstance();
                case 4:
                    return WelcomeScreen5.newInstance();
                default:
                    return WelcomeScreen1.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    }
}
