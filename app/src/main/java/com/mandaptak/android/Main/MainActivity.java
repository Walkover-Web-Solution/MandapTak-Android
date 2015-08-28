package com.mandaptak.android.Main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.mandaptak.android.Matches.MatchesActivity;
import com.mandaptak.android.Models.UndoModel;
import com.mandaptak.android.Preferences.UserPreferences;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.mandaptak.android.Views.BitmapTransform;
import com.mandaptak.android.Views.BlurringView;
import com.mandaptak.android.Views.CircleImageView;
import com.mandaptak.android.Views.TypefaceTextView;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
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
    TextView salary, designation, company, education, weight, currentLocation, viewFullProfile;
    TextView slideName, slideHeight, slideReligion, slideDesignation, slideTraits;
    RippleBackground rippleBackground;
    TextView loadingLabel;
    Toolbar toolbar;
    ImageView mainLikeButton, mainSkipButton, mainUndoButton;
    UndoModel undoModel;

    void init() {
        undoModel = new UndoModel();
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
        loadingLabel = (TextView) findViewById(R.id.search_label);
        viewFullProfile = (TextView) findViewById(R.id.view_full_profile);
        mainLikeButton = (ImageView) findViewById(R.id.like_button);
        mainSkipButton = (ImageView) findViewById(R.id.skip_button);
        mainUndoButton = (ImageView) findViewById(R.id.undo_button);
    }

    void clickListeners() {
        pinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mApp.show_PDialog(context, "Pinning Profile..");
                    ParseObject dislikeParseObject = new ParseObject("PinnedProfile");
                    dislikeParseObject.put("pinnedProfileId", profileList.get(0));
                    dislikeParseObject.put("profileId", ParseUser.getCurrentUser().fetchIfNeeded().getParseObject("profileId"));
                    dislikeParseObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            mApp.dialog.dismiss();
                            if (e == null) {
                                undoModel.setProfileParseObject(profileList.get(0));
                                undoModel.setActionPerformed(2);
                                profileList.remove(0);
                                if (profileList.size() > 0) {
                                    setProfileDetails();
                                } else {
                                    loadingLabel.setText("No more Matching profiles present.");
                                    rippleBackground.setVisibility(View.VISIBLE);
                                    rippleBackground.stopRippleAnimation();
                                    toolbar.setVisibility(View.VISIBLE);
                                }
                            } else {
                                e.printStackTrace();
                                mApp.showToast(context, "Error while pinning profile");
                            }
                        }
                    });
                } catch (Exception e) {
                    mApp.dialog.dismiss();
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
                            try {
                                mApp.show_PDialog(context, "Performing Undo..");
                                ParseQuery<ParseObject> parseQuery = new ParseQuery<>("DislikeProfile");
                                parseQuery.whereEqualTo("dislikeProfileId", undoModel.getProfileParseObject());
                                parseQuery.whereEqualTo("profileId", ParseUser.getCurrentUser().fetchIfNeeded().getParseObject("profileId"));
                                parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject parseObject, ParseException e) {
                                        if (e == null) {
                                            parseObject.deleteInBackground(new DeleteCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    mApp.dialog.dismiss();
                                                    if (e == null) {
                                                        profileList.add(0, undoModel.getProfileParseObject());
                                                        undoModel = new UndoModel();
                                                        setProfileDetails();
                                                    } else {
                                                        mApp.dialog.dismiss();
                                                    }
                                                }
                                            });
                                        } else {
                                            mApp.dialog.dismiss();
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case 1:
                            try {
                                mApp.show_PDialog(context, "Performing Undo..");
                                ParseQuery<ParseObject> parseQuery = new ParseQuery<>("LikedProfile");
                                parseQuery.whereEqualTo("likeProfileId", undoModel.getProfileParseObject());
                                parseQuery.whereEqualTo("profileId", ParseUser.getCurrentUser().fetchIfNeeded().getParseObject("profileId"));
                                parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject parseObject, ParseException e) {
                                        if (e == null) {
                                            parseObject.deleteInBackground(new DeleteCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    mApp.dialog.dismiss();
                                                    if (e == null) {
                                                        profileList.add(0, undoModel.getProfileParseObject());
                                                        undoModel = new UndoModel();
                                                        setProfileDetails();
                                                    } else {
                                                        mApp.dialog.dismiss();
                                                    }
                                                }
                                            });
                                        } else {
                                            mApp.dialog.dismiss();
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case 2:
                            try {
                                mApp.show_PDialog(context, "Performing Undo..");
                                ParseQuery<ParseObject> parseQuery = new ParseQuery<>("PinnedProfile");
                                parseQuery.whereEqualTo("pinnedProfileId", undoModel.getProfileParseObject());
                                parseQuery.whereEqualTo("profileId", ParseUser.getCurrentUser().fetchIfNeeded().getParseObject("profileId"));
                                parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject parseObject, ParseException e) {
                                        if (e == null) {
                                            parseObject.deleteInBackground(new DeleteCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    mApp.dialog.dismiss();
                                                    if (e == null) {
                                                        profileList.add(0, undoModel.getProfileParseObject());
                                                        undoModel = new UndoModel();
                                                        setProfileDetails();
                                                    } else {
                                                        mApp.dialog.dismiss();
                                                    }
                                                }
                                            });
                                        } else {
                                            mApp.dialog.dismiss();
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                }
            }
        });
        mainSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mApp.show_PDialog(context, "Skipping Profile..");
                    ParseObject dislikeParseObject = new ParseObject("DislikeProfile");
                    dislikeParseObject.put("dislikeProfileId", profileList.get(0));
                    dislikeParseObject.put("profileId", ParseUser.getCurrentUser().fetchIfNeeded().getParseObject("profileId"));
                    dislikeParseObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            mApp.dialog.dismiss();
                            if (e == null) {
                                undoModel.setProfileParseObject(profileList.get(0));
                                undoModel.setActionPerformed(0);
                                profileList.remove(0);
                                if (profileList.size() > 0) {
                                    setProfileDetails();
                                } else {
                                    loadingLabel.setText("No more Matching profiles present.");
                                    rippleBackground.setVisibility(View.VISIBLE);
                                    rippleBackground.stopRippleAnimation();
                                    toolbar.setVisibility(View.VISIBLE);
                                }
                            } else {
                                e.printStackTrace();
                                mApp.showToast(context, "Error while skipping profile");
                            }
                        }
                    });
                } catch (Exception e) {
                    mApp.dialog.dismiss();
                    e.printStackTrace();
                    mApp.showToast(context, "Error while skipping profile");
                }
            }
        });
        mainLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mApp.show_PDialog(context, "Liking Profile..");
                    ParseObject dislikeParseObject = new ParseObject("LikedProfile");
                    dislikeParseObject.put("likeProfileId", profileList.get(0));
                    dislikeParseObject.put("profileId", ParseUser.getCurrentUser().fetchIfNeeded().getParseObject("profileId"));
                    dislikeParseObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            mApp.dialog.dismiss();
                            if (e == null) {
                                undoModel.setProfileParseObject(profileList.get(0));
                                undoModel.setActionPerformed(1);
                                profileList.remove(0);
                                if (profileList.size() > 0) {
                                    setProfileDetails();
                                } else {
                                    loadingLabel.setText("No more Matching profiles present.");
                                    rippleBackground.setVisibility(View.VISIBLE);
                                    rippleBackground.stopRippleAnimation();
                                    toolbar.setVisibility(View.VISIBLE);
                                }
                            } else {
                                e.printStackTrace();
                                mApp.showToast(context, "Error while liking profile");
                            }
                        }
                    });
                } catch (Exception e) {
                    mApp.dialog.dismiss();
                    e.printStackTrace();
                    mApp.showToast(context, "Error while liking profile");
                }
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        mApp = (Common) context.getApplicationContext();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setTitle("Mandap Tak");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_red);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        toolbar.setVisibility(View.GONE);

        init();

        blurringView.setBlurredView(backgroundPhoto);
        rippleBackground.startRippleAnimation();
        slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        clickListeners();

        if (mApp.isNetworkAvailable(context)) {
            mApp.show_PDialog(context, "Loading..");
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Profile");
            query.getInBackground(ParseUser.getCurrentUser().getParseObject("profileId").getObjectId(), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (e == null) {
                        validateProfile(parseObject);
                    } else {
                        e.printStackTrace();
                        mApp.dialog.dismiss();
                        mApp.showToast(context, "Connection Error");
                    }
                }
            });
        }
    }

    void getParseData() {
        setNavigationMenu();
        getMatchesFromFunction();
    }

    void setNavigationMenu() {
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
                MainActivity.this.finish();
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
                MainActivity.this.finish();
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
        try {
            HashMap<String, Object> params = new HashMap<>();
            params.put("oid", ParseUser.getCurrentUser().fetchIfNeeded().getParseObject("profileId").fetchIfNeeded().getObjectId());
            ParseCloud.callFunctionInBackground("filterProfileLive", params, new FunctionCallback<Object>() {
                @Override
                public void done(Object o, ParseException e) {
                    if (e == null) {
                        if (o != null) {
                            profileList = (ArrayList<ParseObject>) o;
                            if (profileList.size() > 0) {
                                setProfileDetails();
                            } else {
                                loadingLabel.setText("Matching profile not found");
                                rippleBackground.stopRippleAnimation();
                                toolbar.setVisibility(View.VISIBLE);
                            }
                        } else {
                            loadingLabel.setText("Matching profile not found");
                            toolbar.setVisibility(View.VISIBLE);
                            rippleBackground.stopRippleAnimation();
                        }
                    } else {
                        e.printStackTrace();
                        loadingLabel.setText("Matching profile not found");
                        toolbar.setVisibility(View.VISIBLE);
                        rippleBackground.stopRippleAnimation();
                    }
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setNavigationMenu();
    }

    private void setProfileDetails() {
        try {
            if (profileList.get(0).containsKey("profilePic") && profileList.get(0).getParseFile("profilePic") != null)
                Picasso.with(context)
                        .load(profileList.get(0).fetchIfNeeded().getParseFile("profilePic").getUrl())
                        .placeholder(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_square))
                        .error(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_square))
                        .into(frontPhoto);
            else {
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
                frontProfileName.setText(profileList.get(0).getString("name"));
                slideName.setText(profileList.get(0).getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (profileList.get(0).containsKey("height") && profileList.get(0).getString("height") != null) {
                int[] bases = getResources().getIntArray(R.array.heightCM);
                String[] values = getResources().getStringArray(R.array.height);
                Arrays.sort(bases);
                int index = Arrays.binarySearch(bases, profileList.get(0).getInt("height"));
                frontHeight.setText(values[index]);
                slideHeight.setText(values[index]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (profileList.get(0).containsKey("religionId") && profileList.get(0).getParseObject("religionId") != null) {
                frontReligion.setText(profileList.get(0).fetchIfNeeded().getParseObject("religionId").fetchIfNeeded().getString("name"));
                slideReligion.setText(profileList.get(0).fetchIfNeeded().getParseObject("religionId").fetchIfNeeded().getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (profileList.get(0).containsKey("casteId") && profileList.get(0).getParseObject("casteId") != null) {
                slideReligion.append(", " + profileList.get(0).fetchIfNeeded().getParseObject("casteId").fetchIfNeeded().getString("name"));
                frontReligion.append(", " + profileList.get(0).fetchIfNeeded().getParseObject("casteId").fetchIfNeeded().getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (profileList.get(0).containsKey("designation") && profileList.get(0).getString("designation") != null) {
                frontDesignation.setText(profileList.get(0).fetchIfNeeded().getString("designation"));
                slideDesignation.setText(profileList.get(0).fetchIfNeeded().getString("designation"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (profileList.get(0).containsKey("currentLocation") && profileList.get(0).getParseObject("currentLocation") != null) {
                ParseObject city = profileList.get(0).fetchIfNeeded().getParseObject("currentLocation");
                ParseObject state = profileList.get(0).fetchIfNeeded().getParseObject("currentLocation").fetchIfNeeded().getParseObject("Parent");
                ParseObject country = profileList.get(0).fetchIfNeeded().getParseObject("currentLocation").fetchIfNeeded().getParseObject("Parent").fetchIfNeeded().getParseObject("Parent");
                currentLocation.setText(": " + city.fetchIfNeeded().getString("name"));
                currentLocation.append(", " + state.fetchIfNeeded().getString("name"));
                currentLocation.append(", " + country.fetchIfNeeded().getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (profileList.get(0).containsKey("weight") && profileList.get(0).getInt("weight") != 0) {
                weight.setText(": " + profileList.get(0).getInt("weight") + " KG");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (profileList.get(0).containsKey("package") && profileList.get(0).getLong("package") != 0) {
                salary.setText("Rs. " + mApp.numberToWords(profileList.get(0).getInt("package")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (profileList.get(0).containsKey("education1") && profileList.get(0).getParseObject("education1") != null) {
                education.setText(profileList.get(0).getParseObject("education1").fetchIfNeeded().getString("name").replace("null", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (profileList.get(0).containsKey("education2") && profileList.get(0).getParseObject("education2") != null) {
                education.append(", " + profileList.get(0).getParseObject("education2").fetchIfNeeded().getString("name").replace("null", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (profileList.get(0).containsKey("education3") && profileList.get(0).getParseObject("education3") != null) {
                education.append(", " + profileList.get(0).getParseObject("education3").fetchIfNeeded().getString("name").replace("null", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        rippleBackground.stopRippleAnimation();
        rippleBackground.setVisibility(View.GONE);
        toolbar.setVisibility(View.VISIBLE);
        slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Photo");
        parseQuery.whereEqualTo("profileId", profileList.get(0));
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        for (ParseObject model : list) {
                            try {
                                ImageModel imageModel = new ImageModel();
                                ParseFile file = model.fetchIfNeeded().getParseFile("file");
                                imageModel.setLink(file.getUrl());
                                imageModel.setIsPrimary(true);
                                userProfileImages.add(imageModel);
                                if (model.getBoolean("isPrimary")) {
                                    final int MAX_WIDTH = 512;
                                    final int MAX_HEIGHT = 334;

                                    Picasso.with(context)
                                            .load(file.getUrl())
                                            .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
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
                    } else {
                        Picasso.with(context)
                                .load(Uri.EMPTY)
                                .placeholder(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_square))
                                .error(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_square))
                                .into(frontPhoto);
                        blurringView.invalidate();
                    }
                } else {
                    Picasso.with(context)
                            .load(Uri.EMPTY)
                            .placeholder(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_square))
                            .error(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_square))
                            .into(frontPhoto);
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
                startActivity(new Intent(context, MatchesActivity.class));
                MainActivity.this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void validateProfile(final ParseObject parseObject) {
        if (checkFieldsTab1(parseObject)) {
            if (checkFieldsTab2(parseObject)) {
                if (checkFieldsTab3(parseObject)) {
                    if (!parseObject.containsKey("profilePic") || parseObject.get("profilePic").equals(JSONObject.NULL)) {
                        startActivity(new Intent(MainActivity.this, EditProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        mApp.dialog.dismiss();
                        MainActivity.this.finish();
                    } else {
                        mApp.dialog.dismiss();
                        getParseData();
                    }
                } else {
                    startActivity(new Intent(MainActivity.this, EditProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    mApp.dialog.dismiss();
                    MainActivity.this.finish();
                }
            } else {
                startActivity(new Intent(MainActivity.this, EditProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                mApp.dialog.dismiss();
                MainActivity.this.finish();
            }
        } else {
            startActivity(new Intent(MainActivity.this, EditProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            mApp.dialog.dismiss();
            MainActivity.this.finish();
        }
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
        } else if (!parseObject.containsKey("mangalik") || parseObject.get("mangalik").equals(JSONObject.NULL)) {
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

    public void previewPhoto(Intent intent) {
        startActivityForResult(intent, REQUEST_CODE);
    }
}
