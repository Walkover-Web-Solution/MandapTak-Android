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

    TextView etMinHeight, etMaxHeight, etDegree, etLocation;
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

/*        etDegree.setOnClickListener(new View.OnClickListener() {
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
                    ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Degree");
                    parseQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> list, ParseException e) {
                            if (list != null && list.size() > 0) {
                                final ArrayList<ParseNameModel> degreeList = new ArrayList<>();
                                for (ParseObject model : list) {
                                    ParseNameModel item = new ParseNameModel(model.getString("name"), model);
                                    degreeList.add(item);
                                }
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
                                                                        try {
                                                                            newEducationDetail1 = new ParseNameModel(list.get(index).fetchIfNeeded().getString("name"), list.get(index));
                                                                            etDegree.setText(list.get(index).fetchIfNeeded().getParseObject("degreeId").fetchIfNeeded().getString("name") + " " + newEducationDetail1.getName());
                                                                        } catch (Exception e1) {
                                                                            e1.printStackTrace();
                                                                        }
                                                                    }
                                                                });
                                                        AlertDialog alert = conductor.create();
                                                        mApp.dialog.dismiss();
                                                        alert.show();
                                                    } else {
                                                        try {
                                                            newEducationDetail1 = new ParseNameModel(list.get(0).getString("name"), list.get(0));
                                                            etDegree.setText(list.get(0).fetchIfNeeded().getParseObject("degreeId").fetchIfNeeded().getString("name") + " " + newEducationDetail1.getName());
                                                        } catch (Exception e1) {
                                                            e1.printStackTrace();
                                                        }
                                                        mApp.dialog.dismiss();
                                                    }
                                                }
                                            }
                                        });
                                    }
                                });
                            }
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
                        if (editable.length() == 0) {
                            ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Degree");
                            parseQuery.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> list, ParseException e) {
                                    if (list != null && list.size() > 0) {
                                        final ArrayList<ParseNameModel> degreeList = new ArrayList<>();
                                        for (ParseObject model : list) {
                                            try {
                                                ParseNameModel item = new ParseNameModel(model.fetchIfNeeded().getString("name"), model);
                                                degreeList.add(item);
                                            } catch (Exception e1) {
                                                e1.printStackTrace();
                                            }
                                        }
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
                                                                try {
                                                                    arrayList.add(model.fetchIfNeeded().getString("name"));
                                                                } catch (Exception e1) {
                                                                    e1.printStackTrace();
                                                                }
                                                            }
                                                            Object[] objectList = arrayList.toArray();
                                                            String[] stringArray = Arrays.copyOf(objectList, objectList.length, String[].class);
                                                            if (stringArray.length > 1) {
                                                                conductor.setItems(stringArray,
                                                                        new DialogInterface.OnClickListener() {
                                                                            public void onClick(DialogInterface dialog,
                                                                                                int index) {
                                                                                try {
                                                                                    newEducationDetail1 = new ParseNameModel(list.get(index).fetchIfNeeded().getString("name"), list.get(index));
                                                                                    etDegree.setText(list.get(index).fetchIfNeeded().getParseObject("degreeId").fetchIfNeeded().getString("name") + " " + newEducationDetail1.getName());
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                AlertDialog alert = conductor.create();
                                                                mApp.dialog.dismiss();
                                                                alert.show();
                                                            } else {
                                                                try {
                                                                    newEducationDetail1 = new ParseNameModel(list.get(0).fetchIfNeeded().getString("name"), list.get(0));
                                                                    etDegree.setText(list.get(0).fetchIfNeeded().getParseObject("degreeId").fetchIfNeeded().getString("name") + " " + newEducationDetail1.getName());
                                                                } catch (Exception e1) {
                                                                    e1.printStackTrace();
                                                                }
                                                                mApp.dialog.dismiss();
                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            });
                        } else {
                            ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Degree");
                            parseQuery.whereMatches("name", "(" + editable.length() + ")", "i");
                            parseQuery.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> list, ParseException e) {
                                    if (list != null && list.size() > 0) {
                                        final ArrayList<ParseNameModel> degreeList = new ArrayList<>();
                                        for (ParseObject model : list) {
                                            try {
                                                ParseNameModel item = new ParseNameModel(model.fetchIfNeeded().getString("name"), model);
                                                degreeList.add(item);
                                            } catch (Exception e1) {
                                                e1.printStackTrace();
                                            }
                                        }
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
                                                                try {
                                                                    arrayList.add(model.fetchIfNeeded().getString("name"));
                                                                } catch (Exception e1) {
                                                                    e1.printStackTrace();
                                                                }
                                                            }
                                                            Object[] objectList = arrayList.toArray();
                                                            String[] stringArray = Arrays.copyOf(objectList, objectList.length, String[].class);
                                                            if (stringArray.length > 1) {
                                                                conductor.setItems(stringArray,
                                                                        new DialogInterface.OnClickListener() {
                                                                            public void onClick(DialogInterface dialog,
                                                                                                int index) {
                                                                                try {
                                                                                    newEducationDetail1 = new ParseNameModel(list.get(index).fetchIfNeeded().getString("name"), list.get(index));
                                                                                    etDegree.setText(list.get(index).fetchIfNeeded().getParseObject("degreeId").fetchIfNeeded().getString("name") + " " + newEducationDetail1.getName());
                                                                                } catch (Exception e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                AlertDialog alert = conductor.create();
                                                                mApp.dialog.dismiss();
                                                                alert.show();
                                                            } else {
                                                                try {
                                                                    newEducationDetail1 = new ParseNameModel(list.get(0).fetchIfNeeded().getString("name"), list.get(0));
                                                                    etDegree.setText(list.get(0).fetchIfNeeded().getParseObject("degreeId").fetchIfNeeded().getString("name") + " " + newEducationDetail1.getName());
                                                                } catch (Exception e1) {
                                                                    e1.printStackTrace();
                                                                }
                                                                mApp.dialog.dismiss();
                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                });
                alertDialog.show();
            }
        });*/

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
                            mApp.showToast(context, e.getMessage());
                        }
                    }
                });
            }
    }

    private void saveLocationData(final ParseObject object) {
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("LocationPreferences");
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
                                        if (parseSavedDegreeList.get(i).getDegreeObj().fetchIfNeeded().getObjectId().equalsIgnoreCase(model.getObjectId()))
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
                                ParseObject state = city.fetchIfNeeded().getParseObject("Parent");
                                ParseObject country = city.fetchIfNeeded().getParseObject("Parent").fetchIfNeeded().getParseObject("Parent");
                                locationPreference.setCountry(country);
                                locationPreference.setState(state);
                                locationPreference.setCity(city);
                                locationPreference.setLocationName(city.fetchIfNeeded().getString("name") + ", " + state.fetchIfNeeded().getString("name") + ", " + country.fetchIfNeeded().getString("name"));
                                locationPreference.setParseObject(city);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                            if (parseSavedLocationList.size() > 0) {
                                boolean isPresent = false;
                                for (int i = 0; i < parseSavedLocationList.size(); i++) {
                                    try {
                                        if (parseSavedLocationList.get(i).getParseObject().fetchIfNeeded().getObjectId().equalsIgnoreCase(city.fetchIfNeeded().getObjectId()))
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
                                        ParseObject country = state.fetchIfNeeded().getParseObject("Parent");
                                        locationPreference.setCountry(country);
                                        locationPreference.setState(state);
                                        locationPreference.setCity(null);
                                        locationPreference.setLocationName(state.fetchIfNeeded().getString("name") + " ," + country.fetchIfNeeded().getString("name"));
                                        locationPreference.setParseObject(state);
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }
                                    if (parseSavedLocationList.size() > 0) {
                                        boolean isPresent = false;
                                        for (int i = 0; i < parseSavedLocationList.size(); i++) {
                                            try {
                                                if (parseSavedLocationList.get(i).getParseObject().fetchIfNeeded().getObjectId().equalsIgnoreCase(state.fetchIfNeeded().getObjectId()))
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
        ParseQuery<ParseObject> query2 = new ParseQuery<>("LocationPreferences");
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
                        if (list.size() > 0) {
                            parseSavedLocationList.clear();
                            for (ParseObject parseObject : list) {
                                city = parseObject.fetchIfNeeded().getParseObject("cityId");
                                state = parseObject.fetchIfNeeded().getParseObject("stateId");
                                //     country = parseObject.fetchIfNeeded().getParseObject("countryId");
                                LocationPreference locationPreference = new LocationPreference();
                                if (city != null) {
                                    locationPreference.setParseObject(city);
                                    locationPreference.setIsSelected(true);
                                    locationPreference.setLocationName(city.fetchIfNeeded().getString("name") + ", "
                                            + city.fetchIfNeeded().getParseObject("Parent").fetchIfNeeded().getString("name") + ", " + city.fetchIfNeeded().getParseObject("Parent").fetchIfNeeded().getParseObject("Parent").fetchIfNeeded().getString("name"));
                                    locationPreference.setLocationType(0);
                                    responseText.append(locationPreference.getLocationName());
                                } else {
                                    locationPreference.setParseObject(state);
                                    locationPreference.setIsSelected(true);
                                    locationPreference.setLocationName(state.fetchIfNeeded().getString("name") + ", " + state.fetchIfNeeded().getParseObject("Parent").fetchIfNeeded().getString("name"));
                                    locationPreference.setLocationType(1);
                                    responseText.append(locationPreference.getLocationName()).append(" ");
                                }
                                parseSavedLocationList.add(locationPreference);
                            }
                        }
                        etLocation.setText(responseText.toString());
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getDegreeData(ParseObject object) {
        ParseQuery<ParseObject> query2 = new ParseQuery<>("DegreePreferences");
        query2.whereEqualTo("preferenceId", object);
        query2.include("degreeId");
        query2.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    StringBuilder responseText = new StringBuilder();
                    if (list.size() > 0) {
                        parseSavedDegreeList.clear();
                        for (ParseObject parseObject : list) {
                            try {
                                Degree degree = new Degree(parseObject.fetchIfNeeded().getParseObject("degreeId").fetchIfNeeded().getString("name"), parseObject, true);
                                parseSavedDegreeList.add(degree);
                                responseText.append(degree.getDegreeName() + " ");
                            } catch (ParseException e1) {
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
