package me.iwf.photopicker;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.soundcloud.android.crop.Crop;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.fragment.ImagePagerFragment;
import me.iwf.photopicker.utils.ImageModel;
import me.iwf.photopicker.utils.Prefs;

public class PhotoPagerActivity extends AppCompatActivity {

  public final static String EXTRA_CURRENT_ITEM = "current_item";
  public final static String EXTRA_PHOTOS = "photos";
  private ImagePagerFragment pagerFragment;
  private ActionBar actionBar;
  private Context context;
  private Boolean isPrimary = false;
  private ParseObject tempParseObject = null;
  private Boolean shouldDelete = false;

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
    Intent intent = new Intent();
    intent.putExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS, true);
    setResult(RESULT_OK, intent);
    finish();
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
      ParseQuery<ParseObject> q1 = new ParseQuery<>("Profile");
      q1.getInBackground(Prefs.getProfileId(context), new GetCallback<ParseObject>() {
        @Override
        public void done(ParseObject object, ParseException e) {
          if (e == null) {
            tempParseObject = object;
            ParseQuery<ParseObject> deleteObjectParseQuery = new ParseQuery<>("Photo");
            deleteObjectParseQuery.whereEqualTo("profileId", object);
            deleteObjectParseQuery.getInBackground(deleteParseObjectId, new GetCallback<ParseObject>() {
              @Override
              public void done(ParseObject parseObject, ParseException e) {
                if (parseObject.getBoolean("isPrimary")) {
                  isPrimary = true;
                }
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
                                shouldDelete = true;
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
                        shouldDelete = true;
                        pagerFragment.getPaths().remove(index);
                        pagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
                      }

                      if (isPrimary && shouldDelete) {
                        tempParseObject.put("isComplete", false);
                        tempParseObject.saveInBackground(new SaveCallback() {
                          @Override
                          public void done(ParseException e) {
                            if (e != null) {
                              e.printStackTrace();
                            }
                          }
                        });
                      }
                    } else {
                      PhotoPickerActivity.showToast(context, "Unable to delete from record");
                    }
                  }
                });
              }
            });
          } else {
            PhotoPickerActivity.showToast(context, "Unable to delete from record");
          }
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
    final File file = getApplicationContext().getCacheDir();
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
          Log.e("Crop", "" + crop.exists());
          Log.e("Pic", "" + pic.exists());
          Crop.of(Uri.fromFile(pic), Uri.fromFile(crop))
              .asSquare()
              .start(PhotoPagerActivity.this, Crop.REQUEST_CROP);
        } catch (Exception e) {
          e.printStackTrace();
        }
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
              if (file.exists()) {
                PhotoPickerActivity.show_PDialog(context, "Uploading Profile Photo");
                ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Profile");
                parseQuery.getInBackground(Prefs.getProfileId(context), new GetCallback<ParseObject>() {
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
                                  PhotoPickerActivity.showToast(context, "Profile photo saved");
                                  final int index = pagerFragment.getCurrentItem();
                                  ParseQuery<ParseObject> q1 = new ParseQuery<>("Profile");
                                  q1.getInBackground(Prefs.getProfileId(context), new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject object, ParseException e) {
                                      if (e == null) {
                                        ParseQuery<ParseObject> deleteObjectParseQuery = new ParseQuery<>("Photo");
                                        deleteObjectParseQuery.whereEqualTo("profileId", object);
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
                                      }
                                    }
                                  });
                                } else {
                                  PhotoPickerActivity.showToast(context, "Failed to save profile photo.");
                                  e.printStackTrace();
                                }
                              }
                            });
                          }
                          PhotoPickerActivity.dialog.dismiss();
                        }
                      });
                    } catch (Exception e1) {
                      PhotoPickerActivity.dialog.dismiss();
                      e1.printStackTrace();
                    }
                  }
                });
              }
            }
          }
          break;
      }
    }
  }
}
