package com.mandaptak.android.Adapter;

import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mandaptak.android.Agent.ClientDetailActivity;
import com.mandaptak.android.Models.PermissionModel;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;

import java.util.ArrayList;

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
    viewholder.tvCredits.setText("Balance: " + permissionModel.getBalance() + " Credits");
    viewholder.btnMore.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        PopupMenu popup = new PopupMenu(activity, view);
        popup.getMenuInflater().inflate(R.menu.agent_pop_item_active_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
          public boolean onMenuItemClick(MenuItem item) {

            return true;
          }
        });
        popup.show();
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
}