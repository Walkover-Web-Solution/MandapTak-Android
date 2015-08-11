package com.mandaptak.android.EditProfile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mandaptak.android.R;

public class DetailsProfileFragment extends Fragment {
    TextView religion,height;

    public DetailsProfileFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_details_profile, container, false);
        religion = (TextView) rootView.findViewById(R.id.religion);
        height = (TextView) rootView.findViewById(R.id.height);

        religion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder conductor = new AlertDialog.Builder(
                        getActivity());
                conductor.setTitle("Select Religion");

                int resId = getResources().getIdentifier("religion_array",
                        "array", getActivity().getPackageName());
                conductor.setItems(resId,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int index) {
                                int resId1 = getResources().getIdentifier(
                                        "religion_array", "array",
                                        getActivity().getPackageName());

                                religion.setText(getResources()
                                        .getStringArray(resId1)[index]);
                                religion.setTextColor(getActivity().getResources().getColor(R.color.black_dark));
                            }
                        });
                AlertDialog alert = conductor.create();
                alert.show();
            }
        });

        height.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder conductor = new AlertDialog.Builder(
                        getActivity());
                conductor.setTitle("Select Height");

                int resId = getResources().getIdentifier("height",
                        "array", getActivity().getPackageName());
                conductor.setItems(resId,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int index) {
                                int resId1 = getResources().getIdentifier(
                                        "height", "array",
                                        getActivity().getPackageName());

                                height.setText(getResources()
                                        .getStringArray(resId1)[index]);
                                height.setTextColor(getActivity().getResources().getColor(R.color.black_dark));
                            }
                        });
                AlertDialog alert = conductor.create();
                alert.show();
            }
        });
        return rootView;
    }
}
