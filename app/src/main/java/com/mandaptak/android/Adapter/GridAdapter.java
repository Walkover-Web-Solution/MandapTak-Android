package com.mandaptak.android.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.mandaptak.android.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {
    ArrayList<String> list;
    ArrayList<String> selectedPics = new ArrayList<>();
    Context context;

    public GridAdapter(ArrayList<String> paramArrayList, Context context) {
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

    public View getView(final int paramInt, View paramView, ViewGroup paramViewGroup) {
        ViewHolder viewholder;
        if (paramView == null) {
            paramView = LayoutInflater.from(context).inflate(R.layout.fb_row, null);
            viewholder = new ViewHolder();
            viewholder.profileImage = (ImageView) paramView.findViewById(R.id.thumbnail);
            paramView.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) paramView.getTag();
        }
        viewholder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPics.add(list.get(paramInt));
            }
        });
        Picasso.with(context)
                .load(list.get(paramInt))
                .into(viewholder.profileImage);
        return paramView;
    }


    class ViewHolder {
        public ImageView profileImage;
    }
}