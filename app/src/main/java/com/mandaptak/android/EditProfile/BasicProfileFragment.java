package com.mandaptak.android.EditProfile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.mandaptak.android.MandapTakApplication;
import com.mandaptak.android.Models.Location;
import com.mandaptak.android.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class BasicProfileFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";
    TextView gender, datePicker;
    TextView placeOfBirth, currentLocation;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    EditText displayName;
    Calendar calendar;
    private int year = 1992;
    private int month = 0;
    private int day = 1;
    private int hourofDay = 0;
    private int minute = 0;

    public BasicProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_basic_profile, container, false);
        gender = (TextView) rootView.findViewById(R.id.gender);
        datePicker = (TextView) rootView.findViewById(R.id.date_of_birth);
        placeOfBirth = (TextView) rootView.findViewById(R.id.place_of_birth);
        currentLocation = (TextView) rootView.findViewById(R.id.current_location);
        displayName = (EditText) rootView.findViewById(R.id.display_name);
        calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+0530"));
        calendar.set(year, month, day, hourofDay, minute);
        datePickerDialog = DatePickerDialog.newInstance(this, year, month, day, false);
        timePickerDialog = TimePickerDialog.newInstance(this, hourofDay, minute, false, false);

        placeOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View locationDialog = View.inflate(getActivity(), R.layout.location_search_dialog, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
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
                        } else if (MandapTakApplication.isNetworkAvailable(getActivity())) {
                            empty.setVisibility(View.GONE);
                            new AsyncTask<Void, Void, Void>() {
                                ArrayList<Location> list = new ArrayList<>();

                                @Override
                                protected void onPreExecute() {
                                    super.onPreExecute();
                                    progressBar.setVisibility(View.VISIBLE);
                                }

                                @Override
                                protected Void doInBackground(Void... s) {
                                    list = getCityList(editable.toString());
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Void aVoid) {
                                    super.onPostExecute(aVoid);
                                    progressBar.setVisibility(View.GONE);
                                    listView.setVisibility(View.VISIBLE);
                                    listView.setAdapter(new LocationAdapter(getActivity(), list));
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            placeOfBirth.setText(list.get(i).getDescription());
                                            placeOfBirth.setTextColor(getActivity().getResources().getColor(R.color.black_dark));
                                            alertDialog.dismiss();
                                        }
                                    });
                                }
                            }.execute();
                        }
                    }
                });

                alertDialog.show();
            }
        });

        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View locationDialog = View.inflate(getActivity(), R.layout.location_search_dialog, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setView(locationDialog);

                TextView title = (TextView) locationDialog.findViewById(R.id.title);
                final TextView empty = (TextView) locationDialog.findViewById(R.id.empty);
                EditText searchBar = (EditText) locationDialog.findViewById(R.id.search);
                final ListView listView = (ListView) locationDialog.findViewById(R.id.list);
                final ProgressBar progressBar = (ProgressBar) locationDialog.findViewById(R.id.progress);
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
                            progressBar.setVisibility(View.GONE);
                            empty.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.GONE);
                        } else if (MandapTakApplication.isNetworkAvailable(getActivity())) {
                            empty.setVisibility(View.GONE);
                            new AsyncTask<Void, Void, Void>() {
                                ArrayList<Location> list = new ArrayList<>();

                                @Override
                                protected void onPreExecute() {
                                    super.onPreExecute();
                                    progressBar.setVisibility(View.VISIBLE);
                                }

                                @Override
                                protected Void doInBackground(Void... s) {
                                    list = getCityList(editable.toString());
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Void aVoid) {
                                    super.onPostExecute(aVoid);
                                    progressBar.setVisibility(View.GONE);
                                    listView.setVisibility(View.VISIBLE);
                                    listView.setAdapter(new LocationAdapter(getActivity(), list));
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            currentLocation.setText(list.get(i).getDescription());
                                            currentLocation.setTextColor(getActivity().getResources().getColor(R.color.black_dark));
                                            alertDialog.dismiss();
                                        }
                                    });
                                }
                            }.execute();
                        }
                    }
                });

                alertDialog.show();
            }
        });

        gender.setText("Gender");
        gender.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder conductor = new AlertDialog.Builder(
                        getActivity());
                conductor.setTitle("Select Gender");

                int resId = getResources().getIdentifier("gender_array",
                        "array", getActivity().getPackageName());
                conductor.setItems(resId,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int index) {
                                int resId1 = getResources().getIdentifier(
                                        "gender_array", "array",
                                        getActivity().getPackageName());

                                gender.setText(getResources()
                                        .getStringArray(resId1)[index]);
                                gender.setTextColor(getActivity().getResources().getColor(R.color.black_dark));
                            }
                        });
                AlertDialog alert = conductor.create();
                alert.show();
            }
        });

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.setYearRange(calendar.get(Calendar.YEAR), calendar.get(Calendar.YEAR) + 1);
                datePickerDialog.setCloseOnSingleTapDay(false);
                datePickerDialog.show(getActivity().getSupportFragmentManager(), DATEPICKER_TAG);
            }
        });

        ParseUser user = new ParseUser();
        user.setUsername("arpit");
        user.setPassword("walkover");
        user.setEmail("arpit@walkover.in");

// other fields can be set just like with ParseObject
        user.put("phoneNo", "650-253-0000");
        user.put("isActive", true);

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                Log.e("Mandaptak ", "" + e);
                if (e == null) {
                    // Hooray! Let them use the app now.
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                }
            }
        });
        return rootView;
    }

    private ArrayList<Location> getCityList(String query) {
        ArrayList<Location> list = new ArrayList<>();
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("maps.googleapis.com")
                .appendPath("maps")
                .appendPath("api")
                .appendPath("place")
                .appendPath("autocomplete")
                .appendPath("json")
                .appendQueryParameter("key", "AIzaSyCHNlECEqWmNKkEPtS_hC51F5d29hGOdZ8")
                .appendQueryParameter("types", "(cities)")
                .appendQueryParameter("input", query);
        String url = builder.build().toString();
        String response = getResponse(url);
        if (response != null) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("status").equals("OK")) {
                    JSONArray predictions = jsonObject.getJSONArray("predictions");
                    for (int i = 0; i < predictions.length(); i++) {
                        JSONObject result = predictions.getJSONObject(i);
                        if (result.getJSONArray("terms").length() == 3) {
                            list.add(new Location(result.getString("description"), result.getString("place_id"),
                                    result.getJSONArray("terms").getJSONObject(0).getString("value"),
                                    result.getJSONArray("terms").getJSONObject(1).getString("value"),
                                    result.getJSONArray("terms").getJSONObject(2).getString("value")));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    private String getResponse(String url) {
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url(url)
//                .build();
//        String response = null;
//        try {
//            response = client.newCall(request).execute().body().string();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return null;
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int selectedYear, int selectedMonth, int selectedDay) {
        year = selectedYear;
        month = selectedMonth;
        day = selectedDay;

        // Show selected date
        datePicker.setTextColor(getActivity().getResources().getColor(R.color.black_dark));

        timePickerDialog.setCloseOnSingleTapMinute(false);
        timePickerDialog.show(getActivity().getSupportFragmentManager(), TIMEPICKER_TAG);
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
        this.hourofDay = hourOfDay;
        this.minute = minute;
        datePicker.setText(new StringBuilder().append(month + 1)
                .append("-").append(day).append("-").append(year).append(" ").append(hourOfDay).append(":").append(minute).append(":00"));
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
            viewHolder.descriptionTV.setText(locations.get(position).getDescription());
            return convertView;
        }

        class ViewHolder {
            TextView descriptionTV;
        }
    }
}
