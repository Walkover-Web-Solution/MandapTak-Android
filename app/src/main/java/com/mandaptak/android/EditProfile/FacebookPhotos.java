package com.mandaptak.android.EditProfile;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.GridView;

import com.mandaptak.android.Adapter.GridAdapter;
import com.mandaptak.android.R;

import java.util.ArrayList;


public class FacebookPhotos extends Activity {
    ArrayList<String> images = new ArrayList<>();
    GridView mGrid;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fb_grid);
        context = FacebookPhotos.this;
        mGrid = (GridView) findViewById(R.id.myGrid);
        if (getIntent() != null) {
            if (getIntent().hasExtra("pics-fb")) {
                images = getIntent().getStringArrayListExtra("pics-fb");
                mGrid.setAdapter(new GridAdapter(images,context));
            }
        }
    }
}