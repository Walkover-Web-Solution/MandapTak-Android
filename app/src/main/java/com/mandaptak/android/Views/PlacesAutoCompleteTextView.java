package com.mandaptak.android.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/**
 * Created by arpit on 06/08/15.
 */
public class PlacesAutoCompleteTextView extends AutoCompleteTextView {

    public PlacesAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    /**
//     * Returns the place description corresponding to the selected item
//     */
//    @Override
//    protected CharSequence convertSelectionToString(Object selectedItem) {
//        /** Each item in the autocompetetextview suggestion list is a Location object */
//        Location location = (Location) selectedItem;
//        return location.getDescription();
//    }
}
