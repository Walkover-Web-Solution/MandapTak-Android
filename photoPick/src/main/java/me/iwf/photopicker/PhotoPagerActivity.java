package me.iwf.photopicker;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.ArrayList;

import me.iwf.photopicker.fragment.ImagePagerFragment;
import me.iwf.photopicker.utils.ImageModel;

public class PhotoPagerActivity extends AppCompatActivity {

    public final static String EXTRA_CURRENT_ITEM = "current_item";
    public final static String EXTRA_PHOTOS = "photos";
    private ImagePagerFragment pagerFragment;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_photo_pager);

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
        int p = 0;
        ArrayList<String> photoPaths = new ArrayList<>();
        for (int i = 0; i < pagerFragment.getPaths().size(); i++) {
            photoPaths.add(pagerFragment.getPaths().get(i).getLink());
            if (pagerFragment.getPaths().get(i).isPrimary()) {
                p = i;
            }
        }
        Intent intent = new Intent();
        intent.putExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS, photoPaths);
        intent.putExtra(PhotoPickerActivity.PRIMARY_PHOTO_INDEX, p);
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
            final int index = pagerFragment.getCurrentItem();

            final String deletedPath = pagerFragment.getPaths().get(index).getLink();
            final boolean deletedStatus = pagerFragment.getPaths().get(index).isPrimary();

            Snackbar snackbar = Snackbar.make(pagerFragment.getView(), R.string.deleted_a_photo,
                    Snackbar.LENGTH_LONG);

            if (pagerFragment.getPaths().size() <= 1) {

                // show confirm dialog
                new AlertDialog.Builder(this)
                        .setTitle(R.string.confirm_to_delete)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                setResult(RESULT_OK);
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
                snackbar.show();
                pagerFragment.getPaths().remove(index);
                //pagerFragment.getViewPager().removeViewAt(index);
                pagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
            }

            snackbar.setAction(R.string.undo, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pagerFragment.getPaths().size() > 0) {
                        pagerFragment.getPaths().add(index, new ImageModel(deletedPath, deletedStatus));
                    } else {
                        pagerFragment.getPaths().add(new ImageModel(deletedPath, deletedStatus));
                    }
                    pagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
                    pagerFragment.getViewPager().setCurrentItem(index, true);
                }
            });

            return true;
        }
        if (item.getItemId() == R.id.profile) {
            final int index = pagerFragment.getCurrentItem();
            for (int i = 0; i < pagerFragment.getPaths().size(); i++) {
                pagerFragment.getPaths().get(i).setIsPrimary(false);
            }
            pagerFragment.getPaths().get(index).setIsPrimary(true);
            pagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
            pagerFragment.getViewPager().setCurrentItem(index, true);
            Crop.of(Uri.fromFile(new File(pagerFragment.getPaths().get(index).getLink())), Uri.EMPTY).asSquare().start(this);
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateActionBarTitle() {
        actionBar.setTitle(
                getString(R.string.image_index, pagerFragment.getViewPager().getCurrentItem() + 1,
                        pagerFragment.getPaths().size()));
    }
}
