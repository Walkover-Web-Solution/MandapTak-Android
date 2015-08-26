package me.iwf.photopicker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.adapter.FacebookPhotoGridAdapter;
import me.iwf.photopicker.event.OnPhotoClickListener;
import me.iwf.photopicker.fragment.ImagePagerFragment;
import me.iwf.photopicker.utils.ImageModel;

public class FacebookProfilePhotoPickerActivity extends AppCompatActivity {

    public final static String EXTRA_MAX_COUNT = "MAX_COUNT";
    public final static String KEY_SELECTED_PHOTOS = "SELECTED_PHOTOS";
    public final static int DEFAULT_MAX_COUNT = 9;
    public static ProgressDialog dialog;
    private MenuItem menuDoneItem;
    private int maxCount = DEFAULT_MAX_COUNT;
    private Context context;
    private FacebookPhotoGridAdapter photoGridAdapter;
    private ImagePagerFragment imagePagerFragment;

    /**
     * to prevent multiple calls to inflate menu
     */
    private boolean menuIsInflated = false;

    public static void show_PDialog(Context con, String message) {
        dialog = new ProgressDialog(con, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.show();
    }

    public static void updateDialogProgress(int progress, String message) {
        dialog.setProgress(progress);
        dialog.setMessage(message);
    }

    public static byte[] read(File file) throws IOException {
        ByteArrayOutputStream ous = null;
        InputStream ios = null;
        try {
            byte[] buffer = new byte[4096];
            ous = new ByteArrayOutputStream();
            ios = new FileInputStream(file);
            int read;
            while ((read = ios.read(buffer)) != -1) {
                ous.write(buffer, 0, read);
            }
        } finally {
            try {
                if (ous != null)
                    ous.close();
            } catch (IOException ignored) {
            }

            try {
                if (ios != null)
                    ios.close();
            } catch (IOException ignored) {
            }
        }
        return ous.toByteArray();
    }

    /**
     * Overriding this method allows us to run our exit animation first, then exiting
     * the activity when it complete.
     */
    @Override
    public void onBackPressed() {
        if (imagePagerFragment != null && imagePagerFragment.isVisible()) {
            imagePagerFragment.runExitAnimation(new Runnable() {
                public void run() {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack();
                    }
                }
            });
        } else {
            super.onBackPressed();
        }
    }

    public void addImagePagerFragment(ImagePagerFragment imagePagerFragment) {
        this.imagePagerFragment = imagePagerFragment;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, this.imagePagerFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fb_photo_picker);
        context = this;
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Profile Photos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        maxCount = getIntent().getIntExtra(EXTRA_MAX_COUNT, DEFAULT_MAX_COUNT);
       // photoGridAdapter = new FacebookPhotoGridAdapter(this, directories);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_photos);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(photoGridAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        photoGridAdapter.setOnPhotoClickListener(new OnPhotoClickListener() {
            @Override
            public void onClick(View v, int position, boolean showCamera) {
                final int index = showCamera ? position - 1 : position;

                List<String> photos = photoGridAdapter.getCurrentPhotoPaths();
                ArrayList<ImageModel> pics = new ArrayList<>();
                for (String pic : photos) {
                    pics.add(new ImageModel(pic, false, null));
                }
                int[] screenLocation = new int[2];
                v.getLocationOnScreen(screenLocation);
                ImagePagerFragment imagePagerFragment =
                        ImagePagerFragment.newInstance(pics, index, screenLocation,
                                v.getWidth(), v.getHeight());

                addImagePagerFragment(imagePagerFragment);
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
            final ArrayList<String> photoPaths = getFacebookPhotoGridAdapter().getSelectedPhotoPaths();
            show_PDialog(context, "Uploading Image");
            final int[] i = {1};
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (i[0] != (photoPaths.size() + 1)) {
                        updateDialogProgress(100, "Uploading Image: " + i[0]);
                        final File file = new File(photoPaths.get(i[0] - 1));
                        final ParseObject image = new ParseObject("Photo");
                        try {
                            final ParseFile parseFile = new ParseFile(file.getName(), read(file));
                            parseFile.saveInBackground(new SaveCallback() {
                                public void done(ParseException e) {
                                    image.put("file", parseFile);
                                    image.put("isPrimary", false);
                                    image.put("profileId", ParseUser.getCurrentUser().getParseObject("profileId"));
                                    image.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            dialog.dismiss();
                                            Intent intent = new Intent();
                                            if (e == null) {
                                                Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show();
                                                intent.putExtra(KEY_SELECTED_PHOTOS, true);
                                            } else {
                                                e.printStackTrace();
                                                Toast.makeText(context, "Error while uploading photos", Toast.LENGTH_SHORT).show();
                                                intent.putExtra(KEY_SELECTED_PHOTOS, false);
                                            }
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        }
                                    });
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        i[0]++;
                        handler.postDelayed(this, 500);
                    } else {
                        dialog.dismiss();
                    }
                }
            }, 1000);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public FacebookPhotoGridAdapter getFacebookPhotoGridAdapter() {
        return photoGridAdapter;
    }

    private ArrayList<String> getFacebookProfilePhotos() {
        ArrayList<String> list = new ArrayList<>();

        return list;
    }
}
