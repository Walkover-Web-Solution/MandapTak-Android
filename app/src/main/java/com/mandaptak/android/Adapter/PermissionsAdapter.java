package com.mandaptak.android.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mandaptak.android.Models.PermissionModel;
import com.mandaptak.android.R;

import java.util.ArrayList;

public class PermissionsAdapter extends BaseAdapter {
    Context context;
    ArrayList<PermissionModel> list;

    public PermissionsAdapter(Context context, ArrayList<PermissionModel> paramArrayList) {
        this.list = paramArrayList;
        this.context = context;
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
            paramView = LayoutInflater.from(context).inflate(R.layout.permission_item, null);
            viewholder.tvNumber = (TextView) paramView.findViewById(R.id.number);
            viewholder.tvRelation = ((TextView) paramView.findViewById(R.id.relation));
            viewholder.tvDate = (TextView) paramView.findViewById(R.id.date);
            viewholder.deleteButton = (ImageView) paramView.findViewById(R.id.delete);
            paramView.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) paramView.getTag();
        }
        PermissionModel permissionModel = list.get(paramInt);
        viewholder.tvNumber.setText("+91" + permissionModel.getNumber());
        viewholder.tvRelation.setText(permissionModel.getRelation());
        viewholder.tvDate.setText(permissionModel.getDate());
        viewholder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return paramView;
    }

    static class ViewHolder {
        private TextView tvNumber;
        private TextView tvRelation;
        private TextView tvDate;
        private ImageView deleteButton;
    }
}