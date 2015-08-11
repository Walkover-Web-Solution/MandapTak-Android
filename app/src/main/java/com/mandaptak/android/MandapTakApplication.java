package com.mandaptak.android;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.parse.Parse;
import com.parse.ParseACL;

public class MandapTakApplication extends Application {
    public static ProgressDialog dialog;

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMan.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED)
            return true;//connected to data
        else if (conMan.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED)
            return true; //connected to wifi
        return false;
    }

    public static void show_PDialog(Context con, String message) {
        dialog = new ProgressDialog(con, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "Uj7WryNjRHDQ0O3j8HiyoFfriHV8blt2iUrJkCN0", "F8ySjsm3T6Ur4xOnIkgkS2I7aSFyfBsa2e4pBedN");
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(false);
        ParseACL.setDefaultACL(defaultACL, true);
    }
}