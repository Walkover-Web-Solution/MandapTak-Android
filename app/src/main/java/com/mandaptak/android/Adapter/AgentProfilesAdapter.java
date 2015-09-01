package com.mandaptak.android.Adapter;

import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mandaptak.android.Agent.AgentActivity;
import com.mandaptak.android.Models.AgentProfileModel;
import com.mandaptak.android.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AgentProfilesAdapter extends BaseAdapter {
    AgentActivity activity;
    ArrayList<AgentProfileModel> list;

    public AgentProfilesAdapter(AgentActivity activity, ArrayList<AgentProfileModel> paramArrayList) {
        this.list = paramArrayList;
        this.activity = activity;
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
            paramView = LayoutInflater.from(activity).inflate(R.layout.agent_profile_item, null);
            viewholder.name = (TextView) paramView.findViewById(R.id.name);
            viewholder.date = ((TextView) paramView.findViewById(R.id.create_date));
            viewholder.status = (TextView) paramView.findViewById(R.id.status);
            viewholder.image = (ImageView) paramView.findViewById(R.id.thumbnail);
            viewholder.more = (ImageView) paramView.findViewById(R.id.more);
            paramView.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) paramView.getTag();
        }
        AgentProfileModel agentProfileModel = list.get(paramInt);
        try {
            viewholder.date.setText("Updated On: " + agentProfileModel.getCreateDate());
            if (!agentProfileModel.isComplete() && agentProfileModel.isActive()) {
                viewholder.status.setTextColor(activity.getResources().getColor(R.color.yellow_700));
                viewholder.status.setText("Profile Incomplete");
            } else if (!agentProfileModel.isActive()) {
                viewholder.status.setTextColor(activity.getResources().getColor(R.color.red_500));
                viewholder.status.setText("Deactive");
            } else {
                viewholder.status.setTextColor(activity.getResources().getColor(R.color.green_500));
                viewholder.status.setText("Active");
            }
            if (agentProfileModel.isComplete()) {
                viewholder.name.setText(agentProfileModel.getName());
                Picasso.with(activity)
                        .load(agentProfileModel.getImageUrl())
                        .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                        .error(R.drawable.com_facebook_profile_picture_blank_square)
                        .into(viewholder.image);
            } else {
                viewholder.name.setText("+91" + agentProfileModel.getName());
                Picasso.with(activity)
                        .load(Uri.parse(agentProfileModel.getImageUrl()))
                        .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                        .error(R.drawable.com_facebook_profile_picture_blank_square)
                        .into(viewholder.image);
            }
            if (agentProfileModel.isActive()) {
                viewholder.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popup = new PopupMenu(activity, view);
                        popup.getMenuInflater().inflate(R.menu.agent_pop_item_deactive_menu, popup.getMenu());
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                Toast.makeText(activity, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                                return true;
                            }
                        });
                        popup.show();
                    }
                });
            } else {
                viewholder.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popup = new PopupMenu(activity, view);
                        popup.getMenuInflater().inflate(R.menu.agent_pop_item_active_menu, popup.getMenu());
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                Toast.makeText(activity, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                                return true;
                            }
                        });
                        popup.show();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return paramView;
    }

    static class ViewHolder {
        private TextView name;
        private TextView date;
        private TextView status;
        private ImageView image;
        private ImageView more;
    }
}