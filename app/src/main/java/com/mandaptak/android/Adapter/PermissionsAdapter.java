package com.mandaptak.android.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mandaptak.android.Models.PermissionModel;
import com.mandaptak.android.R;
import com.mandaptak.android.Settings.SettingsActivity;
import com.mandaptak.android.Utils.Common;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.HashMap;

public class PermissionsAdapter extends BaseAdapter {
    SettingsActivity activity;
    ArrayList<PermissionModel> list;
    Common mApp;

    public PermissionsAdapter(SettingsActivity activity, ArrayList<PermissionModel> paramArrayList) {
        this.list = paramArrayList;
        this.activity = activity;
        mApp = (Common) activity.getApplicationContext();
    }

    public int getCount() {

        return this.list.size();
    }

    public Object getItem(int paramInt) {
        return this.list.get(paramInt);
    }

    public long getItemId(int paramInt) {
        return paramInt;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
        ViewHolder viewholder;

        if (paramView == null) {
            viewholder = new ViewHolder();
            paramView = LayoutInflater.from(activity).inflate(R.layout.permission_item, null);
            viewholder.tvNumber = (TextView) paramView.findViewById(R.id.number);
            viewholder.tvRelation = ((TextView) paramView.findViewById(R.id.relation));
            viewholder.tvDate = (TextView) paramView.findViewById(R.id.date);
            viewholder.deleteButton = (ImageView) paramView.findViewById(R.id.delete);
            paramView.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) paramView.getTag();
        }
        final PermissionModel permissionModel = list.get(paramInt);
        viewholder.tvNumber.setText("+91" + permissionModel.getNumber());
        viewholder.tvRelation.setText(permissionModel.getRelation());
        viewholder.tvDate.setText(permissionModel.getDate());
        if (permissionModel.isCurrentUser()) {
            viewholder.deleteButton.setVisibility(View.GONE);
        } else {
            viewholder.deleteButton.setVisibility(View.VISIBLE);
            viewholder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("mobile", permissionModel.getNumber());
                    params.put("profileId", permissionModel.getProfileId());
                    ParseCloud.callFunctionInBackground("deletePermission", params, new FunctionCallback<Object>() {
                        @Override
                        public void done(Object o, ParseException e) {
                            if (e == null) {
                                mApp.showToast(activity, "Permission Removed");
                                activity.getExistingPermissions();
                            } else {
                                mApp.showToast(activity, "Error while deleting permission");
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        }
        return paramView;
    }

    static class ViewHolder {
        private TextView tvNumber;
        private TextView tvRelation;
        private TextView tvDate;
        private ImageView deleteButton;
    }
}