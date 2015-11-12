package com.mandaptak.android.Utils;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
  void inject(Common application);

  Common application();

  Context context();
}
