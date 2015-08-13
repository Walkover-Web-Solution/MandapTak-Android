package com.mandaptak.android.EditProfile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mandaptak.android.R;

public class QualificationEditProfileFragment extends Fragment {
    LinearLayout mainWorkLayout, mainEducationLayout;
    TextView workMoreButton, educationMoreButton;
    EditText currentIncome;
    TextView workAfterMarriage;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_qualification_edit_profile, container, false);
        init();
        workAfterMarriage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return rootView;
    }

    void init() {
        mainWorkLayout = (LinearLayout) rootView.findViewById(R.id.work_layout);
        mainEducationLayout = (LinearLayout) rootView.findViewById(R.id.education_layout);
        workMoreButton = (TextView) rootView.findViewById(R.id.work_more_button);
        educationMoreButton = (TextView) rootView.findViewById(R.id.education_more_button);
        currentIncome = (EditText) rootView.findViewById(R.id.current_income);
        workAfterMarriage = (TextView) rootView.findViewById(R.id.work_after_marriage);
    }
}
