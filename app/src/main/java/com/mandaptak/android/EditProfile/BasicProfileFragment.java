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

import com.mandaptak.android.MandapTakApplication;
import com.mandaptak.android.Models.Location;
import com.mandaptak.android.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class BasicProfileFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
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
        getParseData();

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
                        } else if (MandapTakApplication.isNetworkAvailable(context)) {
                            empty.setVisibility(View.GONE);
                            final ArrayList<Location> list = getCityList(editable.toString());
                            listView.setVisibility(View.VISIBLE);
                            listView.setAdapter(new LocationAdapter(context, list));
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    newPOB = list.get(i).getCityObject();
                                    placeOfBirth.setText(list.get(i).getCity() + ", " + list.get(i).getState() + ", " + list.get(i).getCountry());
                                    placeOfBirth.setTextColor(context.getResources().getColor(R.color.black_dark));
                                    alertDialog.dismiss();
                                }
                            });
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
                        } else if (MandapTakApplication.isNetworkAvailable(context)) {
                            MandapTakApplication.show_PDialog(context, "Loading..");
                            final ArrayList<Location> list = getCityList(editable.toString());
                            MandapTakApplication.dialog.dismiss();
                            empty.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                            listView.setAdapter(new LocationAdapter(context, list));
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    currentLocation.setText(list.get(i).getCity() + ", " + list.get(i).getState() + ", " + list.get(i).getCountry());
                                    newCurrentLocation = list.get(i).getCityObject();
                                    currentLocation.setTextColor(context.getResources().getColor(R.color.black_dark));
                                    alertDialog.dismiss();
                                }
                            });
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

                int resId = getResources().getIdentifier("gender_array",
                        "array", context.getPackageName());
                conductor.setItems(resId,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int index) {
                                int resId1 = getResources().getIdentifier(
                                        "gender_array", "array",
                                        context.getPackageName());
                                newGender = getResources()
                                        .getStringArray(resId1)[index];
                                gender.setText(newGender);
                                gender.setTextColor(context.getResources().getColor(R.color.black_dark));
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
        newDOB = Calendar.getInstance();
        newTOB = Calendar.getInstance();
        newDOB.setTimeZone(TimeZone.getTimeZone("UTC"));
        newTOB.setTimeZone(TimeZone.getTimeZone("UTC"));

        rootView = inflater.inflate(R.layout.fragment_edit_basic_profile, container, false);
        gender = (TextView) rootView.findViewById(R.id.gender);
        datePicker = (TextView) rootView.findViewById(R.id.date_of_birth);
        timepicker = (TextView) rootView.findViewById(R.id.time_of_birth);
        placeOfBirth = (TextView) rootView.findViewById(R.id.place_of_birth);
        currentLocation = (TextView) rootView.findViewById(R.id.current_location);
        displayName = (EditText) rootView.findViewById(R.id.display_name);
    }

    private void getParseData() {
        try {
            ParseObject parseObject = ParseUser.getCurrentUser().fetchIfNeeded().getParseObject("profileId");
            newName = parseObject.getString("name");
            newGender = parseObject.getString("gender");
            newDOB.setTime(parseObject.getDate("dob"));
            newTOB.setTime(parseObject.getDate("tob"));
            newCurrentLocation = parseObject.getParseObject("currentLocation");
            newPOB = parseObject.getParseObject("placeOfBirth");

            if (newName != null) {
                displayName.setText(newName);
                displayName.setTextColor(context.getResources().getColor(R.color.black_dark));
            }
            if (newGender != null) {
                gender.setText(newGender);
                gender.setTextColor(context.getResources().getColor(R.color.black_dark));
            }
            if (newDOB != null) {
                DateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                String subdateStr = df.format(newDOB.getTime());
                datePicker.setText(subdateStr);
                datePicker.setTextColor(context.getResources().getColor(R.color.black_dark));
            }
            if (newTOB != null) {
                DateFormat df = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                String subdateStr = df.format(newTOB.getTime());
                timepicker.setText(subdateStr);
                timepicker.setTextColor(context.getResources().getColor(R.color.black_dark));
            }
            if (newPOB != null) {
                placeOfBirth.setTextColor(context.getResources().getColor(R.color.black_dark));
                placeOfBirth.setText(newPOB.fetchIfNeeded().getString("name")
                        + ", " + newPOB.fetchIfNeeded().getParseObject("Parent").fetchIfNeeded().getString("name") + ", " + newPOB.getParseObject("Parent").fetchIfNeeded().getParseObject("Parent").fetchIfNeeded().getString("name"));
            }
            if (newCurrentLocation != null) {
                currentLocation.setTextColor(context.getResources().getColor(R.color.black_dark));
                currentLocation.setText(newCurrentLocation.fetchIfNeeded().getString("name")
                        + ", " + newCurrentLocation.fetchIfNeeded().getParseObject("Parent").fetchIfNeeded().getString("name") + ", " + newCurrentLocation.fetchIfNeeded().getParseObject("Parent").fetchIfNeeded().getParseObject("Parent").fetchIfNeeded().getString("name"));
            }
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
    }

    private ArrayList<Location> getCityList(String query) {
        final ArrayList<Location> locationArrayList = new ArrayList<>();
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("City");
        parseQuery.whereMatches("name", "(?i)^" + query);
        parseQuery.include("Parent.Parent");
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list != null && list.size() > 0) {
                    for (ParseObject location : list) {
                        locationArrayList.add(new Location(location.getString("name"), location,
                                location.getParseObject("Parent").getString("name"),
                                location.getParseObject("Parent").getParseObject("Parent").getString("name")));
                    }
                }
            }
        });
        return locationArrayList;
    }

    @Override
    public void onDetach() {
        saveInfo();
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        saveInfo();
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        saveInfo();
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        saveInfo();
        super.onStop();
    }

    void saveInfo() {
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Profile");
        parseQuery.getInBackground(ParseUser.getCurrentUser().getParseObject("profileId").getObjectId(), new GetCallback<ParseObject>() {
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

        newDOB.set(year, month, day, 0, 0);
        datePicker.setTextColor(context.getResources().getColor(R.color.black_dark));
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String subdateStr = df.format(newDOB.getTime());
        datePicker.setText(subdateStr);
    }

    @Override
    public void onTimeSet(TimePicker tp, int hourOfDay, int minute) {
        this.hourofDay = hourOfDay;
        this.minute = minute;
        newTOB.set(92, 0, 1, hourOfDay, minute);
        timepicker.setTextColor(context.getResources().getColor(R.color.black_dark));
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
