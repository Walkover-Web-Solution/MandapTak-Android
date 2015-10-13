package com.mandaptak.android.EditProfile;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.mandaptak.android.Main.MainActivity;
import com.mandaptak.android.Preferences.UserPreferences;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.mandaptak.android.Views.MyViewPager;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import me.iwf.photopicker.entity.ParseNameModel;
import me.iwf.photopicker.entity.Profile;
import me.iwf.photopicker.utils.Prefs;

public class EditProfileActivity extends AppCompatActivity implements ActionBar.TabListener {

  SectionsPagerAdapter mSectionsPagerAdapter;
  MyViewPager mViewPager;
  FloatingActionButton skipButton;
  BasicProfileFragment basicProfileFragment;
  DetailsProfileFragment detailsProfileFragment;
  QualificationEditProfileFragment qualificationEditProfileFragment;
  FinalEditProfileFragment finalEditProfileFragment;
  boolean isFirstStart;
  Common mApp;
  Context context;
  Profile profile = new Profile();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_profile);
    context = this;
    mApp = (Common) getApplicationContext();
    if (getIntent() != null) {
      if (getIntent().hasExtra("firstStart")) {
        isFirstStart = getIntent().getBooleanExtra("firstStart", false);
      }
    }
    init();
    if (Prefs.getProfile(context) != null) {
      Log.e("", "Profile Found in Shared Prefs");
    } else {
      try {
        getParseData();
      } catch (Exception ignored) {
      }
    }
  }

  @Override
  public void onBackPressed() {
    mApp.showToast(context, "Save profile to go back.");
  }

  @Override
  public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    mViewPager.setCurrentItem(tab.getPosition(), false);
  }

  @Override
  public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
  }

  @Override
  public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
  }

  void init() {
    final ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
      actionBar.setDisplayHomeAsUpEnabled(false);
      actionBar.setDisplayShowHomeEnabled(true);
      actionBar.setIcon(R.drawable.ic_mode_edit_white);
      actionBar.setTitle("    Edit Profile");
    }
    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

    // Set up the ViewPager with the sections adapter.
    mViewPager = (MyViewPager) findViewById(R.id.pager);
    skipButton = (FloatingActionButton) findViewById(R.id.skip_next);
    skipButton.setSize(FloatingActionButton.SIZE_MINI);
    mViewPager.setAdapter(mSectionsPagerAdapter);
    mViewPager.setOffscreenPageLimit(1);
    mViewPager.setPagingEnabled(false);
    mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
      @Override
      public void onPageSelected(int position) {
        if (actionBar != null) {
          actionBar.setSelectedNavigationItem(position);
        }
        if (position == 3) {
          skipButton.setVisibility(View.GONE);
        } else {
          skipButton.setVisibility(View.VISIBLE);
        }
      }
    });
    if (actionBar != null) {
      actionBar.addTab(
          actionBar.newTab()
              .setIcon(R.drawable.ic_tab1)
              .setTabListener(this));
      actionBar.addTab(
          actionBar.newTab()
              .setIcon(R.drawable.ic_tab2)
              .setTabListener(this));
      actionBar.addTab(
          actionBar.newTab()
              .setIcon(R.drawable.ic_tab3)
              .setTabListener(this));
      actionBar.addTab(
          actionBar.newTab()
              .setIcon(R.drawable.ic_tab4)
              .setTabListener(this));
    }

    skipButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        int index = mViewPager.getCurrentItem();
        if (index < 3) {
          mViewPager.setCurrentItem(index + 1, false);
        }
      }
    });
    basicProfileFragment = new BasicProfileFragment();
    detailsProfileFragment = new DetailsProfileFragment();
    qualificationEditProfileFragment = new QualificationEditProfileFragment();
    finalEditProfileFragment = new FinalEditProfileFragment();
  }

  void getParseData() {
    new AsyncTask<Void, Void, Void>() {
      ParseObject parseObject = null;

      @Override
      protected void onPreExecute() {
        super.onPreExecute();
        mApp.show_PDialog(context, "Loading..");
      }

      @Override
      protected Void doInBackground(Void... params) {
        try {
          ParseQuery<ParseObject> query = new ParseQuery<>("Profile");
          query.include("currentLocation.Parent.Parent");
          query.include("placeOfBirth.Parent.Parent");
          query.include("casteId");
          query.include("religionId");
          query.include("gotraId");
          query.include("education1.degreeId");
          query.include("education2.degreeId");
          query.include("education3.degreeId");
          query.include("industryId");
          parseObject = query.get(Prefs.getProfileId(context));
          if (parseObject != null) {
            String newName = parseObject.getString("name");
            String newGender = parseObject.getString("gender");
            Date tmpDOB = parseObject.getDate("dob");
            Date tmpTOB = parseObject.getDate("tob");
            if (tmpDOB != null) {
              Calendar newDOB = Calendar.getInstance();
              newDOB.setTimeZone(TimeZone.getTimeZone("UTC"));
              newDOB.setTime(tmpDOB);
              profile.setDateOfBirth(newDOB);
            }
            if (tmpTOB != null) {
              Calendar newTOB = Calendar.getInstance();
              newTOB.setTimeZone(TimeZone.getTimeZone("UTC"));
              newTOB.setTime(tmpTOB);
              profile.setTimeOfBirth(newTOB);
            }
            ParseObject newCurrentLocation = parseObject.getParseObject("currentLocation");
            ParseObject newPOB = parseObject.getParseObject("placeOfBirth");

            if (newName != null) {
              profile.setName(newName);
            }
            if (newGender != null) {
              profile.setGender(newGender);
            }
            if (newPOB != null) {
              ParseNameModel pob = new ParseNameModel();
              pob.setName(newPOB.getString("name")
                  + ", " + newPOB.getParseObject("Parent").getString("name") + ", " + newPOB.getParseObject("Parent").getParseObject("Parent").getString("name"));
              pob.setParseObjectId(newPOB.getObjectId());
              pob.setClassName("City");
              profile.setPlaceOfBirth(pob);
            }
            if (newCurrentLocation != null) {
              ParseNameModel currentLocation = new ParseNameModel();
              currentLocation.setName(newCurrentLocation.getString("name")
                  + ", " + newCurrentLocation.getParseObject("Parent").getString("name") + ", " + newCurrentLocation.getParseObject("Parent").getParseObject("Parent").getString("name"));
              currentLocation.setParseObjectId(newCurrentLocation.getObjectId());
              currentLocation.setClassName("City");
              profile.setCurrentLocation(currentLocation);
            }

            ParseObject tmpCaste, tmpReligion, tmpGotra;
            tmpReligion = parseObject.getParseObject("religionId");
            tmpCaste = parseObject.getParseObject("casteId");
            tmpGotra = parseObject.getParseObject("gotraId");
            profile.setManglik(parseObject.getInt("manglik"));
            profile.setHeight(parseObject.getInt("height"));
            profile.setWeight(parseObject.getInt("weight"));
            if (tmpReligion != null) {
              ParseNameModel newReligion = new ParseNameModel(tmpReligion.getString("name"), "Religion", tmpReligion.getObjectId());
              profile.setReligion(newReligion);
            }
            if (tmpCaste != null) {
              ParseNameModel newCaste = new ParseNameModel(tmpCaste.getString("name"), "Caste", tmpCaste.getObjectId());
              profile.setCaste(newCaste);
            }
            if (tmpGotra != null) {
              ParseNameModel newGotra = new ParseNameModel(tmpGotra.getString("name"), "Gotra", tmpGotra.getObjectId());
              profile.setGotra(newGotra);
            }
            profile.setWorkAfterMarriage(parseObject.getInt("workAfterMarriage"));
            profile.setIncome(parseObject.getLong("package"));
            String newDesignation = parseObject.getString("designation");
            String newCompany = parseObject.getString("placeOfWork");
            ParseObject newIndustry = parseObject.getParseObject("industryId");
            ParseObject tmpEdu1 = parseObject.getParseObject("education1");
            ParseObject tmpEdu2 = parseObject.getParseObject("education2");
            ParseObject tmpEdu3 = parseObject.getParseObject("education3");
            if (tmpEdu1 != null) {
              ParseNameModel newEducationDetail1 = new ParseNameModel(tmpEdu1.getString("name"), "Specialization", tmpEdu1.getObjectId(), tmpEdu1.getParseObject("degreeId").getString("name"));
              profile.setEducation1(newEducationDetail1);
            }
            if (tmpEdu2 != null) {
              ParseNameModel newEducationDetail2 = new ParseNameModel(tmpEdu2.getString("name"), "Specialization", tmpEdu2.getObjectId(), tmpEdu1.getParseObject("degreeId").getString("name"));
              profile.setEducation1(newEducationDetail2);
            }
            if (tmpEdu3 != null) {
              ParseNameModel newEducationDetail3 = new ParseNameModel(tmpEdu3.getString("name"), "Specialization", tmpEdu3.getObjectId(), tmpEdu1.getParseObject("degreeId").getString("name"));
              profile.setEducation1(newEducationDetail3);
            }
            if (newIndustry != null) {
              ParseNameModel industry = new ParseNameModel(newIndustry.getString("name"), "Industries", newIndustry.getObjectId());
              profile.setIndustry(industry);
            }
            if (newCompany != null)
              if (!newCompany.trim().equals(""))
                profile.setCompany(newCompany.trim());
            if (newDesignation != null)
              if (!newDesignation.trim().equals(""))
                profile.setDesignation(newDesignation.trim());
            Prefs.setProfile(context, profile);
          }
        } catch (Exception ignored) {
        }
        return null;
      }

      @Override
      protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mApp.dialog.dismiss();
        if (parseObject != null)
          basicProfileFragment.setUserVisibleHint(true);
      }
    }.execute();

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    if (!isFirstStart) {
      getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
      return true;
    } else {
      return super.onCreateOptionsMenu(menu);
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_done) {
      mApp.show_PDialog(context, "Saving Profile..");
      ParseQuery<ParseObject> query = new ParseQuery<>("Profile");
      query.getInBackground(Prefs.getProfileId(context), new GetCallback<ParseObject>() {
        @Override
        public void done(ParseObject parseObject, ParseException e) {
          if (e == null) {
            try {
              switch (mViewPager.getCurrentItem()) {
                case 0:
                  basicProfileFragment.saveInfo();
                  break;
                case 1:
                  detailsProfileFragment.saveInfo();
                  break;
                case 2:
                  qualificationEditProfileFragment.saveInfo();
                  break;
                case 3:
                  finalEditProfileFragment.saveInfo();
                  break;
              }
            } catch (Exception ignored) {
            }
            validateProfile(parseObject);
          } else {
            e.printStackTrace();
            mApp.dialog.dismiss();
            mApp.showToast(context, e.getMessage());
          }
        }
      });
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private boolean checkFieldsTab1(Profile profile) {
    if (profile.getName() == null) {
      return false;
    } else if (profile.getGender() == null) {
      return false;
    } else if (profile.getDateOfBirth() == null) {
      return false;
    } else if (profile.getTimeOfBirth() == null) {
      return false;
    } else if (profile.getCurrentLocation() == null) {
      return false;
    } else if (profile.getPlaceOfBirth() == null) {
      return false;
    } else {
      return true;
    }
  }

  private boolean checkFieldsTab2(Profile profile) {
    if (profile.getHeight() == 0) {
      return false;
    } else if (profile.getWeight() == 0) {
      return false;
    } else if (profile.getReligion() == null) {
      return false;
    } else if (profile.getCaste() == null) {
      return false;
    } else if (profile.getManglik() == -1) {
      return false;
    } else {
      return true;
    }
  }

  private boolean checkFieldsTab3(Profile profile) {
    if (profile.getWorkAfterMarriage() == -1) {
      return false;
    } else if (profile.getIncome() == -1) {
      return false;
    } else if (profile.getDesignation() == null) {
      return false;
    } else if (profile.getPlaceOfBirth() == null) {
      return false;
    } else if (profile.getIndustry() == null) {
      return false;
    } else if (profile.getEducation1() == null) {
      return false;
    } else {
      return true;
    }
  }

  private boolean checkFieldsTab4(ParseObject parseObject) {
    if (!parseObject.containsKey("profilePic") || parseObject.get("profilePic").equals(JSONObject.NULL)) {
      return false;
    } else {
      return true;
    }
  }

  private void validateProfile(final ParseObject parseObject) {
    if (Prefs.getProfile(context) != null) {
      final Profile profile = Prefs.getProfile(context);
      if (checkFieldsTab1(profile)) {
        if (checkFieldsTab2(profile)) {
          if (checkFieldsTab3(profile)) {
            if (checkFieldsTab4(parseObject)) {
              ParseQuery<ParseObject> queryParseQuery = new ParseQuery<>("Photo");
              queryParseQuery.whereEqualTo("profileId", parseObject);
              queryParseQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                  boolean isPrimarySet = false;
                  if (list != null) {
                    for (ParseObject item : list) {
                      if (item.getBoolean("isPrimary")) {
                        isPrimarySet = true;
                      }
                    }
                  }
                  if (isPrimarySet) {
                    profile.getTimeOfBirth().setTimeZone(TimeZone.getTimeZone("UTC"));
                    profile.getDateOfBirth().setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date tob = profile.getTimeOfBirth().getTime();
                    Date dob = profile.getDateOfBirth().getTime();
                    parseObject.put("name", profile.getName());
                    parseObject.put("gender", profile.getGender());
                    parseObject.put("dob", dob);
                    parseObject.put("tob", tob);
                    parseObject.put("placeOfBirth", ParseObject.createWithoutData(profile.getPlaceOfBirth().getClassName(), profile.getPlaceOfBirth().getParseObjectId()));
                    parseObject.put("currentLocation", ParseObject.createWithoutData(profile.getCurrentLocation().getClassName(), profile.getCurrentLocation().getParseObjectId()));
                    parseObject.put("height", profile.getHeight());
                    parseObject.put("weight", profile.getWeight());
                    parseObject.put("religionId", ParseObject.createWithoutData(profile.getReligion().getClassName(), profile.getReligion().getParseObjectId()));
                    parseObject.put("casteId", ParseObject.createWithoutData(profile.getCaste().getClassName(), profile.getCaste().getParseObjectId()));
                    if (profile.getGotra() != null) {
                      parseObject.put("gotraId", ParseObject.createWithoutData(profile.getGotra().getClassName(), profile.getGotra().getParseObjectId()));
                    }
                    parseObject.put("manglik", profile.getManglik());
                    parseObject.put("industryId", ParseObject.createWithoutData(profile.getIndustry().getClassName(), profile.getIndustry().getParseObjectId()));
                    parseObject.put("designation", profile.getDesignation());
                    if (profile.getCompany() != null)
                      parseObject.put("placeOfWork", profile.getCompany());
                    parseObject.put("package", profile.getIncome());

                    parseObject.put("education1", ParseObject.createWithoutData(profile.getEducation1().getClassName(), profile.getEducation1().getParseObjectId()));
                    if (profile.getEducation2() != null)
                      parseObject.put("education2", ParseObject.createWithoutData(profile.getEducation2().getClassName(), profile.getEducation2().getParseObjectId()));
                    if (profile.getEducation3() != null)
                      parseObject.put("education3", ParseObject.createWithoutData(profile.getEducation3().getClassName(), profile.getEducation3().getParseObjectId()));
                    parseObject.put("workAfterMarriage", profile.getWorkAfterMarriage());

                    parseObject.put("isComplete", true);
                    parseObject.saveInBackground(new SaveCallback() {
                      @Override
                      public void done(ParseException e) {
                        mApp.dialog.dismiss();
                        if (e == null) {
                          try {
                            if (isFirstStart) {
                              startActivity(new Intent(EditProfileActivity.this, UserPreferences.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_CLEAR_TOP));
                              EditProfileActivity.this.finish();
                              mApp.showToast(context, "Please Set your Preferences");
                            } else {
                              startActivity(new Intent(EditProfileActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_CLEAR_TOP));
                              EditProfileActivity.this.finish();
                              mApp.showToast(context, "Profile updated");
                            }
                          } catch (Exception e2) {
                            mApp.showToast(context, "Error while updating profile");
                            e2.printStackTrace();
                          }
                        } else {
                          mApp.showToast(context, "Error while updating profile");
                          e.printStackTrace();
                        }
                      }
                    });
                  } else {
                    mApp.dialog.dismiss();
                    mApp.showToast(context, "Please select a primary profile photo");
                  }
                }
              });
            }
          } else {
            mApp.dialog.dismiss();
            mApp.showToast(context, "Please fill all details");
            mViewPager.setCurrentItem(2);
          }
        } else {
          mApp.dialog.dismiss();
          mApp.showToast(context, "Please fill all details");
          mViewPager.setCurrentItem(1);
        }
      } else {
        mApp.dialog.dismiss();
        mApp.showToast(context, "Please fill all details");
        mViewPager.setCurrentItem(0);
      }
    }
  }

  public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      switch (position) {
        case 0:
          return basicProfileFragment;
        case 1:
          return detailsProfileFragment;
        case 2:
          return qualificationEditProfileFragment;
        case 3:
          return finalEditProfileFragment;
        default:
          return basicProfileFragment;
      }
    }

    @Override
    public int getCount() {
      // Show 4 total pages.
      return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      Locale l = Locale.getDefault();
      switch (position) {
        case 0:
          return getString(R.string.title_section1).toUpperCase(l);
        case 1:
          return getString(R.string.title_section2).toUpperCase(l);
        case 2:
          return getString(R.string.title_section3).toUpperCase(l);
        case 3:
          return getString(R.string.title_section4).toUpperCase(l);
      }
      return null;
    }
  }
}
