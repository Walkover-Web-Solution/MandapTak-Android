package com.mandaptak.android.EditProfile;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mandaptak.android.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class QualificationEditProfileFragment extends Fragment {
    LinearLayout mainWorkLayout, mainEducationLayout;
    TextView educationMoreButton;
    EditText currentIncome;
    NiceSpinner workAfterMarriage;
    View rootView;
    LayoutInflater layoutInflater;
    View educationLayoutChild1, educationLayoutChild2, educationLayoutChild3;
    private int newWorkAfterMarriage = 2;
    private TextView eduChildDegree1, eduChildDegree2, eduChildDegree3;
    private TextView eduChildDegreeBranch1, eduChildDegreeBranch2, eduChildDegreeBranch3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_qualification_edit_profile, container, false);

        init();
        List<String> dataset = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.wam_array)));
        workAfterMarriage.attachDataSource(dataset);

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
        return rootView;
    }

    void init() {
        mainWorkLayout = (LinearLayout) rootView.findViewById(R.id.work_layout);
        mainEducationLayout = (LinearLayout) rootView.findViewById(R.id.education_layout);
        educationMoreButton = (TextView) rootView.findViewById(R.id.education_more_button);
        currentIncome = (EditText) rootView.findViewById(R.id.current_income);
        workAfterMarriage = (NiceSpinner) rootView.findViewById(R.id.work_after_marriage);
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
                if (currentIncome.getText() != null)
                    parseObject.put("package", Integer.parseInt(currentIncome.getText().toString()));
                parseObject.put("workAfterMarriage", newWorkAfterMarriage);
                parseObject.saveInBackground();
            }
        });
    }
}
