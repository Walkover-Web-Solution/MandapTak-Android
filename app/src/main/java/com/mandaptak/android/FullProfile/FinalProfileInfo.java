package com.mandaptak.android.FullProfile;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class FinalProfileInfo extends Fragment {
    View rootView;
    TextView uploadBiodata;
    Context context;
    Common mApp;
    TextView minBudget, maxBudget;
    long newMinBudget = 0, newMaxBudget = 0;
    String newBiodataFileName;
    LinearLayout budgetMainLayout;
    private String parseObjectId;

    public FinalProfileInfo() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity().getIntent() != null)
            parseObjectId = getActivity().getIntent().getStringExtra("parseObjectId");
        else
            getActivity().finish();
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
        if (mApp.isNetworkAvailable(context))
            getParseData();
        return rootView;
    }

    private void getParseData() {
        ParseQuery<ParseObject> query = new ParseQuery<>("Profile");
        query.getInBackground(parseObjectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    if (parseObject.containsKey("bioData") && parseObject.getParseObject("bioData") != null)
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
                        uploadBiodata.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });

    }

}
