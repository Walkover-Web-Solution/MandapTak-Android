package com.mandaptak.android.EditProfile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mandaptak.android.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.adapter.FacebookPhotoGridAdapter;
import me.iwf.photopicker.event.OnFacebookItemCheckListener;
import me.iwf.photopicker.utils.Prefs;

public class FacebookPhotos extends Activity {
    ArrayList<String> images = new ArrayList<>();
    FacebookPhotoGridAdapter facebookPhotoGridAdapter;
    Context context;
    Button doneButton;

    public static byte[] read(String url) {
        try {
            ByteArrayOutputStream ous = new ByteArrayOutputStream();
            Bitmap bitmap = getBitmapFromURL(url);
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, ous);
            }
            return ous.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fb_grid);
        context = this;
        doneButton = (Button) findViewById(R.id.done);
        RecyclerView recyclerView = (RecyclerView) findViewById(me.iwf.photopicker.R.id.rv_photos);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        facebookPhotoGridAdapter = new FacebookPhotoGridAdapter(context, images);
        if (getIntent() != null) {
            if (getIntent().hasExtra("pics-fb")) {
                images = getIntent().getStringArrayListExtra("pics-fb");
                Log.e("", "" + images.get(0));
                facebookPhotoGridAdapter = new FacebookPhotoGridAdapter(context, images);
                recyclerView.setAdapter(facebookPhotoGridAdapter);
            }
        }
        facebookPhotoGridAdapter.setOnItemCheckListener(new OnFacebookItemCheckListener() {
            @Override
            public boolean OnItemCheck(int position, String photo, final boolean isCheck, int selectedItemCount) {

                int total = selectedItemCount + (isCheck ? -1 : 1);

                if (total > 4) {
                    PhotoPickerActivity.showToast(context, "Upto " + 4 + " photos can be selected");
                    return false;
                }
                doneButton.setText(getString(me.iwf.photopicker.R.string.done_with_count, total, 4));
                return true;
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ArrayList<String> photoPaths = facebookPhotoGridAdapter.getSelectedPhotoPaths();
                PhotoPickerActivity.show_PDialog(context, "Uploading Image..");
                final int[] i = {1};
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (i[0] != (photoPaths.size() + 1)) {
                            PhotoPickerActivity.updateDialogProgress(new int[]{100}, "Uploading Image: " + i[0]);
                            String path = photoPaths.get(i[0] - 1);
                            final ParseObject image = new ParseObject("Photo");
                            try {
                                final ParseFile parseFile = new ParseFile("fb_pic", read(path));
                                parseFile.saveInBackground(new SaveCallback() {
                                    public void done(ParseException e) {
                                        ParseQuery<ParseObject> q1 = new ParseQuery<>("Profile");
                                        q1.getInBackground(Prefs.getProfileId(context), new GetCallback<ParseObject>() {
                                            @Override
                                            public void done(ParseObject object, ParseException e) {
                                                if (e == null) {
                                                    image.put("file", parseFile);
                                                    image.put("isPrimary", false);
                                                    image.put("profileId", object);
                                                    image.saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            PhotoPickerActivity.dialog.dismiss();
                                                            Intent intent = new Intent();
                                                            if (e == null) {
                                                                PhotoPickerActivity.showToast(context, "Photos Uploaded");
                                                                intent.putExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS, true);
                                                            } else {
                                                                e.printStackTrace();
                                                                PhotoPickerActivity.showToast(context, "Error while uploading photos");
                                                                intent.putExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS, false);
                                                            }
                                                            setResult(RESULT_OK, intent);
                                                            finish();
                                                        }
                                                    });
                                                }
                                            }
                                        });

                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            i[0]++;
                            handler.postDelayed(this, 500);
                        }
                    }
                }, 1000);
            }
        });
    }
}