package com.mandaptak.android;

import com.mandaptak.android.EditProfile.DetailsProfileFragment;
import com.mandaptak.android.Utils.AppComponent;

import dagger.Component;

@PerDetailsProfileFragment
@Component(dependencies = AppComponent.class, modules = DetailsProfileFragmentModule.class)
public interface DetailsProfileFragmentComponent {
  void inject(DetailsProfileFragment detailsProfileFragment);
}
