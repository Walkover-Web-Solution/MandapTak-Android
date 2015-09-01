package me.iwf.photopicker.event;

import me.iwf.photopicker.entity.Photo;

public interface OnItemCheckListener {

    boolean OnItemCheck(int position, Photo path, boolean isCheck, int selectedItemCount);

}
