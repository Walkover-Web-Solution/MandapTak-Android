package com.mandaptak.android.EditProfile;

import android.content.Context;
import android.support.v7.app.ActionBar;

import com.mandaptak.android.Utils.Common;
import com.mandaptak.android.Views.MyViewPager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("deprecation")
@RunWith(MockitoJUnitRunner.class)
public class EditProfileActivityTest {

  private EditProfileActivity editProfileActivity;

  @Mock
  private Context context;

  @Before
  public void setUp() throws Exception {
    editProfileActivity = new EditProfileActivity();
    editProfileActivity.context = context;
  }

  @Test
  public void shouldShowToastMessageToSaveProfileToGoBack() throws Exception {
    String message = "Save profile to go back.";
    Common mockMApp = mock(Common.class);
    editProfileActivity.mApp = mockMApp;
    editProfileActivity.onBackPressed();
    verify(mockMApp).showToast(context, message);
  }

  @Test
  public void shouldSetCurrentItemOnTabSelection() throws Exception {
    Integer currentPosition = 1;
    ActionBar.Tab tab = mock(ActionBar.Tab.class);
    when(tab.getPosition()).thenReturn(currentPosition);
    MyViewPager mockViewPager = mock(MyViewPager.class);
    editProfileActivity.mViewPager = mockViewPager;
    editProfileActivity.onTabSelected(tab, null);
    verify(mockViewPager).setCurrentItem(currentPosition, false);
  }
}