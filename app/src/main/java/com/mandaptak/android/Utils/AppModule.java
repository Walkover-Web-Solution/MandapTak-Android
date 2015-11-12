package com.mandaptak.android.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

  private Common common;

  public AppModule(Common common) {
    this.common = common;
  }

  @Provides
  @Singleton
  public Common application() {
    return this.common;
  }

  @Provides
  @Singleton
  public Context context() {
    return this.application();
  }

  @Provides
  @Singleton
  SharedPreferences provideSharedPreferences() {
    return common.getSharedPreferences(Prefs.MANDAPTAK_SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
  }

}
