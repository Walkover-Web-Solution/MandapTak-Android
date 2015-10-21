package com.mandaptak.android.Main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.PointF;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
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

import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.lucasr.twowayview.widget.TwoWayView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import jp.wasabeef.fresco.processors.BlurPostprocessor;
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
  private SimpleDraweeView backgroundPhoto;
  public Context context;
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

  public void setTraits() {
    try {
      HashMap<String, Object> params = new HashMap<>();
      if (profileObject.fetchIfNeeded().getString("gender").equalsIgnoreCase("Male")) {
        params.put("boyProfileId", profileObject.getObjectId());
        params.put("girlProfileId", profileList.get(0).getObjectId());
      } else {
        params.put("boyProfileId", profileList.get(0).getObjectId());
        params.put("girlProfileId", profileObject.getObjectId());
      }
      frontTraits.setText("...");
      slideTraits.setText("...");
      traitsProgress.showProgress(true);
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

  public void likeProfile() {
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
          slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("userProfileId", profileObject.getObjectId());
        params.put("likeProfileId", likeProfile.getObjectId());
        params.put("userName", profileObject.getString("name"));

        ParseCloud.callFunctionInBackground("likeAndFind", params, new FunctionCallback<Object>() {
          @Override
          public void done(Object o, ParseException e) {
            if (e == null) {
              if (o != null) {
                try {
//                  slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                  undoModel.setProfileParseObject(likeProfile);
                  undoModel.setActionPerformed(1);
                  if (o instanceof ParseObject) {
                    MatchesModel model = prepareDataIfMatchFoundOnLikeAndFind();
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

          private MatchesModel prepareDataIfMatchFoundOnLikeAndFind() throws ParseException {
            ParseObject parseObject = likeProfile;
            String religion = parseObject.fetchIfNeeded().getParseObject("religionId").getString("name");
            String caste = parseObject.fetchIfNeeded().getParseObject("casteId").getString("name");
            MatchesModel model = new MatchesModel();
            model.setName(parseObject.getString("name"));
            model.setProfileId(parseObject.getObjectId());
            model.setReligion(religion + ", " + caste);
            model.setWork(parseObject.getString("designation"));
            model.setUrl(parseObject.fetchIfNeeded().getParseFile("profilePic").getUrl());
            return model;
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
    backgroundPhoto = (SimpleDraweeView) findViewById(R.id.background_photo);
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

  public void setupClickListeners() {
    pinButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (mApp.isNetworkAvailable(context))
          try {
            savePinnedProfileParse();
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
                  deleteDislikeProfileRowFromParse();
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
                  deleteLikedProfileRowFromParse();
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
                  deletePinnedProfileRowFromParse();
                  profileList.add(0, undoModel.getProfileParseObject());
                  undoModel = new UndoModel();
                  setProfileDetails();
                } catch (Exception e) {
                  e.printStackTrace();
                }
              break;
          }
        } else {
          mApp.showToast(context, "Undo not available.");
        }
      }
    });
    mainSkipButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (mApp.isNetworkAvailable(context))
          try {
            saveDataInDislikeProfileParse();

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
              slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
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
        //  MainActivity.this.finish();
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

  private void savePinnedProfileParse() {
    ParseObject dislikeParseObject = new ParseObject("PinnedProfile");
    dislikeParseObject.put("pinnedProfileId", profileList.get(0));
    dislikeParseObject.put("profileId", profileObject);
    dislikeParseObject.saveInBackground();
  }

  private void saveDataInDislikeProfileParse() {
    ParseObject dislikeParseObject = new ParseObject("DislikeProfile");
    dislikeParseObject.put("dislikeProfileId", profileList.get(0));
    dislikeParseObject.put("profileId", profileObject);
    dislikeParseObject.saveInBackground();
  }

  private void deletePinnedProfileRowFromParse() {
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
  }

  private void deleteLikedProfileRowFromParse() {
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
  }

  private void deleteDislikeProfileRowFromParse() {
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
//    try {
//      blurringView.setBlurredView(backgroundPhoto);
//
//    } catch (Exception e) {
//      RaygunClient.Send(new Throwable(e.getMessage() + " blur_imageview_exception"));
//    }
    rippleBackground.startRippleAnimation();
    slidingPanel.setEnabled(false);
    slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    setupClickListeners();
    if (mApp.isNetworkAvailable(context))
      getParseData();
  }

  public void getParseData() {
    String profileId = Prefs.getProfileId(context);
    profileObject = ParseObject.createWithoutData("Profile", profileId);
    setNavigationMenu();
    getMatchesFromFunction();
  }

  public void setNavigationMenu() {
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

    getUserProfile(profileName, profilePicture);
    setSlidingMenu(navigationMenu);
  }

  private void setSlidingMenu(View navigationMenu) {
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

  private void getUserProfile(final TypefaceTextView profileName, final CircleImageView profilePicture) {
    ParseQuery<ParseObject> query = new ParseQuery<>("Profile");
    query.getInBackground(Prefs.getProfileId(context), new GetCallback<ParseObject>() {
      @Override
      public void done(ParseObject parseObject, ParseException e) {
        try {
          if (e == null) {
            profileName.setText(parseObject.getString("name"));
            ParseFile file = parseObject.getParseFile("profilePic");
            setProfilePictures(file);

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

      private void setProfilePictures(ParseFile file) {
        RequestCreator placeholder = Picasso.with(context)
            .load(file.getUrl())
            .error(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_square))
            .placeholder(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_square));

        placeholder.into(profilePicture);
        placeholder.into(loadingProfile);
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

  private void setProfileDetails() {
    isLoading = true;
    rippleBackground.setVisibility(View.VISIBLE);
    labelLoading.setText("Loading Profile...");
    slidingPanel.setEnabled(false);
    slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    final ParseObject parseProfileObject = profileList.get(0);
    try {
      if (parseProfileObject.containsKey("profilePic") && parseProfileObject.getParseFile("profilePic") != null) {
        Picasso mPicasso = Picasso.with(context);

        RequestCreator profilePic = mPicasso.load(parseProfileObject.getParseFile("profilePic").getUrl()).noFade()
            .error(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_portrait))
            .placeholder(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_portrait));
        profilePic.into(frontPhoto);

        ControllerListener listener = new BaseControllerListener<ImageInfo>(){
          @Override
          public void onFinalImageSet(    String id,    ImageInfo imageInfo,    Animatable animatable){
          }
          @Override
          public void onRelease(String id) {
            super.onRelease(id);

          }
        };


        GenericDraweeHierarchy hierarchy = backgroundPhoto.getHierarchy();
        hierarchy.setFadeDuration(1);
        hierarchy.setPlaceholderImage(R.drawable.com_facebook_profile_picture_blank_portrait);

        String photoUrl = parseProfileObject.getParseFile("profilePic").getUrl();
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(photoUrl))
            .setResizeOptions(new ResizeOptions(512, 334))
            .setPostprocessor(new BlurPostprocessor(context, 15))
            .build();

        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
            .setControllerListener(listener)
            .setOldController(backgroundPhoto.getController())
            .setImageRequest(request)
            .build();


        backgroundPhoto.setController(controller);
        rippleBackground.setVisibility(View.GONE);
      } else {
        Picasso.with(context)
            .load(Uri.EMPTY)
            .placeholder(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_square))
            .error(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_square))
            .into(frontPhoto);
      }

      if (parseProfileObject.containsKey("name") && parseProfileObject.getString("name") != null) {
        String name = parseProfileObject.getString("name");
        Log.e("Profile Name", "" + name);
        frontProfileName.setText(name);
        slideName.setText(name);
      }
      if (parseProfileObject.containsKey("age") && parseProfileObject.getInt("age") != 0) {
        int age = parseProfileObject.getInt("age");
        frontHeight.setText("" + age);
        slideHeight.setText("" + age);
      }
      if (parseProfileObject.containsKey("height") && parseProfileObject.getInt("height") != 0) {
        int[] bases = getResources().getIntArray(R.array.heightCM);
        String[] values = getResources().getStringArray(R.array.height);
        Arrays.sort(bases);
        int index = Arrays.binarySearch(bases, parseProfileObject.getInt("height"));
        frontHeight.append(", " + values[index]);
        slideHeight.append(", " + values[index]);
      }
      if (parseProfileObject.containsKey("religionId") && parseProfileObject.getParseObject("religionId") != null) {
        String religion = parseProfileObject.getParseObject("religionId").getString("name");
        frontReligion.setText(religion);
        slideReligion.setText(religion);
      }
      if (parseProfileObject.containsKey("casteId") && parseProfileObject.getParseObject("casteId") != null) {
        String caste = parseProfileObject.getParseObject("casteId").getString("name");
        slideReligion.append(", " + caste);
        frontReligion.append(", " + caste);
      }
      if (parseProfileObject.containsKey("designation") && parseProfileObject.getString("designation") != null) {
        String desig = parseProfileObject.getString("designation");
        frontDesignation.setText(desig);
        slideDesignation.setText(desig);
        designation.setText(desig);
      }
      if (parseProfileObject.containsKey("industryId") && parseProfileObject.getParseObject("industryId") != null) {
        String indust = parseProfileObject.getParseObject("industryId").getString("name");
        industry.setText(indust);
      }
      if (parseProfileObject.containsKey("currentLocation") && parseProfileObject.getParseObject("currentLocation") != null) {
        String city = parseProfileObject.getParseObject("currentLocation").getString("name");
        String state = parseProfileObject.getParseObject("currentLocation").getParseObject("Parent").getString("name");
        String country = parseProfileObject.getParseObject("currentLocation").getParseObject("Parent").getParseObject("Parent").getString("name");
        currentLocation.setText(city);
        currentLocation.append(", " + state);
        currentLocation.append(", " + country);
      }
      if (parseProfileObject.containsKey("weight") && parseProfileObject.getInt("weight") != 0) {
        weight.setText(parseProfileObject.getInt("weight") + " KG");
      }
      if (parseProfileObject.containsKey("package") && parseProfileObject.getLong("package") != 0) {
//        salary.setText("Rs. " + mApp.numberToWords(parseProfileObject.getLong("package")));
        salary.setText("Rs. " + NumberFormat.getNumberInstance(new Locale("en", "in")).format(parseProfileObject.getLong("package")));
      }

      if (parseProfileObject.containsKey("education1") && parseProfileObject.getParseObject("education1") != null) {
        education.setText(parseProfileObject.getParseObject("education1").getParseObject("degreeId").getString("name"));
      }
      if (parseProfileObject.containsKey("education2") && parseProfileObject.getParseObject("education2") != null) {
        education.append("\n" + parseProfileObject.getParseObject("education2").getParseObject("degreeId").getString("name"));
      }

      if (parseProfileObject.containsKey("education3") && parseProfileObject.getParseObject("education3") != null) {
        education.append("\n" + parseProfileObject.getParseObject("education3").getParseObject("degreeId").getString("name"));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    setTraits();
    getAllPhotos(parseProfileObject);
  }

  public Bitmap fastblur(Bitmap sentBitmap, float scale, int radius) {

    int width = Math.round(sentBitmap.getWidth() * scale);
    int height = Math.round(sentBitmap.getHeight() * scale);
    sentBitmap = Bitmap.createScaledBitmap(sentBitmap, width, height, false);

    Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

    if (radius < 1) {
      return (null);
    }

    int w = bitmap.getWidth();
    int h = bitmap.getHeight();

    int[] pix = new int[w * h];
    Log.e("pix", w + " " + h + " " + pix.length);
    bitmap.getPixels(pix, 0, w, 0, 0, w, h);

    int wm = w - 1;
    int hm = h - 1;
    int wh = w * h;
    int div = radius + radius + 1;

    int r[] = new int[wh];
    int g[] = new int[wh];
    int b[] = new int[wh];
    int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
    int vmin[] = new int[Math.max(w, h)];

    int divsum = (div + 1) >> 1;
    divsum *= divsum;
    int dv[] = new int[256 * divsum];
    for (i = 0; i < 256 * divsum; i++) {
      dv[i] = (i / divsum);
    }

    yw = yi = 0;

    int[][] stack = new int[div][3];
    int stackpointer;
    int stackstart;
    int[] sir;
    int rbs;
    int r1 = radius + 1;
    int routsum, goutsum, boutsum;
    int rinsum, ginsum, binsum;

    for (y = 0; y < h; y++) {
      rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
      for (i = -radius; i <= radius; i++) {
        p = pix[yi + Math.min(wm, Math.max(i, 0))];
        sir = stack[i + radius];
        sir[0] = (p & 0xff0000) >> 16;
        sir[1] = (p & 0x00ff00) >> 8;
        sir[2] = (p & 0x0000ff);
        rbs = r1 - Math.abs(i);
        rsum += sir[0] * rbs;
        gsum += sir[1] * rbs;
        bsum += sir[2] * rbs;
        if (i > 0) {
          rinsum += sir[0];
          ginsum += sir[1];
          binsum += sir[2];
        } else {
          routsum += sir[0];
          goutsum += sir[1];
          boutsum += sir[2];
        }
      }
      stackpointer = radius;

      for (x = 0; x < w; x++) {

        r[yi] = dv[rsum];
        g[yi] = dv[gsum];
        b[yi] = dv[bsum];

        rsum -= routsum;
        gsum -= goutsum;
        bsum -= boutsum;

        stackstart = stackpointer - radius + div;
        sir = stack[stackstart % div];

        routsum -= sir[0];
        goutsum -= sir[1];
        boutsum -= sir[2];

        if (y == 0) {
          vmin[x] = Math.min(x + radius + 1, wm);
        }
        p = pix[yw + vmin[x]];

        sir[0] = (p & 0xff0000) >> 16;
        sir[1] = (p & 0x00ff00) >> 8;
        sir[2] = (p & 0x0000ff);

        rinsum += sir[0];
        ginsum += sir[1];
        binsum += sir[2];

        rsum += rinsum;
        gsum += ginsum;
        bsum += binsum;

        stackpointer = (stackpointer + 1) % div;
        sir = stack[(stackpointer) % div];

        routsum += sir[0];
        goutsum += sir[1];
        boutsum += sir[2];

        rinsum -= sir[0];
        ginsum -= sir[1];
        binsum -= sir[2];

        yi++;
      }
      yw += w;
    }
    for (x = 0; x < w; x++) {
      rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
      yp = -radius * w;
      for (i = -radius; i <= radius; i++) {
        yi = Math.max(0, yp) + x;

        sir = stack[i + radius];

        sir[0] = r[yi];
        sir[1] = g[yi];
        sir[2] = b[yi];

        rbs = r1 - Math.abs(i);

        rsum += r[yi] * rbs;
        gsum += g[yi] * rbs;
        bsum += b[yi] * rbs;

        if (i > 0) {
          rinsum += sir[0];
          ginsum += sir[1];
          binsum += sir[2];
        } else {
          routsum += sir[0];
          goutsum += sir[1];
          boutsum += sir[2];
        }

        if (i < hm) {
          yp += w;
        }
      }
      yi = x;
      stackpointer = radius;
      for (y = 0; y < h; y++) {
        // Preserve alpha channel: ( 0xff000000 & pix[yi] )
        pix[yi] = ( 0xff000000 & pix[yi] ) | ( dv[rsum] << 16 ) | ( dv[gsum] << 8 ) | dv[bsum];

        rsum -= routsum;
        gsum -= goutsum;
        bsum -= boutsum;

        stackstart = stackpointer - radius + div;
        sir = stack[stackstart % div];

        routsum -= sir[0];
        goutsum -= sir[1];
        boutsum -= sir[2];

        if (x == 0) {
          vmin[y] = Math.min(y + r1, hm) * w;
        }
        p = x + vmin[y];

        sir[0] = r[p];
        sir[1] = g[p];
        sir[2] = b[p];

        rinsum += sir[0];
        ginsum += sir[1];
        binsum += sir[2];

        rsum += rinsum;
        gsum += ginsum;
        bsum += binsum;

        stackpointer = (stackpointer + 1) % div;
        sir = stack[stackpointer];

        routsum += sir[0];
        goutsum += sir[1];
        boutsum += sir[2];

        rinsum -= sir[0];
        ginsum -= sir[1];
        binsum -= sir[2];

        yi += w;
      }
    }

    Log.e("pix", w + " " + h + " " + pix.length);
    bitmap.setPixels(pix, 0, w, 0, 0, w, h);

    return (bitmap);
  }

  private void getAllPhotos(final ParseObject parseProfileObject) {
    try {
      userProfileImages.clear();
      userImagesAdapter = new UserImagesAdapter(context, MainActivity.this, userProfileImages);
      profileImages.setAdapter(userImagesAdapter);
    } catch (Exception e) {
      e.printStackTrace();
    }
    ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Photo");
    parseQuery.whereEqualTo("profileId", parseProfileObject);
    parseQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
    parseQuery.findInBackground(new FindCallback<ParseObject>() {
      @Override
      public void done(List<ParseObject> list, ParseException e) {
        if (e == null) {
          if (list != null && list.size() > 0) {
            userProfileImages.clear();
            for (ParseObject model : list) {
              try {
                ImageModel imageModel = new ImageModel();
                ParseFile file = model.getParseFile("file");
                imageModel.setLink(file.getUrl());
                imageModel.setIsPrimary(false);
                imageModel.setParseObject(model.getObjectId());
                userProfileImages.add(imageModel);
              } catch (Exception e1) {
                e1.printStackTrace();
              }
            }
            userImagesAdapter = new UserImagesAdapter(context, MainActivity.this, userProfileImages);
            profileImages.setAdapter(userImagesAdapter);

          } else {
            //This has to be handled proper this happens when there is no entry in the photo
            //table for this profile.
            Log.e("mainActivity", parseProfileObject.getObjectId());
          }
        } else {
          //this also has to be handled as per condition till then wait...
          e.printStackTrace();
          Log.e("mainActivity", "handle the el prt for excp");

        }
      }
    });
    slidingPanel.setEnabled(true);
    slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
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
