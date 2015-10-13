package com.mandaptak.android.Utils;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.github.johnpersano.supertoasts.SuperToast;
import com.layer.sdk.exceptions.LayerException;
import com.mandaptak.android.Layer.LayerCallbacks;
import com.mandaptak.android.Layer.LayerImpl;
import com.mandaptak.android.Matches.AtlasIdentityProvider;
import com.mandaptak.android.R;
import com.mandaptak.android.Splash.SplashScreen;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import main.java.com.mindscapehq.android.raygun4android.RaygunClient;
import main.java.com.mindscapehq.android.raygun4android.messages.RaygunUserInfo;

public class Common extends Application implements LayerCallbacks {
  public static AtlasIdentityProvider identityProvider;
  public ProgressDialog dialog;


  public static AtlasIdentityProvider getIdentityProvider() {
    return identityProvider;
  }

  /**
   * Get a file path from a Uri. This will get the the path for Storage Access
   * Framework Documents, as well as the _data field for the MediaStore and
   * other file-based ContentProviders.
   *
   * @param context The context.
   * @param uri     The Uri to query.
   */
  @TargetApi(Build.VERSION_CODES.KITKAT)
  public String getPath(final Context context, final Uri uri) {
    final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    // DocumentProvider
    if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
      // ExternalStorageProvider
      if (isExternalStorageDocument(uri)) {
        Log.e("", "isExternalStorageDocument");
        final String docId = DocumentsContract.getDocumentId(uri);
        final String[] split = docId.split(":");
        final String type = split[0];

        if ("primary".equalsIgnoreCase(type)) {
          return Environment.getExternalStorageDirectory() + "/" + split[1];
        }
      }
      // DownloadsProvider
      else if (isDownloadsDocument(uri)) {
        Log.e("", "isDownloadsDocument");
        final String id = DocumentsContract.getDocumentId(uri);
        final Uri contentUri = ContentUris.withAppendedId(
            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
        return getDataColumn(context, contentUri, null, null);
      }
      // MediaProvider
      else if (isMediaDocument(uri)) {
        Log.e("", "isMediaDocument");
        final String docId = DocumentsContract.getDocumentId(uri);
        final String[] split = docId.split(":");
        final String type = split[0];

        Uri contentUri = null;
        if ("image".equals(type)) {
          contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if ("video".equals(type)) {
          contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else if ("audio".equals(type)) {
          contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }

        final String selection = "_id=?";
        final String[] selectionArgs = new String[]{
            split[1]
        };

        return getDataColumn(context, contentUri, selection, selectionArgs);
      }
    }
    // MediaStore (and general)
    else if ("content".equalsIgnoreCase(uri.getScheme())) {
      Log.e("", "content");
      return getDataColumn(context, uri, null, null);
    }
    // File
    else if ("file".equalsIgnoreCase(uri.getScheme())) {
      Log.e("", "file");
      return uri.getPath();
    }
    return null;
  }

  /**
   * Get the value of the data column for this Uri. This is useful for
   * MediaStore Uris, and other file-based ContentProviders.
   *
   * @param context       The context.
   * @param uri           The Uri to query.
   * @param selection     (Optional) Filter used in the query.
   * @param selectionArgs (Optional) Selection arguments used in the query.
   * @return The value of the _data column, which is typically a file path.
   */

  public String getDataColumn(Context context, Uri uri, String selection,
                              String[] selectionArgs) {

    Cursor cursor = null;
    final String column = "_data";
    final String[] projection = {
        column
    };

    try {
      cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
          null);
      if (cursor != null && cursor.moveToFirst()) {
        final int column_index = cursor.getColumnIndexOrThrow(column);
        return cursor.getString(column_index);
      }
    } finally {
      if (cursor != null)
        cursor.close();
    }
    return null;
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is ExternalStorageProvider.
   */
  public boolean isExternalStorageDocument(Uri uri) {
    return "com.android.externalstorage.documents".equals(uri.getAuthority());
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is DownloadsProvider.
   */
  public boolean isDownloadsDocument(Uri uri) {
    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is MediaProvider.
   */
  public boolean isMediaDocument(Uri uri) {
    return "com.android.providers.media.documents".equals(uri.getAuthority());
  }

  public boolean isNetworkAvailable(Context context) {
    ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (conMan.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED)
      return true;//connected to data
    else if (conMan.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED)
      return true; //connected to wifi
    else {
      startActivity(new Intent(context, InternetConnectionError.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
      return false;
    }
  }

  public void show_PDialog(Context con, String message) {
    try {
      if (dialog != null) {
        dialog.dismiss();
      }
      dialog = new ProgressDialog(con, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
      dialog.setMessage(message);
      dialog.setCancelable(true);
      dialog.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  public void show_PDialog(Context con, String message, Boolean cancelable) {
    try {
      if (dialog != null) {
        dialog.dismiss();
      }
      dialog = new ProgressDialog(con, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
      dialog.setMessage(message);
      dialog.setCancelable(cancelable);
      dialog.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String getNumber() {
    TelephonyManager telemamanger = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    String getSimNumber = telemamanger.getLine1Number();
    if (getSimNumber != null) {
      getSimNumber = getSimNumber.trim();
      if (!getSimNumber.equals(""))
        getSimNumber = getSimNumber.replace("+91", "");
    }
    return getSimNumber;
  }

  public void updateDialogProgress(int progress, String message) {
    dialog.setProgress(progress);
    dialog.setMessage(message);
  }

  public void showToast(Context context, String message) {
    SuperToast superToast = new SuperToast(context);
    SuperToast.cancelAllSuperToasts();
    superToast.setAnimations(SuperToast.Animations.POPUP);
    superToast.setDuration(SuperToast.Duration.SHORT);
    superToast.setText(" " + message);
    superToast.setTextColor(context.getResources().getColor(R.color.primary));
    superToast.setBackground(R.drawable.border_toast);
    superToast.show();
  }

  @Override
  public void onCreate() {
    super.onCreate();
    // testing
    Parse.initialize(this, "Uj7WryNjRHDQ0O3j8HiyoFfriHV8blt2iUrJkCN0", "F8ySjsm3T6Ur4xOnIkgkS2I7aSFyfBsa2e4pBedN");//test
    //production
//    Parse.initialize(this, "XQA3RRfnMim2IyheuTBRkKZNRurkTNhxEiqa8Bs8", "fsdwA6pXp3SYXVk27uf3loRUziyrb7Oh0sMluSlo");
    FacebookSdk.sdkInitialize(getApplicationContext());
    ParseACL defaultACL = new ParseACL();
    defaultACL.setPublicReadAccess(true);
    defaultACL.setPublicWriteAccess(false);
    ParseACL.setDefaultACL(defaultACL, true);
    try {
      PackageInfo info = getPackageManager().getPackageInfo(
          "com.mandaptak.android",
          PackageManager.GET_SIGNATURES);
      for (Signature signature : info.signatures) {
        MessageDigest md = MessageDigest.getInstance("SHA");
        md.update(signature.toByteArray());
        Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
      }
    } catch (PackageManager.NameNotFoundException e) {

    } catch (NoSuchAlgorithmException e) {

    }
    //Initializes and connects the LayerClient if it hasn't been created already
    LayerImpl.initialize(getApplicationContext());
    //Registers the activity so callbacks are executed on the correct class
    LayerImpl.setContext(this);
    RaygunUserInfo user = new RaygunUserInfo();
    ParseUser parseUser = ParseUser.getCurrentUser();
    if (parseUser != null) {
      user.FirstName = parseUser.getUsername();
      user.Uuid = parseUser.getObjectId();
      RaygunClient.SetUser(user);
    }
    identityProvider = new AtlasIdentityProvider(this);
    // Setup handler for uncaught exceptions.
    Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
      @Override
      public void uncaughtException(Thread thread, Throwable e) {
        e.printStackTrace();
        Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
        startActivity(intent);
        System.exit(1); // kill off the crashed app
      }
    });

  }

  public String numberToWords(long number) {
    if (number == 0) {
      return "zero";
    }
    if (number < 0) {
      return "minus " + numberToWords(Math.abs(number));
    }
    String words = "";
    if ((number / 10000000) > 0) {
      words += numberToWords(number / 10000000) + " Crore ";
      number %= 10000000;
    }
    if ((number / 100000) > 0) {
      words += numberToWords(number / 100000) + " Lakh ";
      number %= 100000;
    }
    if ((number / 1000) > 0) {
      words += numberToWords(number / 1000) + " Thousand ";
      number %= 1000;
    }
    if ((number / 100) > 0) {
      words += numberToWords(number / 100) + " Hundred ";
      number %= 100;
    }
    if (number > 0) {
      if (!words.equals("")) {
        words += "and ";
      }
      if (number < 20) {
        words += number;
      } else {
        words += (number);
        if ((number % 10) > 0) {
          words += "-" + (number % 10);
        }
      }
    }
    return words;
  }

  @Override
  public void onLayerConnected() {

  }

  @Override
  public void onLayerDisconnected() {

  }

  @Override
  public void onLayerConnectionError(LayerException e) {

  }

  @Override
  public void onUserAuthenticated(String id) {

  }

  @Override
  public void onUserAuthenticatedError(LayerException e) {

  }

  @Override
  public void onUserDeauthenticated() {

  }

}