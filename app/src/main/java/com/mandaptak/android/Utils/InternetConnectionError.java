package com.mandaptak.android.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.mandaptak.android.R;
import com.mandaptak.android.Splash.SplashScreen;

public class InternetConnectionError extends AppCompatActivity {
  AppCompatButton retry;
  Context context;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_internet_connection_error);
    context = this;
    retry = (AppCompatButton) findViewById(R.id.retry);
    try {
      getSupportActionBar().hide();
    } catch (Exception e) {
      e.printStackTrace();
    }
    retry.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (isNetworkAvailable(context)) {
          startActivity(new Intent(InternetConnectionError.this, SplashScreen.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_CLEAR_TOP & Intent.FLAG_ACTIVITY_NO_ANIMATION));
          InternetConnectionError.this.finish();
        }
      }
    });
  }

  private boolean isNetworkAvailable(Context context) {
    ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (conMan.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED)
      return true;//connected to data
    else if (conMan.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED)
      return true; //connected to wifi
    return false;
  }

  @Override
  public void onBackPressed() {
  }
}
