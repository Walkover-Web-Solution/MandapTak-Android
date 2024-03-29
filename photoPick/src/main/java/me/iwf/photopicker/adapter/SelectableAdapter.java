package me.iwf.photopicker.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.entity.Photo;
import me.iwf.photopicker.entity.PhotoDirectory;
import me.iwf.photopicker.event.Selectable;

public abstract class SelectableAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> implements Selectable {

    public int currentDirectoryIndex = 0;
    protected List<PhotoDirectory> photoDirectories;
    protected List<Photo> selectedPhotos;

    public SelectableAdapter() {
        photoDirectories = new ArrayList<>();
        selectedPhotos = new ArrayList<>();
    }

    /**
     * Indicates if the item at position position is selected
     *
     * @param photo Photo of the item to check
     * @return true if the item is selected, false otherwise
     */
    @Override
    public boolean isSelected(Photo photo) {
        return getSelectedPhotos().contains(photo);
    }

    /**
     * Toggle the selection status of the item at a given position
     *
     * @param photo Photo of the item to toggle the selection status for
     */
    @Override
    public void toggleSelection(Photo photo) {
        if (selectedPhotos.contains(photo)) {
            selectedPhotos.remove(photo);
        } else {
            selectedPhotos.add(photo);
        }
    }

    /**
     * Clear the selection status for all items
     */
    @Override
    public void clearSelection() {
        selectedPhotos.clear();
    }

    /**
     * Count the selected items
     *
     * @return Selected items count
     */
    @Override
    public int getSelectedItemCount() {
        return selectedPhotos.size();
    }

    public void setCurrentDirectoryIndex(int currentDirectoryIndex) {
        this.currentDirectoryIndex = currentDirectoryIndex;
    }

    public List<Photo> getCurrentPhotos() {
        return photoDirectories.get(currentDirectoryIndex).getPhotos();
    }

    public List<String> getCurrentPhotoPaths() {
        List<String> currentPhotoPaths = new ArrayList<>(getCurrentPhotos().size());
        for (Photo photo : getCurrentPhotos()) {
            currentPhotoPaths.add(photo.getPath());
        }
        return currentPhotoPaths;
    }

    @Override
    public List<Photo> getSelectedPhotos() {
        return selectedPhotos;
    }

}