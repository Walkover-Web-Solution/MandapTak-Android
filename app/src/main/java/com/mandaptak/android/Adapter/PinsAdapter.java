package com.mandaptak.android.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mandaptak.android.Models.MatchesModel;
import com.mandaptak.android.R;
import com.mandaptak.android.Views.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PinsAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<MatchesModel> list;

    public PinsAdapter(ArrayList<MatchesModel> paramArrayList, Context paramContext) {
        this.list = paramArrayList;
        this.ctx = paramContext;
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
            paramView = LayoutInflater.from(ctx).inflate(R.layout.matches_row, null);
            viewholder = new ViewHolder();
            viewholder.tvName = (TextView) paramView.findViewById(R.id.title);
            viewholder.tvReligion = ((TextView) paramView.findViewById(R.id.religion));
            viewholder.tvWork = (TextView) paramView.findViewById(R.id.work);
            viewholder.profilePic = (CircleImageView) paramView.findViewById(R.id.thumbnail);
            paramView.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) paramView.getTag();
        }
        MatchesModel matchesModel = list.get(paramInt);
        viewholder.tvName.setText(matchesModel.getName());
        viewholder.tvReligion.setText(matchesModel.getReligion());
        viewholder.tvWork.setText(matchesModel.getWork());
        Picasso.with(ctx)
                .load(matchesModel.getUrl())
                .into(viewholder.profilePic);

        return paramView;
    }

    static class ViewHolder {
        public TextView tvName;
        public TextView tvReligion;
        public TextView tvWork;
        public CircleImageView profilePic;
    }
}