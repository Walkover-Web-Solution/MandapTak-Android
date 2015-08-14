package com.mandaptak.android.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.mandaptak.android.EditProfile.EditProfileActivity;
import com.mandaptak.android.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MainActivity extends AppCompatActivity {
    SlidingMenu menu;
    LinearLayout slidingLayout, bottomLayout;
    SlidingUpPanelLayout slidingPanel;
    ImageView pinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mandap Tak");
        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        slidingLayout = (LinearLayout) findViewById(R.id.sliding_layout);
        bottomLayout = (LinearLayout) findViewById(R.id.bottom_panel);
        slidingPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_panel);
        pinButton = (ImageView) findViewById(R.id.pin_button);
        pinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, EditProfileActivity.class));
            }
        });

        // configure the SlidingMenu
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.fragment_menu);
        menu.setSlidingEnabled(true);
        menu.setBehindOffset(124);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_red);
        slidingPanel.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                slidingLayout.setVisibility(View.VISIBLE);
                slidingLayout.setAlpha(v);
                bottomLayout.setAlpha(1 - v);
            }

            @Override
            public void onPanelCollapsed(View view) {
                slidingLayout.setVisibility(View.GONE);
                bottomLayout.setVisibility(View.VISIBLE);
                menu.setSlidingEnabled(true);
            }

            @Override
            public void onPanelExpanded(View view) {
                slidingLayout.setVisibility(View.VISIBLE);
                bottomLayout.setVisibility(View.GONE);
                menu.setSlidingEnabled(false);
            }

            @Override
            public void onPanelAnchored(View view) {

            }

            @Override
            public void onPanelHidden(View view) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                menu.toggle();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Fragment getItem(int pos) {
            return MainFragment.newInstance(pos);
        }
    }
}
