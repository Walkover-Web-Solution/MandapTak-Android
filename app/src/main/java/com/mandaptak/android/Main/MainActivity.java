package com.mandaptak.android.Main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.mandaptak.android.FullProfile.FullProfileActivity;
import com.mandaptak.android.Login.LoginActivity;
import com.mandaptak.android.Matches.MatchedProfileActivity;
import com.mandaptak.android.Matches.MatchesActivity;
import com.mandaptak.android.Models.MatchesModel;
import com.mandaptak.android.Models.UndoModel;
import com.mandaptak.android.Preferences.UserPreferences;
import com.mandaptak.android.R;
import com.mandaptak.android.Settings.SettingsActivity;
import com.mandaptak.android.Utils.Common;
import com.mandaptak.android.Views.BitmapTransform;
import com.mandaptak.android.Views.BlurringView;
import com.mandaptak.android.Views.CircleImageView;
import com.mandaptak.android.Views.TypefaceTextView;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.LogOutCallback;
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

import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import main.java.com.mindscapehq.android.raygun4android.RaygunClient;
import mbanje.kurt.fabbutton.FabButton;
import me.iwf.photopicker.utils.ImageModel;
import me.iwf.photopicker.utils.Prefs;

public class MainActivity extends AppCompatActivity {
    public final static int REQUEST_CODE = 11;
    private Common mApp;
    private SlidingMenu menu;
    private RelativeLayout slidingLayout;
    private LinearLayout bottomLayout;
    private SlidingUpPanelLayout slidingPanel;
    private ImageView pinButton;
    private ImageView backgroundPhoto;
    private Context context;
    private ArrayList<ImageModel> userProfileImages = new ArrayList<>();
    private TwoWayView profileImages;
    private UserImagesAdapter userImagesAdapter;
    private ArrayList<ParseObject> profileList = new ArrayList<>();
    private TextView frontProfileName, frontHeight, frontDesignation, frontReligion, frontTraits;
    private CircleImageView frontPhoto, loadingProfile;
    private BlurringView blurringView;
    private TextView salary, industry, designation, education, weight, currentLocation, viewFullProfile;
    private TextView slideName, slideHeight, slideReligion, slideDesignation, slideTraits;
    private RippleBackground rippleBackground;
    private ImageButton slideLike;
    private ImageView mainLikeButton, mainSkipButton, mainUndoButton;
    private UndoModel undoModel;
    private TextView labelLoading;
    private ParseObject profileObject;
    private FabButton traitsProgress;
    private boolean isLoading = false;

    void setTraits() {
        try {
            HashMap<String, Object> params = new HashMap<>();
            if (profileObject.fetchIfNeeded().getString("gender").equalsIgnoreCase("Male")) {
                params.put("boyProfileId", profileObject.getObjectId());
                params.put("girlProfileId", profileList.get(0).getObjectId());
            } else {
                params.put("boyProfileId", profileList.get(0).getObjectId());
                params.put("girlProfileId", profileObject.getObjectId());
            }

            ParseCloud.callFunctionInBackground("matchKundli", params, new FunctionCallback<Object>() {
                @Override
                public void done(Object o, ParseException e) {
                    if (e == null) {
                        if (o != null) {
                            try {
                                if (o instanceof Double) {
                                    Double value = (Double) o;
                                    Log.e("", "" + o);
                                    frontTraits.setText(value + "\nTraits\nMatch");
                                    slideTraits.setText(value + " Traits Match");
                                    traitsProgress.setProgress(value.intValue());
                                    traitsProgress.showProgress(true);
                                } else if (o instanceof Integer) {
                                    int value = (int) o;
                                    Log.e("", "" + o);
                                    frontTraits.setText(value + "\nTraits\nMatch");
                                    slideTraits.setText(value + " Traits Match");
                                    traitsProgress.setProgress(value);
                                    traitsProgress.showProgress(true);
                                }
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    } else {
                        e.printStackTrace();
                        RaygunClient.Send(new Throwable(e.getMessage() + " traits_function"));
                        mApp.showToast(context, e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void likeProfile() {
        if (mApp.isNetworkAvailable(context))
            try {
                final ParseObject likeProfile = profileList.get(0);
                profileList.remove(0);
                if (profileList.size() > 0) {
                    setProfileDetails();
                } else {
                    isLoading = false;
                    labelLoading.setText("No matching results found.");
                    rippleBackground.stopRippleAnimation();
                    rippleBackground.setVisibility(View.VISIBLE);
                }
                HashMap<String, Object> params = new HashMap<>();
                params.put("userProfileId", profileObject.getObjectId());
                params.put("likeProfileId", likeProfile.getObjectId());
                params.put("userName", profileObject.fetchIfNeeded().getString("name"));

                ParseCloud.callFunctionInBackground("likeAndFind", params, new FunctionCallback<Object>() {
                    @Override
                    public void done(Object o, ParseException e) {
                        if (e == null) {
                            if (o != null) {
                                try {
                                    slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                                    undoModel.setProfileParseObject(likeProfile);
                                    undoModel.setActionPerformed(1);
                                    if (o instanceof ParseObject) {
                                        ParseObject parseObject = likeProfile;
                                        //ParseObject parseObject = (ParseObject) o;
                                        String religion = parseObject.fetchIfNeeded().getParseObject("religionId").fetchIfNeeded().getString("name");
                                        String caste = parseObject.fetchIfNeeded().getParseObject("casteId").fetchIfNeeded().getString("name");
                                        MatchesModel model = new MatchesModel();
                                        model.setName(parseObject.fetchIfNeeded().getString("name"));
                                        model.setProfileId(parseObject.getObjectId());
                                        model.setReligion(religion + ", " + caste);
                                        model.setWork(parseObject.getString("designation"));
                                        model.setUrl(parseObject.fetchIfNeeded().getParseFile("profilePic").getUrl());
                                        // model.setUserId(parseObject.fetchIfNeeded().getParseUser("userId").getUsername());
                                        Intent intent = new Intent(context, MatchedProfileActivity.class);
                                        intent.putExtra("profile", model);
                                        startActivity(intent);
                                    }
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        } else {
                            e.printStackTrace();
                            mApp.showToast(context, e.getMessage());
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                RaygunClient.Send(new Throwable(e.getMessage() + " like function"));
                mApp.showToast(context, "Error while liking profile");
            }
    }

    void init() {
        undoModel = new UndoModel();
        rippleBackground = (RippleBackground) findViewById(R.id.content);
        slidingLayout = (RelativeLayout) findViewById(R.id.sliding_layout);
        bottomLayout = (LinearLayout) findViewById(R.id.bottom_panel);
        slidingPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_panel);
        pinButton = (ImageView) findViewById(R.id.pin_button);
        profileImages = (TwoWayView) findViewById(R.id.list);
        backgroundPhoto = (ImageView) findViewById(R.id.background_photo);
        profileImages = (TwoWayView) findViewById(R.id.list);
        blurringView = (BlurringView) findViewById(R.id.blurring_view);
        frontProfileName = (TextView) findViewById(R.id.front_name);
        frontPhoto = (CircleImageView) findViewById(R.id.front_photo);
        loadingProfile = (CircleImageView) findViewById(R.id.loading_profile);
        frontDesignation = (TextView) findViewById(R.id.front_designation);
        frontHeight = (TextView) findViewById(R.id.front_height);
        frontReligion = (TextView) findViewById(R.id.front_religion);
        frontTraits = (TextView) findViewById(R.id.front_traits);
        salary = (TextView) findViewById(R.id.salary);
        designation = (TextView) findViewById(R.id.designation);
        industry = (TextView) findViewById(R.id.industry);
        education = (TextView) findViewById(R.id.education);
        weight = (TextView) findViewById(R.id.weight);
        currentLocation = (TextView) findViewById(R.id.current_location);
        slideDesignation = (TextView) findViewById(R.id.slide_designation);
        slideHeight = (TextView) findViewById(R.id.slide_height);
        slideName = (TextView) findViewById(R.id.slide_name);
        slideReligion = (TextView) findViewById(R.id.slide_religion);
        slideTraits = (TextView) findViewById(R.id.slide_traits_match);
        slideLike = (ImageButton) findViewById(R.id.slide_like);
        viewFullProfile = (TextView) findViewById(R.id.view_full_profile);
        mainLikeButton = (ImageView) findViewById(R.id.like_button);
        mainSkipButton = (ImageView) findViewById(R.id.skip_button);
        mainUndoButton = (ImageView) findViewById(R.id.undo_button);
        labelLoading = (TextView) findViewById(R.id.label_loading);
        traitsProgress = (FabButton) findViewById(R.id.traits_progress);
    }

    void clickListeners() {
        pinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mApp.isNetworkAvailable(context))
                    try {
                        ParseObject dislikeParseObject = new ParseObject("PinnedProfile");
                        dislikeParseObject.put("pinnedProfileId", profileList.get(0));
                        dislikeParseObject.put("profileId", profileObject);
                        dislikeParseObject.saveInBackground();
                        undoModel.setProfileParseObject(profileList.get(0));
                        undoModel.setActionPerformed(2);
                        profileList.remove(0);
                        if (profileList.size() > 0) {
                            setProfileDetails();
                        } else {
                            isLoading = false;
                            labelLoading.setText("No matching results found.");
                            rippleBackground.stopRippleAnimation();
                            rippleBackground.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        mApp.showToast(context, "Error while pinning profile");
                    }
            }
        });
        mainUndoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (undoModel.getActionPerformed() != -1) {
                    switch (undoModel.getActionPerformed()) {
                        case 0:
                            if (mApp.isNetworkAvailable(context))
                                try {
                                    ParseQuery<ParseObject> parseQuery = new ParseQuery<>("DislikeProfile");
                                    parseQuery.whereEqualTo("dislikeProfileId", undoModel.getProfileParseObject());
                                    parseQuery.whereEqualTo("profileId", profileObject);
                                    parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                                        @Override
                                        public void done(ParseObject parseObject, ParseException e) {
                                            if (e == null) {
                                                parseObject.deleteInBackground();
                                            }
                                        }
                                    });
                                    profileList.add(0, undoModel.getProfileParseObject());
                                    undoModel = new UndoModel();
                                    setProfileDetails();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            break;
                        case 1:
                            if (mApp.isNetworkAvailable(context))
                                try {
                                    ParseQuery<ParseObject> parseQuery = new ParseQuery<>("LikedProfile");
                                    parseQuery.whereEqualTo("likeProfileId", undoModel.getProfileParseObject());
                                    parseQuery.whereEqualTo("profileId", profileObject);
                                    parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                                        @Override
                                        public void done(ParseObject parseObject, ParseException e) {
                                            if (e == null) {
                                                parseObject.deleteInBackground();
                                            }
                                        }
                                    });
                                    profileList.add(0, undoModel.getProfileParseObject());
                                    undoModel = new UndoModel();
                                    setProfileDetails();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            break;
                        case 2:
                            if (mApp.isNetworkAvailable(context))
                                try {
                                    ParseQuery<ParseObject> parseQuery = new ParseQuery<>("PinnedProfile");
                                    parseQuery.whereEqualTo("pinnedProfileId", undoModel.getProfileParseObject());
                                    parseQuery.whereEqualTo("profileId", profileObject);
                                    parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                                        @Override
                                        public void done(ParseObject parseObject, ParseException e) {
                                            if (e == null) {
                                                parseObject.deleteInBackground();
                                            }
                                        }
                                    });
                                    profileList.add(0, undoModel.getProfileParseObject());
                                    undoModel = new UndoModel();
                                    setProfileDetails();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            break;
                    }
                } else {
                    mApp.showToast(context, "undo not available");
                }
            }
        });
        mainSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mApp.isNetworkAvailable(context))
                    try {
                        ParseObject dislikeParseObject = new ParseObject("DislikeProfile");
                        dislikeParseObject.put("dislikeProfileId", profileList.get(0));
                        dislikeParseObject.put("profileId", profileObject);
                        dislikeParseObject.saveInBackground();
                        undoModel.setProfileParseObject(profileList.get(0));
                        undoModel.setActionPerformed(0);
                        profileList.remove(0);
                        if (profileList.size() > 0) {
                            setProfileDetails();
                        } else {
                            isLoading = false;
                            labelLoading.setText("No matching results found.");
                            rippleBackground.stopRippleAnimation();
                            rippleBackground.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        mApp.showToast(context, "Error while skipping profile");
                    }
            }
        });
        mainLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likeProfile();
            }
        });
        viewFullProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullProfileActivity.class);
                intent.putExtra("parseObjectId", profileList.get(0).getObjectId());
                startActivity(intent);
                MainActivity.this.finish();
            }
        });

        slideLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likeProfile();
            }
        });
        rippleBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLoading) {
                    getMatchesFromFunction();
                }
            }
        });
    }

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
        init();
        blurringView.setBlurredView(backgroundPhoto);
        rippleBackground.startRippleAnimation();
        slidingPanel.setEnabled(false);
        slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        clickListeners();
        if (mApp.isNetworkAvailable(context))
            getParseData();
    }

    void getParseData() {
        profileObject = ParseObject.createWithoutData("Profile", Prefs.getProfileId(context));
        setNavigationMenu();
        getMatchesFromFunction();
    }

    void setNavigationMenu() {
        isLoading = false;
        View navigationMenu = View.inflate(this, R.layout.fragment_menu, null);
        final TypefaceTextView profileName = (TypefaceTextView) navigationMenu.findViewById(R.id.profile_name);
        final TypefaceTextView profileButton = (TypefaceTextView) navigationMenu.findViewById(R.id.profile_button);
        final TypefaceTextView settingsButton = (TypefaceTextView) navigationMenu.findViewById(R.id.settings_button);
        final TypefaceTextView prefsButton = (TypefaceTextView) navigationMenu.findViewById(R.id.pref_button);
        final CircleImageView profilePicture = (CircleImageView) navigationMenu.findViewById(R.id.profile_image);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, EditProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_CLEAR_TOP));
                MainActivity.this.finish();
            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_CLEAR_TOP));
                MainActivity.this.finish();
            }
        });
        prefsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UserPreferences.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_CLEAR_TOP));
                MainActivity.this.finish();
            }
        });

        ParseQuery<ParseObject> query = new ParseQuery<>("Profile");
        query.getInBackground(Prefs.getProfileId(context), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                try {
                    if (e == null) {
                        profileName.setText(parseObject.fetchIfNeeded().getString("name"));
                        ParseFile file = parseObject.fetchIfNeeded().getParseFile("profilePic");
                        Picasso.with(context)
                                .load(file.getUrl())
                                .error(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_square))
                                .placeholder(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_square))
                                .into(profilePicture);
                        Picasso.with(context)
                                .load(file.getUrl())
                                .placeholder(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_square))
                                .error(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_square))
                                .into(loadingProfile);
                    } else if (e.getCode() == 209) {
                        ParseUser.logOutInBackground(new LogOutCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    startActivity(new Intent(MainActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    MainActivity.this.finish();
                                }
                            }
                        });
                    } else {
                        e.printStackTrace();
                    }
                } catch (Exception ignored) {
                }
            }
        });

        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(navigationMenu);
        menu.setBehindOffset(124);
        menu.setSlidingEnabled(false);
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

    private void getMatchesFromFunction() {
        isLoading = false;
        rippleBackground.startRippleAnimation();
        labelLoading.setText("Finding Matches...");
        HashMap<String, Object> params = new HashMap<>();
        params.put("oid", Prefs.getProfileId(context));
        ParseCloud.callFunctionInBackground("filterProfileLive", params, new FunctionCallback<ArrayList<ParseObject>>() {
            @Override
            public void done(ArrayList<ParseObject> o, ParseException e) {
                if (e == null) {
                    if (o != null) {
                        profileList = o;
                        if (profileList.size() > 0) {
                            if (mApp.isNetworkAvailable(context))
                                setProfileDetails();
                        } else {
                            rippleBackground.stopRippleAnimation();
                            labelLoading.setText("No matching results found.");
                        }
                    } else {
                        rippleBackground.stopRippleAnimation();
                        labelLoading.setText("No matching results found.");
                    }
                } else {
                    rippleBackground.stopRippleAnimation();
                    labelLoading.setText("No matching results found.");
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (menu.isMenuShowing()) {
            menu.toggle();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mApp.isNetworkAvailable(context))
            setNavigationMenu();
    }

    private void setProfileDetails() {

        isLoading = true;
        rippleBackground.setVisibility(View.VISIBLE);
        labelLoading.setText("Loading Profile...");
        slidingPanel.setEnabled(false);
        slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        try {
            if (profileList.get(0).containsKey("profilePic") && profileList.get(0).getParseFile("profilePic") != null) {
                Picasso.with(context)
                        .load(profileList.get(0).fetchIfNeeded().getParseFile("profilePic").getUrl())
                        .placeholder(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_square))
                        .error(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_square))
                        .into(frontPhoto);
                Picasso.with(context)
                        .load(profileList.get(0).fetchIfNeeded().getParseFile("profilePic").getUrl())
                        .transform(new BitmapTransform(512, 334))
                        .error(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_portrait))
                        .placeholder(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_portrait))
                        .into(backgroundPhoto, new Callback() {
                            @Override
                            public void onSuccess() {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        blurringView.invalidate();
                                        rippleBackground.setVisibility(View.GONE);
                                    }
                                }, 800);
                            }

                            @Override
                            public void onError() {
                                blurringView.invalidate();
                                setProfileDetails();
                            }
                        });
            } else {
                Picasso.with(context)
                        .load(Uri.EMPTY)
                        .placeholder(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_square))
                        .error(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_square))
                        .into(frontPhoto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (profileList.get(0).containsKey("name") && profileList.get(0).getString("name") != null) {
                String name = profileList.get(0).getString("name");
                Log.e("Profile Name", "" + name);
                frontProfileName.setText(name);
                slideName.setText(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (profileList.get(0).containsKey("age") && profileList.get(0).getInt("age") != 0) {
                int age = profileList.get(0).getInt("age");
                frontHeight.setText("" + age);
                slideHeight.setText("" + age);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (profileList.get(0).containsKey("height") && profileList.get(0).getInt("height") != 0) {
                int[] bases = getResources().getIntArray(R.array.heightCM);
                String[] values = getResources().getStringArray(R.array.height);
                Arrays.sort(bases);
                int index = Arrays.binarySearch(bases, profileList.get(0).getInt("height"));
                frontHeight.append(", " + values[index]);
                slideHeight.append(", " + values[index]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (profileList.get(0).containsKey("religionId") && profileList.get(0).getParseObject("religionId") != null) {
                String religion = profileList.get(0).fetchIfNeeded().getParseObject("religionId").fetchIfNeeded().getString("name");
                frontReligion.setText(religion);
                slideReligion.setText(religion);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (profileList.get(0).containsKey("casteId") && profileList.get(0).getParseObject("casteId") != null) {
                String caste = profileList.get(0).fetchIfNeeded().getParseObject("casteId").fetchIfNeeded().getString("name");
                slideReligion.append(", " + caste);
                frontReligion.append(", " + caste);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (profileList.get(0).containsKey("designation") && profileList.get(0).getString("designation") != null) {
                String desig = profileList.get(0).fetchIfNeeded().getString("designation");
                frontDesignation.setText(desig);
                slideDesignation.setText(desig);
                designation.setText(desig);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (profileList.get(0).containsKey("industryId") && profileList.get(0).getParseObject("industryId") != null) {
                String indust = profileList.get(0).fetchIfNeeded().getParseObject("industryId").fetchIfNeeded().getString("name");
                industry.setText(indust);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (profileList.get(0).containsKey("currentLocation") && profileList.get(0).getParseObject("currentLocation") != null) {
                String city = profileList.get(0).fetchIfNeeded().getParseObject("currentLocation").fetchIfNeeded().getString("name");
                String state = profileList.get(0).fetchIfNeeded().getParseObject("currentLocation").fetchIfNeeded().getParseObject("Parent").fetchIfNeeded().getString("name");
                String country = profileList.get(0).fetchIfNeeded().getParseObject("currentLocation").fetchIfNeeded().getParseObject("Parent").fetchIfNeeded().getParseObject("Parent").fetchIfNeeded().getString("name");
                currentLocation.setText(city);
                currentLocation.append(", " + state);
                currentLocation.append(", " + country);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (profileList.get(0).containsKey("weight") && profileList.get(0).getInt("weight") != 0) {
                weight.setText(profileList.get(0).getInt("weight") + " KG");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (profileList.get(0).containsKey("package") && profileList.get(0).getLong("package") != 0) {
                salary.setText("Rs. " + mApp.numberToWords(profileList.get(0).getLong("package")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (profileList.get(0).containsKey("education1") && profileList.get(0).getParseObject("education1") != null) {
                education.setText(profileList.get(0).getParseObject("education1").fetchIfNeeded().getParseObject("degreeId").fetchIfNeeded().getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (profileList.get(0).containsKey("education2") && profileList.get(0).getParseObject("education2") != null) {
                education.append("\n" + profileList.get(0).getParseObject("education2").fetchIfNeeded().getParseObject("degreeId").fetchIfNeeded().getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (profileList.get(0).containsKey("education3") && profileList.get(0).getParseObject("education3") != null) {
                education.append("\n" + profileList.get(0).getParseObject("education3").fetchIfNeeded().getParseObject("degreeId").fetchIfNeeded().getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setTraits();
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Photo");
        parseQuery.whereEqualTo("profileId", profileList.get(0));
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        userProfileImages.clear();
                        for (ParseObject model : list) {
                            try {
                                ImageModel imageModel = new ImageModel();
                                ParseFile file = model.fetchIfNeeded().getParseFile("file");
                                imageModel.setLink(file.getUrl());
                                imageModel.setIsPrimary(false);
                                imageModel.setParseObject(model.getObjectId());
                                userProfileImages.add(imageModel);
//                                if (model.getBoolean("isPrimary")) {
//                                    final int MAX_WIDTH = 512;
//                                    final int MAX_HEIGHT = 334;
//
//                                    Picasso.with(context)
//                                            .load(file.getUrl())
//                                            .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
//                                            .error(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_portrait))
//                                            .placeholder(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_portrait))
//                                            .into(backgroundPhoto, new Callback() {
//                                                @Override
//                                                public void onSuccess() {
//                                                    new Handler().postDelayed(new Runnable() {
//                                                        @Override
//                                                        public void run() {
//                                                            rippleBackground.setVisibility(View.GONE);
//                                                            slidingPanel.setEnabled(true);
//                                                            slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
//                                                            blurringView.invalidate();
//                                                        }
//                                                    }, 800);
//                                                }
//
//                                                @Override
//                                                public void onError() {
//                                                    blurringView.invalidate();
//                                                    setProfileDetails();
//                                                }
//                                            });
//                                }
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                        }
                        userImagesAdapter = new UserImagesAdapter(context, MainActivity.this, userProfileImages);
                        profileImages.setAdapter(userImagesAdapter);
                        slidingPanel.setEnabled(true);
                        slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    } else {
                        Picasso.with(context)
                                .load(Uri.EMPTY)
                                .placeholder(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_square))
                                .error(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_square))
                                .into(backgroundPhoto);
                        blurringView.invalidate();
                    }
                } else {
                    Picasso.with(context)
                            .load(Uri.EMPTY)
                            .placeholder(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_square))
                            .error(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_square))
                            .into(backgroundPhoto);
                    blurringView.invalidate();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                menu.toggle();
                return true;
            case R.id.action_matches:
                startActivity(new Intent(context, MatchesActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_CLEAR_TOP));
                MainActivity.this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void previewPhoto(Intent intent) {
        startActivityForResult(intent, REQUEST_CODE);
    }
}
