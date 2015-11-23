package com.mandaptak.android.EditProfile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.mandaptak.android.Adapter.LayoutAdapter;
import com.mandaptak.android.Main.MainActivity;
import com.mandaptak.android.Preferences.UserPreferences;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;
import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lucasr.twowayview.TwoWayLayoutManager;
import org.lucasr.twowayview.widget.DividerItemDecoration;
import org.lucasr.twowayview.widget.TwoWayView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import me.iwf.photopicker.PhotoPagerActivity;
import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.entity.Profile;
import me.iwf.photopicker.utils.ImageModel;
import me.iwf.photopicker.utils.PhotoPickerIntent;
import me.iwf.photopicker.utils.Prefs;

public class FinalEditProfileFragment extends Fragment {
  public final static int REQUEST_CODE = 11;
  private static final int FILE_SELECT_CODE = 0;
  public int primaryIndex = -1;
  View rootView;
  String newBiodataFileName;
  TwoWayView imageList;
  TextView importPhotosButton, uploadBioData, selectPrimary;
  Context context;
  LayoutAdapter photoAdapter;
  Common mApp;
  ArrayList<ImageModel> parsePhotos = new ArrayList<>();
  LinearLayout budgetMainLayout;
  EditText minBudget, maxBudget;
  long newMinBudget = -1, newMaxBudget = -1;
  CallbackManager callbackManager;
  ArrayList<String> fbPhotos = new ArrayList<>();
  JSONArray jsonArray;
  private LinearLayout saveProfile;
  private Long fbUserId;
  private String albumId;
  private Boolean isStarted = false;
  private Boolean isVisible = false;
  private Boolean isFirstStart = false;
  private ImageView deleteBioData;
  private Boolean updatedBudget = false;

  public FinalEditProfileFragment() {
    // Required empty public constructor
  }

  public void previewPhoto(Intent intent) {
    saveBudget();
    startActivityForResult(intent, REQUEST_CODE);
  }

  void init() {
    context = getActivity();
    mApp = (Common) context.getApplicationContext();
    budgetMainLayout = (LinearLayout) rootView.findViewById(R.id.budget_layout);
    imageList = (TwoWayView) rootView.findViewById(R.id.list);
    importPhotosButton = (TextView) rootView.findViewById(R.id.import_photos);
    uploadBioData = (TextView) rootView.findViewById(R.id.upload_biodata);
    selectPrimary = (TextView) rootView.findViewById(R.id.select_primary);
    minBudget = (EditText) rootView.findViewById(R.id.budget_from);
    maxBudget = (EditText) rootView.findViewById(R.id.budget_to);
    deleteBioData = (ImageView) rootView.findViewById(R.id.delete_biodata);
    saveProfile = (LinearLayout) rootView.findViewById(R.id.save_profile);
    imageList.setOrientation(TwoWayLayoutManager.Orientation.HORIZONTAL);
    imageList.setHasFixedSize(true);
    imageList.setLongClickable(true);
    imageList.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(context, R.drawable.divider)));
    photoAdapter = new LayoutAdapter(context, FinalEditProfileFragment.this, parsePhotos);
    imageList.setAdapter(photoAdapter);
    FacebookSdk.sdkInitialize(context.getApplicationContext());
    callbackManager = CallbackManager.Factory.create();

    // If the access token is available already assign it.
    LoginManager.getInstance().registerCallback(callbackManager,
        new FacebookCallback<LoginResult>() {
          @Override
          public void onSuccess(LoginResult loginResult) {

            GraphRequest.newMeRequest(
                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                  @Override
                  public void onCompleted(JSONObject me, GraphResponse response) {
                    if (response.getError() != null) {
                      // handle error
                    } else {
                      try {
                        fbUserId = me.getLong("id");
                        getUserPhotos();
                      } catch (JSONException e) {
                        e.printStackTrace();
                      }

                      String email = me.optString("email");
                      String id = me.optString("id");
                      // send email and id to your web server
                    }
                  }
                }).executeAsync();

          }

          @Override
          public void onCancel() {
            // App code
          }

          @Override
          public void onError(FacebookException exception) {
            // App code
          }
        });
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.fragment_final_edit_profile, container, false);
    init();
    if (getActivity().getIntent() != null) {
      if (getActivity().getIntent().hasExtra("firstStart")) {
        isFirstStart = getActivity().getIntent().getBooleanExtra("firstStart", false);
      }
    }
    selectPrimary.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (parsePhotos != null && parsePhotos.size() > 0) {
          Intent intent = new Intent(context, PhotoPagerActivity.class);
          intent.putExtra(PhotoPagerActivity.EXTRA_PHOTOS, parsePhotos);
          previewPhoto(intent);
        }
      }
    });
    importPhotosButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        final View importImageDialog = View.inflate(context, R.layout.image_picker_dialog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setView(importImageDialog);
        RelativeLayout takeCameraButton = (RelativeLayout) importImageDialog.findViewById(R.id.take_camera_button);
        RelativeLayout importGalleryButton = (RelativeLayout) importImageDialog.findViewById(R.id.gallery_import_button);
        RelativeLayout importFbButton = (RelativeLayout) importImageDialog.findViewById(R.id.fb_import_button);
        takeCameraButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            alertDialog.dismiss();
            PhotoPickerIntent intent = new PhotoPickerIntent(context);
            intent.setPhotoCount(8 - parsePhotos.size());
            intent.setShowCamera(true);
            saveBudget();
            startActivityForResult(intent, REQUEST_CODE);
          }
        });
        importGalleryButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            alertDialog.dismiss();
            PhotoPickerIntent intent = new PhotoPickerIntent(context);
            intent.setPhotoCount(8 - parsePhotos.size());
            intent.setShowCamera(false);
            saveBudget();
            startActivityForResult(intent, REQUEST_CODE);
          }
        });
        importFbButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            alertDialog.dismiss();
            //  makeMeRequest();
            getFacebookLogin();

          }
        });
        alertDialog.show();
      }
    });

    uploadBioData.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        showFileChooser();
      }
    });
    deleteBioData.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        deleteBioDataFile();
      }
    });
    saveProfile.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (isAdded()) {
          mApp.show_PDialog(context, "Saving Profile..");
          ParseQuery<ParseObject> query = new ParseQuery<>("Profile");
          query.getInBackground(Prefs.getProfileId(context), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
              if (e == null) {
                saveInfo();
                validateProfile(parseObject);
              } else {
                e.printStackTrace();
                mApp.dialog.dismiss();
                mApp.showToast(context, e.getMessage());
              }
            }
          });
        }
      }
    });
    return rootView;
  }

  private void saveBudget() {
    try {
      if (!minBudget.getText().toString().equals(""))
        newMinBudget = Long.valueOf(minBudget.getText().toString());
      if (!maxBudget.getText().toString().equals(""))
        newMaxBudget = Long.valueOf(maxBudget.getText().toString());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void getFacebookLogin() {
    LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email", "user_photos"));
  }

  private void deleteBioDataFile() {
    mApp.show_PDialog(context, "Removing...");
    ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Profile");
    parseQuery.getInBackground(Prefs.getProfileId(context), new GetCallback<ParseObject>() {
      @Override
      public void done(final ParseObject parseObject, ParseException e) {
        if (e == null) {
          parseObject.put("bioData", JSONObject.NULL);
          parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
              if (e == null) {
                uploadBioData.setTextColor(getResources().getColor(R.color.red_500));
                uploadBioData.setText("+ UPLOAD BIODATA");
                mApp.dialog.dismiss();
                mApp.showToast(context, "Biodata Removed");
                deleteBioData.setVisibility(View.GONE);
              } else {
                e.printStackTrace();
                mApp.showToast(context, e.getMessage());
                mApp.dialog.dismiss();
              }

            }
          });
        }
      }
    });
  }

  private void validateProfile(final ParseObject parseObject) {
    if (Prefs.getProfile(context) != null) {
      final Profile profile = Prefs.getProfile(context);
      if (checkFieldsTab1(profile)) {
        if (checkFieldsTab2(profile)) {
          if (checkFieldsTab3(profile)) {
            if (!parseObject.containsKey("profilePic") || parseObject.get("profilePic").equals(JSONObject.NULL)) {
              mApp.dialog.dismiss();
              mApp.showToast(context, "Please select a primary profile photo");
            } else if (newMinBudget > newMaxBudget) {
              mApp.dialog.dismiss();
              mApp.showToast(context, "Minimum budget should be less then maximum budget");
            } else {
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
                    profile.getTimeOfBirth().setTimeZone(TimeZone.getTimeZone("UTC"));
                    profile.getDateOfBirth().setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date tob = profile.getTimeOfBirth().getTime();
                    Date dob = profile.getDateOfBirth().getTime();
                    parseObject.put("name", profile.getName());
                    parseObject.put("gender", profile.getGender());
                    parseObject.put("dob", dob);
                    parseObject.put("tob", tob);
                    parseObject.put("placeOfBirth", ParseObject.createWithoutData(profile.getPlaceOfBirth().getClassName(), profile.getPlaceOfBirth().getParseObjectId()));
                    parseObject.put("currentLocation", ParseObject.createWithoutData(profile.getCurrentLocation().getClassName(), profile.getCurrentLocation().getParseObjectId()));
                    parseObject.put("height", profile.getHeight());
                    parseObject.put("weight", profile.getWeight());
                    parseObject.put("religionId", ParseObject.createWithoutData(profile.getReligion().getClassName(), profile.getReligion().getParseObjectId()));
                    parseObject.put("casteId", ParseObject.createWithoutData(profile.getCaste().getClassName(), profile.getCaste().getParseObjectId()));
                    if (profile.getGotra() != null) {
                      parseObject.put("gotraId", ParseObject.createWithoutData(profile.getGotra().getClassName(), profile.getGotra().getParseObjectId()));
                    }
                    parseObject.put("manglik", profile.getManglik());
                    parseObject.put("industryId", ParseObject.createWithoutData(profile.getIndustry().getClassName(), profile.getIndustry().getParseObjectId()));
                    parseObject.put("designation", profile.getDesignation());
                    if (profile.getCompany() != null)
                      parseObject.put("placeOfWork", profile.getCompany());
                    parseObject.put("package", profile.getIncome());

                    parseObject.put("education1", ParseObject.createWithoutData(profile.getEducation1().getClassName(), profile.getEducation1().getParseObjectId()));
                    if (profile.getEducation2() != null)
                      parseObject.put("education2", ParseObject.createWithoutData(profile.getEducation2().getClassName(), profile.getEducation2().getParseObjectId()));
                    if (profile.getEducation3() != null)
                      parseObject.put("education3", ParseObject.createWithoutData(profile.getEducation3().getClassName(), profile.getEducation3().getParseObjectId()));
                    parseObject.put("workAfterMarriage", profile.getWorkAfterMarriage());

                    parseObject.put("isComplete", true);
                    parseObject.saveInBackground(new SaveCallback() {
                      @Override
                      public void done(ParseException e) {
                        mApp.dialog.dismiss();
                        if (e == null) {
                          try {
                            if (isFirstStart) {
                              startActivity(new Intent(getActivity(), UserPreferences.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_CLEAR_TOP));
                              getActivity().finish();
                              mApp.showToast(context, "Please Set your Preferences");
                            } else {
                              startActivity(new Intent(getActivity(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_CLEAR_TOP));
                              getActivity().finish();
                              mApp.showToast(context, "Profile updated");
                            }
                          } catch (Exception e2) {
                            mApp.showToast(context, "Error while updating profile");
                            e2.printStackTrace();
                          }
                        } else {
                          mApp.showToast(context, "Error while updating profile");
                          e.printStackTrace();
                        }
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
            ((EditProfileActivity) getActivity()).mViewPager.setCurrentItem(2);
          }
        } else {
          mApp.dialog.dismiss();
          mApp.showToast(context, "Please fill all details");
          ((EditProfileActivity) getActivity()).mViewPager.setCurrentItem(1);
        }
      } else {
        mApp.dialog.dismiss();
        mApp.showToast(context, "Please fill all details");
        ((EditProfileActivity) getActivity()).mViewPager.setCurrentItem(0);
      }
    }
  }

  public void setPrimaryIndex(int index) {
    primaryIndex = index;
  }

  private boolean checkFieldsTab1(Profile profile) {
    if (profile.getName() == null || profile.getName().equals("")) {
      showSnakeBar("Please enter Name");
      return false;
    } else if (profile.getGender() == null) {
      showSnakeBar("Please Select Gender");
      return false;
    } else if (profile.getDateOfBirth() == null) {
      showSnakeBar("Please Select DateOfBirth");
      return false;
    } else if (profile.getTimeOfBirth() == null) {
      showSnakeBar("Please Select TimeOfBirth");
      return false;
    } else if (profile.getCurrentLocation() == null) {
      showSnakeBar("Please Select Current Location");
      return false;
    } else if (profile.getPlaceOfBirth() == null) {
      showSnakeBar("Please Select Place Of Birth");
      return false;
    } else {
      return true;
    }
  }

  private boolean checkFieldsTab2(Profile profile) {
    if (profile.getHeight() == 0) {
      showSnakeBar("Please Select Height");
      return false;
    } else if (profile.getWeight() == 0) {
      showSnakeBar("Please Select Weight");
      return false;
    } else if (profile.getReligion() == null) {
      showSnakeBar("Please Select Religion");
      return false;
    } else if (profile.getCaste() == null) {
      showSnakeBar("Please Select Caste");
      return false;
    } else if (profile.getManglik() == -1) {
      showSnakeBar("Please Select Manglik");
      return false;
    } else {
      return true;
    }
  }

  private boolean checkFieldsTab3(Profile profile) {
    if (profile.getWorkAfterMarriage() == -1) {
      showSnakeBar("Please Select Work After Marriage");
      return false;
    } else if (profile.getIncome() == -1) {
      showSnakeBar("Please Select Income");
      return false;
    } else if (profile.getDesignation() == null) {
      showSnakeBar("Please Select Designation");
      return false;
    } else if (profile.getCompany() == null) {
      showSnakeBar("Please Select Company");
      return false;
    } else if (profile.getIndustry() == null) {
      showSnakeBar("Please Select Industry");
      return false;
    } else if (profile.getEducation1() == null) {
      showSnakeBar("Please Select Education");
      return false;
    } else {
      return true;
    }
  }

  private void showFileChooser() {
    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    intent.setType("*/*");
    intent.addCategory(Intent.CATEGORY_OPENABLE);
    try {
      saveBudget();
      startActivityForResult(
          Intent.createChooser(intent, "Select a File to Upload"),
          FILE_SELECT_CODE);
    } catch (android.content.ActivityNotFoundException ex) {
      Toast.makeText(context, "Please install a File Manager.",
          Toast.LENGTH_SHORT).show();
    }
  }

  public void getParseData() {
    try {
      mApp.show_PDialog(context, "Loading..");
      ParseQuery<ParseObject> query = new ParseQuery<>("Profile");
      query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
      query.getInBackground(Prefs.getProfileId(context), new GetCallback<ParseObject>() {
        @Override
        public void done(ParseObject profileObject, ParseException e) {
          if (isAdded()) {
            isStarted = false;
            if (e == null) {
              if (profileObject.containsKey("bioData") && profileObject.getParseFile("bioData") != null)
                newBiodataFileName = profileObject.getParseFile("bioData").getName();
              if (!updatedBudget)
                if (profileObject.getBoolean("isBudgetVisible")) {
                  budgetMainLayout.setVisibility(View.VISIBLE);
                  newMinBudget = profileObject.getLong("minMarriageBudget");
                  newMaxBudget = profileObject.getLong("maxMarriageBudget");
                  if (newMinBudget != -1)
                    minBudget.setText("" + newMinBudget);
                  if (newMaxBudget != -1)
                    maxBudget.setText("" + newMaxBudget);
                } else {
                  budgetMainLayout.setVisibility(View.GONE);
                }

              if (newBiodataFileName != null) {
                uploadBioData.setTextColor(getResources().getColor(R.color.black_light));
                uploadBioData.setText(newBiodataFileName);
                deleteBioData.setVisibility(View.VISIBLE);
              }
              ParseQuery<ParseObject> queryParseQuery = new ParseQuery<>("Photo");
              queryParseQuery.whereEqualTo("profileId", profileObject);
              queryParseQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                  if (list != null) {
                    parsePhotos.clear();
                    for (ParseObject item : list) {
                      parsePhotos.add(new ImageModel(item.getParseFile("file").getUrl(), item.getBoolean("isPrimary"), item.getObjectId()));
                    }
                    photoAdapter.notifyDataSetChanged();
                  }
                }
              });
            } else {
              mApp.showToast(context, e.getMessage());
              e.printStackTrace();
            }
          }
          mApp.dialog.dismiss();
        }
      });
    } catch (Exception ignored) {
    }
  }

  @Override
  public void onStart() {
    super.onStart();
    isStarted = true;
    if (isVisible) {
      getParseData();
    }
  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    isVisible = isVisibleToUser;
    if (isVisible && isStarted) {
      getParseData();
    } else if (!isVisible) {
      saveInfo();
    }
  }


  public void saveInfo() {

    try {
      String minimumBudget = minBudget.getText().toString();
      String maximumBudget = maxBudget.getText().toString();
      if (!minimumBudget.equals("") && !maximumBudget.equals("")) {
        try {
          newMinBudget = Long.parseLong(minimumBudget.replaceAll("[^0-9]+", ""));
          newMaxBudget = Long.parseLong(maximumBudget.replaceAll("[^0-9]+", ""));
          if (newMinBudget > newMaxBudget)
            return;
          ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Profile");
          parseQuery.getInBackground(Prefs.getProfileId(context), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
              if (newMinBudget != -1)
                parseObject.put("minMarriageBudget", newMinBudget);
              if (newMaxBudget != -1)
                parseObject.put("maxMarriageBudget", newMaxBudget);
              parseObject.saveInBackground();
            }
          });
        } catch (Exception ignored) {
        }
      }
      Log.e("Save Screen", "4");
    } catch (Exception ignored) {
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (isAdded()) {
      try {
        if (newMaxBudget != -1) {
          updatedBudget = true;
          maxBudget.setText(String.valueOf(newMaxBudget));
        }
        if (newMinBudget != -1) {
          minBudget.setText(String.valueOf(newMinBudget));
          updatedBudget = true;
        }
      } catch (Exception e) {
      }
      callbackManager.onActivityResult(requestCode, resultCode, data);
      if (resultCode == Activity.RESULT_OK) {
        switch (requestCode) {
          case FILE_SELECT_CODE:
            mApp.show_PDialog(context, "Uploading..");
            Uri uri = data.getData();
            String path = mApp.getPath(context, uri);
            if (path != null) {
              final File file = new File(path);
              ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Profile");
              parseQuery.getInBackground(Prefs.getProfileId(context), new GetCallback<ParseObject>() {
                @Override
                public void done(final ParseObject parseObject, ParseException e) {
                  try {
                    final ParseFile parseFile = new ParseFile(file.getName(), read(file));
                    parseFile.saveInBackground(new SaveCallback() {
                      public void done(ParseException e) {
                        parseObject.put("bioData", parseFile);
                        parseObject.saveInBackground(new SaveCallback() {
                          @Override
                          public void done(ParseException e) {
                            if (e == null) {
                              uploadBioData.setTextColor(getResources().getColor(R.color.black_light));
                              uploadBioData.setText(file.getName());
                              mApp.dialog.dismiss();
                              deleteBioData.setVisibility(View.VISIBLE);
                              mApp.showToast(context, "Biodata Uploaded");
                            } else {
                              e.printStackTrace();
                              mApp.showToast(context, e.getMessage());
                              mApp.dialog.dismiss();
                            }

                          }
                        });
                      }
                    }, new ProgressCallback() {
                      public void done(Integer percentDone) {
                        // Update your progress spinner here. percentDone will be between 0 and 100.
                        if (percentDone == 100) {
                          mApp.updateDialogProgress(percentDone, "Finishing..");
                        } else {
                          mApp.updateDialogProgress(percentDone, "Uploading: " + percentDone + "%");
                        }
                      }
                    });
                  } catch (IOException e1) {
                    e1.printStackTrace();
                    mApp.dialog.dismiss();
                    mApp.showToast(context, "Error while uploading file");
                  }
                }
              });
            } else {
              mApp.dialog.dismiss();
              mApp.showToast(context, "Error: File not found");
            }
            break;

          case REQUEST_CODE:
            if (data != null) {
              if (data.hasExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS)) {
                if (data.getBooleanExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS, false)) {

                  ParseQuery<ParseObject> profileParseQuery = new ParseQuery<>("Profile");
                  profileParseQuery.getInBackground(Prefs.getProfileId(context), new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                      if (e == null) {
                        ParseQuery<ParseObject> queryParseQuery = new ParseQuery<>("Photo");
                        queryParseQuery.whereEqualTo("profileId", object);
                        queryParseQuery.findInBackground(new FindCallback<ParseObject>() {
                          @Override
                          public void done(List<ParseObject> list, ParseException e) {
                            if (e == null) {
                              parsePhotos.clear();
                              for (int i = 0; i < list.size(); i++) {
                                try {
                                  ImageModel imageModel = new ImageModel();
                                  imageModel.setIsPrimary(list.get(i).getBoolean("isPrimary"));
                                  imageModel.setLink(list.get(i).fetchIfNeeded().getParseFile("file").getUrl());
                                  imageModel.setParseObject(list.get(i).getObjectId());
                                  parsePhotos.add(imageModel);
                                } catch (ParseException e1) {
                                  e1.printStackTrace();
                                }
                              }
                              photoAdapter.notifyDataSetChanged();
                            } else {
                              mApp.showToast(context, e.getMessage());
                            }
                          }
                        });
                      } else {
                        mApp.showToast(context, e.getMessage());
                      }
                    }
                  });
                }
              }
            }
            break;
          case Crop.REQUEST_CROP:
            if (data != null) {
              if (data.hasExtra(MediaStore.EXTRA_OUTPUT)) {
                uri = data.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
                final File file = new File(uri.getPath());
                if (file.exists()) {
                  mApp.show_PDialog(context, "Uploading Profile Photo");
                  ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Profile");
                  parseQuery.getInBackground(Prefs.getProfileId(context), new GetCallback<ParseObject>() {
                    @Override
                    public void done(final ParseObject profileObject, ParseException e) {
                      try {
                        final ParseFile parseFile = new ParseFile(file.getName(), PhotoPickerActivity.read(file));
                        parseFile.saveInBackground(new SaveCallback() {
                          public void done(ParseException e) {
                            if (e == null) {
                              profileObject.put("profilePic", parseFile);
                              profileObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                  if (e == null) {
                                    ParseQuery<ParseObject> queryParseQuery = new ParseQuery<>("Photo");
                                    queryParseQuery.whereEqualTo("profileId", profileObject);
                                    queryParseQuery.findInBackground(new FindCallback<ParseObject>() {
                                      @Override
                                      public void done(List<ParseObject> list, ParseException e) {
                                        if (e == null) {
                                          parsePhotos.clear();
                                          for (int i = 0; i < list.size(); i++) {
                                            try {
                                              ImageModel model = new ImageModel();
                                              model.setParseObject(list.get(i).getObjectId());
                                              model.setLink(list.get(i).getParseFile("file").getUrl());
                                              ParseObject parseObject = list.get(i);
                                              if (i == primaryIndex) {
                                                model.setIsPrimary(true);
                                                parseObject.put("isPrimary", true);
                                                parseObject.saveInBackground();
                                              } else {
                                                model.setIsPrimary(false);
                                                parseObject.put("isPrimary", false);
                                                parseObject.saveInBackground();
                                              }
                                              parsePhotos.add(model);
                                            } catch (Exception e1) {
                                              e1.printStackTrace();
                                            }
                                          }
                                          photoAdapter.notifyDataSetChanged();
                                        } else {
                                          mApp.showToast(context, e.getMessage());
                                        }
                                        mApp.dialog.dismiss();
                                      }
                                    });
                                    mApp.showToast(context, "Profile photo saved");

                                  } else {
                                    mApp.showToast(context, e.getMessage());
                                    e.printStackTrace();
                                  }
                                }
                              });
                            } else {
                              mApp.showToast(context, e.getMessage());
                            }
                            mApp.dialog.dismiss();
                          }
                        });
                      } catch (Exception e1) {
                        mApp.dialog.dismiss();
                        e1.printStackTrace();
                      }
                    }
                  });
                }
              }
            }
            break;
        }
      }

    }
  }

  public byte[] read(File file) throws IOException {
    ByteArrayOutputStream ous = null;
    InputStream ios = null;
    try {
      byte[] buffer = new byte[4096];
      ous = new ByteArrayOutputStream();
      ios = new FileInputStream(file);
      int read;
      while ((read = ios.read(buffer)) != -1) {
        ous.write(buffer, 0, read);
      }
    } finally {
      try {
        if (ous != null)
          ous.close();
      } catch (IOException ignored) {
      }

      try {
        if (ios != null)
          ios.close();
      } catch (IOException ignored) {
      }
    }
    return ous.toByteArray();
  }

  public void getUserPhotos() {
    new GraphRequest(
        AccessToken.getCurrentAccessToken(),
        "/" + fbUserId + "/albums",
        null,
        HttpMethod.GET,
        new GraphRequest.Callback() {
          public void onCompleted(GraphResponse response) {
            try {
              if (response != null) {
                try {
                  JSONObject jsonObject = response.getJSONObject();
                  JSONArray albumsArr = jsonObject.getJSONArray("data");
                  for (int i = 0; i < albumsArr.length(); i++) {
                    jsonObject = albumsArr.getJSONObject(i);
                    if (jsonObject.getString("name").equalsIgnoreCase("Profile Pictures")) {
                      albumId = jsonObject.getString("id");
                    }
                  }
                  getImageUrls();
                } catch (JSONException e) {
                  e.printStackTrace();
                }
              }
            } catch (Exception e) {
              mApp.dialog.dismiss();
              mApp.showToast(context, "Error connecting to facebook");
              e.printStackTrace();
            }
          }
        }
    ).executeAsync();
  }

  private void getImageUrls() {
    mApp.show_PDialog(context, "Please wait");
    Bundle params = new Bundle();
    params.putString("url", "{image-url}");
    new GraphRequest(
        AccessToken.getCurrentAccessToken(),
        "/" + albumId + "/photos",
        params,
        HttpMethod.GET,
        new GraphRequest.Callback() {
          public void onCompleted(GraphResponse response) {
            try {
              jsonArray = response.getJSONObject().getJSONArray("data");
              StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
              StrictMode.setThreadPolicy(policy);
              for (int i = 0; i < jsonArray.length(); i++) {
                Bundle params = new Bundle();
                params.putBoolean("redirect", false);
                try {
                  new GraphRequest(
                      AccessToken.getCurrentAccessToken(),
                      "/" + jsonArray.getJSONObject(i).getString("id") + "/picture",
                      params,
                      HttpMethod.GET,
                      new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                          try {
                            fbPhotos.add(response.getJSONObject().getJSONObject("data").getString("url"));
                          } catch (Exception e) {
                            e.printStackTrace();
                          }
                        }
                      }
                  ).executeAndWait();
                } catch (JSONException e) {
                  e.printStackTrace();
                }
                if (i == (jsonArray.length() - 1)) {
                  mApp.dialog.dismiss();
                  Intent intent = new Intent(context, FacebookPhotos.class);
                  intent.putStringArrayListExtra("pics-fb", fbPhotos);
                  saveBudget();
                  startActivityForResult(intent, REQUEST_CODE);
                }
              }
            } catch (Exception e) {
              mApp.dialog.dismiss();
              mApp.showToast(context, "Insufficient Permissions");
            }
          }
        }
    ).executeAsync();
  }

  private void showSnakeBar(String msg) {
    Snackbar snackbar = Snackbar
        .make(rootView, msg, Snackbar.LENGTH_LONG)
        .setAction("OK", new View.OnClickListener() {
          @Override
          public void onClick(View v) {

          }
        });
    snackbar.setActionTextColor(context.getResources().getColor(R.color.white));
    View snackbarView = snackbar.getView();
    snackbarView.setBackgroundColor(context.getResources().getColor(R.color.red_400));//change Snackbar's background color;
    TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
    textView.setTextColor(Color.WHITE);
    snackbar.show();
  }
}
