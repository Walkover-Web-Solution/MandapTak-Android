package com.mandaptak.android.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CommonUtils {

  public ProgressDialog dialog;
  private Context context;

  public CommonUtils(Context context) {
    this.context = context;
  }

  public boolean isNetworkAvailable() {
    ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (conMan.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED)
      return true;//connected to data
    else if (conMan.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED)
      return true; //connected to wifi
    else {
      context.startActivity(new Intent(context, InternetConnectionError.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
      return false;
    }
  }

  public void show_PDialog(String message) {
    try {
      if (dialog != null) {
        dialog.dismiss();
      }
      dialog = new ProgressDialog(context, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
      dialog.setMessage(message);
      dialog.setCancelable(true);
      dialog.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
