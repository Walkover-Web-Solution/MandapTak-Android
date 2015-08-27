package com.mandaptak.android.FullProfile;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mandaptak.android.Models.ParseNameModel;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


public class QualificationInfo extends android.support.v4.app.Fragment {
    private TextView industry, workAfterMarriage, degreeView, currentIncome, company, designation;
    private Context context;
    private View rootView;
    private ParseObject newIndustry;
    private int newWorkAfterMarriage = 0;
    private long newCurrentIncome = 0;
    private String newDesignation, newCompany;
    private ParseNameModel newEducationDetail1, newEducationDetail2, newEducationDetail3;
    private Common mApp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.qualification_info, container, false);
        context = getActivity();
        mApp = (Common) context.getApplicationContext();
        init();

        getParseData();
        return rootView;
    }

    private void getParseData() {
        mApp.show_PDialog(context, "Loading..");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Profile");
        query.getInBackground(ParseUser.getCurrentUser().getParseObject("profileId").getObjectId(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    try {
                        newWorkAfterMarriage = parseObject.getInt("workAfterMarriage");
                        newCurrentIncome = parseObject.getLong("package");
                        newDesignation = parseObject.getString("designation");
                        newCompany = parseObject.getString("placeOfWork");
                        newIndustry = parseObject.getParseObject("industryId");
                        ParseObject tmpEdu1 = parseObject.getParseObject("education1");
                        ParseObject tmpEdu2 = parseObject.getParseObject("education2");
                        ParseObject tmpEdu3 = parseObject.getParseObject("education3");
                        StringBuilder stringBuilder = new StringBuilder();
                        if (tmpEdu1 != null) {
                            newEducationDetail1 = new ParseNameModel(tmpEdu1.fetchIfNeeded().getString("name"), tmpEdu1);
                            stringBuilder.append(newEducationDetail1.getParseObject().fetchIfNeeded().getParseObject("degreeId").fetchIfNeeded().getString("name") + " (" + newEducationDetail1.getName() + ") ");
                        }
                        if (tmpEdu2 != null) {
                            newEducationDetail2 = new ParseNameModel(tmpEdu2.fetchIfNeeded().getString("name"), tmpEdu2);
                            stringBuilder.append(newEducationDetail2.getParseObject().fetchIfNeeded().getParseObject("degreeId").fetchIfNeeded().getString("name") + " (" + newEducationDetail2.getName() + ") ");
                        }
                        if (tmpEdu3 != null) {
                            newEducationDetail3 = new ParseNameModel(tmpEdu3.fetchIfNeeded().getString("name"), tmpEdu3);
                            stringBuilder.append(newEducationDetail3.getParseObject().fetchIfNeeded().getParseObject("degreeId").fetchIfNeeded().getString("name") + " (" + newEducationDetail3.getName() + ")");
                        }
                        degreeView.setText(stringBuilder.toString());
                        if (newCurrentIncome != 0)
                            currentIncome.setText(String.valueOf(newCurrentIncome));
                        if (newIndustry != null)
                            industry.setText(newIndustry.fetchIfNeeded().getString("name"));
                        if (newCompany != null)
                            company.setText(newCompany);
                        if (newDesignation != null)
                            designation.setText(newDesignation);
                        switch (newWorkAfterMarriage) {
                            case 0:
                                workAfterMarriage.setText("No");
                                break;
                            case 1:
                                workAfterMarriage.setText("Yes");
                                break;
                            case 2:
                                workAfterMarriage.setText("Maybe");
                                break;
                        }
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    e.printStackTrace();
                }
                mApp.dialog.dismiss();
            }
        });
    }

    void init() {
        currentIncome = (TextView) rootView.findViewById(R.id.current_income);
        industry = (TextView) rootView.findViewById(R.id.industry);
        company = (TextView) rootView.findViewById(R.id.company);
        designation = (TextView) rootView.findViewById(R.id.designation);
        workAfterMarriage = (TextView) rootView.findViewById(R.id.work_after_marriage);
        degreeView = (TextView) rootView.findViewById(R.id.degree);
    }


}

