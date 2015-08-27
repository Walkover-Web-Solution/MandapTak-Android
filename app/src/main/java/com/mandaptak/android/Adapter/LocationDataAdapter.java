package com.mandaptak.android.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.mandaptak.android.Models.LocationPreference;
import com.mandaptak.android.Preferences.UserPreferences;
import com.mandaptak.android.R;

import java.util.ArrayList;

public class LocationDataAdapter extends ArrayAdapter<LocationPreference> {

    private final ArrayList<LocationPreference> list;
    private final UserPreferences userPreferences;

    public LocationDataAdapter(UserPreferences userPreferences, ArrayList<LocationPreference> list) {
        super(userPreferences, R.layout.location_list_row, list);
        this.userPreferences = userPreferences;
        this.list = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflator = userPreferences.getLayoutInflater();
            convertView = inflator.inflate(R.layout.location_list_row, parent, false);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView.findViewById(R.id.label);
            viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.check);
            convertView.setTag(viewHolder);
            viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    list.get(position).setIsSelected(isChecked);
                    if (isChecked)
                        userPreferences.addLocation((LocationPreference) viewHolder.checkbox.getTag());
                    else
                        userPreferences.removeLocation((LocationPreference) viewHolder.checkbox.getTag());
                }
            });
            viewHolder.checkbox.setTag(list.get(position));
        } else {
            ((ViewHolder) convertView.getTag()).checkbox.setTag(list.get(position));
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.text.setText(list.get(position).getLocationName());
        holder.checkbox.setChecked(list.get(position).isSelected());

        return convertView;
    }

    static class ViewHolder {
        protected TextView text;
        protected CheckBox checkbox;
    }
}