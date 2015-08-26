package com.mandaptak.android.Main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.mandaptak.android.Adapter.UserImagesAdapter;
import com.mandaptak.android.EditProfile.EditProfileActivity;
import com.mandaptak.android.Preferences.UserPreferences;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.mandaptak.android.Utils.GaussianBlur;
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
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
    public final static int REQUEST_CODE = 11;
    ImageButton mLikeUser;
    Boolean liked = false;
    ArrayList<ParseObject> profileList = new ArrayList<>();
    TextView frontProfileName, frontHeight, frontDesignation, frontImage, frontReligion;
    CircleImageView frontPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
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
        slidingLayout = (RelativeLayout) findViewById(R.id.sliding_layout);
        bottomLayout = (LinearLayout) findViewById(R.id.bottom_panel);
        slidingPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_panel);
        pinButton = (ImageView) findViewById(R.id.pin_button);
        profileImages = (TwoWayView) findViewById(R.id.list);
        backgroundPhoto = (ImageView) findViewById(R.id.background_photo);
        mLikeUser = (ImageButton) findViewById(R.id.like_user);
        twoWayView = (TwoWayView) findViewById(R.id.list);
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
                    mApp.showToast(context,"Liked");

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
                            profileName.setText(parseObject.fetchIfNeeded().getString("name"));
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
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("oid", ParseUser.getCurrentUser().fetchIfNeeded().getParseObject("profileId").fetchIfNeeded().getObjectId());
            ParseCloud.callFunctionInBackground("filterProfileLive", params, new FunctionCallback<Object>() {
                @Override
                public void done(Object o, ParseException e) {
                    if (e == null) {
                        if (o != null)
                            profileList = (ArrayList<ParseObject>) o;
                        if (profileList.size() > 0) {
                            for (ParseObject profile : profileList) {
                                Log.e("Profile", "" + profile.getObjectId());
                            }
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
        new AsyncTask<Void, Void, Void>() {
            Bitmap bitmap = null;

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Bitmap theBitmap = Glide.with(context)
                            .load("http://files.parsetfss.com/bf1cef06-c333-4108-a908-b6a35d80542a/tfss-404d1145-72a3-4793-8ea2-46aba821cd03-IMG_1440170537690.jpeg")
                            .asBitmap()
                            .into(500, 500)
                            .get();
                    if (theBitmap != null)
                        bitmap = GaussianBlur.fastblur(context, theBitmap, 4);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (bitmap != null) {
                    backgroundPhoto.setImageBitmap(bitmap);
                }
            }
        }.execute();

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
                                Target target = new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        backgroundPhoto.setImageBitmap(bitmap);
                                    }

                                    @Override
                                    public void onBitmapFailed(Drawable errorDrawable) {

                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                    }
                                };
                                Picasso.with(context).load(file.getUrl()).into(target);

//                                Picasso.with(context)
//                                        .load(Uri.parse(file.getUrl()))
//                                        .error(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_portrait))
//                                        .placeholder(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_portrait))
//                                        .into(backgroundPhoto);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

        }
    }
}
