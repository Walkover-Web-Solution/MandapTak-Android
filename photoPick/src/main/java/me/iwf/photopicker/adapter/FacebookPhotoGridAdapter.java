package me.iwf.photopicker.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import me.iwf.photopicker.R;
import me.iwf.photopicker.event.OnFacebookItemCheckListener;

/**
 * Created by donglua on 15/5/31.
 */
public class FacebookPhotoGridAdapter extends FacebookSelectableAdapter<FacebookPhotoGridAdapter.PhotoViewHolder> {

    private LayoutInflater inflater;
    private Context mContext;
    private OnFacebookItemCheckListener onItemCheckListener = null;
    private ArrayList<String> photos;

    public FacebookPhotoGridAdapter(Context mContext, ArrayList<String> photos) {
        this.photos = photos;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_photo, parent, false);
        PhotoViewHolder holder = new PhotoViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, final int position) {

        Glide.with(mContext)
                .load(Uri.parse(photos.get(position)))
                .centerCrop()
                .thumbnail(0.1f)
                .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                .error(R.drawable.com_facebook_profile_picture_blank_square)
                .into(holder.ivPhoto);

        final boolean isChecked = isSelected(photos.get(position));

        holder.vSelected.setSelected(isChecked);
        holder.ivPhoto.setSelected(isChecked);

        holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isEnable = true;

                if (onItemCheckListener != null) {
                    isEnable = onItemCheckListener.OnItemCheck(position, photos.get(position), isChecked,
                            getSelectedPhotos().size());
                }
                if (isEnable) {
                    toggleSelection(photos.get(position));
                    notifyItemChanged(position);
                }
            }
        });
        holder.vSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean isEnable = true;

                if (onItemCheckListener != null) {
                    isEnable = onItemCheckListener.OnItemCheck(position, photos.get(position), isChecked,
                            getSelectedPhotos().size());
                }
                if (isEnable) {
                    toggleSelection(photos.get(position));
                    notifyItemChanged(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public void setOnItemCheckListener(OnFacebookItemCheckListener onItemCheckListener) {
        this.onItemCheckListener = onItemCheckListener;
    }

    public ArrayList<String> getSelectedPhotoPaths() {
        return selectedPhotos;
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;
        private View vSelected;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
            vSelected = itemView.findViewById(R.id.v_selected);
        }
    }
}
