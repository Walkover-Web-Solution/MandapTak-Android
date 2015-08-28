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
import com.parse.ParseFacebookUtils;
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
        loginButton.setText("LOGIN");
        etNumber = (EditText) findViewById(R.id.number);
        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        CirclePageIndicator titleIndicator = (CirclePageIndicator) findViewById(R.id.circles);
        titleIndicator.setViewPager(pager);
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_NO_ANIMATION));
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

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
                                startActivity(new Intent(LoginActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                mApp.dialog.dismiss();
                                LoginActivity.this.finish();
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
