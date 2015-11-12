package com.mandaptak.android.FullProfile;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.NumberFormat;
import java.util.Locale;

import me.iwf.photopicker.entity.ParseNameModel;

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
  private String parseObjectId;
  private boolean isVisible = false;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.qualification_info, container, false);
    context = getActivity();
    mApp = (Common) context.getApplicationContext();
    init();

    if (mApp.isNetworkAvailable(context))
      getParseData();
    return rootView;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getActivity().getIntent() != null)
      parseObjectId = getActivity().getIntent().getStringExtra("parseObjectId");
    else
      getActivity().finish();
  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    this.isVisible = isVisibleToUser;

  }

  private void getParseData() {
    Log.e(" qualification profile", parseObjectId);
    if (parseObjectId != null) {
      ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Profile");
      parseQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
      parseQuery.include("education1.degreeId");
      parseQuery.include("education2.degreeId");
      parseQuery.include("education3.degreeId");
      parseQuery.include("industryId");
      parseQuery.getInBackground(parseObjectId, new GetCallback<ParseObject>() {
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
                newEducationDetail1 = new ParseNameModel(tmpEdu1.getString("name"), "Specialization", tmpEdu1.getObjectId());
                stringBuilder.append(tmpEdu1.getParseObject("degreeId").getString("name")).append(" (").append(newEducationDetail1.getName()).append(") ");
              }
              if (tmpEdu2 != null) {
                newEducationDetail2 = new ParseNameModel(tmpEdu2.getString("name"), "Specialization", tmpEdu2.getObjectId());
                stringBuilder.append(tmpEdu2.getParseObject("degreeId").getString("name")).append(" (").append(newEducationDetail2.getName()).append(") ");
              }
              if (tmpEdu3 != null) {
                newEducationDetail3 = new ParseNameModel(tmpEdu3.getString("name"), "Specialization", tmpEdu3.getObjectId());
                stringBuilder.append(tmpEdu3.getParseObject("degreeId").getString("name")).append(" (").append(newEducationDetail3.getName()).append(")");
              }
              degreeView.setText(stringBuilder.toString());
              if (newCurrentIncome != 0)
                currentIncome.setText(NumberFormat.getCurrencyInstance(new Locale("en", "in")).format(newCurrentIncome));
              if (newIndustry != null)
                industry.setText(newIndustry.getString("name"));
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
            } catch (Exception e1) {
              e1.printStackTrace();
            }
          } else {
            e.printStackTrace();
          }
        }
      });
    } else {
      getActivity().finish();
    }
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

