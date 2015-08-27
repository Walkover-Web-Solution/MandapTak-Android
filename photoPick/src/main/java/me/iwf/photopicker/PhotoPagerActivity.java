package me.iwf.photopicker;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.soundcloud.android.crop.Crop;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.fragment.ImagePagerFragment;
import me.iwf.photopicker.utils.ImageModel;

public class PhotoPagerActivity extends AppCompatActivity {

    public final static String EXTRA_CURRENT_ITEM = "current_item";
    public final static String EXTRA_PHOTOS = "photos";
    private ImagePagerFragment pagerFragment;
    private ActionBar actionBar;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_photo_pager);
        context = this;
        int currentItem = getIntent().getIntExtra(EXTRA_CURRENT_ITEM, 0);
        ArrayList<ImageModel> paths = (ArrayList<ImageModel>) getIntent().getSerializableExtra(EXTRA_PHOTOS);

        pagerFragment =
                (ImagePagerFragment) getSupportFragmentManager().findFragmentById(R.id.photoPagerFragment);
        pagerFragment.setPhotos(paths, currentItem);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        updateActionBarTitle();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            actionBar.setElevation(25);
        }

        pagerFragment.getViewPager().addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                updateActionBarTitle();
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_preview, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        ArrayList<String> photoPaths = new ArrayList<>();
        for (int i = 0; i < pagerFragment.getPaths().size(); i++) {
            photoPaths.add(pagerFragment.getPaths().get(i).getLink());
        }
        Intent intent = new Intent();
        intent.putExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS, true);
        setResult(RESULT_OK, intent);
        finish();
//        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (item.getItemId() == R.id.delete) {
            PhotoPickerActivity.show_PDialog(context, "Deleting Photo..");
            final int index = pagerFragment.getCurrentItem();
            final String deleteParseObjectId = pagerFragment.getPaths().get(index).getParseObjectId();
            ParseQuery<ParseObject> deleteObjectParseQuery = new ParseQuery<>("Photo");
            deleteObjectParseQuery.whereEqualTo("profileId", ParseUser.getCurrentUser().getParseObject("profileId"));
            deleteObjectParseQuery.getInBackground(deleteParseObjectId, new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    parseObject.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            PhotoPickerActivity.dialog.dismiss();
                            if (e == null) {
                                if (pagerFragment.getPaths().size() <= 1) {
                                    // show confirm dialog
                                    new AlertDialog.Builder(context)
                                            .setTitle(R.string.confirm_to_delete)
                                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                    Intent intent = new Intent();
                                                    intent.putExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS, true);
                                                    setResult(RESULT_OK, intent);
                                                    finish();
                                                }
                                            })
                                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            })
                                            .show();
                                } else {
                                    pagerFragment.getPaths().remove(index);
                                    pagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
                                }
                            } else {
                                Toast.makeText(context, "Unable to delete from record", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });

            return true;
        }
        if (item.getItemId() == R.id.profile) {
            PhotoPickerActivity.show_PDialog(context, "Loading Photo..");
            final int index = pagerFragment.getCurrentItem();
            downloadFile(pagerFragment.getPaths().get(index).getLink());

        }
        return super.onOptionsItemSelected(item);
    }

    public void downloadFile(final String link) {
        final File file = new File(Environment.getExternalStorageDirectory() + "/pics/");
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
                    OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/pics/pic.jpg");
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
                Crop.of(Uri.fromFile(new File(file.getPath() + "/" + "pic.jpg")), Uri.fromFile(new File(file.getPath() + "/" + "cropped.jpg"))).asSquare().start(PhotoPagerActivity.this, Crop.REQUEST_CROP);

            }
        }.execute();
    }

    public void updateActionBarTitle() {
        actionBar.setTitle(
                getString(R.string.image_index, pagerFragment.getViewPager().getCurrentItem() + 1,
                        pagerFragment.getPaths().size()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Crop.REQUEST_CROP:
                    if (data != null) {
                        if (data.hasExtra(MediaStore.EXTRA_OUTPUT)) {
                            Uri uri = data.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
                            final File file = new File(uri.getPath());
                            ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Profile");
                            parseQuery.whereEqualTo("objectId", ParseUser.getCurrentUser().getParseObject("profileId").getObjectId());
                            parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                                @Override
                                public void done(final ParseObject parseObject, ParseException e) {
                                    try {
                                        final ParseFile parseFile = new ParseFile(file.getName(), PhotoPickerActivity.read(file));
                                        parseFile.saveInBackground(new SaveCallback() {
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    parseObject.put("profilePic", parseFile);
                                                    parseObject.saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            if (e == null) {
                                                                file.delete();
                                                                Toast.makeText(context, "Profile photo saved", Toast.LENGTH_SHORT).show();
                                                                final int index = pagerFragment.getCurrentItem();
                                                                ParseQuery<ParseObject> deleteObjectParseQuery = new ParseQuery<>("Photo");
                                                                deleteObjectParseQuery.whereEqualTo("profileId", ParseUser.getCurrentUser().getParseObject("profileId"));
                                                                deleteObjectParseQuery.findInBackground(new FindCallback<ParseObject>() {
                                                                    @Override
                                                                    public void done(List<ParseObject> list, ParseException e) {
                                                                        if (e == null) {
                                                                            for (int i = 0; i < list.size(); i++) {
                                                                                ParseObject parseObject = list.get(i);
                                                                                if (i == index) {
                                                                                    pagerFragment.getPaths().get(i).setIsPrimary(true);
                                                                                    parseObject.put("isPrimary", true);
                                                                                    parseObject.saveInBackground();
                                                                                } else {
                                                                                    pagerFragment.getPaths().get(i).setIsPrimary(false);
                                                                                    parseObject.put("isPrimary", false);
                                                                                    parseObject.saveInBackground();
                                                                                }
                                                                            }
                                                                            pagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
                                                                            pagerFragment.getViewPager().setCurrentItem(index, true);
                                                                        }
                                                                    }
                                                                });
                                                            } else {
                                                                Toast.makeText(context, "Failed to save profile photo.", Toast.LENGTH_SHORT).show();
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            });

                        }
                    }
                    break;
            }
        }
    }
}
