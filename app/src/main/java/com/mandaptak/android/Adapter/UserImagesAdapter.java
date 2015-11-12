package com.mandaptak.android.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.mandaptak.android.Main.MainActivity;
import com.mandaptak.android.Matches.ViewProfilePage;
import com.mandaptak.android.R;

import java.io.File;
import java.util.ArrayList;

import me.iwf.photopicker.PhotoPagerActivity;
import me.iwf.photopicker.PhotoViewerActivity;
import me.iwf.photopicker.utils.ImageModel;

public class UserImagesAdapter extends RecyclerView.Adapter<UserImagesAdapter.SimpleViewHolder> {

  private final Context mContext;
  MainActivity mainActivity;
  ViewProfilePage viewProfilePage;
  private ArrayList<ImageModel> mItems;

  public UserImagesAdapter(Context context, MainActivity fragment, ArrayList<ImageModel> list) {
    mContext = context;
    this.mItems = list;
    this.mainActivity = fragment;
  }

  public UserImagesAdapter(Context context, ViewProfilePage fragment, ArrayList<ImageModel> list) {
    mContext = context;
    this.mItems = list;
    this.viewProfilePage = fragment;
  }

  @Override
  public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final View view = LayoutInflater.from(mContext).inflate(R.layout.user_images, parent, false);
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
    holder.image.setImageURI(uri);
    holder.image.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(mContext, PhotoViewerActivity.class);
        intent.putExtra(PhotoPagerActivity.EXTRA_CURRENT_ITEM, position);
        intent.putExtra(PhotoPagerActivity.EXTRA_PHOTOS, mItems);
        if (mainActivity != null)
          mainActivity.previewPhoto(intent);
        else viewProfilePage.previewPhoto(intent);
      }
    });

  }

  @Override
  public int getItemCount() {

    return mItems.size();
  }

  public static class SimpleViewHolder extends RecyclerView.ViewHolder {
    public final SimpleDraweeView image;

    public SimpleViewHolder(View view) {
      super(view);
      image = (SimpleDraweeView) view.findViewById(R.id.profile_image);
    }
  }
}

