package com.mandaptak.android.EditProfile;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.mandaptak.android.Models.Location;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import me.iwf.photopicker.entity.ParseNameModel;
import me.iwf.photopicker.entity.Profile;
import me.iwf.photopicker.utils.Prefs;

public class BasicProfileFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
  Common mApp;
  boolean isStarted = false;
  boolean isVisible = false;
  private TextView gender, datePicker, timePicker;
  private TextView placeOfBirth, currentLocation;
  private EditText displayName;
  private View rootView;
  private int year = 1992;
  private int month = 0;
  private int day = 1;
  private int hourOfDay = 0;
  private int minute = 0;
  private String newName, newGender;
  private Calendar newTOB, newDOB;
  private ParseNameModel newPOB, newCurrentLocation;
  private Context context;

  public BasicProfileFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    init(inflater, container);

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
            if (editable == null || editable.length() == 0) {
              empty.setVisibility(View.VISIBLE);
              listView.setVisibility(View.GONE);
            } else if (mApp.isNetworkAvailable(context)) {
              empty.setVisibility(View.GONE);
              listView.setVisibility(View.VISIBLE);
              new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                  getPOB(editable.toString(), listView, alertDialog);
                }
              }, 300);
            } else {
              empty.setVisibility(View.VISIBLE);
              listView.setVisibility(View.GONE);
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
            if (editable == null || editable.length() == 0) {
              empty.setVisibility(View.VISIBLE);
              listView.setVisibility(View.GONE);
            } else if (mApp.isNetworkAvailable(context)) {
              empty.setVisibility(View.GONE);
              listView.setVisibility(View.VISIBLE);
              new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                  getCurrentLocation(editable.toString(), listView, alertDialog);
                }
              }, 300);
            } else {
              empty.setVisibility(View.VISIBLE);
              listView.setVisibility(View.GONE);
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
        DateTime now = DateTime.now(DateTimeZone.forID("Asia/Kolkata"));
        DateTime ageLimit = now.minusYears(18);
        DatePickerDialog newFragment = new DatePickerDialog(context, BasicProfileFragment.this, year, month, day);
        newFragment.getDatePicker().setMaxDate(ageLimit.getMillis());
        newFragment.show();
      }
    });

    timePicker.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        TimePickerDialog newFragment = new TimePickerDialog(context, BasicProfileFragment.this, hourOfDay, minute, false);
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
    timePicker = (TextView) rootView.findViewById(R.id.time_of_birth);
    placeOfBirth = (TextView) rootView.findViewById(R.id.place_of_birth);
    currentLocation = (TextView) rootView.findViewById(R.id.current_location);
    displayName = (EditText) rootView.findViewById(R.id.display_name);
    displayName.setFilters(new InputFilter[]{
        new InputFilter() {
          public CharSequence filter(CharSequence src, int start,
                                     int end, Spanned dst, int dstart, int dend) {
            if (src.equals("")) { // for backspace
              return src;
            }
            if (src.toString().matches("[a-zA-Z ]+")) {
              return src;
            }
            return src.toString().replaceAll("[^A-Za-z ]", "");
          }
        }
        , new InputFilter.LengthFilter(32)});
  }

  public void getParseData() {
    if (Prefs.getProfile(context) != null) {
      Profile profile = Prefs.getProfile(context);
      try {
        newName = profile.getName();
        newGender = profile.getGender();
        newTOB = profile.getTimeOfBirth();
        newDOB = profile.getDateOfBirth();
        newCurrentLocation = profile.getCurrentLocation();
        newPOB = profile.getPlaceOfBirth();

        if (newName != null) {
          displayName.setText(newName);
        }
        if (newGender != null) {
          gender.setText(newGender);
        }
        if (newDOB != null) {
          DateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
//                    df.setTimeZone(TimeZone.getTimeZone("UTC"));
          String subdateStr = df.format(newDOB.getTime());
          datePicker.setText(subdateStr);
        }
        if (newTOB != null) {
          DateFormat df = new SimpleDateFormat("hh:mm a", Locale.getDefault());
//                    df.setTimeZone(TimeZone.getTimeZone("UTC"));
          String subdateStr = df.format(newTOB.getTime());
          timePicker.setText(subdateStr);
        }
        if (newPOB != null) {
          placeOfBirth.setText(newPOB.getName());
        }
        if (newCurrentLocation != null) {
          currentLocation.setText(newCurrentLocation.getName());
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private ArrayList<Location> getPOB(String query, final ListView listView, final AlertDialog alertDialog) {
    final ArrayList<Location> locationArrayList = new ArrayList<>();
    ParseQuery<ParseObject> parseQuery = new ParseQuery<>("City");
    parseQuery.whereMatches("name", "(" + query + ")", "i");
    parseQuery.include("Parent.Parent");
    parseQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
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
              newPOB = new ParseNameModel(locationArrayList.get(i).getCity() + ", " + locationArrayList.get(i).getState() + ", " + locationArrayList.get(i).getCountry(), "City", locationArrayList.get(i).getCityObject().getObjectId());
              placeOfBirth.setText(newPOB.getName());
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
    parseQuery.whereMatches("name", "(" + query + ")", "i");
    parseQuery.include("Parent.Parent");
    parseQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
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
              newCurrentLocation = new ParseNameModel(locationArrayList.get(i).getCity() + ", " + locationArrayList.get(i).getState() + ", " + locationArrayList.get(i).getCountry(), "City", locationArrayList.get(i).getCityObject().getObjectId());
              currentLocation.setText(newCurrentLocation.getName());
              alertDialog.dismiss();
            }
          });
        }
      }
    });
    return locationArrayList;
  }

  public void onStart() {
    super.onStart();
    isStarted = true;
    if (isVisible) {
      getParseData();
    }
  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    isVisible = isVisibleToUser;
    if (isVisible && isStarted) {
      getParseData();
    } else if (!isVisible) {
      saveInfo();
    }
  }

  public void saveInfo() {
    try {
      Profile profile = new Profile();
      if (Prefs.getProfile(context) != null) {
        profile = Prefs.getProfile(context);
      }
      newName = displayName.getText().toString();
      if (newGender != null)
        profile.setGender(newGender);
//      if (newName != null)
//        if (!newName.trim().equals(""))
      profile.setName(newName.trim());
      if (newPOB != null)
        profile.setPlaceOfBirth(newPOB);
      if (newCurrentLocation != null)
        profile.setCurrentLocation(newCurrentLocation);
      if (newTOB != null)
        profile.setTimeOfBirth(newTOB);
      if (newDOB != null)
        profile.setDateOfBirth(newDOB);
      Prefs.setProfile(context, profile);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onDateSet(DatePicker dp, int selectedYear, int selectedMonth, int selectedDay) {
    this.year = selectedYear;
    this.month = selectedMonth;
    this.day = selectedDay;
    newDOB = Calendar.getInstance();
//        newDOB.setTimeZone(TimeZone.getTimeZone("UTC"));

    newDOB.set(year, month, day, 0, 0);
    SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
//        df.setTimeZone(TimeZone.getTimeZone("UTC"));
    String subdateStr = df.format(newDOB.getTime());
    datePicker.setText(subdateStr);
  }

  @Override
  public void onTimeSet(TimePicker tp, int hourOfDay, int minute) {
    this.hourOfDay = hourOfDay;
    this.minute = minute;
    newTOB = Calendar.getInstance();
//        newTOB.setTimeZone(TimeZone.getTimeZone("UTC"));
    newTOB.set(1992, 0, 1, hourOfDay, minute);
    SimpleDateFormat df = new SimpleDateFormat("hh:mm a", Locale.getDefault());
//        df.setTimeZone(TimeZone.getTimeZone("UTC"));
    String subdateStr = df.format(newTOB.getTime());
    timePicker.setText(subdateStr);
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
