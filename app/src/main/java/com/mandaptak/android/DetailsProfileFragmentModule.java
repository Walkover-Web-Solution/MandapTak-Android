package com.mandaptak.android;

import com.mandaptak.android.EditProfile.DetailsProfileFragment;
import com.mandaptak.android.Profile.Religion.ReligionController;

import dagger.Module;
import dagger.Provides;

@Module
public class DetailsProfileFragmentModule {

  private DetailsProfileFragment detailsProfileFragment;


  public DetailsProfileFragmentModule(DetailsProfileFragment detailsProfileFragment) {
    this.detailsProfileFragment = detailsProfileFragment;
  }

  @Provides
  @PerDetailsProfileFragment
  ReligionController provideReligionController() {
    return detailsProfileFragment;
  }
}
