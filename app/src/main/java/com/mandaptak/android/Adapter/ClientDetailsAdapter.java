package com.mandaptak.android.Adapter;

import android.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mandaptak.android.Agent.ClientDetailActivity;
import com.mandaptak.android.Models.PermissionModel;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.mandaptak.android.Views.ExtendedEditText;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ClientDetailsAdapter extends BaseAdapter {
  ClientDetailActivity activity;
  ArrayList<PermissionModel> list;
  Common mApp;

  public ClientDetailsAdapter(ClientDetailActivity activity, ArrayList<PermissionModel> paramArrayList) {
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
      paramView = LayoutInflater.from(activity).inflate(R.layout.client_detail_item, null);
      viewholder.tvNumber = (TextView) paramView.findViewById(R.id.number);
      viewholder.tvRelation = ((TextView) paramView.findViewById(R.id.relation));
      viewholder.tvDate = (TextView) paramView.findViewById(R.id.date);
      viewholder.btnMore = (ImageView) paramView.findViewById(R.id.more);
      paramView.setTag(viewholder);
    } else {
      viewholder = (ViewHolder) paramView.getTag();
    }
    final PermissionModel permissionModel = list.get(paramInt);
    viewholder.tvNumber.setText("+91" + permissionModel.getNumber());
    viewholder.tvRelation.setText(permissionModel.getRelation());
    viewholder.tvDate.setText(permissionModel.getDate());
    if (permissionModel.isCurrentUser()) {
      viewholder.tvRelation.setTextColor(activity.getResources().getColor(R.color.red_500));
    }
    viewholder.btnMore.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        editPermission(permissionModel);
      }
    });
    return paramView;
  }

  static class ViewHolder {
    private TextView tvNumber;
    private TextView tvRelation;
    private TextView tvDate;
    private ImageView btnMore;
  }

  void editPermission(final PermissionModel permissionModel) {
    if (mApp.isNetworkAvailable(activity)) {
      final View permissionDialog = View.inflate(activity, R.layout.add_permission_dialog, null);
      final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
      alertDialog.setView(permissionDialog);
      final ExtendedEditText etNumber = (ExtendedEditText) permissionDialog.findViewById(R.id.number);
      AppCompatButton giveButton = (AppCompatButton) permissionDialog.findViewById(R.id.give_button);
      giveButton.setText("Update Edit Permission");
      final Spinner relations = (Spinner) permissionDialog.findViewById(R.id.relations);
      etNumber.setPrefix("+91");
      etNumber.setText(permissionModel.getNumber());
      List<String> relationList = new LinkedList<>(Arrays.asList(activity.getResources().getStringArray(R.array.relation_array)));
      if (!permissionModel.isCurrentUser()) {
        relationList.remove(permissionModel.getRelation());
      }
      relations.setAdapter(new ArrayAdapter<>(activity, R.layout.location_list_item, relationList));
      giveButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          final String mobileNumber = etNumber.getText().toString();
          Boolean duplicateNumber = false;
          for (PermissionModel model : list
              ) {

            if (!permissionModel.equals(model) && model.getNumber().equals(mobileNumber)) {
              duplicateNumber = true;
            }
          }
          if (!mobileNumber.equals("")) {
            if (mobileNumber.length() == 10 && !duplicateNumber) {
              alertDialog.dismiss();
              if (mApp.isNetworkAvailable(activity)) {
                mApp.show_PDialog(activity, "Modifying Permission..");
                HashMap<String, Object> params = new HashMap<>();
                params.put("userId", permissionModel.getUserId());
                params.put("profileId", permissionModel.getProfileId());
                params.put("relation", relations.getSelectedItem());
                params.put("username", mobileNumber);
                ParseCloud.callFunctionInBackground("changeUserNameAndUserRelation", params, new FunctionCallback<Object>() {
                  @Override
                  public void done(Object o, ParseException e) {
                    mApp.dialog.dismiss();
                    if (e == null) {
                      mApp.showToast(activity, "Permission Modified");
                      activity.getExistingPermissions();
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
}