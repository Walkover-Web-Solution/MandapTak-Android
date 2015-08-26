package com.mandaptak.android.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mandaptak.android.EditProfile.FinalEditProfileFragment;
import com.mandaptak.android.R;

import java.io.File;
import java.util.ArrayList;

import me.iwf.photopicker.PhotoPagerActivity;
import me.iwf.photopicker.utils.ImageModel;

public class LayoutAdapter extends RecyclerView.Adapter<LayoutAdapter.SimpleViewHolder> {

    private final Context mContext;
    FinalEditProfileFragment fragment;
    private ArrayList<ImageModel> mItems;

    public LayoutAdapter(Context context, FinalEditProfileFragment fragment, ArrayList<ImageModel> list) {
        mContext = context;
        this.mItems = list;
        this.fragment = fragment;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, final int position) {
        final String path = mItems.get(position).getLink();
        final Uri uri;
        if (path.startsWith("http")) {
            uri = Uri.parse(path);
        } else {
            uri = Uri.fromFile(new File(path));
        }

        if (mItems.get(position).isPrimary())
            holder.indicator.setVisibility(View.VISIBLE);
        else
            holder.indicator.setVisibility(View.GONE);

        Glide.with(mContext)
                .load(uri)
                .error(me.iwf.photopicker.R.drawable.ic_broken_image_black_48dp)
                .into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PhotoPagerActivity.class);
                intent.putExtra(PhotoPagerActivity.EXTRA_CURRENT_ITEM, position);
                intent.putExtra(PhotoPagerActivity.EXTRA_PHOTOS, mItems);
                fragment.previewPhoto(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public final ImageView image, indicator;

        public SimpleViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.profile_image);
            indicator = (ImageView) view.findViewById(R.id.primary_indicator);
        }
    }
}