package com.mandaptak.android.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import com.mandaptak.android.Main.MainActivity;
import com.mandaptak.android.R;
import com.viewpagerindicator.CirclePageIndicator;

public class LoginActivity extends ActionBarActivity {
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try {
            getSupportActionBar().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }

        loginButton = (Button) findViewById(R.id.login_button);
        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        //Bind the title indicator to the adapter
        CirclePageIndicator titleIndicator = (CirclePageIndicator) findViewById(R.id.circles);
        titleIndicator.setViewPager(pager);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
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
