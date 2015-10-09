package com.mandaptak.android.EditProfile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
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

public class QualificationEditProfileFragment extends Fragment {
  private LinearLayout mainEducationLayout;
  private TextView educationMoreButton, industry;
  private ExtendedEditText currentIncome;
  private EditText company, designation;
  private Context context;
  private Spinner workAfterMarriage;
  private View rootView;
  private View educationLayoutChild2, educationLayoutChild3;
  private ImageView eduChildClose2, eduChildClose3;
  private ParseNameModel newIndustry;
  private int newWorkAfterMarriage = 0;
  private long newCurrentIncome = -1;
  private String newDesignation, newCompany;
  private ParseNameModel newEducationDetail1, newEducationDetail2, newEducationDetail3;
  private TextView eduChildDegree1, eduChildDegree2, eduChildDegree3;
  private TextView eduChildDegreeBranch1, eduChildDegreeBranch2, eduChildDegreeBranch3;
  private Common mApp;
  private Boolean isStarted = false;
  private Boolean isVisible = false;
  private ArrayList<ParseNameModel> industryList = new ArrayList<>();
  private int forViewIndex = 0;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.fragment_qualification_edit_profile, container, false);
    context = getActivity();
    mApp = (Common) context.getApplicationContext();
    init();
    getIndustries();
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
          mApp.showToast(context, "Fill your education information");
        } else if (mainEducationLayout.getChildCount() == 1 && newEducationDetail2 != null) {
          mainEducationLayout.addView(educationLayoutChild3);
          educationMoreButton.setVisibility(View.GONE);
        } else if (newEducationDetail2 == null) {
          mApp.showToast(context, "Fill your education information");
        }
      }
    });

    eduChildDegree1.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        forViewIndex = 1;
        showDegreeDialog();
      }
    });

    eduChildDegree2.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        forViewIndex = 2;
        showDegreeDialog();
      }
    });
    eduChildDegree3.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        forViewIndex = 3;
        showDegreeDialog();
      }
    });


    industry.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        showIndustryList();
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
          newCurrentIncome = -1;
          currentIncome.setPrefix("");
        } else {
          currentIncome.setPrefix("Rs. ");
          newCurrentIncome = Long.parseLong(editable.toString().replaceAll("[^0-9]+", ""));
        }
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
          educationMoreButton.setVisibility(View.VISIBLE);
        }

      }
    });
    eduChildClose3.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        newEducationDetail3 = null;
        eduChildDegree3.setText(null);
        eduChildDegreeBranch3.setText(null);
        mainEducationLayout.removeView(educationLayoutChild3);
        educationMoreButton.setVisibility(View.VISIBLE);
      }
    });
    return rootView;
  }

  private void showDegreeDialog() {
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
      getDegreeList(null, listView, alertDialog);
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
        getDegreeList(editable, listView, alertDialog);
      }
    });
    alertDialog.show();
  }

  private void getDegreeList(Editable editable, final ListView listView, final AlertDialog alertDialog) {
    final ArrayList<ParseNameModel> degreeList = new ArrayList<>();
    ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Degree");
    parseQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
    if (null != editable && editable.length() != 0) {
      parseQuery.whereMatches("name", "(" + editable.toString() + ")", "i");
    }
    parseQuery.findInBackground(new FindCallback<ParseObject>() {
      @Override
      public void done(List<ParseObject> list, ParseException e) {
        if (list != null && list.size() > 0) {
          for (ParseObject model : list) {
            degreeList.add(new ParseNameModel(model.getString("name"), "Degree", model.getObjectId()));
          }
          listView.setAdapter(new DataAdapter(context, degreeList));
          listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
              if (checkForRedundantDegree(degreeList.get(i).getName())) {
                alertDialog.dismiss();
                mApp.show_PDialog(context, "Loading..");
                getAndShowSpecialization(i, degreeList);
              } else {
                mApp.showToast(context, "Already Selected Please Choose Different Degree");
              }
            }
          });
        } else {
          e.printStackTrace();
        }
      }

    });
  }

  private boolean checkForRedundantDegree(String newDegreeName) {
    boolean returnFlag = true;
    switch (forViewIndex) {
      case 1:
        if (null != newEducationDetail2 && newDegreeName.equals(newEducationDetail2.getDegreeName())) {
          returnFlag = false;
        }
        if (null != newEducationDetail3 && newDegreeName.equals(newEducationDetail3.getDegreeName())) {
          returnFlag = false;
        }
//        eduChildDegree1.setText(degreeName);
//        eduChildDegreeBranch1.setText(specializationName);
        break;
      case 2:
        if (null != newEducationDetail1 && newDegreeName.equals(newEducationDetail1.getDegreeName())) {
          returnFlag = false;
        }
        if (null != newEducationDetail3 && newDegreeName.equals(newEducationDetail3.getDegreeName())) {
          returnFlag = false;
        }
//        eduChildDegree2.setText(degreeName);
//        eduChildDegreeBranch2.setText(specializationName);
        break;
      case 3:
        if (null != newEducationDetail1 && newDegreeName.equals(newEducationDetail1.getDegreeName())) {
          returnFlag = false;
        }
        if (null != newEducationDetail2 && newDegreeName.equals(newEducationDetail2.getDegreeName())) {
          returnFlag = false;
        }
//        eduChildDegree3.setText(degreeName);
//        eduChildDegreeBranch3.setText(specializationName);
        break;
      default:
        break;
    }
    return returnFlag;
  }

  private void getAndShowSpecialization(int i, ArrayList<ParseNameModel> degreeList) {
    final AlertDialog.Builder conductor = new AlertDialog.Builder(context);
    conductor.setTitle("Select Specialization");
    ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Specialization");
    parseQuery.include("degreeId");
    parseQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
    parseQuery.whereEqualTo("degreeId", ParseObject.createWithoutData(degreeList.get(i).getClassName(), degreeList.get(i).getParseObjectId()));
    parseQuery.findInBackground(new FindCallback<ParseObject>() {
      @Override
      public void done(final List<ParseObject> list, ParseException e) {
        if (list != null && list.size() > 0) {
          ArrayList<String> arrayList = new ArrayList<>();
          for (ParseObject model : list) {
            try {
              arrayList.add(model.getString("name"));
            } catch (Exception e1) {
              e1.printStackTrace();
            }
          }
          Object[] objectList = arrayList.toArray();
          String[] stringArray = Arrays.copyOf(objectList, objectList.length, String[].class);
          try {
            if (stringArray.length > 1) {
              conductor.setItems(stringArray,
                  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int index) {
                      setParseModel(index, list);
                    }
                  });
              AlertDialog alert = conductor.create();
              mApp.dialog.dismiss();
              alert.show();
            } else {
              setParseModel(0, list);
              mApp.dialog.dismiss();
            }
          } catch (Exception e1) {
            e1.printStackTrace();
          }
        }
      }
    });
  }

  private void setParseModel(int index, List<ParseObject> list) {
    String specializationName = list.get(index).getString("name");
    String degreeName = list.get(index).getParseObject("degreeId").getString("name");

    ParseNameModel specialization = new ParseNameModel(specializationName, "Specialization",
        list.get(index).getObjectId(), degreeName);

    switch (forViewIndex) {
      case 1:
        newEducationDetail1 = specialization;
        eduChildDegree1.setText(degreeName);
        eduChildDegreeBranch1.setText(specializationName);
        break;
      case 2:
        newEducationDetail2 = specialization;
        eduChildDegree2.setText(degreeName);
        eduChildDegreeBranch2.setText(specializationName);
        break;
      case 3:
        newEducationDetail3 = specialization;
        eduChildDegree3.setText(degreeName);
        eduChildDegreeBranch3.setText(specializationName);
        break;
      default:
        break;
    }
  }

  public void getParseData() {
    if (Prefs.getProfile(context) != null) {
      Profile profile = Prefs.getProfile(context);
      try {
        mainEducationLayout.removeAllViews();
        newWorkAfterMarriage = profile.getWorkAfterMarriage();
        newCurrentIncome = profile.getIncome();
        newDesignation = profile.getDesignation();
        newCompany = profile.getCompany();
        newIndustry = profile.getIndustry();
        newEducationDetail1 = profile.getEducation1();
        newEducationDetail2 = profile.getEducation2();
        newEducationDetail3 = profile.getEducation3();
        if (null != newEducationDetail1) {
          eduChildDegreeBranch1.setText(newEducationDetail1.getName());
          eduChildDegree1.setText(newEducationDetail1.getDegreeName());
        }
        if (null != newEducationDetail2) {
          mainEducationLayout.addView(educationLayoutChild2);
          eduChildDegreeBranch2.setText(newEducationDetail2.getName());
          eduChildDegree2.setText(newEducationDetail2.getDegreeName());
        }
        if (null != newEducationDetail3) {
          mainEducationLayout.addView(educationLayoutChild3);
          educationMoreButton.setVisibility(View.GONE);
          eduChildDegreeBranch3.setText(newEducationDetail3.getName());
          eduChildDegree3.setText(newEducationDetail3.getDegreeName());
        } else {
          educationMoreButton.setVisibility(View.VISIBLE);
        }

        if (newCurrentIncome != -1)
          currentIncome.setText(String.valueOf(newCurrentIncome));
        if (newIndustry != null)
          industry.setText(newIndustry.getName());
        if (newCompany != null)
          if (!newCompany.trim().equals(""))
            company.setText(newCompany.trim());
        if (newDesignation != null)
          if (!newDesignation.trim().equals(""))
            designation.setText(newDesignation.trim());
        workAfterMarriage.setSelection(newWorkAfterMarriage);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private void getIndustries() {
    if (mApp.isNetworkAvailable(context)) {
      ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Industries");
      parseQuery.findInBackground(new FindCallback<ParseObject>() {
        @Override
        public void done(List<ParseObject> list, ParseException e) {
          if (list != null && list.size() > 0) {
            for (ParseObject model : list) {
              industryList.add(new ParseNameModel(model.getString("name"), "Industries", model.getObjectId()));
            }
          }
        }
      });
    }
  }

  private void showIndustryList() {
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
              newIndustry = industryList.get(index);
              industry.setText(newIndustry.getName());
            }
          });
      AlertDialog alert = conductor.create();
      alert.show();
    } else {
      getIndustries();
      mApp.showToast(context, "Please Try Again");
    }
  }

  void init() {
    mainEducationLayout = (LinearLayout) rootView.findViewById(R.id.education_layout);
    educationMoreButton = (TextView) rootView.findViewById(R.id.education_more_button);
    currentIncome = (ExtendedEditText) rootView.findViewById(R.id.current_income);
    industry = (TextView) rootView.findViewById(R.id.industry);
    company = (EditText) rootView.findViewById(R.id.company);
    designation = (EditText) rootView.findViewById(R.id.designation);
    workAfterMarriage = (Spinner) rootView.findViewById(R.id.work_after_marriage);
    LayoutInflater layoutInflater = (LayoutInflater)
        getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    educationLayoutChild2 = layoutInflater.inflate(R.layout.item_education_layout, mainEducationLayout, false);
    educationLayoutChild3 = layoutInflater.inflate(R.layout.item_education_layout, mainEducationLayout, false);
    eduChildDegree1 = (TextView) rootView.findViewById(R.id.degree);
    eduChildDegree2 = (TextView) educationLayoutChild2.findViewById(R.id.degree);
    eduChildDegree3 = (TextView) educationLayoutChild3.findViewById(R.id.degree);
    eduChildDegreeBranch1 = (TextView) rootView.findViewById(R.id.degree_branch);
    eduChildDegreeBranch2 = (TextView) educationLayoutChild2.findViewById(R.id.degree_branch);
    eduChildDegreeBranch3 = (TextView) educationLayoutChild3.findViewById(R.id.degree_branch);
    eduChildClose2 = (ImageView) educationLayoutChild2.findViewById(R.id.remove_button);
    eduChildClose3 = (ImageView) educationLayoutChild3.findViewById(R.id.remove_button);
    company.setFilters(new InputFilter[]{
        new InputFilter() {
          public CharSequence filter(CharSequence src, int start,
                                     int end, Spanned dst, int dstart, int dend) {
            if (src.equals("")) { // for backspace
              return src;
            }
            if (src.toString().matches("[a-zA-Z0-9]+")) {
              return src;
            }
            return src.toString().replaceAll("[^A-Za-z0-9 ]", "");
          }
        }
        , new InputFilter.LengthFilter(32)});
    designation.setFilters(new InputFilter[]{
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

  public void saveInfo() {
    try {
      newDesignation = designation.getText().toString();
      newCompany = company.getText().toString();
      Profile profile = new Profile();
      if (null != Prefs.getProfile(context)) {
        profile = Prefs.getProfile(context);
      }
      if (newCurrentIncome != -1)
        profile.setIncome(newCurrentIncome);
      if (null != newCompany && !newCompany.equals("") && !newCompany.trim().equals(""))
        profile.setCompany(newCompany);
      if (null != newDesignation && !newDesignation.equals("") && !newDesignation.trim().equals(""))
        profile.setDesignation(newDesignation);
      if (null != newIndustry)
        profile.setIndustry(newIndustry);
      if (null != newEducationDetail1)
        profile.setEducation1(newEducationDetail1);
//      if (null != newEducationDetail2)
      profile.setEducation2(newEducationDetail2);
//      if (null != newEducationDetail3)
      profile.setEducation3(newEducationDetail3);

      profile.setWorkAfterMarriage(newWorkAfterMarriage);
      Prefs.setProfile(context, profile);
      Log.e("Save Screen", "3");
    } catch (Exception ignored) {
    }
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
