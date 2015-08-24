package com.mandaptak.android.Preferences;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import com.mandaptak.android.Models.LocationPreference;
import com.mandaptak.android.Models.ParseNameModel;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserPreferences extends AppCompatActivity {

    TextView etMinHeight, etMaxHeight, etDegree, etLocation;
    EditText etMinAge, etMaxAge, etIncome, etBudgetMin, etBudgetMax;
    Spinner mWrokingPartner, manglikStatus;
    Button btnSavePreferences;
    String prefsId;
    Context context = UserPreferences.this;
    LocationDataAdapter locationDataAdapter;
    ArrayList<LocationPreference> locationList;
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
        btnSavePreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
        mWrokingPartner.setAdapter(ArrayAdapter.createFromResource(context,
                R.array.wam_array, R.layout.location_list_item));
        mWrokingPartner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                newWorkAfterMarriage = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        manglikStatus.setAdapter(ArrayAdapter.createFromResource(context,
                R.array.wam_array, R.layout.location_list_item));
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
                        StringBuffer responseText = new StringBuffer();
                        if (locationDataAdapter != null) {
                            for (LocationPreference locationPreference : locationList) {
                                if (locationPreference.getSelected())
                                    responseText.append(locationPreference.getLocationName());
                            }
                            etLocation.setText(responseText);
                        }

                    }
                });
                searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                            String query = searchBar.getText().toString();
                            //final ArrayList<LocationPreference> locationList = getCityList(query);
                            locationList = getCityList(query);
                            locationDataAdapter = new LocationDataAdapter(UserPreferences.this, locationList);
                            listView.setAdapter(locationDataAdapter);
                            return true;
                        }
                        return false;
                    }
                });
                /*searchBar.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(final Editable editable) {
                        final ArrayList<LocationPreference> locationList = getCityList(editable.toString());
                        listView.setAdapter(new LocationDataAdapter(context, locationList));
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                mApp.show_PDialog(context, "Loading..");


                            }
                        });
                    }
                });*/
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
                                                                //      eduChildDegreeBranch1.setText(newEducationDetail1.getName());
                                                            }
                                                        });
                                                AlertDialog alert = conductor.create();
                                                mApp.dialog.dismiss();
                                                alert.show();
                                            } else {
                                                newEducationDetail1 = new ParseNameModel(list.get(0).getString("name"), list.get(0));
                                                etDegree.setText(list.get(0).getParseObject("degreeId").getString("name") + " " + newEducationDetail1.getName());
                                                //    eduChildDegreeBranch1.setText(newEducationDetail1.getName());
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

    private void init() {
        locationList = new ArrayList<>();
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
        mWrokingPartner = (Spinner) findViewById(R.id.work_after_marriage);
        manglikStatus = (Spinner) findViewById(R.id.manglik);
        btnSavePreferences = (Button) findViewById(R.id.store_preference);
        getParseData();
    }

    public void saveData() {
        try {
            if (!etBudgetMin.getText().toString().equals(""))
                minBudget = Integer.valueOf(etBudgetMin.getText().toString());
            if (!etBudgetMax.getText().toString().equals(""))
                maxBudget = Integer.valueOf(etBudgetMax.getText().toString());
            if (!etMinAge.getText().toString().equals(""))
                minAge = Integer.valueOf(etMinAge.getText().toString());
            if (!etMaxAge.getText().toString().equals(""))
                maxAge = Integer.valueOf(etMaxAge.getText().toString());
            if (!etIncome.getText().toString().equals(""))
                minIncome = Integer.valueOf(etIncome.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mApp.isNetworkAvailable(context)) {
            ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Preference");
            parseQuery.whereEqualTo("profileId", ParseUser.getCurrentUser().getParseObject("profileId"));
            parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (e == null) {
                        parseObject.put("minHeight", minHeight);
                        parseObject.put("maxHeight", maxHeight);
                        parseObject.put("ageFrom", minAge);
                        parseObject.put("ageTo", maxAge);
                        parseObject.put("minBudget", minBudget);
                        parseObject.put("maxBudget", maxBudget);
                        parseObject.put("working", newWorkAfterMarriage);
                        parseObject.put("manglik", manglik);
                        parseObject.put("minIncome", minIncome);
                        parseObject.put("minGunMatch", 0);
                        parseObject.saveInBackground();
                        prefsId = parseObject.getObjectId();
                        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("LocationPreferences");
                        parseQuery.whereEqualTo("preferencesId", prefsId);
                        parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject parseObject, ParseException e) {
                                if (e == null) {
                                    parseObject.put("cityId", minHeight);
                                    parseObject.put("countryId", maxHeight);
                                    parseObject.put("stateId", minAge);
                                }
                            }
                        });

                    } else {
                        ParseObject parseObjectNew = new ParseObject("Preference");
                        parseObjectNew.put("minHeight", minHeight);
                        parseObjectNew.put("maxHeight", maxHeight);
                        parseObjectNew.put("ageFrom", minAge);
                        parseObjectNew.put("ageTo", maxAge);
                        parseObjectNew.put("minBudget", minBudget);
                        parseObjectNew.put("maxBudget", maxBudget);
                        parseObjectNew.put("working", newWorkAfterMarriage);
                        parseObjectNew.put("manglik", manglik);
                        if (minIncome != 0)
                            parseObject.put("minIncome", minIncome);
                        parseObjectNew.put("minGunMatch", 0);
                        parseObjectNew.put("profileId", ParseUser.getCurrentUser().getParseObject("profileId"));
                        parseObjectNew.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                                if (e != null) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        prefsId = parseObject.getObjectId();
                        ParseObject parseObjectLocation = new ParseObject("LocationPreferences");
                        parseObjectLocation.put("cityId", minHeight);
                        parseObjectLocation.put("countryId", maxHeight);
                        parseObjectLocation.put("stateId", minAge);
                        parseObjectLocation.put("preferencesId", prefsId);
                        parseObjectLocation.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
            });
        }
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
                    for (ParseObject model : list) {
                        LocationPreference locationPreference = new LocationPreference();
                        locationPreference.setLocatinoId(model.getObjectId());
                        locationPreference.setLocationType(0);
                        try {
                            ParseObject state = model.fetchIfNeeded().getParseObject("Parent");
                            ParseObject country = model.fetchIfNeeded().getParseObject("Parent").getParseObject("Parent");
                            locationPreference.setLocationName(model.getString("name") + ", " + state.getString("name") + ", " + country.getString("name"));
                        } catch (Exception e1) {
                            e1.printStackTrace();
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
                    for (ParseObject model : list) {
                        LocationPreference locationPreference = new LocationPreference();
                        locationPreference.setLocatinoId(model.getObjectId());
                        locationPreference.setLocationType(1);
                        try {
                            ParseObject country = model.fetchIfNeeded().getParseObject("Parent");
                            locationPreference.setLocationName(model.getString("name") + " ," + country.getString("name")
                            );
                        } catch (Exception e1) {
                            e1.printStackTrace();
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
                        prefsId = parseObject.getObjectId();
                        maxHeight = parseObject.getInt("maxHeight");
                        minHeight = parseObject.getInt("minHeight");
                        minAge = parseObject.getInt("ageFrom");
                        maxAge = parseObject.getInt("ageTo");
                        minBudget = parseObject.getInt("minBudget");
                        maxBudget = parseObject.getInt("maxBudget");
                        manglik = parseObject.getInt("manglik");
                        minIncome = parseObject.getInt("minIncome");
                        newWorkAfterMarriage = parseObject.getInt("working");
                        minIncome = parseObject.getInt("minIncome");
                        manglik = parseObject.getInt("manglik");
                        mWrokingPartner.setSelection(newWorkAfterMarriage);
                        manglikStatus.setSelection(manglik);
                        if (minIncome != 0)
                            etIncome.setText("" + minIncome);
                        if (maxHeight != 0 && minHeight != 0) {
                            etMinHeight.setText("" + minHeight);
                            etMaxHeight.setText("" + maxHeight);
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
        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("LocationPreferences");
        query2.whereEqualTo("preferencesId", prefsId);
        query2.include("cityId");
        query2.include("stateId");
        query2.include("countryId");
        query2.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e != null) {
                    ParseObject city, state, country;
                   /* try {
                        city = parseObject.fetchIfNeeded().getParseObject("cityId");
                        state = parseObject.fetchIfNeeded().getParseObject("stateId");
                        country = parseObject.fetchIfNeeded().getParseObject("countyryId");
                        etCity.setText(city.getString("name") + " " + state.getString("name") + " " + country.getString("name"));
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }*/
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
