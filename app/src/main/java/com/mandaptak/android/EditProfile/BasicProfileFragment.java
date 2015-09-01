package com.mandaptak.android.EditProfile;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.mandaptak.android.Models.Location;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import me.iwf.photopicker.utils.Prefs;

public class BasicProfileFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    Common mApp;
    private TextView gender, datePicker, timepicker;
    private TextView placeOfBirth, currentLocation;
    private EditText displayName;
    private View rootView;
    private int year = 1992;
    private int month = 0;
    private int day = 1;
    private int hourofDay = 0;
    private int minute = 0;
    private String newName, newGender;
    private Calendar newTOB, newDOB;
    private ParseObject newPOB, newCurrentLocation;
    private Context context;

    public BasicProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        init(inflater, container);

        displayName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                newName = editable.toString();
            }
        });
        placeOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View locationDialog = View.inflate(context, R.layout.location_search_dialog, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setView(locationDialog);

                TextView title = (TextView) locationDialog.findViewById(R.id.title);
                final TextView empty = (TextView) locationDialog.findViewById(R.id.empty);
                EditText searchBar = (EditText) locationDialog.findViewById(R.id.search);
                final ListView listView = (ListView) locationDialog.findViewById(R.id.list);
                final ProgressBar progressBar = (ProgressBar) locationDialog.findViewById(R.id.progress);
                title.setText("Place of birth");
                locationDialog.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
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
                            progressBar.setVisibility(View.GONE);
                            empty.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.GONE);
                        } else if (mApp.isNetworkAvailable(context)) {
                            empty.setVisibility(View.GONE);
                            getPOB(editable.toString(), listView, alertDialog);
                        }
                    }
                });
                alertDialog.show();
            }
        });

        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View locationDialog = View.inflate(context, R.layout.location_search_dialog, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setView(locationDialog);

                TextView title = (TextView) locationDialog.findViewById(R.id.title);
                final TextView empty = (TextView) locationDialog.findViewById(R.id.empty);
                EditText searchBar = (EditText) locationDialog.findViewById(R.id.search);
                final ListView listView = (ListView) locationDialog.findViewById(R.id.list);
                title.setText("Current Location");
                locationDialog.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
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
                            empty.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.GONE);
                        } else if (mApp.isNetworkAvailable(context)) {
                            mApp.show_PDialog(context, "Loading..");
                            getCurrentLocation(editable.toString(), listView, alertDialog);
                            mApp.dialog.dismiss();
                        }
                    }
                });
                alertDialog.show();
            }
        });

        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder conductor = new AlertDialog.Builder(
                        context);
                conductor.setTitle("Select Gender");
                final int resId = getResources().getIdentifier("gender_array",
                        "array", context.getPackageName());
                conductor.setItems(resId,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int index) {
                                newGender = getResources()
                                        .getStringArray(resId)[index];
                                gender.setText(newGender);
                            }
                        });
                AlertDialog alert = conductor.create();
                alert.show();
            }
        });

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog newFragment = new DatePickerDialog(context, BasicProfileFragment.this, year, month, day);
                newFragment.show();
            }
        });

        timepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog newFragment = new TimePickerDialog(context, BasicProfileFragment.this, hourofDay, minute, false);
                newFragment.show();
            }
        });
        return rootView;
    }

    private void init(LayoutInflater inflater, ViewGroup container) {
        context = getActivity();
        mApp = (Common) context.getApplicationContext();

        rootView = inflater.inflate(R.layout.fragment_edit_basic_profile, container, false);
        gender = (TextView) rootView.findViewById(R.id.gender);
        datePicker = (TextView) rootView.findViewById(R.id.date_of_birth);
        timepicker = (TextView) rootView.findViewById(R.id.time_of_birth);
        placeOfBirth = (TextView) rootView.findViewById(R.id.place_of_birth);
        currentLocation = (TextView) rootView.findViewById(R.id.current_location);
        displayName = (EditText) rootView.findViewById(R.id.display_name);
    }

    public void getParseData() {
        mApp.show_PDialog(context, "Loading..");
        ParseQuery<ParseObject> query = new ParseQuery<>("Profile");
        query.getInBackground(Prefs.getProfileId(context), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    try {
                        newName = parseObject.getString("name");
                        newGender = parseObject.getString("gender");
                        Date tmpDOB = parseObject.getDate("dob");
                        Date tmpTOB = parseObject.getDate("tob");
                        if (tmpDOB != null) {
                            newDOB = Calendar.getInstance();
                            newDOB.setTimeZone(TimeZone.getTimeZone("UTC"));
                            newDOB.setTime(tmpDOB);
                        }
                        if (tmpTOB != null) {
                            newTOB = Calendar.getInstance();
                            newTOB.setTimeZone(TimeZone.getTimeZone("UTC"));
                            newTOB.setTime(tmpTOB);
                        }
                        newCurrentLocation = parseObject.getParseObject("currentLocation");
                        newPOB = parseObject.getParseObject("placeOfBirth");

                        if (newName != null) {
                            displayName.setText(newName);
                        }
                        if (newGender != null) {
                            gender.setText(newGender);
                        }
                        if (newDOB != null) {
                            DateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                            df.setTimeZone(TimeZone.getTimeZone("UTC"));
                            String subdateStr = df.format(newDOB.getTime());
                            datePicker.setText(subdateStr);
                        }
                        if (newTOB != null) {
                            DateFormat df = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                            df.setTimeZone(TimeZone.getTimeZone("UTC"));
                            String subdateStr = df.format(newTOB.getTime());
                            timepicker.setText(subdateStr);
                        }
                        if (newPOB != null) {
                            placeOfBirth.setText(newPOB.fetchIfNeeded().getString("name")
                                    + ", " + newPOB.fetchIfNeeded().getParseObject("Parent").fetchIfNeeded().getString("name") + ", " + newPOB.getParseObject("Parent").fetchIfNeeded().getParseObject("Parent").fetchIfNeeded().getString("name"));
                        }
                        if (newCurrentLocation != null) {
                            currentLocation.setText(newCurrentLocation.fetchIfNeeded().getString("name")
                                    + ", " + newCurrentLocation.fetchIfNeeded().getParseObject("Parent").fetchIfNeeded().getString("name") + ", " + newCurrentLocation.fetchIfNeeded().getParseObject("Parent").fetchIfNeeded().getParseObject("Parent").fetchIfNeeded().getString("name"));
                        }
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    e.printStackTrace();
                }
                mApp.dialog.dismiss();
            }
        });
    }

    private ArrayList<Location> getPOB(String query, final ListView listView, final AlertDialog alertDialog) {
        final ArrayList<Location> locationArrayList = new ArrayList<>();
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("City");
        parseQuery.whereMatches("name", "(?i)^" + query);
        parseQuery.include("Parent.Parent");
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> list, ParseException e) {
                if (list != null && list.size() > 0) {
                    for (ParseObject location : list) {
                        locationArrayList.add(new Location(location.getString("name"), location,
                                location.getParseObject("Parent").getString("name"),
                                location.getParseObject("Parent").getParseObject("Parent").getString("name")));
                    }
                    listView.setAdapter(new LocationAdapter(context, locationArrayList));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            newPOB = locationArrayList.get(i).getCityObject();
                            placeOfBirth.setText(locationArrayList.get(i).getCity() + ", " + locationArrayList.get(i).getState() + ", " + locationArrayList.get(i).getCountry());
                            alertDialog.dismiss();
                        }
                    });
                }
            }
        });
        return locationArrayList;
    }

    private ArrayList<Location> getCurrentLocation(String query, final ListView listView, final AlertDialog alertDialog) {
        final ArrayList<Location> locationArrayList = new ArrayList<>();
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("City");
        parseQuery.whereMatches("name", "(?i)^" + query);
        parseQuery.include("Parent.Parent");
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> list, ParseException e) {
                if (list != null && list.size() > 0) {
                    for (ParseObject location : list) {
                        locationArrayList.add(new Location(location.getString("name"), location,
                                location.getParseObject("Parent").getString("name"),
                                location.getParseObject("Parent").getParseObject("Parent").getString("name")));
                    }
                    listView.setAdapter(new LocationAdapter(context, locationArrayList));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            currentLocation.setText(locationArrayList.get(i).getCity() + ", " + locationArrayList.get(i).getState() + ", " + locationArrayList.get(i).getCountry());
                            newCurrentLocation = locationArrayList.get(i).getCityObject();
                            alertDialog.dismiss();
                        }
                    });
                }
            }
        });
        return locationArrayList;
    }

    @Override
    public void onPause() {
        saveInfo();
        super.onPause();
    }

    void saveInfo() {
        Log.e("Save Screen", "1");
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Profile");
        parseQuery.getInBackground(Prefs.getProfileId(context), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (newGender != null)
                    parseObject.put("gender", newGender);
                if (newName != null)
                    parseObject.put("name", newName);
                if (newTOB != null) {
                    parseObject.put("tob", newTOB.getTime());
                }
                if (newDOB != null) {
                    parseObject.put("dob", newDOB.getTime());
                }
                if (newPOB != null)
                    parseObject.put("placeOfBirth", newPOB);
                if (newCurrentLocation != null)
                    parseObject.put("currentLocation", newCurrentLocation);
                parseObject.saveInBackground();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker dp, int selectedYear, int selectedMonth, int selectedDay) {
        this.year = selectedYear;
        this.month = selectedMonth;
        this.day = selectedDay;
        newDOB = Calendar.getInstance();
        newDOB.setTimeZone(TimeZone.getTimeZone("UTC"));

        newDOB.set(year, month, day, 0, 0);
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String subdateStr = df.format(newDOB.getTime());
        datePicker.setText(subdateStr);
    }

    @Override
    public void onTimeSet(TimePicker tp, int hourOfDay, int minute) {
        this.hourofDay = hourOfDay;
        this.minute = minute;
        newTOB = Calendar.getInstance();
        newTOB.setTimeZone(TimeZone.getTimeZone("UTC"));

        newTOB.set(92, 0, 1, hourOfDay, minute);
        SimpleDateFormat df = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String subdateStr = df.format(newTOB.getTime());
        timepicker.setText(subdateStr);
    }

    public class LocationAdapter extends ArrayAdapter<Location> {
        ArrayList<Location> locations;

        public LocationAdapter(Context context, ArrayList<Location> locations) {
            super(context, R.layout.location_list_item, locations);
            this.locations = locations;
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
            viewHolder.descriptionTV.setText(locations.get(position).getCity() + ", " + locations.get(position).getState() + ", " + locations.get(position).getCountry());
            return convertView;
        }

        class ViewHolder {
            TextView descriptionTV;
        }
    }
}
