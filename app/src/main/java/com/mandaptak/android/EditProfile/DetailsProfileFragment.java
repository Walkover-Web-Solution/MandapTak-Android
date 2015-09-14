package com.mandaptak.android.EditProfile;

import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.mandaptak.android.Views.ExtendedEditText;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.iwf.photopicker.entity.ParseNameModel;
import me.iwf.photopicker.entity.Profile;
import me.iwf.photopicker.utils.Prefs;

public class DetailsProfileFragment extends Fragment {
    Common mApp;
    private TextView religion, height, caste, gotra;
    private Spinner manglik;
    private ExtendedEditText weight;
    private Context context;
    private View rootView;
    private int newHeight = 0, newWeight = 0, newManglik = 0;
    private ParseNameModel newReligion, newCaste, newGotra;
    private Boolean isStarted = false;
    private Boolean isVisible = false;

    public DetailsProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        mApp = (Common) context.getApplicationContext();
        rootView = inflater.inflate(R.layout.fragment_details_profile, container, false);
        init();

        weight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    weight.setSuffix("");
                    newWeight = 0;
                } else {
                    newWeight = Integer.parseInt(editable.toString());
                    weight.setSuffix(" KG");
                }
            }
        });

        religion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View locationDialog = View.inflate(context, R.layout.location_search_dialog, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setView(locationDialog);

                TextView title = (TextView) locationDialog.findViewById(R.id.title);
                final TextView empty = (TextView) locationDialog.findViewById(R.id.empty);
                EditText searchBar = (EditText) locationDialog.findViewById(R.id.search);
                final ListView listView = (ListView) locationDialog.findViewById(R.id.list);
                title.setText("Select Religion");
                empty.setText("Search");
                locationDialog.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                listView.setVisibility(View.VISIBLE);
                if (mApp.isNetworkAvailable(context)) {
                    getReligionList(null, listView, alertDialog);
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
                        if (editable == null || editable.length() == 0) {
                            getReligionList(null, listView, alertDialog);
                        } else {
                            listView.setVisibility(View.VISIBLE);
                            getReligionList(editable.toString(), listView, alertDialog);
                        }
                    }
                });
                alertDialog.show();
            }
        });

        caste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newReligion != null) {
                    final View locationDialog = View.inflate(context, R.layout.location_search_dialog, null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setView(locationDialog);

                    TextView title = (TextView) locationDialog.findViewById(R.id.title);
                    final TextView empty = (TextView) locationDialog.findViewById(R.id.empty);
                    EditText searchBar = (EditText) locationDialog.findViewById(R.id.search);
                    final ListView listView = (ListView) locationDialog.findViewById(R.id.list);
                    title.setText("Select Caste");
                    empty.setText("Search");
                    locationDialog.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                    listView.setVisibility(View.VISIBLE);
                    if (mApp.isNetworkAvailable(context)) {
                        getCasteList(null, listView, alertDialog);

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
                            if (editable == null || editable.length() == 0) {
                                getCasteList(null, listView, alertDialog);
                            } else {
                                getCasteList(editable.toString(), listView, alertDialog);
                            }
                        }
                    });
                    alertDialog.show();
                } else {
                    mApp.showToast(context, "Select a religion first.");
                }
            }
        });

        gotra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newCaste != null) {
                    final View locationDialog = View.inflate(context, R.layout.location_search_dialog, null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setView(locationDialog);

                    TextView title = (TextView) locationDialog.findViewById(R.id.title);
                    final TextView empty = (TextView) locationDialog.findViewById(R.id.empty);
                    EditText searchBar = (EditText) locationDialog.findViewById(R.id.search);
                    final ListView listView = (ListView) locationDialog.findViewById(R.id.list);
                    title.setText("Select Gotra");
                    empty.setText("Search");
                    locationDialog.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                    listView.setVisibility(View.VISIBLE);
                    if (mApp.isNetworkAvailable(context)) {
                        getGotraList(null, listView, alertDialog);
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
                                getGotraList(null, listView, alertDialog);
                            } else {
                                getGotraList(editable.toString(), listView, alertDialog);
                            }
                        }
                    });
                    alertDialog.show();
                } else {
                    mApp.showToast(context, "Select a caste first.");
                }
            }
        });

        height.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder conductor = new AlertDialog.Builder(
                        getActivity());
                conductor.setTitle("Select Height");

                int resId = getResources().getIdentifier("height",
                        "array", getActivity().getPackageName());
                conductor.setItems(resId,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int index) {
                                int resId1 = getResources().getIdentifier(
                                        "height", "array",
                                        getActivity().getPackageName());
                                int resId2 = getResources().getIdentifier(
                                        "heightCM", "array",
                                        getActivity().getPackageName());
                                newHeight = getResources()
                                        .getIntArray(resId2)[index];
                                height.setText(getResources()
                                        .getStringArray(resId1)[index]);
                                height.setTextColor(getActivity().getResources().getColor(R.color.black_dark));
                            }
                        });
                AlertDialog alert = conductor.create();
                alert.show();
            }
        });
        manglik.setAdapter(ArrayAdapter.createFromResource(getActivity(),
                R.array.manglik_array, R.layout.location_list_item));
        manglik.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                newManglik = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return rootView;
    }

    private ArrayList<ParseNameModel> getReligionList(String query, final ListView listView, final AlertDialog alertDialog) {
        final ArrayList<ParseNameModel> models = new ArrayList<>();
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Religion");
        if (query != null)
            parseQuery.whereMatches("name", "(" + query + ")", "i");
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> list, ParseException e) {
                if (list != null && list.size() > 0) {
                    for (ParseObject model : list) {
                        models.add(new ParseNameModel(model.getString("name"), "Religion", model.getObjectId()));
                    }
                    listView.setAdapter(new DataAdapter(context, models));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            newReligion = models.get(i);
                            newCaste = null;
                            newGotra = null;
                            caste.setText("");
                            gotra.setText("");
                            religion.setText(models.get(i).getName());
                            religion.setTextColor(context.getResources().getColor(R.color.black_dark));
                            alertDialog.dismiss();
                        }
                    });
                }
            }
        });
        return models;
    }

    private ArrayList<ParseNameModel> getCasteList(String query, final ListView listView, final AlertDialog alertDialog) {
        final ArrayList<ParseNameModel> models = new ArrayList<>();
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Caste");
        if (query != null)
            parseQuery.whereMatches("name", "(" + query + ")", "i");
        parseQuery.whereEqualTo("religionId", ParseObject.createWithoutData(newReligion.getClassName(), newReligion.getParseObjectId()));
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list != null && list.size() > 0) {
                    for (ParseObject model : list) {
                        models.add(new ParseNameModel(model.getString("name"), "Caste", model.getObjectId()));
                    }
                    listView.setAdapter(new DataAdapter(context, models));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            newCaste = models.get(i);
                            newGotra = null;
                            gotra.setText("");
                            caste.setText(models.get(i).getName());
                            caste.setTextColor(context.getResources().getColor(R.color.black_dark));
                            alertDialog.dismiss();
                        }
                    });
                }
            }
        });
        return models;
    }

    private ArrayList<ParseNameModel> getGotraList(String query, final ListView listView, final AlertDialog alertDialog) {
        final ArrayList<ParseNameModel> models = new ArrayList<>();
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Gotra");
        if (query != null)
            parseQuery.whereMatches("name", "(" + query + ")", "i");
        parseQuery.whereEqualTo("casteId", ParseObject.createWithoutData(newCaste.getClassName(), newCaste.getParseObjectId()));
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> list, ParseException e) {
                if (list != null && list.size() > 0) {
                    for (ParseObject model : list) {
                        models.add(new ParseNameModel(model.getString("name"), "Gotra", model.getObjectId()));
                    }
                    listView.setAdapter(new DataAdapter(context, models));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            newGotra = models.get(i);
                            gotra.setText(models.get(i).getName());
                            gotra.setTextColor(context.getResources().getColor(R.color.black_dark));
                            alertDialog.dismiss();
                        }
                    });
                }
            }
        });
        return models;
    }

    @Override
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

    public void getParseData() {
        if (Prefs.getProfile(context) != null) {
            Profile profile = Prefs.getProfile(context);
            try {
                newHeight = profile.getHeight();
                newWeight = profile.getWeight();
                newReligion = profile.getReligion();
                newCaste = profile.getCaste();
                newGotra = profile.getGotra();
                newManglik = profile.getManglik();
                manglik.setSelection(newManglik);
                if (newHeight != 0) {
                    if (isAdded()) {
                        int[] bases = getResources().getIntArray(R.array.heightCM);
                        String[] values = getResources().getStringArray(R.array.height);
                        Arrays.sort(bases);
                        int index = Arrays.binarySearch(bases, newHeight);
                        height.setText(values[index]);
                        height.setTextColor(context.getResources().getColor(R.color.black_dark));
                    }
                }
                if (newWeight != 0) {
                    weight.setText(String.valueOf(newWeight));
                    weight.setTextColor(context.getResources().getColor(R.color.black_dark));
                }
                if (newReligion != null) {
                    religion.setText(newReligion.getName());
                    religion.setTextColor(context.getResources().getColor(R.color.black_dark));
                }
                if (newCaste != null) {
                    caste.setText(newCaste.getName());
                    caste.setTextColor(context.getResources().getColor(R.color.black_dark));
                }
                if (newGotra != null) {
                    gotra.setText(newGotra.getName());
                    gotra.setTextColor(context.getResources().getColor(R.color.black_dark));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void saveInfo() {
        try {
            Profile profile = new Profile();
            if (Prefs.getProfile(context) != null) {
                profile = Prefs.getProfile(context);
            }
            if (newReligion != null && newCaste != null) {
                profile.setReligion(newReligion);
                profile.setCaste(newCaste);
            }
            if (newGotra != null)
                profile.setCaste(newCaste);
            profile.setHeight(newHeight);
            profile.setWeight(newWeight);
            profile.setManglik(newManglik);
            Prefs.setProfile(context, profile);
            Log.e("Save Screen", "2");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        religion = (TextView) rootView.findViewById(R.id.religion);
        height = (TextView) rootView.findViewById(R.id.height);
        caste = (TextView) rootView.findViewById(R.id.caste);
        gotra = (TextView) rootView.findViewById(R.id.gotra);
        weight = (ExtendedEditText) rootView.findViewById(R.id.weight);
        manglik = (Spinner) rootView.findViewById(R.id.manglik);
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
