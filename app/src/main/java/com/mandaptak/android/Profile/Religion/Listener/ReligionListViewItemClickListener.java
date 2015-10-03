package com.mandaptak.android.Profile.Religion.Listener;

import android.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.mandaptak.android.Profile.Religion.ReligionController;

import java.util.ArrayList;

import me.iwf.photopicker.entity.ParseNameModel;

public class ReligionListViewItemClickListener implements OnItemClickListener {
    private final ArrayList<ParseNameModel> models;
    private final AlertDialog alertDialog;
    private ReligionController religionController;

    public ReligionListViewItemClickListener(ArrayList<ParseNameModel> models, AlertDialog alertDialog, ReligionController religionController) {
        this.models = models;
        this.alertDialog = alertDialog;
        this.religionController = religionController;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        religionController.prepareReligionListView(position, models, alertDialog);
    }
}
