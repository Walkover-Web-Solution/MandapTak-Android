package com.mandaptak.android.FullProfile;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.mandaptak.android.EditProfile.BasicProfileFragment;
import com.mandaptak.android.EditProfile.DetailsProfileFragment;
import com.mandaptak.android.EditProfile.FinalEditProfileFragment;
import com.mandaptak.android.EditProfile.QualificationEditProfileFragment;
import com.mandaptak.android.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.IconPageIndicator;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.iwf.photopicker.utils.ImageModel;

public class FullProfileActivity extends AppCompatActivity implements ActionBar.TabListener {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ImagePagerAdapter imagePagerAdapter;
    ViewPager mMenuPager, mImagesPager;
    CirclePageIndicator circlePageIndicator;
    ArrayList<ImageModel> parsePhotos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_profile_activity);
        try {
            getSupportActionBar().hide();
        } catch (Exception ignored) {
        }
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        getImages();
        mMenuPager = (ViewPager) findViewById(R.id.pager_menu);
        mImagesPager = (ViewPager) findViewById(R.id.pager_images);
        mMenuPager.setAdapter(mSectionsPagerAdapter);
        mMenuPager.setOffscreenPageLimit(1);
        final TabPageIndicator iconPageIndicator = (TabPageIndicator) findViewById(R.id.icons);
        iconPageIndicator.setViewPager(mMenuPager);
        circlePageIndicator = (CirclePageIndicator) findViewById(R.id.circles);

        mMenuPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        mMenuPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mMenuPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter implements IconPagerAdapter {
        private final int[] ICONS = new int[]{
                R.drawable.ic_tab1,
                R.drawable.ic_tab2,
                R.drawable.ic_tab3,
                R.drawable.ic_tab4,
        };

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new BasicProfileInfo();
                case 1:
                    return new DetailsProfileInfo();
                case 2:
                    return new QualificationInfo();
                case 3:
                    return new FinalProfileInfo();
                default:
                    return new BasicProfileFragment();
            }
        }

        @Override
        public int getIconResId(int index) {
            return ICONS[index];
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }
    }

    class ImagePagerAdapter extends PagerAdapter {
        LayoutInflater mLayoutInflater;
        ArrayList<ImageModel> list;

        public ImagePagerAdapter(ArrayList<ImageModel> list) {
            mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.picture_item_view, container, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            final String path = list.get(position).getLink();
            final Uri uri;
            if (path.startsWith("http")) {
                uri = Uri.parse(path);
            } else {
                uri = Uri.fromFile(new File(path));
            }
            Glide.with(FullProfileActivity.this)
                    .load(uri)
                    .error(me.iwf.photopicker.R.drawable.ic_broken_image_black_48dp)
                    .into(imageView);
            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    private void getImages() {
        ParseQuery<ParseObject> queryParseQuery = new ParseQuery<>("Photo");
        queryParseQuery.whereEqualTo("profileId", ParseUser.getCurrentUser().getParseObject("profileId"));
        queryParseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list != null) {
                    for (ParseObject item : list) {
                        parsePhotos.add(new ImageModel(item.getParseFile("file").getUrl(), item.getBoolean("isPrimary"), item.getObjectId()));
                    }
                    imagePagerAdapter = new ImagePagerAdapter(parsePhotos);
                    mImagesPager.setAdapter(imagePagerAdapter);
                    circlePageIndicator.setViewPager(mImagesPager);
                }
            }
        });
    }
}
