package com.mandaptak.android.Profile.Religion;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.ListView;

import com.mandaptak.android.EditProfile.DataAdapter;
import com.mandaptak.android.Profile.Religion.Listener.ReligionListViewItemClickListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.entity.ParseNameModel;

public class ReligionParseFindCallback implements FindCallback<ParseObject> {

    private static final String RELIGION = "Religion";
    private static final String NAME = "name";

    private ReligionController religionController;
    private final ListView listView;
    private final AlertDialog alertDialog;
    private Context context;

    public ReligionParseFindCallback(ReligionController religionController, ListView listView, AlertDialog alertDialog, Context context) {
        this.religionController = religionController;
        this.listView = listView;
        this.alertDialog = alertDialog;
        this.context = context;
    }

    @Override
    public void done(List<ParseObject> list, ParseException e) {
        ArrayList<ParseNameModel> models = new ArrayList<>();

        if (list != null && list.size() > 0) {
            for (ParseObject model : list) {
                models.add(new ParseNameModel(model.getString(NAME), RELIGION, model.getObjectId()));
            }
            listView.setAdapter(new DataAdapter(context, models));
            listView.setOnItemClickListener(new ReligionListViewItemClickListener(models, alertDialog, religionController));
        }
    }
}
