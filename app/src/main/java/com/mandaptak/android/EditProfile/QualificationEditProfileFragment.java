package com.mandaptak.android.EditProfile;

import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.pierry.simpletoast.SimpleToast;
import com.mandaptak.android.Utils.Common;
import com.mandaptak.android.Models.ParseNameModel;
import com.mandaptak.android.R;
import com.mandaptak.android.Views.ExtendedEditText;
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

public class QualificationEditProfileFragment extends Fragment {
    private ArrayList<ParseNameModel> industryList;
    private LinearLayout mainWorkLayout, mainEducationLayout;
    private TextView educationMoreButton, industry;
    private ExtendedEditText currentIncome;
    private EditText company, designation;
    private Context context;
    private Spinner workAfterMarriage;
    private View rootView;
    private LayoutInflater layoutInflater;
    private View educationLayoutChild2, educationLayoutChild3;
    private ImageView eduChildClose2, eduChildClose3;
    private ParseObject newIndustry;
    private int newWorkAfterMarriage = 0, newCurrentIncome = 0;
    private String newDesignation, newCompany;
    private ParseNameModel newEducationDetail1, newEducationDetail2, newEducationDetail3;
    private TextView eduChildDegree1, eduChildDegree2, eduChildDegree3;
    private TextView eduChildDegreeBranch1, eduChildDegreeBranch2, eduChildDegreeBranch3;
    private Common mApp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_qualification_edit_profile, container, false);
        context = getActivity();
        mApp = (Common) context.getApplicationContext();
        init();

        workAfterMarriage.setAdapter(ArrayAdapter.createFromResource(getActivity(),
                R.array.wam_array, R.layout.location_list_item));
        workAfterMarriage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                newWorkAfterMarriage = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        educationMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mainEducationLayout.getChildCount() == 0 && newEducationDetail1 != null) {
                    mainEducationLayout.addView(educationLayoutChild2);
                } else if (newEducationDetail1 == null) {
                    SimpleToast.warning(context, "Fill your education information");
                } else if (mainEducationLayout.getChildCount() == 1 && newEducationDetail2 != null) {
                    mainEducationLayout.addView(educationLayoutChild3);
                    educationMoreButton.setVisibility(View.GONE);
                } else if (newEducationDetail2 == null) {
                    SimpleToast.warning(context, "Fill your education information");
                }
            }
        });

        eduChildDegree1.setOnClickListener(new View.OnClickListener() {
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
                                                            eduChildDegree1.setText(list.get(index).getParseObject("degreeId").getString("name"));
                                                            eduChildDegreeBranch1.setText(newEducationDetail1.getName());
                                                        }
                                                    });
                                            AlertDialog alert = conductor.create();
                                            mApp.dialog.dismiss();
                                            alert.show();
                                        } else {
                                            newEducationDetail1 = new ParseNameModel(list.get(0).getString("name"), list.get(0));
                                            eduChildDegree1.setText(list.get(0).getParseObject("degreeId").getString("name"));
                                            eduChildDegreeBranch1.setText(newEducationDetail1.getName());
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
                                                                eduChildDegree1.setText(list.get(index).getParseObject("degreeId").getString("name"));
                                                                eduChildDegreeBranch1.setText(newEducationDetail1.getName());
                                                            }
                                                        });
                                                AlertDialog alert = conductor.create();
                                                mApp.dialog.dismiss();
                                                alert.show();
                                            } else {
                                                newEducationDetail1 = new ParseNameModel(list.get(0).getString("name"), list.get(0));
                                                eduChildDegree1.setText(list.get(0).getParseObject("degreeId").getString("name"));
                                                eduChildDegreeBranch1.setText(newEducationDetail1.getName());
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
        eduChildDegree2.setOnClickListener(new View.OnClickListener() {
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
                                                            newEducationDetail2 = new ParseNameModel(list.get(index).getString("name"), list.get(index));
                                                            eduChildDegree2.setText(list.get(index).getParseObject("degreeId").getString("name"));
                                                            eduChildDegreeBranch2.setText(newEducationDetail2.getName());
                                                        }
                                                    });
                                            AlertDialog alert = conductor.create();
                                            mApp.dialog.dismiss();
                                            alert.show();
                                        } else {
                                            newEducationDetail2 = new ParseNameModel(list.get(0).getString("name"), list.get(0));
                                            eduChildDegree2.setText(list.get(0).getParseObject("degreeId").getString("name"));
                                            eduChildDegreeBranch2.setText(newEducationDetail2.getName());
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
                                                                newEducationDetail2 = new ParseNameModel(list.get(index).getString("name"), list.get(index));
                                                                eduChildDegree2.setText(list.get(index).getParseObject("degreeId").getString("name"));
                                                                eduChildDegreeBranch2.setText(newEducationDetail2.getName());
                                                            }
                                                        });
                                                AlertDialog alert = conductor.create();
                                                mApp.dialog.dismiss();
                                                alert.show();
                                            } else {
                                                newEducationDetail2 = new ParseNameModel(list.get(0).getString("name"), list.get(0));
                                                eduChildDegree2.setText(list.get(0).getParseObject("degreeId").getString("name"));
                                                eduChildDegreeBranch2.setText(newEducationDetail2.getName());
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
        eduChildDegree3.setOnClickListener(new View.OnClickListener() {
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
                                                            newEducationDetail3 = new ParseNameModel(list.get(index).getString("name"), list.get(index));
                                                            eduChildDegree3.setText(list.get(index).getParseObject("degreeId").getString("name"));
                                                            eduChildDegreeBranch3.setText(newEducationDetail3.getName());
                                                        }
                                                    });
                                            AlertDialog alert = conductor.create();
                                            mApp.dialog.dismiss();
                                            alert.show();
                                        } else {
                                            newEducationDetail3 = new ParseNameModel(list.get(0).getString("name"), list.get(0));
                                            eduChildDegree3.setText(list.get(0).getParseObject("degreeId").getString("name"));
                                            eduChildDegreeBranch3.setText(newEducationDetail3.getName());
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
                                                                newEducationDetail3 = new ParseNameModel(list.get(index).getString("name"), list.get(index));
                                                                eduChildDegree3.setText(list.get(index).getParseObject("degreeId").getString("name"));
                                                                eduChildDegreeBranch3.setText(newEducationDetail3.getName());
                                                            }
                                                        });
                                                AlertDialog alert = conductor.create();
                                                mApp.dialog.dismiss();
                                                alert.show();
                                            } else {
                                                newEducationDetail3 = new ParseNameModel(list.get(0).getString("name"), list.get(0));
                                                eduChildDegree3.setText(list.get(0).getParseObject("degreeId").getString("name"));
                                                eduChildDegreeBranch3.setText(newEducationDetail3.getName());
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
        industry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (industryList != null) {
                    AlertDialog.Builder conductor = new AlertDialog.Builder(
                            context);
                    conductor.setTitle("Select Work Industry");
                    ArrayList<String> arrayList = new ArrayList<>();
                    for (ParseNameModel parseNameModel : industryList) {
                        arrayList.add(parseNameModel.getName());
                    }
                    Object[] objectList = arrayList.toArray();
                    String[] stringArray = Arrays.copyOf(objectList, objectList.length, String[].class);
                    conductor.setItems(stringArray,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int index) {
                                    newIndustry = industryList.get(index).getParseObject();
                                    industry.setText(industryList.get(index).getName());
                                }
                            });
                    AlertDialog alert = conductor.create();
                    alert.show();
                } else {
                    SimpleToast.warning(context, "Error loading content");
                }
            }
        });
        currentIncome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    newCurrentIncome = 0;
                    currentIncome.setPrefix("");
                } else {
                    currentIncome.setPrefix("Rs. ");
                    newCurrentIncome = Integer.parseInt(editable.toString());
                }
            }
        });
        company.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                newCompany = editable.toString();
            }
        });
        designation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                newDesignation = editable.toString();
            }
        });
        eduChildClose2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mainEducationLayout.getChildCount() == 1) {
                    eduChildDegree2.setText(null);
                    eduChildDegreeBranch2.setText(null);
                    mainEducationLayout.removeView(educationLayoutChild2);
                    newEducationDetail2 = null;
                } else {
                    if (newEducationDetail3 != null) {
                        eduChildDegree2.setText(eduChildDegree3.getText());
                        eduChildDegreeBranch2.setText(eduChildDegreeBranch3.getText());
                        eduChildDegree3.setText(null);
                        eduChildDegreeBranch3.setText(null);
                        mainEducationLayout.removeView(educationLayoutChild3);
                        newEducationDetail2 = newEducationDetail3;
                        newEducationDetail3 = null;
                    } else {
                        eduChildDegree2.setText(null);
                        eduChildDegreeBranch2.setText(null);
                        mainEducationLayout.removeView(educationLayoutChild2);
                        newEducationDetail2 = null;
                        eduChildDegree3.setText(null);
                        eduChildDegreeBranch3.setText(null);
                        mainEducationLayout.removeView(educationLayoutChild3);
                        newEducationDetail3 = null;
                    }
                }

            }
        });
        eduChildClose3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newEducationDetail3 = null;
                mainEducationLayout.removeView(educationLayoutChild3);
                educationMoreButton.setVisibility(View.VISIBLE);
            }
        });
        getParseData();
        return rootView;
    }

    private void getParseData() {
        mApp.show_PDialog(context, "Loading..");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Profile");
        query.getInBackground(ParseUser.getCurrentUser().getParseObject("profileId").getObjectId(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    try {
                        newWorkAfterMarriage = parseObject.getInt("workAfterMarriage");
                        newCurrentIncome = parseObject.getInt("package");
                        newDesignation = parseObject.getString("designation");
                        newCompany = parseObject.getString("placeOfWork");
                        newIndustry = parseObject.getParseObject("industryId");
                        ParseObject tmpEdu1 = parseObject.getParseObject("education1");
                        ParseObject tmpEdu2 = parseObject.getParseObject("education2");
                        ParseObject tmpEdu3 = parseObject.getParseObject("education3");
                        if (tmpEdu1 != null) {
                            newEducationDetail1 = new ParseNameModel(tmpEdu1.fetchIfNeeded().getString("name"), tmpEdu1);
                            eduChildDegreeBranch1.setText(newEducationDetail1.getName());
                            eduChildDegree1.setText(newEducationDetail1.getParseObject().fetchIfNeeded().getParseObject("degreeId").fetchIfNeeded().getString("name"));
                        }
                        if (tmpEdu2 != null) {
                            mainEducationLayout.addView(educationLayoutChild2);
                            newEducationDetail2 = new ParseNameModel(tmpEdu2.fetchIfNeeded().getString("name"), tmpEdu2);
                            eduChildDegreeBranch2.setText(newEducationDetail2.getName());
                            eduChildDegree2.setText(newEducationDetail2.getParseObject().fetchIfNeeded().getParseObject("degreeId").fetchIfNeeded().getString("name"));
                        }
                        if (tmpEdu3 != null) {
                            mainEducationLayout.addView(educationLayoutChild3);
                            educationMoreButton.setVisibility(View.GONE);
                            newEducationDetail3 = new ParseNameModel(tmpEdu3.fetchIfNeeded().getString("name"), tmpEdu3);
                            eduChildDegreeBranch3.setText(newEducationDetail3.getName());
                            eduChildDegree3.setText(newEducationDetail3.getParseObject().fetchIfNeeded().getParseObject("degreeId").fetchIfNeeded().getString("name"));
                        }
                        if (newCurrentIncome != 0)
                            currentIncome.setText(String.valueOf(newCurrentIncome));
                        if (newIndustry != null)
                            industry.setText(newIndustry.fetchIfNeeded().getString("name"));
                        if (newCompany != null)
                            company.setText(newCompany);
                        if (newDesignation != null)
                            designation.setText(newDesignation);
                        workAfterMarriage.setSelection(newWorkAfterMarriage);
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

    private ArrayList<ParseNameModel> getIndustryList() {
        final ArrayList<ParseNameModel> models = new ArrayList<>();
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Industries");
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

    void init() {
        mainWorkLayout = (LinearLayout) rootView.findViewById(R.id.work_layout);
        mainEducationLayout = (LinearLayout) rootView.findViewById(R.id.education_layout);
        educationMoreButton = (TextView) rootView.findViewById(R.id.education_more_button);
        currentIncome = (ExtendedEditText) rootView.findViewById(R.id.current_income);
        industry = (TextView) rootView.findViewById(R.id.industry);
        company = (EditText) rootView.findViewById(R.id.company);
        designation = (EditText) rootView.findViewById(R.id.designation);
        workAfterMarriage = (Spinner) rootView.findViewById(R.id.work_after_marriage);
        layoutInflater = (LayoutInflater)
                getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        educationLayoutChild2 = layoutInflater.inflate(R.layout.item_education_layout, mainWorkLayout, false);
        educationLayoutChild3 = layoutInflater.inflate(R.layout.item_education_layout, mainWorkLayout, false);
        eduChildDegree1 = (TextView) rootView.findViewById(R.id.degree);
        eduChildDegree2 = (TextView) educationLayoutChild2.findViewById(R.id.degree);
        eduChildDegree3 = (TextView) educationLayoutChild3.findViewById(R.id.degree);
        eduChildDegreeBranch1 = (TextView) rootView.findViewById(R.id.degree_branch);
        eduChildDegreeBranch2 = (TextView) educationLayoutChild2.findViewById(R.id.degree_branch);
        eduChildDegreeBranch3 = (TextView) educationLayoutChild3.findViewById(R.id.degree_branch);
        eduChildClose2 = (ImageView) educationLayoutChild2.findViewById(R.id.remove_button);
        eduChildClose3 = (ImageView) educationLayoutChild3.findViewById(R.id.remove_button);
        industryList = getIndustryList();
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

    private void saveInfo() {
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Profile");
        parseQuery.getInBackground(ParseUser.getCurrentUser().getParseObject("profileId").getObjectId(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (newCurrentIncome != 0)
                    parseObject.put("package", newCurrentIncome);
                else
                    parseObject.put("package", JSONObject.NULL);
                if (newCompany != null)
                    parseObject.put("placeOfWork", newCompany);
                else
                    parseObject.put("placeOfWork", JSONObject.NULL);
                if (newDesignation != null)
                    parseObject.put("designation", newDesignation);
                else
                    parseObject.put("designation", JSONObject.NULL);
                if (newIndustry != null)
                    parseObject.put("industryId", newIndustry);
                if (newEducationDetail1 != null)
                    parseObject.put("education1", newEducationDetail1.getParseObject());
                else
                    parseObject.put("education1", JSONObject.NULL);
                if (newEducationDetail2 != null)
                    parseObject.put("education2", newEducationDetail2.getParseObject());
                else
                    parseObject.put("education2", JSONObject.NULL);
                if (newEducationDetail3 != null)
                    parseObject.put("education3", newEducationDetail3.getParseObject());
                else
                    parseObject.put("education3", JSONObject.NULL);
                parseObject.put("workAfterMarriage", newWorkAfterMarriage);
                parseObject.saveInBackground();
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
