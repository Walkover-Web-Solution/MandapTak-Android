package com.mandaptak.android.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mandaptak.android.Models.MatchesModel;
import com.mandaptak.android.R;
import com.mandaptak.android.Views.CircleImageView;

import java.io.File;
import java.util.ArrayList;

public class MatchesAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<MatchesModel> list;

    public MatchesAdapter(ArrayList<MatchesModel> paramArrayList, Context paramContext) {
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

        if (paramView == null) {
            paramView = LayoutInflater.from(ctx).inflate(R.layout.matches_row, null);
            ViewHolder viewholder = new ViewHolder();
            viewholder.tvName = (TextView) paramView.findViewById(R.id.title);
            viewholder.tvReligion = ((TextView) paramView.findViewById(R.id.religion));
            viewholder.tvWork = (TextView) paramView.findViewById(R.id.work);
            viewholder.profilePic = (CircleImageView) paramView.findViewById(R.id.thumbnail);
            paramView.setTag(viewholder);
        }

        final ViewHolder viewholder = (ViewHolder) paramView.getTag();
        MatchesModel matchesModel = list.get(paramInt);
        viewholder.tvName.setText(matchesModel.getName());
        viewholder.tvReligion.setText(matchesModel.getCaste());
        viewholder.tvWork.setText(matchesModel.getWork());
        final String path = list.get(paramInt).getUrl();
        final Uri uri;
        if (path.startsWith("http")) {
            uri = Uri.parse(path);
        } else {
            uri = Uri.fromFile(new File(path));
        }
        Glide.with(ctx)
                .load(uri)
                .error(me.iwf.photopicker.R.drawable.ic_broken_image_black_48dp)
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