package com.mandaptak.android.EditProfile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mandaptak.android.R;

import java.util.ArrayList;

import me.iwf.photopicker.entity.ParseNameModel;

public class DataAdapter extends ArrayAdapter<ParseNameModel> {
  ArrayList<ParseNameModel> models;

  public DataAdapter(Context context, ArrayList<ParseNameModel> models) {
    super(context, R.layout.location_list_item, models);
    this.models = models;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder;
    if (convertView == null) {
      viewHolder = new ViewHolder();
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.location_list_item, parent, false);
      viewHolder.descriptionTV = (TextView) convertView.findViewById(android.R.id.text1);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }
    viewHolder.descriptionTV.setText(models.get(position).getName());
    return convertView;
  }

  class ViewHolder {
    TextView descriptionTV;
  }
}
