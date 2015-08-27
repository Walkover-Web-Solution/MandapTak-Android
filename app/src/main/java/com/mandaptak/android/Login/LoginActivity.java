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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mandaptak.android.Main.MainActivity;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.viewpagerindicator.CirclePageIndicator;

public class LoginActivity extends AppCompatActivity {
    Button loginButton;
    Context context;
    Common mApp;
    EditText etNumber;

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
        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        //Bind the title indicator to the adapter
        CirclePageIndicator titleIndicator = (CirclePageIndicator) findViewById(R.id.circles);
        titleIndicator.setViewPager(pager);
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            this.finish();
        }
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (!etNumber.getText().toString().equals("") && etNumber.getText().toString() != null) {
                    showDialogVerifyNumber();
                } else
                    mApp.showToast(context, "Invalid Number");

            }
        });
//        importLocation();
    }

//    void importLocation() {
//        InputStream inputStream = getResources().openRawResource(R.raw.location);
//        CSVFile csvFile = new CSVFile(inputStream);
//        final List<String[]> scoreList = csvFile.read();
//        final int[] i = {scoreList.size()};
//
//        final Handler handler = new Handler();
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                if (i[0] != 0) {
//                    i[0]--;
//                    String[] location = scoreList.get(i[0]);
//                    final ParseACL groupACL = new ParseACL();
//                    groupACL.setPublicReadAccess(true);
//                    groupACL.setPublicWriteAccess(false);
//                    final String city = location[2].replace("�", "").trim(), state = location[1].trim(), country = location[0].trim();
//                    final ParseQuery<ParseObject> parseQueryCountry = new ParseQuery<>("Country");
//                    parseQueryCountry.whereEqualTo("name", country);
//                    parseQueryCountry.getFirstInBackground(new GetCallback<ParseObject>() {
//                        @Override
//                        public void done(final ParseObject parseObject1, ParseException e) {
//                            if (parseObject1 != null) {
//                                ParseQuery<ParseObject> parseQueryState = new ParseQuery<>("State");
//                                parseQueryState.whereEqualTo("name", state);
//                                parseQueryState.getFirstInBackground(new GetCallback<ParseObject>() {
//                                    @Override
//                                    public void done(final ParseObject parseObject, ParseException e) {
//                                        if (parseObject != null) {
//                                            ParseObject parseObjectCity = new ParseObject("City");
//                                            parseObjectCity.put("name", city);
//                                            parseObjectCity.put("Parent", parseObject);
//                                            parseObjectCity.setACL(groupACL);
//                                            parseObjectCity.saveInBackground();
//                                        } else {
//                                            ParseObject parseObjectState = new ParseObject("State");
//                                            parseObjectState.put("name", state);
//                                            parseObjectState.put("Parent", parseObject1);
//                                            parseObjectState.setACL(groupACL);
//                                            parseObjectState.saveInBackground();
//                                            ParseObject parseObjectCity = new ParseObject("City");
//                                            parseObjectCity.put("name", city);
//                                            parseObjectCity.put("Parent", parseObjectState);
//                                            parseObjectCity.setACL(groupACL);
//                                            parseObjectCity.saveInBackground();
//                                        }
//                                    }
//                                });
//                            } else {
//                                ParseObject parseObjectCountry = new ParseObject("Country");
//                                parseObjectCountry.put("name", country);
//                                parseObjectCountry.saveInBackground();
//                                parseObjectCountry.setACL(groupACL);
//                                ParseObject parseObjectState = new ParseObject("State");
//                                parseObjectState.put("name", state);
//                                parseObjectState.put("Parent", parseObjectCountry);
//                                parseObjectState.setACL(groupACL);
//                                parseObjectState.saveInBackground();
//                                ParseObject parseObjectCity = new ParseObject("City");
//                                parseObjectCity.put("name", city);
//                                parseObjectCity.put("Parent", parseObjectState);
//                                parseObjectCity.setACL(groupACL);
//                                parseObjectCity.saveInBackground();
//                            }
//                        }
//                    });
//                    handler.postDelayed(this, 2000);
//                }
//            }
//        });
//    }

    private void showDialogVerifyNumber() {
        final View locationDialog = View.inflate(context, R.layout.verify_otp_dialog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setView(locationDialog);
        TextView title = (TextView) locationDialog.findViewById(R.id.title);
        final EditText searchBar = (EditText) locationDialog.findViewById(R.id.search);
        title.setText("Verify Otp");
        final Button done = (Button) locationDialog.findViewById(R.id.cancel_button);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                mApp.show_PDialog(context, "Verifying");
                if (!searchBar.getText().toString().equals("") && searchBar.getText().toString() != null) {
                    ParseUser.logInInBackground("Arpit", "walkover", new LogInCallback() {
                        public void done(ParseUser user, ParseException e) {
                            if (user != null) {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                mApp.dialog.dismiss();
                                LoginActivity.this.finish();
                                mApp.showToast(context, "Welcome");
                            } else {
                                Log.e("Login", "" + e);
                                mApp.showToast(context, "Invalid ID/Password");
                            }
                        }
                    });
                }

            }
        });

        alertDialog.show();
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
