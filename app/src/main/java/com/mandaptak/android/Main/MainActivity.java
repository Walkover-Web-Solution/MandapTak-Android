package com.mandaptak.android.Main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.mandaptak.android.Adapter.UserImagesAdapter;
import com.mandaptak.android.EditProfile.EditProfileActivity;
import com.mandaptak.android.Preferences.UserPreferences;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.mandaptak.android.Views.BlurringView;
import com.mandaptak.android.Views.CircleImageView;
import com.mandaptak.android.Views.TypefaceTextView;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.skyfishjy.library.RippleBackground;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import me.iwf.photopicker.utils.ImageModel;

public class MainActivity extends AppCompatActivity {
    public final static int REQUEST_CODE = 11;
    Common mApp;
    SlidingMenu menu;
    RelativeLayout slidingLayout;
    LinearLayout bottomLayout;
    SlidingUpPanelLayout slidingPanel;
    ImageView pinButton;
    View navigationMenu;
    ImageView backgroundPhoto;
    Context context;
    ArrayList<ImageModel> userProfileImages = new ArrayList<>();
    TwoWayView profileImages;
    UserImagesAdapter userImagesAdapter;
    ImageButton mLikeUser;
    Boolean liked = false;
    ArrayList<ParseObject> profileList = new ArrayList<>();
    TextView frontProfileName, frontHeight, frontDesignation, frontReligion;
    CircleImageView frontPhoto;
    BlurringView blurringView;
    TextView salary, designation, company, education, weight, currentLocation;
    TextView slideName, slideHeight, slideReligion, slideDesignation, slideTraits;
    RippleBackground rippleBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        mApp = (Common) context.getApplicationContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setTitle("Mandap Tak");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_red);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        rippleBackground = (RippleBackground) findViewById(R.id.content);
        slidingLayout = (RelativeLayout) findViewById(R.id.sliding_layout);
        bottomLayout = (LinearLayout) findViewById(R.id.bottom_panel);
        slidingPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_panel);
        pinButton = (ImageView) findViewById(R.id.pin_button);
        profileImages = (TwoWayView) findViewById(R.id.list);
        backgroundPhoto = (ImageView) findViewById(R.id.background_photo);
        mLikeUser = (ImageButton) findViewById(R.id.like_user);
        profileImages = (TwoWayView) findViewById(R.id.list);
        blurringView = (BlurringView) findViewById(R.id.blurring_view);
        frontProfileName = (TextView) findViewById(R.id.front_name);
        frontPhoto = (CircleImageView) findViewById(R.id.front_photo);
        frontDesignation = (TextView) findViewById(R.id.front_designation);
        frontHeight = (TextView) findViewById(R.id.front_height);
        frontReligion = (TextView) findViewById(R.id.front_religion);
        salary = (TextView) findViewById(R.id.salary);
        designation = (TextView) findViewById(R.id.designation);
        company = (TextView) findViewById(R.id.company);
        education = (TextView) findViewById(R.id.education);
        weight = (TextView) findViewById(R.id.weight);
        currentLocation = (TextView) findViewById(R.id.current_location);
        slideDesignation = (TextView) findViewById(R.id.slide_designation);
        slideHeight = (TextView) findViewById(R.id.slide_height);
        slideName = (TextView) findViewById(R.id.slide_name);
        slideReligion = (TextView) findViewById(R.id.slide_religion);
        slideTraits = (TextView) findViewById(R.id.slide_traits_match);
        blurringView.setBlurredView(backgroundPhoto);
        rippleBackground.startRippleAnimation();

        pinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mLikeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (liked) {
                    mLikeUser.setBackgroundResource(R.drawable.unlike);
                    liked = false;
                    mApp.showToast(context, "Liked");

                } else {
                    mLikeUser.setBackgroundResource(R.drawable.like);
                    liked = true;
                }
            }
        });
        getParseData();
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(navigationMenu);
        menu.setSlidingEnabled(true);
        menu.setBehindOffset(124);
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

    void getParseData() {
        navigationMenu = View.inflate(this, R.layout.fragment_menu, null);
        final TypefaceTextView profileName = (TypefaceTextView) navigationMenu.findViewById(R.id.profile_name);
        final TypefaceTextView profileButton = (TypefaceTextView) navigationMenu.findViewById(R.id.profile_button);
        final TypefaceTextView settingsButton = (TypefaceTextView) navigationMenu.findViewById(R.id.settings_button);
        final TypefaceTextView prefsButton = (TypefaceTextView) navigationMenu.findViewById(R.id.pref_button);
        final CircleImageView profilePicture = (CircleImageView) navigationMenu.findViewById(R.id.profile_image);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, EditProfileActivity.class));
            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        prefsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UserPreferences.class));
            }
        });
        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Profile");
            query.getInBackground(ParseUser.getCurrentUser().fetchIfNeeded().getParseObject("profileId").fetchIfNeeded().getObjectId(), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (e == null) {
                        try {
                            ParseFile file = parseObject.getParseFile("profilePic");
                            profileName.setText(parseObject.fetchIfNeeded().getString("name"));
                            Picasso.with(context)
                                    .load(file.getUrl())
                                    .error(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_square))
                                    .placeholder(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_square))
                                    .into(profilePicture);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
        getMatchesFromFunction();
    }

    private void getMatchesFromFunction() {
        try {
            HashMap<String, Object> params = new HashMap<>();
            params.put("oid", ParseUser.getCurrentUser().fetchIfNeeded().getParseObject("profileId").fetchIfNeeded().getObjectId());
            ParseCloud.callFunctionInBackground("filterProfileLive", params, new FunctionCallback<Object>() {
                @Override
                public void done(Object o, ParseException e) {
                    if (e == null) {
                        Log.e("", "" + o);
                        if (o != null)
                            profileList = (ArrayList<ParseObject>) o;
                        if (profileList.size() > 0) {
                            setProfileDetails();
                        }
                    } else
                        e.printStackTrace();
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setProfileDetails() {
        try {
            if (profileList.get(0).containsKey("profilePic") && profileList.get(0).getParseFile("profilePic") != JSONObject.NULL)
                Picasso.with(context)
                        .load(profileList.get(0).getParseFile("profilePic").getUrl())
                        .placeholder(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_square))
                        .error(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_square))
                        .into(frontPhoto);
            if (profileList.get(0).containsKey("name") && profileList.get(0).getString("name") != JSONObject.NULL) {
                frontProfileName.setText(profileList.get(0).getString("name"));
                slideName.setText(profileList.get(0).getString("name"));
            }
            if (profileList.get(0).containsKey("height") && profileList.get(0).getString("height") != JSONObject.NULL) {
                int[] bases = getResources().getIntArray(R.array.heightCM);
                String[] values = getResources().getStringArray(R.array.height);
                Arrays.sort(bases);
                int index = Arrays.binarySearch(bases, profileList.get(0).getInt("height"));
                frontHeight.setText(values[index]);
                slideHeight.setText(values[index]);
            }
            if (profileList.get(0).containsKey("religionId") && profileList.get(0).getParseObject("religionId") != JSONObject.NULL) {
                frontReligion.setText(profileList.get(0).getParseObject("religionId").fetchIfNeeded().getString("name"));
                slideReligion.setText(profileList.get(0).getParseObject("religionId").fetchIfNeeded().getString("name"));
            }
            if (profileList.get(0).containsKey("casteId") && profileList.get(0).getParseObject("casteId") != JSONObject.NULL) {
                slideReligion.append(", " + profileList.get(0).getParseObject("casteId").fetchIfNeeded().getString("name"));
                frontReligion.append(", " + profileList.get(0).getParseObject("casteId").fetchIfNeeded().getString("name"));
            }
            if (profileList.get(0).containsKey("designation") && profileList.get(0).getString("designation") != JSONObject.NULL) {
                frontDesignation.setText(profileList.get(0).getString("designation"));
                slideDesignation.setText(profileList.get(0).getString("designation"));
            }
            if (profileList.get(0).containsKey("currentLocation") && profileList.get(0).getParseObject("currentLocation") != JSONObject.NULL) {
                ParseObject city = profileList.get(0).getParseObject("currentLocation");
                ParseObject state = profileList.get(0).getParseObject("currentLocation").fetchIfNeeded().getParseObject("Parent");
                ParseObject country = profileList.get(0).getParseObject("currentLocation").fetchIfNeeded().getParseObject("Parent").fetchIfNeeded().getParseObject("Parent");
                currentLocation.setText(": " + city.fetchIfNeeded().getString("name"));
                currentLocation.append(", " + state.fetchIfNeeded().getString("name"));
                currentLocation.append(", " + country.fetchIfNeeded().getString("name"));
            }
            if (profileList.get(0).containsKey("weight") && profileList.get(0).getInt("weight") != 0) {
                weight.setText(": " + profileList.get(0).getInt("weight") + " KG");
            }
            if (profileList.get(0).containsKey("package") && profileList.get(0).getLong("package") != 0) {
                salary.setText("Rs. " + mApp.numberToWords(profileList.get(0).getInt("package")));
            }
            if (profileList.get(0).containsKey("education1") && profileList.get(0).getParseObject("education1") != JSONObject.NULL) {
                education.setText(profileList.get(0).getParseObject("education1").fetchIfNeeded().getString("name"));
            }
            if (profileList.get(0).containsKey("education2") && profileList.get(0).getParseObject("education2") != JSONObject.NULL) {
                education.append(", " + profileList.get(0).getParseObject("education2").fetchIfNeeded().getString("name"));
            }
            if (profileList.get(0).containsKey("education3") && profileList.get(0).getParseObject("education3") != JSONObject.NULL) {
                education.append(", " + profileList.get(0).getParseObject("education3").fetchIfNeeded().getString("name"));
            }
            rippleBackground.stopRippleAnimation();
            rippleBackground.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Photo");
        parseQuery.whereEqualTo("profileId", profileList.get(0));
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list != null && list.size() > 0) {
                    for (ParseObject model : list) {
                        try {
                            ImageModel imageModel = new ImageModel();
                            ParseFile file = model.fetchIfNeeded().getParseFile("file");
                            imageModel.setLink(file.getUrl());
                            imageModel.setIsPrimary(true);
                            userProfileImages.add(imageModel);
                            if (model.getBoolean("isPrimary")) {
                                Picasso.with(context)
                                        .load(file.getUrl())
                                        .error(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_portrait))
                                        .placeholder(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_portrait))
                                        .into(backgroundPhoto, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        blurringView.invalidate();
                                                    }
                                                }, 800);
                                            }

                                            @Override
                                            public void onError() {
                                                blurringView.invalidate();
                                            }
                                        });
                            }
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                    userImagesAdapter = new UserImagesAdapter(context, MainActivity.this, userProfileImages);
                    profileImages.setAdapter(userImagesAdapter);
                }
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

    public void previewPhoto(Intent intent) {
        startActivityForResult(intent, REQUEST_CODE);
    }
}
