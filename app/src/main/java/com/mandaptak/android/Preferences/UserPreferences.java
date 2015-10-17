package com.mandaptak.android.Preferences;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.mandaptak.android.Adapter.DegreeDataAdapter;
import com.mandaptak.android.Adapter.LocationDataAdapter;
import com.mandaptak.android.Main.MainActivity;
import com.mandaptak.android.Models.Degree;
import com.mandaptak.android.Models.LocationPreference;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.iwf.photopicker.utils.Prefs;

public class UserPreferences extends AppCompatActivity {

  TextView etMinHeight, etMaxHeight, etDegree, etLocation, tittle_age_limit, tittle_height;
  EditText etMinAge, etMaxAge, etIncome, etBudgetMin, etBudgetMax;
  Spinner workingPartner, manglikStatus;
  Button btnSavePreferences;
  Context context = UserPreferences.this;
  LocationDataAdapter locationDataAdapter;
  DegreeDataAdapter degreeDataAdapter;
  ArrayList<LocationPreference> parseSavedLocationList = new ArrayList<>();
  ArrayList<Degree> parseSavedDegreeList = new ArrayList<>();
  private int newWorkAfterMarriage = 0;
  private int minAge = 0, maxAge = 0, minBudget = 0, maxBudget = 0, manglik = 0, minIncome = 0;
  private int minHeight = 0, maxHeight = 0;
  private Common mApp;
  private RangeBar rangeAge, rangeHeight;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.fragment_set_preferences);
    try {
      getSupportActionBar().setHomeButtonEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle("Preferences");
    } catch (Exception e) {
      e.printStackTrace();
    }
    init();
    btnSavePreferences.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        saveData();
      }
    });
    workingPartner.setAdapter(ArrayAdapter.createFromResource(context,
        R.array.wam_array_for_prefrences, R.layout.location_list_item));
    workingPartner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        newWorkAfterMarriage = i;
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {

      }
    });
    rangeAge.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
      @Override
      public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
        tittle_age_limit.setText("Show Ages: " + String.valueOf(minAge) + " - " + String.valueOf(maxAge));
      }
    });
    rangeHeight.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
      @Override
      public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
        tittle_height.setText("Show Height: " + String.valueOf(leftPinValue) + " - " + String.valueOf(rightPinValue));
      }
    });
    manglikStatus.setAdapter(ArrayAdapter.createFromResource(context,
        R.array.manglik_status_array_for_prefrences, R.layout.location_list_item));
    manglikStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        manglik = i;
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {

      }
    });
    etLocation.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final View locationDialog = View.inflate(context, R.layout.location_search_dialog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setView(locationDialog);
        TextView title = (TextView) locationDialog.findViewById(R.id.title);
        final EditText searchBar = (EditText) locationDialog.findViewById(R.id.search);
        final ListView listView = (ListView) locationDialog.findViewById(R.id.list);
        title.setText("Select Location");
        final Button done = (Button) locationDialog.findViewById(R.id.cancel_button);
        done.setText("DONE");
        done.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            alertDialog.dismiss();
          }
        });
        locationDataAdapter = new LocationDataAdapter(UserPreferences.this, parseSavedLocationList);
        listView.setAdapter(locationDataAdapter);
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
          @Override
          public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
              String query = searchBar.getText().toString();
              getCityList(query, listView);
              return true;
            }
            return false;
          }
        });
        searchBar.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

          }

          @Override
          public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

          }

          @Override
          public void afterTextChanged(final Editable editable) {
            if (editable.length() == 0) {
              locationDataAdapter = new LocationDataAdapter(UserPreferences.this, parseSavedLocationList);
              listView.setAdapter(locationDataAdapter);
            } else {
              getCityList(editable.toString(), listView);
            }
          }
        });
        alertDialog.show();
      }
    });

    etMinHeight.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        AlertDialog.Builder conductor = new AlertDialog.Builder(
            context);
        conductor.setTitle("Select Height");

        int resId = getResources().getIdentifier("height",
            "array", getPackageName());
        conductor.setItems(resId,
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog,
                                  int index) {
                int resId1 = getResources().getIdentifier(
                    "height", "array",
                    getPackageName());
                int resId2 = getResources().getIdentifier(
                    "heightCM", "array",
                    getPackageName());
                minHeight = getResources()
                    .getIntArray(resId2)[index];
                etMinHeight.setText(getResources()
                    .getStringArray(resId1)[index]);
                etMinHeight.setTextColor(getResources().getColor(R.color.black_dark));
              }
            });
        AlertDialog alert = conductor.create();
        alert.show();
      }
    });
    etMaxHeight.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        AlertDialog.Builder conductor = new AlertDialog.Builder(
            context);
        conductor.setTitle("Select Height");
        int resId = getResources().getIdentifier("height",
            "array", getPackageName());
        conductor.setItems(resId,
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog,
                                  int index) {
                int resId1 = getResources().getIdentifier(
                    "height", "array",
                    getPackageName());
                int resId2 = getResources().getIdentifier(
                    "heightCM", "array",
                    getPackageName());
                maxHeight = getResources()
                    .getIntArray(resId2)[index];
                etMaxHeight.setText(getResources()
                    .getStringArray(resId1)[index]);
                etMaxHeight.setTextColor(getResources().getColor(R.color.black_dark));
              }
            });
        AlertDialog alert = conductor.create();
        alert.show();
      }
    });

    etDegree.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final View locationDialog = View.inflate(context, R.layout.location_search_dialog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setView(locationDialog);
        TextView title = (TextView) locationDialog.findViewById(R.id.title);
        final EditText searchBar = (EditText) locationDialog.findViewById(R.id.search);
        final ListView listView = (ListView) locationDialog.findViewById(R.id.list);
        title.setText("Select Degree");
        final Button done = (Button) locationDialog.findViewById(R.id.cancel_button);
        done.setText("DONE");
        done.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

            alertDialog.dismiss();
          }
        });
        if (mApp.isNetworkAvailable(context)) {
          getDegreeList(null, listView);
        } else {
          mApp.showToast(context, "No network");
        }

        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
          @Override
          public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
              String query = searchBar.getText().toString();
              getCityList(query, listView);
              return true;
            }
            return false;
          }
        });
        searchBar.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

          }

          @Override
          public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

          }

          @Override
          public void afterTextChanged(final Editable editable) {
            if (editable.length() == 0) {
              getDegreeList(null, listView);
            } else {
              getDegreeList(editable.toString(), listView);
            }
          }
        });
        alertDialog.show();
      }
    });
  }

  @Override
  public void onBackPressed() {
    startActivity(new Intent(UserPreferences.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_CLEAR_TOP));
    UserPreferences.this.finish();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      onBackPressed();
    }
    return super.onOptionsItemSelected(item);
  }

  private void init() {
    mApp = (Common) context.getApplicationContext();
    etMinAge = (EditText) findViewById(R.id.min_age);
    etMaxAge = (EditText) findViewById(R.id.max_age);
    tittle_age_limit = (TextView) findViewById(R.id.tittle_age_limit);
    etMinHeight = (TextView) findViewById(R.id.min_height);
    etMaxHeight = (TextView) findViewById(R.id.max_height);
    etDegree = (TextView) findViewById(R.id.degree);
    etIncome = (EditText) findViewById(R.id.income);
    etLocation = (TextView) findViewById(R.id.location);
    etBudgetMin = (EditText) findViewById(R.id.budget_min);
    etBudgetMax = (EditText) findViewById(R.id.budget_max);
    workingPartner = (Spinner) findViewById(R.id.work_after_marriage);
    manglikStatus = (Spinner) findViewById(R.id.manglik);
    rangeAge = (RangeBar) findViewById(R.id.rangebar_age);
    btnSavePreferences = (Button) findViewById(R.id.store_preference);
    tittle_height = (TextView) findViewById(R.id.tittle_height);
    rangeHeight = (RangeBar) findViewById(R.id.height_range);
    getParseData();
  }

  public void addLocation(LocationPreference item) {
    etLocation.setText(etLocation.getText().toString() + item.getLocationName() + " ");
    parseSavedLocationList.add(item);
  }

  public void removeLocation(LocationPreference item) {
    for (int i = 0; i < parseSavedLocationList.size(); i++) {
      if (item.getParseObject().getObjectId().equalsIgnoreCase(parseSavedLocationList.get(i).getParseObject().getObjectId())) {
        parseSavedLocationList.remove(i);
        etLocation.setText(etLocation.getText().toString().replace(item.getLocationName() + " ", ""));
      }
    }
  }

  public void addDegree(Degree item) {
    etDegree.setText(etDegree.getText().toString() + item.getDegreeName() + " ");
    parseSavedDegreeList.add(item);
  }

  public void removeDegree(Degree item) {
    for (int i = 0; i < parseSavedDegreeList.size(); i++) {
      if (item.getDegreeObj().getObjectId().equalsIgnoreCase(parseSavedDegreeList.get(i).getDegreeObj().getObjectId())) {
        parseSavedDegreeList.remove(i);
        etDegree.setText(etDegree.getText().toString().replace(item.getDegreeName() + " ", ""));
      }
    }
  }

  public void saveData() {
    mApp.show_PDialog(context, "Saving Preferences..");
    try {
      if (!etBudgetMin.getText().toString().equals(""))
        minBudget = Integer.valueOf(etBudgetMin.getText().toString());
      else
        minBudget = 0;
      if (!etBudgetMax.getText().toString().equals(""))
        maxBudget = Integer.valueOf(etBudgetMax.getText().toString());
      else
        maxBudget = 0;
//      if (!etMinAge.getText().toString().equals(""))
//        minAge = Integer.valueOf(etMinAge.getText().toString());
//      else
//        minAge = 0;
//      if (!etMaxAge.getText().toString().equals(""))
//        maxAge = Integer.valueOf(etMaxAge.getText().toString());
//      else
//        maxAge = 0;
      if (!etIncome.getText().toString().equals(""))
        minIncome = Integer.valueOf(etIncome.getText().toString());
      else
        minIncome = 0;
    } catch (Exception e) {
      e.printStackTrace();
    }
    Boolean mValidField = true;

    int[] bases = getResources().getIntArray(R.array.height_range);
    Arrays.sort(bases);
//    int resId2 = getResources().getIdentifier(
//        "height_range", "array",
//        getPackageName());
    minHeight = bases[rangeHeight.getLeftIndex()];
    maxHeight = bases[rangeHeight.getRightIndex()];
    if (minHeight > maxHeight) {
      mValidField = false;
      mApp.showToast(context, "Minimum height should be less than maximum");
    }
    if (minBudget > maxBudget) {
      mValidField = false;
      mApp.showToast(context, "Minimum budget should be less than maximum");
    }
    minAge = rangeAge.getLeftIndex() + 18;
    maxAge = rangeAge.getRightIndex() + 18;
    if (minAge > maxAge) {
      mValidField = false;
      mApp.showToast(context, "Minimum Age should be less than maximum");
    }
    if (!mValidField) {
      mApp.dialog.dismiss();
    }
    if (mValidField)
      if (mApp.isNetworkAvailable(context)) {
        btnSavePreferences.setEnabled(false);
        ParseQuery<ParseObject> q1 = new ParseQuery<>("Profile");
        q1.getInBackground(Prefs.getProfileId(context), new GetCallback<ParseObject>() {
          @Override
          public void done(final ParseObject profileObject, ParseException e) {
            if (e == null) {
              ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Preference");
              parseQuery.whereEqualTo("profileId", profileObject);
              parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                  if (e == null) {
                    if (minHeight != 0)
                      parseObject.put("minHeight", minHeight);
                    else
                      parseObject.put("minHeight", JSONObject.NULL);
                    if (maxHeight != 0)
                      parseObject.put("maxHeight", maxHeight);
                    else
                      parseObject.put("maxHeight", JSONObject.NULL);
                    if (minAge != 0)
                      parseObject.put("ageFrom", minAge);
                    else
                      parseObject.put("ageFrom", JSONObject.NULL);
                    if (maxAge != 0)
                      parseObject.put("ageTo", maxAge);
                    else
                      parseObject.put("ageTo", JSONObject.NULL);
                    if (minBudget != 0)
                      parseObject.put("minBudget", minBudget);
                    else
                      parseObject.put("minBudget", minBudget);
                    if (maxBudget != 0)
                      parseObject.put("maxBudget", maxBudget);
                    else
                      parseObject.put("maxBudget", JSONObject.NULL);
                    if (minIncome != 0)
                      parseObject.put("minIncome", minIncome);
                    else
                      parseObject.put("minIncome", JSONObject.NULL);

                    parseObject.put("working", newWorkAfterMarriage);
                    parseObject.put("manglik", manglik);
                    parseObject.put("minGunMatch", 0);
                    parseObject.saveInBackground(new SaveCallback() {
                      @Override
                      public void done(ParseException e) {
                        if (e == null)
                          mApp.showToast(context, "Preferences Saved");
                        else
                          mApp.showToast(context, e.getMessage());
                        mApp.dialog.dismiss();
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        UserPreferences.this.finish();
                      }
                    });
                    saveLocationData(parseObject);
                    saveDegreeData(parseObject);
                  } else if (e.getCode() == 101) {
                    ParseObject parseObjectNew = new ParseObject("Preference");
                    if (minHeight != 0)
                      parseObjectNew.put("minHeight", minHeight);
                    if (maxHeight != 0)
                      parseObjectNew.put("maxHeight", maxHeight);
                    if (minAge != 0)
                      parseObjectNew.put("ageFrom", minAge);
                    if (maxAge != 0)
                      parseObjectNew.put("ageTo", maxAge);
                    if (minBudget != 0)
                      parseObjectNew.put("minBudget", minBudget);
                    if (maxBudget != 0)
                      parseObjectNew.put("maxBudget", maxBudget);
                    if (minIncome != 0)
                      parseObjectNew.put("minIncome", minIncome);
                    parseObjectNew.put("working", newWorkAfterMarriage);
                    parseObjectNew.put("manglik", manglik);
                    parseObjectNew.put("minGunMatch", 0);
                    parseObjectNew.put("profileId", profileObject);
                    parseObjectNew.saveInBackground(new SaveCallback() {
                      @Override
                      public void done(ParseException e) {
                        if (e == null)
                          mApp.showToast(context, "Preferences Saved");
                        else
                          mApp.showToast(context, e.getMessage());
                        mApp.dialog.dismiss();
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        UserPreferences.this.finish();
                      }
                    });
                    saveLocationData(parseObject);
                    saveDegreeData(parseObject);
                  } else {
                    e.printStackTrace();
                  }
                }
              });
            } else {
              btnSavePreferences.setEnabled(true);
              mApp.showToast(context, e.getMessage());
            }
          }
        });
      }
  }

  private void saveLocationData(final ParseObject object) {
    ParseQuery<ParseObject> parseQuery = new ParseQuery<>("LocationPreferences");
    parseQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
    parseQuery.whereEqualTo("preferenceId", object);
    parseQuery.findInBackground(new FindCallback<ParseObject>() {
      @Override
      public void done(List<ParseObject> list, ParseException e) {
        if (e == null)
          if (list.size() > 0)
            for (ParseObject parseObject : list) {
              parseObject.deleteInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                  if (e != null)
                    e.printStackTrace();
                }
              });
            }
        if (parseSavedLocationList.size() > 0)
          for (LocationPreference preference : parseSavedLocationList) {
            ParseObject parseObjectLocation = new ParseObject("LocationPreferences");
            if (preference.getLocationType() == 0)
              parseObjectLocation.put("cityId", preference.getParseObject());
            else
              parseObjectLocation.put("stateId", preference.getParseObject());
            parseObjectLocation.put("preferenceId", object);
            parseObjectLocation.saveEventually();
          }
      }
    });
  }

  private void saveDegreeData(final ParseObject object) {
    ParseQuery<ParseObject> parseQuery = new ParseQuery<>("DegreePreferences");
    parseQuery.whereEqualTo("preferenceId", object);
    parseQuery.findInBackground(new FindCallback<ParseObject>() {
      @Override
      public void done(List<ParseObject> list, ParseException e) {
        if (e == null)
          if (list.size() > 0)
            for (ParseObject parseObject : list) {
              parseObject.deleteInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                  if (e != null)
                    e.printStackTrace();

                }
              });
            }
        if (parseSavedDegreeList.size() > 0)
          for (Degree preference : parseSavedDegreeList) {
            ParseObject parseObjectLocation = new ParseObject("DegreePreferences");
            parseObjectLocation.put("degreeId", preference.getDegreeObj());
            parseObjectLocation.put("preferenceId", object);
            parseObjectLocation.saveEventually();
          }

      }
    });
  }

  private void getDegreeList(final String query, final ListView listView) {
    if (query != null) {
      final ArrayList<Degree> degreeList = new ArrayList<>();
      ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Degree");
      parseQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
      parseQuery.whereMatches("name", "(" + query + ")", "i");
      parseQuery.findInBackground(new FindCallback<ParseObject>() {
        @Override
        public void done(List<ParseObject> list, ParseException e) {
          if (list != null && list.size() > 0) {
            for (ParseObject model : list) {
              Degree item = new Degree(model.getString("name"), model, false);
              if (parseSavedDegreeList.size() > 0) {
                boolean isPresent = false;
                for (int i = 0; i < parseSavedDegreeList.size(); i++) {
                  try {
                    if (parseSavedDegreeList.get(i).getDegreeObj().getObjectId().equalsIgnoreCase(model.getObjectId()))
                      isPresent = true;
                  } catch (Exception e1) {
                    e1.printStackTrace();
                  }
                }
                if (isPresent)
                  item.setSelected(true);
              }
              degreeList.add(item);
            }
            if (query != null)
              degreeDataAdapter = new DegreeDataAdapter(UserPreferences.this, degreeList);
            else
              degreeDataAdapter = new DegreeDataAdapter(UserPreferences.this, parseSavedDegreeList);
            listView.setAdapter(degreeDataAdapter);
          }
        }
      });
    } else {
      degreeDataAdapter = new DegreeDataAdapter(UserPreferences.this, parseSavedDegreeList);
      listView.setAdapter(degreeDataAdapter);
    }
  }

  private void getCityList(final String query, final ListView listView) {
    if (query != null) {
      final ArrayList<LocationPreference> models = new ArrayList<>();
      ParseQuery<ParseObject> parseQuery = new ParseQuery<>("City");
      parseQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
      parseQuery.whereMatches("name", "(" + query + ")", "i");
      parseQuery.include("Parent");
      parseQuery.include("Parent.Parent");
      parseQuery.findInBackground(new FindCallback<ParseObject>() {
        @Override
        public void done(List<ParseObject> list, ParseException e) {
          if (list != null && list.size() > 0) {
            for (ParseObject city : list) {
              LocationPreference locationPreference = new LocationPreference();
              locationPreference.setLocationType(0);
              try {
                ParseObject state = city.getParseObject("Parent");
                ParseObject country = city.getParseObject("Parent").getParseObject("Parent");
                locationPreference.setCountry(country);
                locationPreference.setState(state);
                locationPreference.setCity(city);
                locationPreference.setLocationName(city.getString("name") + ", " + state.getString("name") + ", " + country.getString("name"));
                locationPreference.setParseObject(city);
              } catch (Exception e1) {
                e1.printStackTrace();
              }
              if (parseSavedLocationList.size() > 0) {
                boolean isPresent = false;
                for (int i = 0; i < parseSavedLocationList.size(); i++) {
                  try {
                    if (parseSavedLocationList.get(i).getParseObject().getObjectId().equalsIgnoreCase(city.getObjectId()))
                      isPresent = true;
                  } catch (Exception e1) {
                    e1.printStackTrace();
                  }
                }
                if (isPresent)
                  locationPreference.setIsSelected(true);
              }
              models.add(locationPreference);
            }

          }
          ParseQuery<ParseObject> parseQuery2 = new ParseQuery<>("State");
          parseQuery2.include("Parent");
          parseQuery2.whereMatches("name", "(" + query + ")", "i");
          parseQuery2.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
              if (list != null && list.size() > 0) {
                for (ParseObject state : list) {
                  LocationPreference locationPreference = new LocationPreference();
                  locationPreference.setLocationType(1);
                  try {
                    ParseObject country = state.getParseObject("Parent");
                    locationPreference.setCountry(country);
                    locationPreference.setState(state);
                    locationPreference.setCity(null);
                    locationPreference.setLocationName(state.getString("name") + " ," + country.getString("name"));
                    locationPreference.setParseObject(state);
                  } catch (Exception e1) {
                    e1.printStackTrace();
                  }
                  if (parseSavedLocationList.size() > 0) {
                    boolean isPresent = false;
                    for (int i = 0; i < parseSavedLocationList.size(); i++) {
                      try {
                        if (parseSavedLocationList.get(i).getParseObject().getObjectId().equalsIgnoreCase(state.getObjectId()))
                          isPresent = true;
                      } catch (Exception e1) {
                        e1.printStackTrace();
                      }
                    }
                    if (isPresent)
                      locationPreference.setIsSelected(true);
                  }
                  models.add(locationPreference);
                }
              }
              if (query != null)
                locationDataAdapter = new LocationDataAdapter(UserPreferences.this, models);
              else
                locationDataAdapter = new LocationDataAdapter(UserPreferences.this, parseSavedLocationList);
              listView.setAdapter(locationDataAdapter);
            }
          });
        }
      });
    } else {
      locationDataAdapter = new LocationDataAdapter(UserPreferences.this, parseSavedLocationList);
      listView.setAdapter(locationDataAdapter);
    }
  }

  private void getParseData() {
    mApp.show_PDialog(context, "Loading..");
    ParseQuery<ParseObject> q1 = new ParseQuery<>("Profile");
    q1.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
    q1.getInBackground(Prefs.getProfileId(context), new GetCallback<ParseObject>() {
      @Override
      public void done(ParseObject object, ParseException e) {
        if (e == null) {
          ParseQuery<ParseObject> query = new ParseQuery<>("Preference");
          query.whereEqualTo("profileId", object);
          query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
              if (e == null) {
                try {
                  if (parseObject.containsKey("maxHeight") && parseObject.get("maxHeight") != JSONObject.NULL)
                    maxHeight = parseObject.getInt("maxHeight");
                  if (parseObject.containsKey("minHeight") && parseObject.get("minHeight") != JSONObject.NULL)
                    minHeight = parseObject.getInt("minHeight");
                  if (parseObject.containsKey("ageFrom") && parseObject.get("ageFrom") != JSONObject.NULL)
                    minAge = parseObject.getInt("ageFrom");
                  if (parseObject.containsKey("ageTo") && parseObject.get("ageTo") != JSONObject.NULL)
                    maxAge = parseObject.getInt("ageTo");
                  if (parseObject.containsKey("minBudget") && parseObject.get("minBudget") != JSONObject.NULL)
                    minBudget = parseObject.getInt("minBudget");
                  if (parseObject.containsKey("maxBudget") && parseObject.get("maxBudget") != JSONObject.NULL)
                    maxBudget = parseObject.getInt("maxBudget");
                  if (parseObject.containsKey("manglik") && parseObject.get("manglik") != JSONObject.NULL) {
                    manglik = parseObject.getInt("manglik");
                    manglikStatus.setSelection(manglik);
                  }
                  if (parseObject.containsKey("minIncome") && parseObject.get("minIncome") != JSONObject.NULL)
                    minIncome = parseObject.getInt("minIncome");
                  if (parseObject.containsKey("working") && parseObject.get("working") != JSONObject.NULL) {
                    newWorkAfterMarriage = parseObject.getInt("working");
                    workingPartner.setSelection(newWorkAfterMarriage);
                  }
                  if (minIncome != 0)
                    etIncome.setText("" + minIncome);
                  if (minHeight != 0 && maxHeight != 0) {
                    int[] bases = getResources().getIntArray(R.array.height_range);
                    Arrays.sort(bases);
                    String[] values = getResources().getStringArray(R.array.height_values);
                    int indexmax = Arrays.binarySearch(bases, maxHeight);
                    int indexmin = Arrays.binarySearch(bases, minHeight);
                    rangeHeight.setRangePinsByValue(Float.valueOf(values[indexmin]), Float.valueOf(values[indexmax]));
                  }
                  if (minAge != 0 && maxAge != 0) {
                    rangeAge.setRangePinsByValue(minAge, maxAge);
                    tittle_age_limit.setText("Show Ages: " + String.valueOf(minAge) + " - " + String.valueOf(maxAge));
                  }
                  if (maxBudget != 0 && minBudget != 0) {
                    etBudgetMin.setText("" + minBudget);
                    etBudgetMax.setText("" + maxBudget);
                  }
                  if (minIncome != 0)
                    etIncome.setText("" + minIncome);
                  getLocationData(parseObject);
                  getDegreeData(parseObject);
                } catch (Exception e1) {
                  mApp.dialog.dismiss();
                  e1.printStackTrace();
                }
              } else {
                mApp.dialog.dismiss();
                e.printStackTrace();
              }
            }
          });
        } else {
          mApp.showToast(context, e.getMessage());
        }
      }
    });
  }

  private void getLocationData(ParseObject object) {

    ParseQuery<ParseObject> locationPreferences = new ParseQuery<>("LocationPreferences");
//    locationPreferences.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
    locationPreferences.whereEqualTo("preferenceId", object);
    locationPreferences.include("cityId");
    locationPreferences.include("cityId.Parent.Parent");
    locationPreferences.include("stateId.Parent");
    locationPreferences.findInBackground(new FindCallback<ParseObject>() {
      @Override
      public void done(List<ParseObject> list, ParseException e) {
        if (e == null) {
          ParseObject city, state;
          try {
            StringBuilder responseText = new StringBuilder();
            if (list.size() > 0) {
              parseSavedLocationList.clear();
              for (ParseObject parseObject : list) {
                city = parseObject.getParseObject("cityId");
                state = parseObject.getParseObject("stateId");
                //     country = parseObject.fetchIfNeeded().getParseObject("countryId");
                LocationPreference locationPreference = new LocationPreference();
                if (city != null) {
                  locationPreference.setParseObject(city);
                  locationPreference.setIsSelected(true);
                  locationPreference.setLocationName(city.getString("name") + ", "
                      + city.getParseObject("Parent").getString("name") + ", " + city.getParseObject("Parent").getParseObject("Parent").getString("name"));
                  locationPreference.setLocationType(0);
                  responseText.append(locationPreference.getLocationName());
                } else {
                  locationPreference.setParseObject(state);
                  locationPreference.setIsSelected(true);
                  locationPreference.setLocationName(state.getString("name") + ", " + state.getParseObject("Parent").getString("name"));
                  locationPreference.setLocationType(1);
                  responseText.append(locationPreference.getLocationName()).append(" ");
                }
                parseSavedLocationList.add(locationPreference);
              }
            }
            etLocation.setText(responseText.toString());
          } catch (Exception e1) {
            e1.printStackTrace();
          }
        } else {
          e.printStackTrace();
        }
      }
    });
  }

  private void getDegreeData(ParseObject object) {

    ParseQuery<ParseObject> degreePreferences = new ParseQuery<>("DegreePreferences");
    degreePreferences.whereEqualTo("preferenceId", object);
//    degreePreferences.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
    degreePreferences.include("degreeId");
    degreePreferences.findInBackground(new FindCallback<ParseObject>() {
      @Override
      public void done(List<ParseObject> list, ParseException e) {
        if (e == null) {
          StringBuilder responseText = new StringBuilder();
          if (list.size() > 0) {
            parseSavedDegreeList.clear();
            for (ParseObject parseObject : list) {
              try {
                Degree degree = new Degree(parseObject.getParseObject("degreeId").getString("name"), parseObject, true);
                parseSavedDegreeList.add(degree);
                responseText.append(degree.getDegreeName() + " ");
              } catch (Exception e1) {
                e1.printStackTrace();
              }
            }
            etDegree.setText(responseText.toString());
          }
        } else {
          e.printStackTrace();
        }
        mApp.dialog.dismiss();
      }
    });
  }

}
