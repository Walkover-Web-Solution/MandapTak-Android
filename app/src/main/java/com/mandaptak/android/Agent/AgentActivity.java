package com.mandaptak.android.Agent;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.mandaptak.android.Adapter.AgentProfilesAdapter;
import com.mandaptak.android.Models.AgentProfileModel;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.mandaptak.android.Views.ExtendedEditText;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import me.iwf.photopicker.utils.Prefs;

public class AgentActivity extends AppCompatActivity {
  Common mApp;
  Context context;
  ListView profileList;
  AgentProfilesAdapter adapter;
  ArrayList<AgentProfileModel> profileModels = new ArrayList<>();
  TextView addProfile, availableCredits;
  int creditBalance = 0;
  ProgressBar progressBar;
  int limit = 15;
  int skip = 0;
  Boolean loadMore = false;
  Boolean isSearching = false;
  SearchView searchView;
  int lastResultSize = 0;
  Timer mTimer;
  String deductCredit = "10";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_agent);
    context = this;
    mApp = (Common) getApplicationContext();
    try {
      getSupportActionBar().setTitle("Representative Home");
    } catch (Exception ignored) {
    }
    progressBar = (ProgressBar) findViewById(R.id.progress);
    profileList = (ListView) findViewById(R.id.list);
    addProfile = (TextView) findViewById(R.id.add_button);
    availableCredits = (TextView) findViewById(R.id.available_credits);
    addProfile.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (creditBalance >= 20) {

          if (mApp.isNetworkAvailable(context)) {
            final View permissionDialog = View.inflate(context, R.layout.add_user_dialog, null);
            final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setView(permissionDialog);
            final ExtendedEditText etNumber = (ExtendedEditText) permissionDialog.findViewById(R.id.number);
            final RangeBar userCreditsBar = (RangeBar) permissionDialog.findViewById(R.id.user_credits_seek);
            userCreditsBar.setSeekPinByIndex(0);
            final TextView credit_text = (TextView) permissionDialog.findViewById(R.id.credit_text);
            userCreditsBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
              @Override
              public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                deductCredit = rightPinValue;
                credit_text.setText(rightPinValue + " Credits");
              }
            });
            AppCompatButton giveButton = (AppCompatButton) permissionDialog.findViewById(R.id.give_button);
            final Spinner relations = (Spinner) permissionDialog.findViewById(R.id.relations);
            relations.setAdapter(ArrayAdapter.createFromResource(context,
                R.array.relation_array, R.layout.location_list_item));
            etNumber.setPrefix("+91");
            giveButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                String mobileNumber = etNumber.getText().toString();
                if (!mobileNumber.equals("")) {
                  if (mobileNumber.length() == 10) {
                    alertDialog.dismiss();
                    if (creditBalance >= Integer.valueOf(deductCredit) + 10) {
                      createUser(mobileNumber, relations.getSelectedItem().toString(), deductCredit);
                    } else {
                      mApp.showToast(context, "Insufficient Balance");
                    }
                  } else {
                    mApp.showToast(context, "Invalid Mobile Number");
                  }
                } else {
                  mApp.showToast(context, "Enter Mobile Number");
                }
              }
            });
            alertDialog.show();
          } else {
            mApp.showToast(context, "Internet connection required");
          }
        } else {
          mApp.showToast(context, "Insufficient Balance");
        }
      }
    });
    adapter = new AgentProfilesAdapter(AgentActivity.this, profileModels);
    profileList.setAdapter(adapter);
    if (mApp.isNetworkAvailable(context)) {
      isSearching = false;
      getProfiles();
    }
    profileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AgentProfileModel agentProfileModel = (AgentProfileModel) parent.getItemAtPosition(position);
        String name;
        if (agentProfileModel.getName() != null && !agentProfileModel.getName().equalsIgnoreCase(""))
          name = agentProfileModel.getName();
        else
          name = agentProfileModel.getNumber();
        Intent detailIntent = new Intent(context, ClientDetailActivity.class);
        detailIntent.putExtra("data", agentProfileModel.getProfileObject().getObjectId()).putExtra("name", name).putExtra("userId", agentProfileModel.getUserId());
        startActivity(detailIntent);
      }
    });
    profileList.setOnScrollListener(new AbsListView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
        int threshold = 1;
        int count = profileList.getCount();

        if (scrollState == SCROLL_STATE_IDLE) {
          if (profileList.getLastVisiblePosition() >= count
              - threshold) {
            if (lastResultSize > 0) {
              if (!isSearching)
                getProfiles();
              else if (!searchView.getQuery().toString().equals("")) {
                getSearchResults(searchView.getQuery().toString());
              }
            }
          }
        }
      }

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

      }
    });
  }

  protected void createUser(final String mobileNumber, String relation, final String credit) {
    if (mApp.isNetworkAvailable(context)) {
      mApp.show_PDialog(context, "Creating User...");
      final HashMap<String, Object> params = new HashMap<>();
      params.put("mobile", mobileNumber);
      params.put("relation", relation);
      params.put("agentId", ParseUser.getCurrentUser().getObjectId());
      ParseCloud.callFunctionInBackground("addNewUserForAgent", params, new FunctionCallback<Object>() {
        @Override
        public void done(Object o, ParseException e) {
          mApp.dialog.dismiss();
          if (e == null) {
            if (!credit.equalsIgnoreCase("10")) {
              ParseQuery<ParseUser> query = ParseUser.getQuery();
              query.whereEqualTo("username", mobileNumber);
              query.getFirstInBackground(new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                  if (e == null) {
                    final HashMap<String, Object> balParam = new HashMap<>();
                    String deductValue = String.valueOf(Integer.valueOf(credit) - 10);
                    balParam.put("amount", deductValue);
                    balParam.put("userid", parseUser.getObjectId());
                    balParam.put("agentid", ParseUser.getCurrentUser().getObjectId());
                    ParseCloud.callFunctionInBackground("fundTransfer", balParam, new FunctionCallback<Object>() {
                      @Override
                      public void done(Object o, ParseException e) {
                        if (e == null) {
                          resetProfileData();
                          getProfiles();
                          mApp.showToast(context, "Profile Created");
                        } else {
                          mApp.showToast(context, e.getMessage());
                          e.printStackTrace();
                        }
                      }
                    });
                  }
                }
              });
            }
          } else {
            mApp.showToast(context, e.getMessage());
            e.printStackTrace();
          }
        }
      });
    }
  }

  public void resetProfileData() {
    skip = 0;
    limit = 15;
    loadMore = false;
    isSearching = false;
    profileModels.clear();
  }

  public void getProfiles() {
    profileList.setVisibility(View.GONE);
    progressBar.setVisibility(View.VISIBLE);
    ParseQuery<ParseObject> balanceQuery = new ParseQuery<>("UserCredits");
    balanceQuery.whereEqualTo("userId", ParseUser.getCurrentUser());
    balanceQuery.getFirstInBackground(new GetCallback<ParseObject>() {
      @Override
      public void done(ParseObject parseObject, ParseException e) {
        if (e == null) {
          creditBalance = parseObject.getInt("credits");
        }
        availableCredits.setText(creditBalance + " Credits");
      }
    });
    ParseQuery<ParseObject> query = new ParseQuery<>("UserProfile");
    query.include("profileId.userId");
    query.orderByDescending("createdAt");
    query.whereEqualTo("userId", ParseUser.getCurrentUser());
    query.whereNotEqualTo("profileId", ParseObject.createWithoutData("Profile", Prefs.getProfileId(context)));
    query.whereEqualTo("relation", "Agent");
    if (loadMore) {
      limit = limit + 15;
      query.setLimit(15);
    } else
      query.setLimit(limit);
    query.setSkip(skip);
    query.findInBackground(new FindCallback<ParseObject>() {
      @Override
      public void done(List<ParseObject> list, ParseException e) {
        if (e == null) {
          lastResultSize = list.size();
          if (lastResultSize > 0) {
            for (ParseObject parseObject : list) {
              try {
                final ParseObject profileObject = parseObject.getParseObject("profileId");
                boolean isComplete = profileObject.getBoolean("isComplete");
                final AgentProfileModel agentProfileModel = new AgentProfileModel();
                agentProfileModel.setProfileObject(profileObject);
                agentProfileModel.setNumber(profileObject.getParseUser("userId").getUsername());
                agentProfileModel.setUserId(profileObject.getParseUser("userId").getObjectId());
                if (isComplete) {
                  SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
                  String date = sdf.format(profileObject.getUpdatedAt());
                  agentProfileModel.setCreateDate(date);
                  agentProfileModel.setActive(profileObject.getBoolean("isActive"));
                  agentProfileModel.setImageUrl(profileObject.getParseFile("profilePic").getUrl());
                  agentProfileModel.setName(profileObject.getString("name"));
                  agentProfileModel.setComplete(true);
                } else {
                  SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
                  String date = sdf.format(profileObject.getUpdatedAt());
                  agentProfileModel.setCreateDate(date);
                  agentProfileModel.setActive(profileObject.getBoolean("isActive"));
                  agentProfileModel.setImageUrl("android.resource://com.mandaptak.android/drawable/com_facebook_profile_picture_blank_square");
                  agentProfileModel.setComplete(false);
                }
                profileModels.add(agentProfileModel);
              } catch (Exception e1) {
                e1.printStackTrace();
              }
            }
            adapter.notifyDataSetChanged();
            if (loadMore) {
              int position = profileList.getLastVisiblePosition();
              profileList.setSelectionFromTop(position, 0);
            }
            skip = limit;
            loadMore = true;
          } else {
            profileList.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
          }
        } else {
          profileList.setVisibility(View.GONE);
          progressBar.setVisibility(View.GONE);
          mApp.showToast(context, e.getMessage());
          e.printStackTrace();
        }
        profileList.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

      }
    });
  }

  public void getSearchResults(String searchText) {
    profileList.setVisibility(View.GONE);
    progressBar.setVisibility(View.VISIBLE);
    ParseQuery innerQuery = new ParseQuery("Profile");
    if (Character.isDigit(searchText.charAt(0))) {
      ParseQuery<ParseUser> queryUser = ParseUser.getQuery();
      queryUser.whereMatches("username", "(" + searchText + ")", "i");
      innerQuery.whereMatchesQuery("userId", queryUser);
    } else {
      innerQuery.whereMatches("name", "(" + searchText + ")", "i");
    }
    ParseQuery<ParseObject> query = new ParseQuery<>("UserProfile");
    query.include("profileId.userId");
    query.orderByDescending("createdAt");
    query.whereEqualTo("userId", ParseUser.getCurrentUser());
    query.whereNotEqualTo("profileId", ParseObject.createWithoutData("Profile", Prefs.getProfileId(context)));
    query.whereEqualTo("relation", "Agent");
    query.whereMatchesQuery("profileId", innerQuery);
    if (loadMore) {
      limit = limit + 15;
      query.setLimit(15);
    } else
      query.setLimit(limit);
    query.setSkip(skip);
    query.findInBackground(new FindCallback<ParseObject>() {
      @Override
      public void done(List<ParseObject> list, ParseException e) {
        if (e == null) {
          lastResultSize = list.size();
          if (lastResultSize > 0) {
            for (ParseObject parseObject : list) {
              try {
                final ParseObject profileObject = parseObject.getParseObject("profileId");
                boolean isComplete = profileObject.getBoolean("isComplete");
                final AgentProfileModel agentProfileModel = new AgentProfileModel();
                agentProfileModel.setProfileObject(profileObject);
                agentProfileModel.setNumber(profileObject.getParseUser("userId").getUsername());
                agentProfileModel.setUserId(profileObject.getParseUser("userId").getObjectId());
                if (isComplete) {
                  SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
                  String date = sdf.format(profileObject.getUpdatedAt());
                  agentProfileModel.setCreateDate(date);
                  agentProfileModel.setActive(profileObject.getBoolean("isActive"));
                  agentProfileModel.setImageUrl(profileObject.getParseFile("profilePic").getUrl());
                  agentProfileModel.setName(profileObject.getString("name"));
                  agentProfileModel.setComplete(true);
                } else {
                  SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
                  String date = sdf.format(profileObject.getUpdatedAt());
                  agentProfileModel.setCreateDate(date);
                  agentProfileModel.setActive(profileObject.getBoolean("isActive"));
                  agentProfileModel.setImageUrl("android.resource://com.mandaptak.android/drawable/com_facebook_profile_picture_blank_square");
                  agentProfileModel.setComplete(false);
                }
                profileModels.add(agentProfileModel);
              } catch (Exception e1) {
                e1.printStackTrace();
              }
            }
            adapter.notifyDataSetChanged();
            if (loadMore) {
              int position = profileList.getLastVisiblePosition();
              profileList.setSelectionFromTop(position, 0);
            }
            skip = limit;
            loadMore = true;
          } else {
            profileList.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
          }
        } else {
          profileList.setVisibility(View.GONE);
          progressBar.setVisibility(View.GONE);
          mApp.showToast(context, e.getMessage());
          e.printStackTrace();
        }
        profileList.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.agent_search_actionbar, menu);
    SearchManager searchManager =
        (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    searchView =
        (SearchView) menu.findItem(R.id.menu_search).getActionView();
    searchView.setSearchableInfo(
        searchManager.getSearchableInfo(getComponentName()));
    searchView.setOnCloseListener(new SearchView.OnCloseListener() {
      @Override
      public boolean onClose() {
        resetProfileData();
        getProfiles();
        return false;
      }
    });
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        return false;
      }

      @Override
      public boolean onQueryTextChange(final String newText) {
        if (mTimer != null) {
          mTimer.cancel();
        }
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
          @Override
          public void run() {
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                if (newText != null && !newText.equals("")) {
                  skip = 0;
                  limit = 15;
                  loadMore = false;
                  isSearching = true;
                  profileModels.clear();
                  getSearchResults(newText);
                }
              }
            });

          }
        }, 2500);

        return true;
      }
    });

    return true;
  }
}


