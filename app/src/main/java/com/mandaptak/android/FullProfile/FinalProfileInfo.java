package com.mandaptak.android.FullProfile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.mandaptak.android.EditProfile.EditProfileActivity;
import com.mandaptak.android.Login.UserDetailsActivity;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

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
import java.util.List;

import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.ImageModel;
import me.iwf.photopicker.utils.PhotoPickerIntent;

public class FinalProfileInfo extends Fragment {
    View rootView;
    TextView uploadBiodata;
    Context context;
    Common mApp;
    TextView minBudget, maxBudget;
    long newMinBudget = 0, newMaxBudget = 0;
    String newBiodataFileName;
    LinearLayout budgetMainLayout;

    public FinalProfileInfo() {
        // Required empty public constructor
    }


    void init() {
        context = getActivity();
        mApp = (Common) context.getApplicationContext();
        uploadBiodata = (TextView) rootView.findViewById(R.id.download_biodata);
        minBudget = (TextView) rootView.findViewById(R.id.budget_from);
        maxBudget = (TextView) rootView.findViewById(R.id.budget_to);
        budgetMainLayout = (LinearLayout) rootView.findViewById(R.id.budget_layout);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.final_profile_info, container, false);
        init();
        getParseData();
        return rootView;
    }


    private void getParseData() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Profile");
        query.getInBackground(ParseUser.getCurrentUser().getParseObject("profileId").getObjectId(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    newBiodataFileName = parseObject.getParseFile("bioData").getName();

                    if (!parseObject.containsKey("minMarriageBudget")) {
                        budgetMainLayout.setVisibility(View.GONE);
                    } else {
                        budgetMainLayout.setVisibility(View.VISIBLE);
                        newMinBudget = parseObject.getLong("minMarriageBudget");
                        newMaxBudget = parseObject.getLong("maxMarriageBudget");
                        if (newMinBudget != 0)
                            minBudget.setText("\u20B9" + newMinBudget);
                        if (newMaxBudget != 0)
                            maxBudget.setText("\u20B9" + newMaxBudget);
                    }

                    if (newBiodataFileName != null) {
                        uploadBiodata.setText("↓ DOWNLOAD BIODATA (" + newBiodataFileName + ")");
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });

    }


}
