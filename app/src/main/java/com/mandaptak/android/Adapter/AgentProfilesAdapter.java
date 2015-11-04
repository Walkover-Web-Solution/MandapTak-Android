package com.mandaptak.android.Adapter;

import android.app.AlertDialog;
import android.net.Uri;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mandaptak.android.Agent.AgentActivity;
import com.mandaptak.android.Models.AgentProfileModel;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.mandaptak.android.Views.ExtendedEditText;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class AgentProfilesAdapter extends BaseAdapter {
  AgentActivity activity;
  ArrayList<AgentProfileModel> list;
  Common mApp;

  public AgentProfilesAdapter(AgentActivity activity, ArrayList<AgentProfileModel> paramArrayList) {
    this.list = paramArrayList;
    this.activity = activity;
    mApp = (Common) activity.getApplicationContext();
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
      paramView = LayoutInflater.from(activity).inflate(R.layout.agent_profile_item, paramViewGroup, false);
      viewholder.name = (TextView) paramView.findViewById(R.id.name);
      viewholder.date = ((TextView) paramView.findViewById(R.id.create_date));
      viewholder.status = (TextView) paramView.findViewById(R.id.status);
      viewholder.image = (ImageView) paramView.findViewById(R.id.thumbnail);
      viewholder.more = (ImageView) paramView.findViewById(R.id.more);
      viewholder.number = (TextView) paramView.findViewById(R.id.number);
      paramView.setTag(viewholder);
    } else {
      viewholder = (ViewHolder) paramView.getTag();
    }
    try {
      final AgentProfileModel agentProfileModel = list.get(paramInt);
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
      viewholder.number.setText(agentProfileModel.getNumber());
      if (agentProfileModel.isComplete()) {
        viewholder.name.setText(agentProfileModel.getName());
        Picasso.with(activity)
            .load(agentProfileModel.getImageUrl())
            .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
            .error(R.drawable.com_facebook_profile_picture_blank_square)
            .into(viewholder.image);
      } else {
        viewholder.name.setText("No Name");
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
                if (item.getItemId() == R.id.status) {
                  mApp.show_PDialog(activity, "Deactivating Profile..");
                  ParseObject parseObject = agentProfileModel.getProfileObject();
                  parseObject.put("isActive", false);
                  parseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                      mApp.dialog.dismiss();
                      if (e == null) {
                        mApp.showToast(activity, "Profile Deactivated");
                        activity.resetProfileData();
                        activity.getProfiles();
                      } else {
                        mApp.showToast(activity, e.getMessage());
                      }
                    }
                  });
                } else if (item.getItemId() == R.id.permission) {
                  givePermission(agentProfileModel.getProfileObject().getObjectId());
                }
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
                if (item.getItemId() == R.id.status) {
                  mApp.show_PDialog(activity, "Activating Profile..");
                  ParseObject parseObject = agentProfileModel.getProfileObject();
                  parseObject.put("isActive", true);
                  parseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                      mApp.dialog.dismiss();
                      if (e == null) {
                        mApp.showToast(activity, "Profile Activated");
                        activity.resetProfileData();
                        activity.getProfiles();
                      } else {
                        mApp.showToast(activity, e.getMessage());
                      }
                    }
                  });
                } else if (item.getItemId() == R.id.permission) {
                  givePermission(agentProfileModel.getProfileObject().getObjectId());
                }
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

  void givePermission(final String profileId) {
    if (mApp.isNetworkAvailable(activity)) {
      final View permissionDialog = View.inflate(activity, R.layout.add_permission_dialog, null);
      final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
      alertDialog.setView(permissionDialog);
      final ExtendedEditText etNumber = (ExtendedEditText) permissionDialog.findViewById(R.id.number);
      AppCompatButton giveButton = (AppCompatButton) permissionDialog.findViewById(R.id.give_button);
      final Spinner relations = (Spinner) permissionDialog.findViewById(R.id.relations);
      etNumber.setPrefix("+91");
      relations.setAdapter(ArrayAdapter.createFromResource(activity,
          R.array.relation_array, R.layout.location_list_item));
      giveButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          String mobileNumber = etNumber.getText().toString();
          if (!mobileNumber.equals("")) {
            if (mobileNumber.length() == 10) {
              alertDialog.dismiss();
              if (mApp.isNetworkAvailable(activity)) {
                mApp.show_PDialog(activity, "Giving Permission..");
                HashMap<String, Object> params = new HashMap<>();
                params.put("mobile", mobileNumber);
                params.put("profileId", profileId);
                params.put("relation", relations.getSelectedItem());

                ParseCloud.callFunctionInBackground("givePermissiontoNewUser", params, new FunctionCallback<Object>() {
                  @Override
                  public void done(Object o, ParseException e) {
                    mApp.dialog.dismiss();
                    if (e == null) {
                      mApp.showToast(activity, "Permission Given");
                    } else {
                      e.printStackTrace();
                      mApp.showToast(activity, e.getMessage());
                    }
                  }
                });
              }
            } else {
              mApp.showToast(activity, "Invalid Mobile Number");
            }
          } else {
            mApp.showToast(activity, "Enter Mobile Number");
          }
        }
      });
      alertDialog.show();
    } else {
      mApp.showToast(activity, "Internet connection required");
    }
  }

  static class ViewHolder {
    private TextView name;
    private TextView date;
    private TextView status;
    private ImageView image;
    private ImageView more;
    private TextView number;
  }
}