package com.mandaptak.android.Profile.Religion.Listener;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.mandaptak.android.Profile.Religion.ReligionController;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;

import static android.text.TextUtils.isEmpty;

public class ReligionOnClickListener implements View.OnClickListener {

    private Context context;
    private ReligionController religionController;

    public ReligionOnClickListener(Context context, ReligionController religionController) {
        this.context = context;
        this.religionController = religionController;
    }

    @Override
    public void onClick(View v) {
        final View searchDialog = View.inflate(context, R.layout.location_search_dialog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setView(searchDialog);

        TextView title = (TextView) searchDialog.findViewById(R.id.title);
        title.setText("Select Religion");

        View cancelButton = searchDialog.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        final ListView listView = (ListView) searchDialog.findViewById(R.id.list);
        listView.setVisibility(View.VISIBLE);

        Common mApp = (Common) context.getApplicationContext();
        if (mApp.isNetworkAvailable(context)) {
            religionController.fetchReligionList(null, listView, alertDialog);
        }

        EditText searchBar = (EditText) searchDialog.findViewById(R.id.search);
        searchBar.addTextChangedListener(textWatcher(alertDialog, listView));
        alertDialog.show();
    }

    private TextWatcher textWatcher(final AlertDialog alertDialog, final ListView listView) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(final Editable editable) {
                religionController.fetchReligionList(isEmpty(editable) ? null : editable.toString(), listView, alertDialog);
            }
        };
    }

}
