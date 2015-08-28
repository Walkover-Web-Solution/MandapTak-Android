package com.mandaptak.android.FullProfile;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.mandaptak.android.Models.ParseNameModel;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.mandaptak.android.Views.ExtendedEditText;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Arrays;

public class DetailsProfileInfo extends Fragment {

    Common mApp;
    private TextView religion, height, caste, gotra, mangalikStatus;
    private ExtendedEditText weight;
    private Context context;
    private View rootView;
    private int newHeight = 0, newWeight = 0, newMangalik = 0;
    private ParseNameModel newReligion, newCaste, newGotra;

    public DetailsProfileInfo() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        mApp = (Common) context.getApplicationContext();
        rootView = inflater.inflate(R.layout.detail_profile_info, container, false);
        init();
        getParseData();
        return rootView;
    }


    private void init() {
        religion = (TextView) rootView.findViewById(R.id.religion);
        height = (TextView) rootView.findViewById(R.id.height);
        caste = (TextView) rootView.findViewById(R.id.caste);
        gotra = (TextView) rootView.findViewById(R.id.gotra);
        weight = (ExtendedEditText) rootView.findViewById(R.id.weight);
        mangalikStatus = (TextView) rootView.findViewById(R.id.mangalik);
    }

    private void getParseData() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Profile");
        query.getInBackground(ParseUser.getCurrentUser().getParseObject("profileId").getObjectId(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    try {
                        newHeight = parseObject.getInt("height");
                        newWeight = parseObject.getInt("weight");
                        ParseObject tmpCaste, tmpReligion, tmpGotra;
                        tmpReligion = parseObject.fetchIfNeeded().getParseObject("religionId");
                        tmpCaste = parseObject.fetchIfNeeded().getParseObject("casteId");
                        tmpGotra = parseObject.fetchIfNeeded().getParseObject("gotraId");
                        newMangalik = parseObject.getInt("mangalik");
                        switch (newMangalik) {
                            case 0:
                                mangalikStatus.setText("No");
                                break;
                            case 1:
                                mangalikStatus.setText("Yes");
                                break;
                            case 2:
                                mangalikStatus.setText("Aanshik");
                                break;
                        }

                        if (newHeight != 0) {
                            if (isAdded()) {
                                int[] bases = getResources().getIntArray(R.array.heightCM);
                                String[] values = getResources().getStringArray(R.array.height);
                                Arrays.sort(bases);
                                int index = Arrays.binarySearch(bases, newHeight);
                                height.setText(values[index]);
                                height.setTextColor(context.getResources().getColor(R.color.black_dark));
                            }
                        }
                        if (newWeight != 0) {
                            weight.setText(String.valueOf(newWeight));
                            weight.setTextColor(context.getResources().getColor(R.color.black_dark));
                        }
                        if (tmpReligion != null) {
                            newReligion = new ParseNameModel(tmpReligion.fetchIfNeeded().getString("name"), tmpReligion);
                            religion.setText(newReligion.getName());
                            religion.setTextColor(context.getResources().getColor(R.color.black_dark));
                        }
                        if (tmpCaste != null) {
                            newCaste = new ParseNameModel(tmpCaste.fetchIfNeeded().getString("name"), tmpCaste);
                            caste.setText(newCaste.getName());
                            caste.setTextColor(context.getResources().getColor(R.color.black_dark));
                        }
                        if (tmpGotra != null) {
                            newGotra = new ParseNameModel(tmpGotra.fetchIfNeeded().getString("name"), tmpGotra);
                            gotra.setText(newGotra.getName());
                            gotra.setTextColor(context.getResources().getColor(R.color.black_dark));
                        }
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

}