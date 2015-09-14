package com.mandaptak.android.EditProfile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.mandaptak.android.Main.MainActivity;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.mandaptak.android.Views.MyViewPager;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

import me.iwf.photopicker.utils.Prefs;

public class EditProfileActivity extends AppCompatActivity implements ActionBar.TabListener {

    SectionsPagerAdapter mSectionsPagerAdapter;
    MyViewPager mViewPager;
    FloatingActionButton skipButton;
    BasicProfileFragment basicProfileFragment;
    DetailsProfileFragment detailsProfileFragment;
    QualificationEditProfileFragment qualificationEditProfileFragment;
    FinalEditProfileFragment finalEditProfileFragment;
    boolean isFirstStart;
    Common mApp;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        context = this;
        mApp = (Common) getApplicationContext();
        if (getIntent() != null) {
            if (getIntent().hasExtra("firstStart")) {
                isFirstStart = getIntent().getBooleanExtra("firstStart", false);
            }
        }
        init();
    }

    @Override
    public void onBackPressed() {
        mApp.showToast(context, "Save profile to go back.");
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition(), false);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    void init() {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setIcon(R.drawable.ic_mode_edit_white);
            actionBar.setTitle("    Edit Profile");
        }
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (MyViewPager) findViewById(R.id.pager);
        skipButton = (FloatingActionButton) findViewById(R.id.skip_next);
        skipButton.setSize(FloatingActionButton.SIZE_MINI);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setPagingEnabled(false);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (actionBar != null) {
                    actionBar.setSelectedNavigationItem(position);
                }
                if (position == 3) {
                    skipButton.setVisibility(View.GONE);
                } else {
                    skipButton.setVisibility(View.VISIBLE);
                }
            }
        });
        if (actionBar != null) {
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
        }

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = mViewPager.getCurrentItem();
                if (index < 3) {
                    mViewPager.setCurrentItem(index + 1, false);
                }
            }
        });
        basicProfileFragment = new BasicProfileFragment();
        detailsProfileFragment = new DetailsProfileFragment();
        qualificationEditProfileFragment = new QualificationEditProfileFragment();
        finalEditProfileFragment = new FinalEditProfileFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isFirstStart) {
            getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
            return true;
        } else {
            return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            mApp.show_PDialog(context, "Saving Profile..");
            ParseQuery<ParseObject> query = new ParseQuery<>("Profile");
            query.getInBackground(Prefs.getProfileId(context), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (e == null) {
                        validateProfile(parseObject);
                    } else {
                        e.printStackTrace();
                        mApp.dialog.dismiss();
                        mApp.showToast(context, e.getMessage());
                    }
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private boolean checkFieldsTab4(ParseObject parseObject) {
        if (!parseObject.containsKey("profilePic") || parseObject.get("profilePic").equals(JSONObject.NULL)) {
            return false;
        } else if (!parseObject.has("package") || parseObject.get("package").equals(JSONObject.NULL)) {
            return false;
        } else {
            return true;
        }
    }

    private void validateProfile(final ParseObject parseObject) {
        if (checkFieldsTab1(parseObject)) {
            if (checkFieldsTab2(parseObject)) {
                if (checkFieldsTab3(parseObject)) {
                    if (checkFieldsTab4(parseObject)) {
                        ParseQuery<ParseObject> queryParseQuery = new ParseQuery<>("Photo");
                        queryParseQuery.whereEqualTo("profileId", parseObject);
                        queryParseQuery.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> list, ParseException e) {
                                boolean isPrimarySet = false;
                                if (list != null) {
                                    for (ParseObject item : list) {
                                        if (item.getBoolean("isPrimary")) {
                                            isPrimarySet = true;
                                        }
                                    }
                                }
                                if (isPrimarySet) {
                                    parseObject.put("isComplete", true);
                                    parseObject.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                try {
                                                    startActivity(new Intent(EditProfileActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                    EditProfileActivity.this.finish();
                                                    mApp.showToast(context, "Profile updated");
                                                } catch (Exception e2) {
                                                    mApp.showToast(context, "Error while updating profile");
                                                    e2.printStackTrace();
                                                }
                                            } else {
                                                mApp.showToast(context, "Error while updating profile");
                                                e.printStackTrace();
                                            }
                                            mApp.dialog.dismiss();
                                        }
                                    });
                                } else {
                                    mApp.dialog.dismiss();
                                    mApp.showToast(context, "Please select a primary profile photo");
                                }
                            }
                        });
                    }
                } else {
                    mApp.dialog.dismiss();
                    mApp.showToast(context, "Please fill all details");
                    mViewPager.setCurrentItem(2);
                }
            } else {
                mApp.dialog.dismiss();
                mApp.showToast(context, "Please fill all details");
                mViewPager.setCurrentItem(1);
            }
        } else {
            mApp.dialog.dismiss();
            mApp.showToast(context, "Please fill all details");
            mViewPager.setCurrentItem(0);
        }
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
