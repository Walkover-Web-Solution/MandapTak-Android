package com.mandaptak.android.Login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mandaptak.android.R;

public class WelcomeScreen2 extends Fragment {

    public WelcomeScreen2() {
        // Required empty public constructor
    }

    public static WelcomeScreen2 newInstance() {
        WelcomeScreen2 fragment = new WelcomeScreen2();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome_screen2, container, false);
    }
}
