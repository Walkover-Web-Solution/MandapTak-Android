package com.mandaptak.android.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.mandaptak.android.Models.Degree;
import com.mandaptak.android.Preferences.UserPreferences;
import com.mandaptak.android.R;

import java.util.ArrayList;

public class DegreeDataAdapter extends ArrayAdapter<Degree> {

    private final ArrayList<Degree> list;
    private final UserPreferences userPreferences;

    public DegreeDataAdapter(UserPreferences userPreferences, ArrayList<Degree> list) {
        super(userPreferences, R.layout.location_list_row, list);
        this.userPreferences = userPreferences;
        this.list = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.location_list_row, null);
            viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView.findViewById(R.id.label);
            viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.check);
            viewHolder.checkbox.setTag(list.get(position));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.text.setText(list.get(position).getDegreeName());
        viewHolder.checkbox.setChecked(list.get(position).getSelected());
        viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                list.get(position).setSelected(isChecked);
                if (isChecked)
                    userPreferences.addDegree(list.get(position));
                else
                    userPreferences.removeDegree(list.get(position));
            }
        });
        return convertView;
    }

    static class ViewHolder {
        protected TextView text;
        protected CheckBox checkbox;
    }
}