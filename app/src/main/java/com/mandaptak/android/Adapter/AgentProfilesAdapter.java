package com.mandaptak.android.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mandaptak.android.Models.AgentProfileModel;
import com.mandaptak.android.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AgentProfilesAdapter extends BaseAdapter {
    Context context;
    ArrayList<AgentProfileModel> list;

    public AgentProfilesAdapter(Context context, ArrayList<AgentProfileModel> paramArrayList) {
        this.list = paramArrayList;
        this.context = context;
    }

    public int getCount() {

        return this.list.size();
    }

    public Object getItem(int paramInt) {
        return this.list.get(paramInt);
    }

    public long getItemId(int paramInt) {
        return paramInt;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
        ViewHolder viewholder;

        if (paramView == null) {
            viewholder = new ViewHolder();
            paramView = LayoutInflater.from(context).inflate(R.layout.agent_profile_item, null);
            viewholder.name = (TextView) paramView.findViewById(R.id.name);
            viewholder.date = ((TextView) paramView.findViewById(R.id.date));
            viewholder.status = (TextView) paramView.findViewById(R.id.status);
            viewholder.image = (ImageView) paramView.findViewById(R.id.image);
            paramView.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) paramView.getTag();
        }
        AgentProfileModel agentProfileModel = list.get(paramInt);

        viewholder.name.setText(agentProfileModel.getName());
        viewholder.date.setText("Uploaded On: " + agentProfileModel.getCreateDate());
        if (agentProfileModel.isActive()) {
            viewholder.status.setTextColor(context.getResources().getColor(R.color.green_500));
            viewholder.status.setText("Active");
        } else {
            viewholder.status.setTextColor(context.getResources().getColor(R.color.red_500));
            viewholder.status.setText("Deactivated");
        }
        Picasso.with(context)
                .load(agentProfileModel.getImageUrl())
                .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                .error(R.drawable.com_facebook_profile_picture_blank_square)
                .into(viewholder.image);
        return paramView;
    }

    static class ViewHolder {
        private TextView name;
        private TextView date;
        private TextView status;
        private ImageView image;
    }
}