package com.mandaptak.android.Main;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
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

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
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

import org.lucasr.twowayview.widget.TwoWayView;

import java.io.File;
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
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


public class MainActivity extends AppCompatActivity {
  public final static int REQUEST_CODE = 11;
  private Common mApp;
  private SlidingMenu menu;
  private RelativeLayout slidingLayout;
  private LinearLayout bottomLayout;
  private SlidingUpPanelLayout slidingPanel;
  private ImageView pinButton;
  private SimpleDraweeView backgroundPhoto, frontProfile, loadingProfile;
  public Context context;
  private ArrayList<ImageModel> userProfileImages = new ArrayList<>();
  private TwoWayView profileImages;
  private UserImagesAdapter userImagesAdapter;
  private ArrayList<ParseObject> profileList = new ArrayList<>();
  private TextView frontProfileName, frontHeight, frontDesignation, frontReligion, frontTraits;

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
  private static final String SHOWCASE_ID = "sequence showcase";

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
    frontProfileName = (TextView) findViewById(R.id.front_name);
    loadingProfile = (SimpleDraweeView) findViewById(R.id.loading_profile);
    frontProfile = (SimpleDraweeView) findViewById(R.id.front_pic);
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
    final SimpleDraweeView profilePicture = (SimpleDraweeView) navigationMenu.findViewById(R.id.profile_image);

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

  private void getUserProfile(final TypefaceTextView profileName, final SimpleDraweeView profilePicture) {
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
        String photoUrl = file.getUrl();
        final Uri uri;
        if (photoUrl.startsWith("http")) {
          uri = Uri.parse(photoUrl);
        } else {
          uri = Uri.fromFile(new File(photoUrl));
        }
        profilePicture.setImageURI(uri);
        loadingProfile.setImageURI(uri);
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
      try {
        if (parseProfileObject.containsKey("profilePic") && parseProfileObject.getParseFile("profilePic") != null) {
          ControllerListener listener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
              //MaterialShowcaseView.resetSingleUse(context, SHOWCASE_ID);
              presentShowcaseSequence(); // one second delay
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
          final Uri uri;
          if (photoUrl.startsWith("http")) {
            uri = Uri.parse(photoUrl);
          } else {
            uri = Uri.fromFile(new File(photoUrl));
          }
          frontProfile.setImageURI(uri);
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
          frontProfile.setImageURI(Uri.EMPTY);
//          Picasso.with(context)
//              .load(Uri.EMPTY)
//              .placeholder(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_square))
//              .error(ContextCompat.getDrawable(context, R.drawable.com_facebook_profile_picture_blank_square))
//              .into(frontPhoto);
        }
      } catch (OutOfMemoryError error) {
        error.printStackTrace();
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

  private void presentShowcaseSequence() {
    ShowcaseConfig config = new ShowcaseConfig();
    config.setDelay(500); // half second between each showcase view
    MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);
    sequence.setConfig(config);
    sequence.addSequenceItem(pinButton, "Click to Pin Profile", "GOT IT");

    sequence.addSequenceItem(
        new MaterialShowcaseView.Builder(this)
            .setTarget(mainUndoButton)
            .setDismissText("GOT IT")
            .setContentText("Click to Undo Profile")
            .build()
    );
    sequence.addSequenceItem(
        new MaterialShowcaseView.Builder(this)
            .setTarget(mainLikeButton)
            .setDismissText("GOT IT")
            .setContentText("Click to Like Profile")
            .build()
    );
    sequence.addSequenceItem(
        new MaterialShowcaseView.Builder(this)
            .setTarget(mainSkipButton)
            .setDismissText("GOT IT")
            .setContentText("Click to Dislike Profile")
            .build()
    );
    sequence.start();
  }
}
