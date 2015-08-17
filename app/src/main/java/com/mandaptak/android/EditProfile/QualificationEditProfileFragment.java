package com.mandaptak.android.EditProfile;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.mandaptak.android.R;
import com.mandaptak.android.Views.ExtendedEditText;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONObject;

public class QualificationEditProfileFragment extends Fragment {
    LinearLayout mainWorkLayout, mainEducationLayout;
    TextView educationMoreButton, industry;
    ExtendedEditText currentIncome;
    EditText company, designation;
    Spinner workAfterMarriage;
    View rootView;
    LayoutInflater layoutInflater;
    View educationLayoutChild1, educationLayoutChild2, educationLayoutChild3;
    private int newWorkAfterMarriage = 0, newCurrentIncome = 0;
    private String newDesignation, newCompany;
    private TextView eduChildDegree1, eduChildDegree2, eduChildDegree3;
    private TextView eduChildDegreeBranch1, eduChildDegreeBranch2, eduChildDegreeBranch3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_qualification_edit_profile, container, false);

        init();

        workAfterMarriage.setAdapter(ArrayAdapter.createFromResource(getActivity(),
                R.array.wam_array, R.layout.location_list_item));
        workAfterMarriage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                newWorkAfterMarriage = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        educationMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mainEducationLayout.getChildCount() == 1) {
                    mainEducationLayout.addView(educationLayoutChild2);
                } else if (mainEducationLayout.getChildCount() == 2) {
                    mainEducationLayout.addView(educationLayoutChild3);
                    educationMoreButton.setVisibility(View.GONE);
                }
            }
        });

        eduChildDegree1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        industry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        currentIncome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    newCurrentIncome = 0;
                    currentIncome.setPrefix("");
                } else {
                    currentIncome.setPrefix("Rs. ");
                    newCurrentIncome = Integer.parseInt(editable.toString());
                }
            }
        });
        company.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                newCompany = editable.toString();
            }
        });
        designation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                newDesignation = editable.toString();
            }
        });
        getParseData();
        return rootView;
    }

    private void getParseData() {
        try {
            ParseObject parseObject = ParseUser.getCurrentUser().fetchIfNeeded().getParseObject("profileId");
            newWorkAfterMarriage = parseObject.getInt("workAfterMarriage");
            newCurrentIncome = parseObject.getInt("package");
            newDesignation = parseObject.getString("designation");
            newCompany = parseObject.getString("placeOfWork");

            if (newCurrentIncome != 0) {
                currentIncome.setText(String.valueOf(newCurrentIncome));
            }
            if (newCompany != null)
                company.setText(newCompany);
            if (newDesignation != null)
                designation.setText(newDesignation);
            workAfterMarriage.setSelection(newWorkAfterMarriage);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    void init() {
        mainWorkLayout = (LinearLayout) rootView.findViewById(R.id.work_layout);
        mainEducationLayout = (LinearLayout) rootView.findViewById(R.id.education_layout);
        educationMoreButton = (TextView) rootView.findViewById(R.id.education_more_button);
        currentIncome = (ExtendedEditText) rootView.findViewById(R.id.current_income);
        industry = (TextView) rootView.findViewById(R.id.industry);
        company = (EditText) rootView.findViewById(R.id.company);
        designation = (EditText) rootView.findViewById(R.id.designation);
        workAfterMarriage = (Spinner) rootView.findViewById(R.id.work_after_marriage);
        layoutInflater = (LayoutInflater)
                getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        educationLayoutChild1 = layoutInflater.inflate(R.layout.item_education_layout, mainWorkLayout, false);
        educationLayoutChild2 = layoutInflater.inflate(R.layout.item_education_layout, mainWorkLayout, false);
        educationLayoutChild3 = layoutInflater.inflate(R.layout.item_education_layout, mainWorkLayout, false);
        mainEducationLayout.addView(educationLayoutChild1);
        eduChildDegree1 = (TextView) educationLayoutChild1.findViewById(R.id.degree);
        eduChildDegree2 = (TextView) educationLayoutChild2.findViewById(R.id.degree);
        eduChildDegree3 = (TextView) educationLayoutChild3.findViewById(R.id.degree);
        eduChildDegreeBranch1 = (TextView) educationLayoutChild1.findViewById(R.id.degree_branch);
        eduChildDegreeBranch2 = (TextView) educationLayoutChild2.findViewById(R.id.degree_branch);
        eduChildDegreeBranch3 = (TextView) educationLayoutChild3.findViewById(R.id.degree_branch);
    }

    @Override
    public void onDetach() {
        saveInfo();
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        saveInfo();
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        saveInfo();
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        saveInfo();
        super.onStop();
    }

    private void saveInfo() {
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Profile");
        parseQuery.getInBackground(ParseUser.getCurrentUser().getParseObject("profileId").getObjectId(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (newCurrentIncome != 0)
                    parseObject.put("package", newCurrentIncome);
                else
                    parseObject.put("package", JSONObject.NULL);
                if (newCompany != null)
                    parseObject.put("placeOfWork", newCompany);
                else
                    parseObject.put("placeOfWork", JSONObject.NULL);
                if (newDesignation != null)
                    parseObject.put("designation", newDesignation);
                else
                    parseObject.put("designation", JSONObject.NULL);
                parseObject.put("workAfterMarriage", newWorkAfterMarriage);
                parseObject.saveInBackground();
            }
        });
    }
}
