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
import java.util.HashMap;

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
      viewholder.tvCredits = ((TextView) paramView.findViewById(R.id.credits));
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
    //  viewholder.tvCredits.setText("Balance: " + permissionModel.getBalance() + " Credits");
    viewholder.btnMore.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        editPermission(permissionModel.getProfileId());
      }
    });
    return paramView;
  }

  static class ViewHolder {
    private TextView tvNumber;
    private TextView tvRelation;
    private TextView tvDate;
    private ImageView btnMore;
    private TextView tvCredits;
  }

  void editPermission(final String profileId) {
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

//                ParseCloud.callFunctionInBackground("givePermissiontoNewUser", params, new FunctionCallback<Object>() {
//                  @Override
//                  public void done(Object o, ParseException e) {
//                    mApp.dialog.dismiss();
//                    if (e == null) {
//                      mApp.showToast(activity, "Permission Given");
//                    } else {
//                      e.printStackTrace();
//                      mApp.showToast(activity, e.getMessage());
//                    }
//                  }
//                });
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