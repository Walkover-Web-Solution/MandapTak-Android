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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mandaptak.android.Adapter.LocationDataAdapter;
import com.mandaptak.android.Main.MainActivity;
import com.mandaptak.android.Models.LocationPreference;
import com.mandaptak.android.Models.ParseNameModel;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserPreferences extends AppCompatActivity {

    TextView etMinHeight, etMaxHeight, etDegree, etLocation;
    EditText etMinAge, etMaxAge, etIncome, etBudgetMin, etBudgetMax;
    Spinner workingPartner, manglikStatus;
    Button btnSavePreferences;
    Context context = UserPreferences.this;
    LocationDataAdapter locationDataAdapter;
    ArrayList<LocationPreference> locationList;
    ArrayList<LocationPreference> parseSavedLocationList = new ArrayList<>();
    private int newWorkAfterMarriage = 0;
    private int minAge = 0, maxAge = 0, minBudget = 0, maxBudget = 0, manglik = 0, minIncome = 0;
    private int minHeight = 0, maxHeight = 0;
    private Common mApp;
    private ParseNameModel newEducationDetail1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_set_preferences);
        init();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Preferences");
        btnSavePreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
        workingPartner.setAdapter(ArrayAdapter.createFromResource(context,
                R.array.wam_array, R.layout.location_list_item));
        workingPartner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                newWorkAfterMarriage = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        manglikStatus.setAdapter(ArrayAdapter.createFromResource(context,
                R.array.manglik_status_array, R.layout.location_list_item));
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
                            locationList = getCityList(query);
                            locationDataAdapter = new LocationDataAdapter(UserPreferences.this, locationList);
                            listView.setAdapter(locationDataAdapter);
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
                        String query = editable.toString();
                        if (query == null || query.length() == 0) {
                            locationDataAdapter = new LocationDataAdapter(UserPreferences.this, parseSavedLocationList);
                            listView.setAdapter(locationDataAdapter);
                        } else {
                            locationList = getCityList(query);
                            locationDataAdapter = new LocationDataAdapter(UserPreferences.this, locationList);
                            listView.setAdapter(locationDataAdapter);
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
            public void onClick(View view) {
                final View locationDialog = View.inflate(context, R.layout.location_search_dialog, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setView(locationDialog);
                TextView title = (TextView) locationDialog.findViewById(R.id.title);
                EditText searchBar = (EditText) locationDialog.findViewById(R.id.search);
                final ListView listView = (ListView) locationDialog.findViewById(R.id.list);
                title.setText("Select Degree");
                locationDialog.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                if (mApp.isNetworkAvailable(context)) {
                    final ArrayList<ParseNameModel> degreeList = getDegreeList(null);
                    listView.setAdapter(new DataAdapter(context, degreeList));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            alertDialog.dismiss();
                            mApp.show_PDialog(context, "Loading..");
                            final AlertDialog.Builder conductor = new AlertDialog.Builder(
                                    context);
                            conductor.setTitle("Select Specialization");
                            ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Specialization");
                            parseQuery.whereEqualTo("degreeId", degreeList.get(i).getParseObject());
                            parseQuery.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(final List<ParseObject> list, ParseException e) {
                                    if (list != null && list.size() > 0) {
                                        ArrayList<String> arrayList = new ArrayList<>();
                                        for (ParseObject model : list) {
                                            arrayList.add(model.getString("name"));
                                        }
                                        Object[] objectList = arrayList.toArray();
                                        String[] stringArray = Arrays.copyOf(objectList, objectList.length, String[].class);
                                        if (stringArray.length > 1) {
                                            conductor.setItems(stringArray,
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog,
                                                                            int index) {
                                                            newEducationDetail1 = new ParseNameModel(list.get(index).getString("name"), list.get(index));
                                                            etDegree.setText(list.get(index).getParseObject("degreeId").getString("name") + " " + newEducationDetail1.getName());
                                                            //  eduChildDegreeBranch1.setText(newEducationDetail1.getName());
                                                        }
                                                    });
                                            AlertDialog alert = conductor.create();
                                            mApp.dialog.dismiss();
                                            alert.show();
                                        } else {
                                            newEducationDetail1 = new ParseNameModel(list.get(0).getString("name"), list.get(0));
                                            etDegree.setText(list.get(0).getParseObject("degreeId").getString("name") + " " + newEducationDetail1.getName());
                                            //eduChildDegreeBranch1.setText(newEducationDetail1.getName());
                                            mApp.dialog.dismiss();
                                        }
                                    }
                                }
                            });

                        }
                    });
                }
                searchBar.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(final Editable editable) {
                        final ArrayList<ParseNameModel> degreeList = getDegreeList(null);
                        listView.setAdapter(new DataAdapter(context, degreeList));
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                alertDialog.dismiss();
                                mApp.show_PDialog(context, "Loading..");
                                final AlertDialog.Builder conductor = new AlertDialog.Builder(
                                        context);
                                conductor.setTitle("Select Specialization");
                                ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Specialization");
                                parseQuery.whereEqualTo("degreeId", degreeList.get(i).getParseObject());
                                parseQuery.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(final List<ParseObject> list, ParseException e) {
                                        if (list != null && list.size() > 0) {
                                            ArrayList<String> arrayList = new ArrayList<>();
                                            for (ParseObject model : list) {
                                                arrayList.add(model.getString("name"));
                                            }
                                            Object[] objectList = arrayList.toArray();
                                            String[] stringArray = Arrays.copyOf(objectList, objectList.length, String[].class);
                                            if (stringArray.length > 1) {
                                                conductor.setItems(stringArray,
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog,
                                                                                int index) {
                                                                newEducationDetail1 = new ParseNameModel(list.get(index).getString("name"), list.get(index));
                                                                etDegree.setText(list.get(index).getParseObject("degreeId").getString("name") + " " + newEducationDetail1.getName());
                                                            }
                                                        });
                                                AlertDialog alert = conductor.create();
                                                mApp.dialog.dismiss();
                                                alert.show();
                                            } else {
                                                newEducationDetail1 = new ParseNameModel(list.get(0).getString("name"), list.get(0));
                                                etDegree.setText(list.get(0).getParseObject("degreeId").getString("name") + " " + newEducationDetail1.getName());
                                                mApp.dialog.dismiss();
                                            }
                                        }
                                    }
                                });
                            }
                        });
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
        etMinHeight = (TextView) findViewById(R.id.min_height);
        etMaxHeight = (TextView) findViewById(R.id.max_height);
        etDegree = (TextView) findViewById(R.id.degree);
        etIncome = (EditText) findViewById(R.id.income);
        etLocation = (TextView) findViewById(R.id.location);
        etBudgetMin = (EditText) findViewById(R.id.budget_min);
        etBudgetMax = (EditText) findViewById(R.id.budget_max);
        workingPartner = (Spinner) findViewById(R.id.work_after_marriage);
        manglikStatus = (Spinner) findViewById(R.id.manglik);
        btnSavePreferences = (Button) findViewById(R.id.store_preference);
        locationList = new ArrayList<>();
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

    public void saveData() {
        try {
            if (!etBudgetMin.getText().toString().equals(""))
                minBudget = Integer.valueOf(etBudgetMin.getText().toString());
            else
                minBudget = 0;
            if (!etBudgetMax.getText().toString().equals(""))
                maxBudget = Integer.valueOf(etBudgetMax.getText().toString());
            else
                maxBudget = 0;
            if (!etMinAge.getText().toString().equals(""))
                minAge = Integer.valueOf(etMinAge.getText().toString());
            else
                minAge = 0;
            if (!etMaxAge.getText().toString().equals(""))
                maxAge = Integer.valueOf(etMaxAge.getText().toString());
            else
                maxAge = 0;
            if (!etIncome.getText().toString().equals(""))
                minIncome = Integer.valueOf(etIncome.getText().toString());
            else
                minIncome = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Boolean mValidField = true;
        if (minHeight > maxHeight) {
            mValidField = false;
            mApp.showToast(context, "Minimum height should be less than maximum");
        }
        if (minBudget > maxBudget) {
            mValidField = false;
            mApp.showToast(context, "Minimum budget should be less than maximum");
        }
        if (minAge > maxAge) {
            mValidField = false;
            mApp.showToast(context, "Minimum Age should be less than maximum");
        }
        if (mValidField)
            if (mApp.isNetworkAvailable(context)) {
                ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Preference");
                parseQuery.whereEqualTo("profileId", ParseUser.getCurrentUser().getParseObject("profileId"));
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
                            parseObject.saveInBackground();
                            saveLocationData(parseObject);
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
                            parseObjectNew.put("profileId", ParseUser.getCurrentUser().getParseObject("profileId"));
                            parseObjectNew.saveInBackground();
                            saveLocationData(parseObject);
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }


    }

    private void saveLocationData(ParseObject object) {
        deleteAllLocations();
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

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        UserPreferences.this.finish();
    }

    private void deleteAllLocations() {
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("LocationPreferences");
        parseQuery.whereEqualTo("preferenceId", ParseUser.getCurrentUser().getParseObject("profileId"));
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
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
            }
        });
    }

    private ArrayList<ParseNameModel> getDegreeList(String query) {
        final ArrayList<ParseNameModel> models = new ArrayList<>();
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Degree");
        if (query != null)
            parseQuery.whereMatches("name", "(?i)^" + query);
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list != null && list.size() > 0) {
                    for (ParseObject model : list) {
                        models.add(new ParseNameModel(model.getString("name"), model));
                    }
                }
            }
        });
        return models;
    }

    private ArrayList<LocationPreference> getCityList(String query) {
        final ArrayList<LocationPreference> models = new ArrayList<>();
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("City");
        if (query != null)
            parseQuery.whereMatches("name", "(?i)^" + query);
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
                            ParseObject state = city.fetchIfNeeded().getParseObject("Parent");
                            ParseObject country = city.fetchIfNeeded().getParseObject("Parent").getParseObject("Parent");
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
                                if (parseSavedLocationList.get(i).getParseObject().getObjectId().equalsIgnoreCase(city.getObjectId()))
                                    isPresent = true;
                            }
                            if (isPresent)
                                locationPreference.setIsSelected(true);
                        }
                        models.add(locationPreference);
                    }
                }
            }
        });

        ParseQuery<ParseObject> parseQuery2 = new ParseQuery<>("State");
        parseQuery2.include("Parent");
        if (query != null)
            parseQuery2.whereMatches("name", "(?i)^" + query);
        parseQuery2.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list != null && list.size() > 0) {
                    for (ParseObject state : list) {
                        LocationPreference locationPreference = new LocationPreference();
                        locationPreference.setLocationType(1);
                        try {
                            ParseObject country = state.fetchIfNeeded().getParseObject("Parent");
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
                                if (parseSavedLocationList.get(i).getParseObject().getObjectId().equalsIgnoreCase(state.getObjectId()))
                                    isPresent = true;
                            }
                            if (isPresent)
                                locationPreference.setIsSelected(true);
                        }
                        models.add(locationPreference);
                    }
                }
            }
        });
        return models;
    }

    private void getParseData() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Preference");
        query.whereEqualTo("profileId", ParseUser.getCurrentUser().getParseObject("profileId"));
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
                        getLocationData(parseObject);
                        if (minIncome != 0)
                            etIncome.setText("" + minIncome);
                        if (maxHeight != 0) {
                            int[] bases = getResources().getIntArray(R.array.heightCM);
                            String[] values = getResources().getStringArray(R.array.height);
                            Arrays.sort(bases);
                            int index = Arrays.binarySearch(bases, maxHeight);
                            etMaxHeight.setTextColor(context.getResources().getColor(R.color.black_dark));
                            etMaxHeight.setText(values[index]);
                        }
                        if (minHeight != 0) {
                            int[] bases = getResources().getIntArray(R.array.heightCM);
                            String[] values = getResources().getStringArray(R.array.height);
                            Arrays.sort(bases);
                            int index = Arrays.binarySearch(bases, minHeight);
                            etMinHeight.setTextColor(context.getResources().getColor(R.color.black_dark));
                            etMinHeight.setText(values[index]);
                        }
                        if (minAge != 0 && maxAge != 0) {
                            etMinAge.setText("" + minAge);
                            etMaxAge.setText("" + maxAge);
                        }
                        if (maxBudget != 0 && minBudget != 0) {
                            etBudgetMin.setText("" + minBudget);
                            etBudgetMax.setText("" + maxBudget);
                        }
                        if (minIncome != 0)
                            etIncome.setText("" + minIncome);

                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                } else {
                    e.printStackTrace();
                }

            }
        });
    }

    private void getLocationData(ParseObject object) {
        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("LocationPreferences");
        query2.whereEqualTo("preferenceId", object);
        query2.include("cityId");
        query2.include("cityId.Parent.Parent");
        query2.include("stateId.Parent");
        query2.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    ParseObject city, state;
                    try {
                        StringBuilder responseText = new StringBuilder();
                        if (list.size() > 0)
                            parseSavedLocationList.clear();
                        for (ParseObject parseObject : list) {
                            city = parseObject.fetchIfNeeded().getParseObject("cityId");
                            state = parseObject.fetchIfNeeded().getParseObject("stateId");
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
                        etLocation.setText(responseText.toString());
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    public class DataAdapter extends ArrayAdapter<ParseNameModel> {
        ArrayList<ParseNameModel> models;

        public DataAdapter(Context context, ArrayList<ParseNameModel> models) {
            super(context, R.layout.location_list_item, models);
            this.models = models;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.location_list_item, parent, false);
                viewHolder.descriptionTV = (TextView) convertView.findViewById(android.R.id.text1);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.descriptionTV.setText(models.get(position).getName());
            return convertView;
        }

        class ViewHolder {
            TextView descriptionTV;
        }
    }
}
