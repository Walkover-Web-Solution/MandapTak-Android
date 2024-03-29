package me.iwf.photopicker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.johnpersano.supertoasts.SuperToast;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.entity.Photo;
import me.iwf.photopicker.event.OnItemCheckListener;
import me.iwf.photopicker.fragment.PhotoPickerFragment;
import me.iwf.photopicker.utils.Prefs;

public class PhotoPickerActivity extends AppCompatActivity {

    public final static String EXTRA_MAX_COUNT = "MAX_COUNT";
    public final static String EXTRA_SHOW_CAMERA = "SHOW_CAMERA";
    public final static String KEY_SELECTED_PHOTOS = "SELECTED_PHOTOS";
    public final static int DEFAULT_MAX_COUNT = 9;
    public static ProgressDialog dialog;
    private PhotoPickerFragment pickerFragment;
    private MenuItem menuDoneItem;
    private int maxCount = DEFAULT_MAX_COUNT;
    private Context context;
    /**
     * to prevent multiple calls to inflate menu
     */
    private boolean menuIsInflated = false;

    public static void showToast(Context context, String message) {
        SuperToast superToast = new SuperToast(context);
        superToast.cancelAllSuperToasts();
        superToast.setAnimations(SuperToast.Animations.POPUP);
        superToast.setDuration(SuperToast.Duration.MEDIUM);
        superToast.setText(" " + message);
        superToast.setTextColor(context.getResources().getColor(R.color.red_300));
        superToast.setBackground(R.drawable.border_toast);
        superToast.show();
    }

    public static void show_PDialog(Context con, String message) {
        dialog = new ProgressDialog(con, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        dialog.setMessage(message);
        dialog.setCancelable(true);
        dialog.show();
    }

    public static void updateDialogProgress(int[] progress, String message) {
        dialog.setProgress(progress[0]);
        dialog.setMessage(message);
    }

    public static byte[] read(File file) {
        try {
            ByteArrayOutputStream ous = new ByteArrayOutputStream();
            Bitmap bitmap = decodeFile(file);
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, ous);
            }
            return ous.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap decodeFile(File f) {
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            //The new size we want to scale to
            final float REQUIRED_WIDTH = 612.0f;
            final float REQUIRED_HIGHT = 816.0f;
            //Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_WIDTH && o.outHeight / scale / 2 >= REQUIRED_HIGHT)
                scale *= 2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_photo_picker);
        context = this;
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.images);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            actionBar.setElevation(25);
        }

        maxCount = getIntent().getIntExtra(EXTRA_MAX_COUNT, DEFAULT_MAX_COUNT);
        boolean showCamera = getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA, true);

        pickerFragment =
                (PhotoPickerFragment) getSupportFragmentManager().findFragmentById(R.id.photoPickerFragment);

        pickerFragment.getPhotoGridAdapter().setShowCamera(showCamera);

        pickerFragment.getPhotoGridAdapter().setOnItemCheckListener(new OnItemCheckListener() {
            @Override
            public boolean OnItemCheck(int position, Photo photo, final boolean isCheck, int selectedItemCount) {

                int total = selectedItemCount + (isCheck ? -1 : 1);

                menuDoneItem.setEnabled(total > 0);

                if (maxCount <= 1) {
                    List<Photo> photos = pickerFragment.getPhotoGridAdapter().getSelectedPhotos();
                    if (!photos.contains(photo)) {
                        photos.clear();
                        pickerFragment.getPhotoGridAdapter().notifyDataSetChanged();
                    }
                    return true;
                }

                if (total > maxCount) {
                    PhotoPickerActivity.showToast(context, "Upto " + maxCount + " photos can be selected");
                    return false;
                }
                menuDoneItem.setTitle(getString(R.string.done_with_count, total, maxCount));
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!menuIsInflated) {
            getMenuInflater().inflate(R.menu.menu_picker, menu);
            menuDoneItem = menu.findItem(R.id.done);
            menuDoneItem.setEnabled(false);
            menuIsInflated = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        if (item.getItemId() == R.id.done) {
            final ArrayList<String> photoPaths = pickerFragment.getPhotoGridAdapter().getSelectedPhotoPaths();
            show_PDialog(context, "Uploading Image..");
            final int[] i = {1};
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (i[0] != (photoPaths.size() + 1)) {
                        updateDialogProgress(new int[]{100}, "Uploading Image: " + i[0]);
                        final File file = new File(photoPaths.get(i[0] - 1));
                        final ParseObject image = new ParseObject("Photo");
                        try {
                            final ParseFile parseFile = new ParseFile(file.getName(), read(file));
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
                                                        dialog.dismiss();
                                                        Intent intent = new Intent();
                                                        if (e == null) {
                                                            PhotoPickerActivity.showToast(context, "Photos Uploaded");
                                                            intent.putExtra(KEY_SELECTED_PHOTOS, true);
                                                        } else {
                                                            e.printStackTrace();
                                                            PhotoPickerActivity.showToast(context, "Error while uploading photos");
                                                            intent.putExtra(KEY_SELECTED_PHOTOS, false);
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public PhotoPickerActivity getActivity() {
        return this;
    }
}
