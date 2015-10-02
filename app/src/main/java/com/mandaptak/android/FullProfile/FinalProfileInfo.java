package com.mandaptak.android.FullProfile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.io.FileOutputStream;

public class FinalProfileInfo extends Fragment {
    View rootView;
    TextView downLoadBiodata;
    Context context;
    Common mApp;
    TextView minBudget, maxBudget;
    long newMinBudget = 0, newMaxBudget = 0;
    String newBiodataFileName;
    LinearLayout budgetMainLayout;
    ParseFile bioData;
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
        downLoadBiodata = (TextView) rootView.findViewById(R.id.download_biodata);
        minBudget = (TextView) rootView.findViewById(R.id.budget_from);
        maxBudget = (TextView) rootView.findViewById(R.id.budget_to);
        budgetMainLayout = (LinearLayout) rootView.findViewById(R.id.budget_layout);
        downLoadBiodata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bioData != null)
                    SaveFile(bioData);
            }
        });
    }

    private void SaveFile(ParseFile finalBitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/MandapTak");
        if (!myDir.exists())
            myDir.mkdirs();
        String fname = finalBitmap.getName();
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(finalBitmap.getData());
            out.flush();
            out.close();
            mApp.showToast(context, "File downloaded in phone");
            Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW);
            String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
            String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            myIntent.setDataAndType(Uri.fromFile(file), mimetype);
            startActivity(myIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.final_profile_info, container, false);
        init();
        if (mApp.isNetworkAvailable(context))
            getParseData();
        else mApp.showToast(context, "Not available");
        return rootView;
    }

    private void getParseData() {
        ParseQuery<ParseObject> query = new ParseQuery<>("Profile");
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.getInBackground(parseObjectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    if (parseObject.containsKey("bioData") && parseObject.getParseFile("bioData") != null) {
                        bioData = parseObject.getParseFile("bioData");
                        newBiodataFileName = parseObject.getParseFile("bioData").getName();
                    } else {
                        downLoadBiodata.setVisibility(View.GONE);
                    }
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
                        downLoadBiodata.setText("↓ DOWNLOAD BIODATA (" + newBiodataFileName + ")");
                    /*    downLoadBiodata.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });*/
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });

    }

}
