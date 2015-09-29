package com.mandaptak.android.Profile.Religion;

import android.app.AlertDialog;
import android.widget.ListView;
import me.iwf.photopicker.entity.ParseNameModel;

import java.util.ArrayList;

public interface ReligionController {
	void fetchReligionList(String query, final ListView listView, final AlertDialog alertDialog);

	void prepareReligionListView(int i, ArrayList<ParseNameModel> models, AlertDialog alertDialog);
}
