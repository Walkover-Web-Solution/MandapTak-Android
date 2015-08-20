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
import com.mandaptak.android.R;

import java.io.File;
import java.util.ArrayList;

import me.iwf.photopicker.PhotoPagerActivity;

public class LayoutAdapter extends RecyclerView.Adapter<LayoutAdapter.SimpleViewHolder> {

    private final Context mContext;
    private ArrayList<String> mItems;

    public LayoutAdapter(Context context, ArrayList<String> list) {
        mContext = context;
        this.mItems = list;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, final int position) {
        Uri uri = Uri.fromFile(new File(mItems.get(position)));

        Glide.with(mContext)
                .load(uri)
                .centerCrop()
                .thumbnail(0.1f)
                .placeholder(R.drawable.ic_photo_black_48dp)
                .error(R.drawable.ic_broken_image_black_48dp)
                .into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PhotoPagerActivity.class);
                intent.putExtra(PhotoPagerActivity.EXTRA_CURRENT_ITEM, position);
                intent.putExtra(PhotoPagerActivity.EXTRA_PHOTOS, mItems);
                previewPhoto(intent);
            }
        });

    }

    public void previewPhoto(Intent intent) {
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public final ImageView image;

        public SimpleViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.profile_image);
        }
    }
}