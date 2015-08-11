package com.mandaptak.android;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.parse.Parse;

public class MandapTakApplication extends Application {
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMan.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED)
            return true;//connected to data
        else if (conMan.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED)
            return true; //connected to wifi
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "ZcJYUNXpXEMX7Dv14FUYuT1nzHcEqsfjBvPp25K3", "kvyRKd4ZZiEfl9S8MVZasaMAfAnB5Yc2TusZObvl");
    }
}