package com.mandaptak.android.FullProfile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;
import com.mandaptak.android.Main.MainActivity;
import com.mandaptak.android.Matches.MatchedProfileActivity;
import com.mandaptak.android.Models.MatchesModel;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.java.com.mindscapehq.android.raygun4android.RaygunClient;
import me.iwf.photopicker.utils.ImageModel;

public class FullProfileActivity extends AppCompatActivity {

    ImagePagerAdapter imagePagerAdapter;
    ViewPager mImagesPager;
    CirclePageIndicator circlePageIndicator;
    ArrayList<ImageModel> parsePhotos = new ArrayList<>();
    ImageButton backButton, likeButton;
    String parseObjectId;
    Context context;
    ParseObject userProfileObject;
    Common mApp;
    BasicProfileInfo basicProfileInfo = null;
    DetailsProfileInfo detailsProfileInfo = null;
    QualificationInfo qualificationInfo = null;
    FinalProfileInfo finalProfileInfo = null;
    private Boolean isLiked = false;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(FullProfileActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_CLEAR_TOP));
        FullProfileActivity.this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_profile_activity);
        mApp = (Common) getApplicationContext();
        context = this;
        if (getIntent() != null) {
            parseObjectId = getIntent().getStringExtra("parseObjectId");
        } else {
            this.finish();
        }
        try {
            getSupportActionBar().hide();
        } catch (Exception ignored) {
        }
        if (mApp.isNetworkAvailable(context)) {
            getImages();
            ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Profile");
            parseQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
            parseQuery.getInBackground(me.iwf.photopicker.utils.Prefs.getProfileId(context), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    userProfileObject = parseObject;
                }
            });
        }
        mImagesPager = (ViewPager) findViewById(R.id.pager_images);
        backButton = (ImageButton) findViewById(R.id.home);
        likeButton = (ImageButton) findViewById(R.id.like_profile);
        circlePageIndicator = (CirclePageIndicator) findViewById(R.id.circles);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likeProfile();
            }
        });
        CompoundButton.OnCheckedChangeListener btnNavBarOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switch (buttonView.getId()) {
                        case R.id.basic:
                            loadBasicInfo();
                            break;
                        case R.id.detail:
                            loadDetailInfo();
                            break;
                        case R.id.qualification:
                            loadQualificationsInfo();
                            break;
                        case R.id.final_pic:
                            loadFinalProfileInfo();
                            break;
                    }
                }
            }
        };
        RadioButton radioButton;
        radioButton = (RadioButton) findViewById(R.id.basic);
        radioButton.setOnCheckedChangeListener(btnNavBarOnCheckedChangeListener);
        radioButton = (RadioButton) findViewById(R.id.detail);
        radioButton.setOnCheckedChangeListener(btnNavBarOnCheckedChangeListener);
        radioButton = (RadioButton) findViewById(R.id.qualification);
        radioButton.setOnCheckedChangeListener(btnNavBarOnCheckedChangeListener);
        radioButton = (RadioButton) findViewById(R.id.final_pic);
        radioButton.setOnCheckedChangeListener(btnNavBarOnCheckedChangeListener);
        loadBasicInfo();
        RadioGroup menuGroup = (RadioGroup) findViewById(R.id.radio_parent);
        menuGroup.check(R.id.basic);
    }

    void loadBasicInfo() {
        if (basicProfileInfo == null) {
            basicProfileInfo = new BasicProfileInfo();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_place, basicProfileInfo).commit();
    }

    void loadDetailInfo() {
        if (detailsProfileInfo == null) {
            detailsProfileInfo = new DetailsProfileInfo();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_place, detailsProfileInfo).commit();
    }

    void loadQualificationsInfo() {
        if (qualificationInfo == null) {
            qualificationInfo = new QualificationInfo();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_place, qualificationInfo).commit();
    }

    void loadFinalProfileInfo() {
        if (finalProfileInfo == null) {
            finalProfileInfo = new FinalProfileInfo();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_place, finalProfileInfo).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    private void getImages() {
        if (parseObjectId != null) {
            ParseQuery<ParseObject> queryParseQuery = new ParseQuery<>("Photo");
            queryParseQuery.whereEqualTo("profileId", ParseObject.createWithoutData("Profile", parseObjectId));
            queryParseQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
            queryParseQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (list != null) {
                        for (ParseObject item : list) {
                            parsePhotos.add(new ImageModel(item.getParseFile("file").getUrl(), item.getBoolean("isPrimary"), item.getObjectId()));
                        }
                        imagePagerAdapter = new ImagePagerAdapter(parsePhotos);
                        mImagesPager.setAdapter(imagePagerAdapter);
                        circlePageIndicator.setViewPager(mImagesPager);
                    }
                }
            });
        } else {
            this.finish();
        }
    }

    class ImagePagerAdapter extends PagerAdapter {

        LayoutInflater mLayoutInflater;
        ArrayList<ImageModel> list;

        public ImagePagerAdapter(ArrayList<ImageModel> list) {
            mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.picture_item_view, container, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            final String path = list.get(position).getLink();
            final Uri uri;
            if (path.startsWith("http")) {
                uri = Uri.parse(path);
            } else {
                uri = Uri.fromFile(new File(path));
            }
            Glide.with(FullProfileActivity.this)
                    .load(uri)
                    .error(me.iwf.photopicker.R.drawable.ic_broken_image_black_48dp)
                    .into(imageView);
            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    void likeProfile() {
        if (mApp.isNetworkAvailable(context))
            if (!isLiked) {
                isLiked = true;
                likeButton.setImageResource(R.drawable.like);
                if (mApp.isNetworkAvailable(context))
                    try {
                        HashMap<String, Object> params = new HashMap<>();
                        params.put("userProfileId", userProfileObject.getObjectId());
                        params.put("likeProfileId", parseObjectId);
                        params.put("userName", userProfileObject.fetchIfNeeded().getString("name"));

                        ParseCloud.callFunctionInBackground("likeAndFind", params, new FunctionCallback<Object>() {
                            @Override
                            public void done(Object o, ParseException e) {
                                if (e == null) {
                                    if (o != null) {
                                        try {
                                            if (o instanceof ParseObject) {
                                                ParseObject parseObject = ParseObject.createWithoutData("Profile", parseObjectId);
                                                String religion = parseObject.fetchIfNeeded().getParseObject("religionId").fetchIfNeeded().getString("name");
                                                String caste = parseObject.fetchIfNeeded().getParseObject("casteId").fetchIfNeeded().getString("name");
                                                MatchesModel model = new MatchesModel();
                                                model.setName(parseObject.fetchIfNeeded().getString("name"));
                                                model.setProfileId(parseObject.getObjectId());
                                                model.setReligion(religion + ", " + caste);
                                                model.setWork(parseObject.getString("designation"));
                                                model.setUrl(parseObject.fetchIfNeeded().getParseFile("profilePic").getUrl());
                                                Intent intent = new Intent(context, MatchedProfileActivity.class);
                                                intent.putExtra("profile", model);
                                                startActivity(intent);
                                            } else {
                                                onBackPressed();
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


            } else {
                isLiked = false;
                likeButton.setImageResource(R.drawable.ic_like);
            }
    }
}
