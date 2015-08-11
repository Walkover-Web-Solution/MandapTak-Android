package com.mandaptak.android.Login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mandaptak.android.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link WelcomeScreen4#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WelcomeScreen4 extends Fragment {

    public WelcomeScreen4() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment WelcomeScreen1.
     */
    public static WelcomeScreen4 newInstance() {
        WelcomeScreen4 fragment = new WelcomeScreen4();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome_screen4, container, false);
    }
}
