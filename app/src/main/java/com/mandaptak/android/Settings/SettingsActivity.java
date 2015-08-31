package com.mandaptak.android.Settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mandaptak.android.Adapter.PermissionsAdapter;
import com.mandaptak.android.Main.MainActivity;
import com.mandaptak.android.Models.PermissionModel;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.HashMap;

import me.iwf.photopicker.utils.Prefs;

public class SettingsActivity extends AppCompatActivity {
    LinearLayout resetButton;
    Common mApp;
    Context context;
    ListView permissionList;
    View permissionFooter;
    PermissionsAdapter permissionsAdapter;
    ArrayList<PermissionModel> permissionModels;
    TextView morePermission;

    void init() {
        context = this;
        mApp = (Common) getApplicationContext();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        permissionList = (ListView) findViewById(R.id.permissions_list);
        permissionFooter = View.inflate(context, R.layout.permission_list_footer, null);
        resetButton = (LinearLayout) permissionFooter.findViewById(R.id.reset_profiles_button);
        morePermission = (TextView) permissionFooter.findViewById(R.id.more_permission);
        permissionList.addFooterView(permissionFooter);
        permissionList.setFooterDividersEnabled(true);
        permissionModels = new ArrayList<>();
        permissionsAdapter = new PermissionsAdapter(context, permissionModels);
        permissionList.setAdapter(permissionsAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
        morePermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mApp.isNetworkAvailable(context)) {

                }
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mApp.isNetworkAvailable(context)) {
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("oid", Prefs.getProfileId(context));
                    ParseCloud.callFunctionInBackground("resetProfiles", params, new FunctionCallback<Object>() {
                        @Override
                        public void done(Object o, ParseException e) {
                            if (e == null) {
                                onBackPressed();
                            } else {
                                e.printStackTrace();
                                mApp.showToast(context, "Error while resetting profiles");
                            }
                        }
                    });
                } else {
                    mApp.showToast(context, "Internet connection required");
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SettingsActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_CLEAR_TOP));
        this.finish();
    }
}
