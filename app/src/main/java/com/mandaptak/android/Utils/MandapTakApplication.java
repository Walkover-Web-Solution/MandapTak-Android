//package com.mandaptak.android.Utils;
//
//import android.app.Application;
//import android.content.Context;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//
//import com.facebook.FacebookSdk;
//import com.parse.Parse;
//import com.parse.ParseACL;
//import com.parse.ParseCrashReporting;
//import com.parse.ParseUser;
//
//public class MandapTakApplication extends Application {
//    public static boolean isNetworkAvailable(Context context) {
//        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (conMan.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED)
//            return true;//connected to data
//        else if (conMan.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED)
//            return true; //connected to wifi
//        return false;
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        ParseCrashReporting.enable(this);
//        // Enable Local Datastore.
//        Parse.enableLocalDatastore(this);
//        Parse.initialize(this);
//       // Parse.initialize(this, "6VKC4u0ysZmMG3YiOml8Otcygm75P0IUg11sqosQ", "79eXBP3GueXojoG1uHIuUzl4OBJ6cuLB5vIpw6Tl");
//        ParseUser.enableAutomaticUser();
//        ParseACL defaultACL = new ParseACL();
//        // Optionally enable public read access.
//        // defaultACL.setPublicReadAccess(true);
//        ParseACL.setDefaultACL(defaultACL, true);
//    }
//}