package me.iwf.photopicker.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.R;
import me.iwf.photopicker.utils.ImageModel;

public class PhotoPagerAdapter extends PagerAdapter {

    private ArrayList<ImageModel> paths = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public PhotoPagerAdapter(Context mContext, ArrayList<ImageModel> paths) {
        this.mContext = mContext;
        this.paths = paths;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View itemView = mLayoutInflater.inflate(R.layout.item_pager, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.iv_pager);
        ImageView primaryIndicator = (ImageView) itemView.findViewById(R.id.primary_icon);

        final String path = paths.get(position).getLink();
        final Uri uri;
        if (path.startsWith("http")) {
            uri = Uri.parse(path);
        } else {
            uri = Uri.fromFile(new File(path));
        }
        if (paths.get(position).isPrimary()) {
            primaryIndicator.setVisibility(View.VISIBLE);
        }
        Glide.with(mContext)
                .load(uri)
                .override(800, 800)
                .error(R.drawable.ic_broken_image_black_48dp)
                .into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mContext instanceof PhotoPickerActivity) {
                    if (!((Activity) mContext).isFinishing()) {
                        ((Activity) mContext).onBackPressed();
                    }
                }
            }
        });

        container.addView(itemView);

        return itemView;
    }

    @Override
    public int getCount() {
        return paths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
