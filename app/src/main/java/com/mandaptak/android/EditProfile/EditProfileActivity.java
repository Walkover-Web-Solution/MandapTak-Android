package com.mandaptak.android.EditProfile;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Locale;

import me.iwf.photopicker.utils.Prefs;

public class EditProfileActivity extends AppCompatActivity implements ActionBar.TabListener {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    FloatingActionButton skipButton;
    BasicProfileFragment basicProfileFragment;
    DetailsProfileFragment detailsProfileFragment;
    QualificationEditProfileFragment qualificationEditProfileFragment;
    FinalEditProfileFragment finalEditProfileFragment;
    Common mApp;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        context = this;
        mApp = (Common) getApplicationContext();
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.ic_mode_edit_white);
        actionBar.setTitle("    Edit Profile");
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        skipButton = (FloatingActionButton) findViewById(R.id.skip_next);
        skipButton.setSize(FloatingActionButton.SIZE_MINI);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
                if (position == 3) {
                    skipButton.setVisibility(View.GONE);
                } else {
                    skipButton.setVisibility(View.VISIBLE);
                }
            }
        });
        actionBar.addTab(
                actionBar.newTab()
                        .setIcon(R.drawable.ic_tab1)
                        .setTabListener(this));
        actionBar.addTab(
                actionBar.newTab()
                        .setIcon(R.drawable.ic_tab2)
                        .setTabListener(this));
        actionBar.addTab(
                actionBar.newTab()
                        .setIcon(R.drawable.ic_tab3)
                        .setTabListener(this));
        actionBar.addTab(
                actionBar.newTab()
                        .setIcon(R.drawable.ic_tab4)
                        .setTabListener(this));

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewPager.getCurrentItem() < 3) {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                }
            }
        });
        init();
        if (mApp.isNetworkAvailable(context)) {
            ParseQuery<ParseObject> parseQueryParseQuery = new ParseQuery<>("Profile");
            parseQueryParseQuery.getInBackground(Prefs.getProfileId(EditProfileActivity.this), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    parseObject.put("isComplete", false);
                    parseObject.saveInBackground();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    void init() {
        basicProfileFragment = new BasicProfileFragment();
        detailsProfileFragment = new DetailsProfileFragment();
        qualificationEditProfileFragment = new QualificationEditProfileFragment();
        finalEditProfileFragment = new FinalEditProfileFragment();
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return basicProfileFragment;
                case 1:
                    return detailsProfileFragment;
                case 2:
                    return qualificationEditProfileFragment;
                case 3:
                    return finalEditProfileFragment;
                default:
                    return basicProfileFragment;
            }
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
                case 3:
                    return getString(R.string.title_section4).toUpperCase(l);
            }
            return null;
        }
    }
}
