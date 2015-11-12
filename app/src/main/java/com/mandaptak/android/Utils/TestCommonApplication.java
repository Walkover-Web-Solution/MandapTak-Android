package com.mandaptak.android.Utils;

import android.app.Application;

import com.layer.sdk.exceptions.LayerException;
import com.mandaptak.android.Layer.LayerCallbacks;

public class TestCommonApplication extends Application implements LayerCallbacks {
  @Override
  public void onCreate() {
    super.onCreate();
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