package com.mandaptak.android.EditProfile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.mandaptak.android.Adapter.LayoutAdapter;
import com.mandaptak.android.Main.MainActivity;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
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
import java.util.List;

import me.iwf.photopicker.PhotoPickerActivity;
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
    TextView importPhotosButton, uploadBiodata;
    Context context;
    LayoutAdapter photoAdapter;
    Common mApp;
    ArrayList<ImageModel> parsePhotos = new ArrayList<>();
    LinearLayout budgetMainLayout;
    EditText minBudget, maxBudget;
    long newMinBudget = 0, newMaxBudget = 0;
    private LinearLayout saveProfile;
    private Long fbUserId;
    private String albumId;
    private Boolean isStarted = false;
    private Boolean isVisible = false;

    public FinalEditProfileFragment() {
        // Required empty public constructor
    }

    public void previewPhoto(Intent intent) {
        startActivityForResult(intent, REQUEST_CODE);
    }

    void init() {
        context = getActivity();
        mApp = (Common) context.getApplicationContext();
        budgetMainLayout = (LinearLayout) rootView.findViewById(R.id.budget_layout);
        imageList = (TwoWayView) rootView.findViewById(R.id.list);
        importPhotosButton = (TextView) rootView.findViewById(R.id.import_photos);
        uploadBiodata = (TextView) rootView.findViewById(R.id.upload_biodata);
        minBudget = (EditText) rootView.findViewById(R.id.budget_from);
        maxBudget = (EditText) rootView.findViewById(R.id.budget_to);
        saveProfile = (LinearLayout) rootView.findViewById(R.id.save_profile);
        imageList.setOrientation(TwoWayLayoutManager.Orientation.HORIZONTAL);
        imageList.setHasFixedSize(true);
        imageList.setLongClickable(true);
        imageList.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(context, R.drawable.divider)));
        photoAdapter = new LayoutAdapter(context, FinalEditProfileFragment.this, parsePhotos);
        imageList.setAdapter(photoAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_final_edit_profile, container, false);
        init();

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
                        startActivityForResult(intent, REQUEST_CODE);
                    }
                });
                importFbButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        //  makeMeRequest();
                    }
                });
                alertDialog.show();
            }
        });

        uploadBiodata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
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

    private void validateProfile(final ParseObject parseObject) {
        if (checkFieldsTab1(parseObject)) {
            if (checkFieldsTab2(parseObject)) {
                if (checkFieldsTab3(parseObject)) {
                    if (!parseObject.containsKey("profilePic") || parseObject.get("profilePic").equals(JSONObject.NULL)) {
                        mApp.dialog.dismiss();
                        mApp.showToast(context, "Please select a primary profile photo");
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
                                    mApp.dialog.dismiss();
                                    parseObject.put("isComplete", true);
                                    parseObject.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                try {
                                                    startActivity(new Intent(getActivity(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                    getActivity().finish();
                                                    mApp.showToast(context, "Profile updated");
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

    public void setPrimaryIndex(int index) {
        primaryIndex = index;
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

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
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
            query.getInBackground(Prefs.getProfileId(context), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject profileObject, ParseException e) {
                    if (e == null) {
                        if (profileObject.containsKey("bioData") && profileObject.getParseFile("bioData") != null)
                            newBiodataFileName = profileObject.getParseFile("bioData").getName();
                        if (profileObject.getBoolean("isBudgetVisible")) {
                            budgetMainLayout.setVisibility(View.VISIBLE);
                            newMinBudget = profileObject.getLong("minMarriageBudget");
                            newMaxBudget = profileObject.getLong("maxMarriageBudget");
                            if (newMinBudget != 0)
                                minBudget.setText("" + newMinBudget);
                            if (newMaxBudget != 0)
                                maxBudget.setText("" + newMaxBudget);
                        } else {
                            budgetMainLayout.setVisibility(View.GONE);
                        }

                        if (newBiodataFileName != null) {
                            uploadBiodata.setTextColor(getResources().getColor(R.color.black_light));
                            uploadBiodata.setText(newBiodataFileName);
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
            if (!minBudget.getText().toString().equals("")) {
                try {
                    newMinBudget = Long.parseLong(minBudget.getText().toString().replaceAll("[^0-9]+", ""));
                } catch (Exception ignored) {
                }
            }
            if (!maxBudget.getText().toString().equals("")) {
                try {
                    newMaxBudget = Long.parseLong(maxBudget.getText().toString().replaceAll("[^0-9]+", ""));
                } catch (Exception ignored) {
                }
            }
            ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Profile");
            parseQuery.getInBackground(Prefs.getProfileId(context), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (newMinBudget != 0)
                        parseObject.put("minMarriageBudget", newMinBudget);
                    if (newMaxBudget != 0)
                        parseObject.put("maxMarriageBudget", newMaxBudget);
                    parseObject.saveInBackground();
                }
            });
            Log.e("Save Screen", "4");
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case FILE_SELECT_CODE:
                    Uri uri = data.getData();
                    String path = mApp.getPath(context, uri);
                    if (path != null) {
                        final File file = new File(path);
                        mApp.show_PDialog(context, "Uploading..");
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
                                                        uploadBiodata.setTextColor(getResources().getColor(R.color.black_light));
                                                        uploadBiodata.setText(file.getName());
                                                        mApp.showToast(context, "Biodata Uploaded");
                                                    } else {
                                                        e.printStackTrace();
                                                        mApp.showToast(context, e.getMessage());
                                                    }
                                                    mApp.dialog.dismiss();
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
                                                                                for (int i = 0; i < list.size(); i++) {
                                                                                    ParseObject parseObject = list.get(i);
                                                                                    if (i == primaryIndex) {
                                                                                        parsePhotos.get(i).setIsPrimary(true);
                                                                                        parseObject.put("isPrimary", true);
                                                                                        parseObject.saveInBackground();
                                                                                    } else {
                                                                                        parsePhotos.get(i).setIsPrimary(false);
                                                                                        parseObject.put("isPrimary", false);
                                                                                        parseObject.saveInBackground();
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
                                        } catch (IOException e1) {
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
        makeMeRequest();
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + fbUserId + "/albums",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        if (response != null) {
                            Log.e("album_response", response.toString());
                            try {
                                JSONObject jsonObject = new JSONObject(response.toString());
                                JSONObject graphObject = jsonObject.getJSONObject("graphObject");
                                JSONArray albumsArr = graphObject.getJSONArray("data");
                                for (int i = 0; i < albumsArr.length(); i++) {
                                    jsonObject = albumsArr.getJSONObject(i);
                                    if (jsonObject.getString("type").equalsIgnoreCase("profile")) {
                                        albumId = jsonObject.getString("id");
                                        Log.e("Link", "" + jsonObject.getString("link"));
                                    }
                                }
                                getImageUrls();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        ).executeAsync();
    }

    private void getImageUrls() {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + albumId,
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        if (response != null) {
                            Log.e("", "" + response.toString());
                        }
                    }
                }
        ).executeAsync();

    }

    private void makeMeRequest() {

        List<String> permissions = Arrays.asList("public_profile", "email", "user_photos");
        // NOTE: for extended permissions, like "user_about_me", your app must be reviewed by the Facebook team
        // (https://developers.facebook.com/docs/facebook-login/permissions/)

        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d("facebook", "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Log.d("facebook", "User signed up and logged in through Facebook!");
                } else {
                    Log.d("facebook", "User logged in through Facebook!");
                    mApp.showToast(context, "Login Successful");
                    Log.e("makeMeRequest", "Start");
                    GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                                    if (jsonObject != null) {
                                        try {
                                            fbUserId = jsonObject.getLong("id");
                                            Log.e("fbUserId", "" + fbUserId);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        getUserPhotos();
                                    } else if (graphResponse.getError() != null) {
                                        switch (graphResponse.getError().getCategory()) {
                                            case LOGIN_RECOVERABLE:
                                                Log.d("facebook",
                                                        "Authentication error: " + graphResponse.getError());
                                                break;

                                            case TRANSIENT:
                                                Log.d("facebook",
                                                        "Transient error. Try again. " + graphResponse.getError());
                                                break;

                                            case OTHER:
                                                Log.d("facebook",
                                                        "Some other error: " + graphResponse.getError());
                                                break;
                                        }
                                    }
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,email,gender,name");
                    request.setParameters(parameters);
                    request.executeAsync();
                    Log.e("makeMeRequest", "End");
                    getUserPhotos();
                }
            }
        });
    }

}
