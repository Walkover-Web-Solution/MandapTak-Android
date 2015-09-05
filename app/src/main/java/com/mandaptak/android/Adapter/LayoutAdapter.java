package com.mandaptak.android.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mandaptak.android.EditProfile.FinalEditProfileFragment;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.soundcloud.android.crop.Crop;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import me.iwf.photopicker.PhotoPagerActivity;
import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.ImageModel;

public class LayoutAdapter extends RecyclerView.Adapter<LayoutAdapter.SimpleViewHolder> {

    private FinalEditProfileFragment fragment;
    private Common mApp;
    private Context context;
    private ArrayList<ImageModel> mItems;

    public LayoutAdapter(Context context, FinalEditProfileFragment fragment, ArrayList<ImageModel> list) {
        this.context = context;
        this.mItems = list;
        this.fragment = fragment;
        mApp = (Common) context.getApplicationContext();
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, final int position) {
        final String path = mItems.get(position).getLink();
        final Uri uri;
        uri = Uri.parse(path);

        if (mItems.get(position).isPrimary()) {
            holder.indicator.setImageResource(R.drawable.ic_star_yellow);
            holder.indicator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mApp.showToast(context, "Photo is already set primary.");
                }
            });
        } else {
            holder.indicator.setImageResource(R.drawable.ic_star_outline_yellow);
            holder.indicator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PhotoPickerActivity.show_PDialog(context, "Loading Photo..");
                    fragment.setPrimaryIndex(position);
                    downloadFile(path);
                }
            });
        }

        Glide.with(context)
                .load(uri)
                .placeholder(me.iwf.photopicker.R.drawable.com_facebook_profile_picture_blank_portrait)
                .error(me.iwf.photopicker.R.drawable.ic_broken_image_black_48dp)
                .into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PhotoPagerActivity.class);
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

    public void downloadFile(final String link) {
        final File file = context.getApplicationContext().getCacheDir();
        new AsyncTask<Void, int[], Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                int count;
                try {
                    URL url = new URL(link);
                    URLConnection conection = url.openConnection();
                    conection.connect();
                    int lenghtOfFile = conection.getContentLength();
                    InputStream input = new BufferedInputStream(url.openStream(), 8192);
                    OutputStream output = new FileOutputStream(file.getPath() + "/pic.jpg");
                    byte data[] = new byte[1024];
                    long total = 0;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        publishProgress(new int[]{(int) ((total * 100) / lenghtOfFile)});
                        output.write(data, 0, count);
                    }
                    output.flush();
                    output.close();
                    input.close();
                } catch (Exception e) {
                    Log.e("Error: ", e.getMessage());
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(int[]... values) {
                super.onProgressUpdate(values);
                PhotoPickerActivity.updateDialogProgress(values[0], "Loading Photo..");
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                PhotoPickerActivity.dialog.dismiss();
                try {
                    File pic = new File(file.getAbsolutePath() + "/pic.jpg");
                    File crop = new File(file.getAbsolutePath() + "/cropped.jpg");
                    Crop.of(Uri.fromFile(pic), Uri.fromFile(crop))
                            .asSquare()
                            .start(context, fragment);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
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