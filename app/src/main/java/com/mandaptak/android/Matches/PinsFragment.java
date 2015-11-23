package com.mandaptak.android.Matches;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mandaptak.android.Adapter.PinsAdapter;
import com.mandaptak.android.FullProfile.FullProfileActivity;
import com.mandaptak.android.Models.MatchesModel;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.parse.DeleteCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.iwf.photopicker.utils.Prefs;

public class PinsFragment extends Fragment implements
    SwipeActionAdapter.SwipeActionListener {
  Common mApp;
  ListView listViewMatches;
  ArrayList<MatchesModel> pinsList = new ArrayList<>();
  TextView empty;
  ProgressBar progressBar;
  private View rootView;
  private Context context;
  private PinsAdapter pinsAdapter;
  protected SwipeActionAdapter mAdapter;
  private MatchesModel undoMatch;
  ParseObject userProfileObject;

  public PinsFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    init(inflater, container);
    getParseData();
    return rootView;
  }

  private void init(LayoutInflater inflater, ViewGroup container) {
    context = getActivity();
    mApp = (Common) context.getApplicationContext();
    rootView = inflater.inflate(R.layout.fragment_list, container, false);
    listViewMatches = (ListView) rootView.findViewById(R.id.list);
    empty = (TextView) rootView.findViewById(R.id.empty);
    progressBar = (ProgressBar) rootView.findViewById(R.id.progress);
    empty.setText("No Pins");
    listViewMatches.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(), FullProfileActivity.class);
        intent.putExtra("parseObjectId", pinsList.get(i).getProfileId());
        startActivity(intent);
      }
    });
    if (mApp.isNetworkAvailable(context)) {
      ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Profile");
      parseQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
      parseQuery.getInBackground(me.iwf.photopicker.utils.Prefs.getProfileId(context), new GetCallback<ParseObject>() {
        @Override
        public void done(ParseObject parseObject, ParseException e) {
          userProfileObject = parseObject;
        }
      });
    }
  }

  public void getParseData() {
    if (mApp.isNetworkAvailable(context)) {
      new AsyncTask<Void, Void, Void>() {
        List<ParseObject> list;

        @Override
        protected void onPreExecute() {
          super.onPreExecute();
          progressBar.setVisibility(View.VISIBLE);
          listViewMatches.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params) {
          try {
            ParseQuery<ParseObject> query = new ParseQuery<>("PinnedProfile");
            query.include("pinnedProfileId.religionId");
            query.include("pinnedProfileId.userId");
            query.whereEqualTo("profileId", ParseObject.createWithoutData("Profile", Prefs.getProfileId(context)));
            list = query.find();
            if (list != null)
              if (list.size() > 0) {
                pinsList.clear();
                for (ParseObject parseObject : list) {
                  try {
                    MatchesModel model = new MatchesModel();
                    String name = parseObject.getParseObject("pinnedProfileId").getString("name");
                    if (name != null)
                      model.setName(name);
                    String work = parseObject.getParseObject("pinnedProfileId").getString("designation");
                    if (work != null)
                      model.setWork(work);
                    String religion = parseObject.getParseObject("pinnedProfileId").getParseObject("religionId").fetchIfNeeded().getString("name");
                    if (religion != null)
                      model.setReligion(religion);
                    String url = parseObject.getParseObject("pinnedProfileId").getParseFile("profilePic").getUrl();
                    if (url != null) {
                      model.setUrl(url);
                    }
                    model.setProfileId(parseObject.getParseObject("pinnedProfileId").getObjectId());
                    model.setUserId(parseObject.getParseObject("pinnedProfileId").getParseObject("userId").getObjectId());
                    pinsList.add(model);
                  } catch (ParseException e1) {
                    e1.printStackTrace();
                  }
                }
              }
          } catch (ParseException e) {
            e.printStackTrace();
          }
          return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
          super.onPostExecute(aVoid);
          if (list != null) {
            if (list.size() > 0) {
              setPinsAdapter();
            } else {
              progressBar.setVisibility(View.GONE);
              empty.setVisibility(View.VISIBLE);
            }
          } else {
            getParseData();
            progressBar.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
          }
        }
      }.execute();
    }
  }

  private void setPinsAdapter() {
    pinsAdapter = new PinsAdapter(PinsFragment.this, pinsList, context);
    mAdapter = new SwipeActionAdapter(pinsAdapter);
    mAdapter.setSwipeActionListener(this)
        .setDimBackgrounds(false).setFixedBackgrounds(true)
        .setListView(listViewMatches);
    listViewMatches.setAdapter(mAdapter);
    progressBar.setVisibility(View.GONE);
    listViewMatches.setVisibility(View.VISIBLE);
    mAdapter.addBackground(SwipeDirections.DIRECTION_FAR_LEFT, R.layout.row_bg_left_far)
        .addBackground(SwipeDirections.DIRECTION_NORMAL_LEFT, R.layout.row_bg_left_far)
        .addBackground(SwipeDirections.DIRECTION_FAR_RIGHT, R.layout.row_bg_right_far)
        .addBackground(SwipeDirections.DIRECTION_NORMAL_RIGHT, R.layout.row_bg_right_far);
  }

  @Override
  public boolean hasActions(int position) {
    return true;
  }

  @Override
  public boolean shouldDismiss(int position, int direction) {
    return direction == SwipeDirections.DIRECTION_NORMAL_LEFT;
  }

  @Override
  public void onSwipe(int[] positionList, int[] directionList) {
    for (int i = 0; i < positionList.length; i++) {
      int direction = directionList[i];
      int position = positionList[i];
      undoMatch = (MatchesModel) mAdapter.getItem(position);
      switch (direction) {
        case SwipeDirections.DIRECTION_FAR_LEFT:
          pinsList.remove(undoMatch);
          likeProfile(undoMatch);
          mAdapter.notifyDataSetChanged();
          break;
        case SwipeDirections.DIRECTION_NORMAL_LEFT:
          pinsList.remove(undoMatch);
          likeProfile(undoMatch);
          mAdapter.notifyDataSetChanged();
          break;
        case SwipeDirections.DIRECTION_FAR_RIGHT:
          pinsList.remove(undoMatch);
          disLikeProfile(undoMatch);
          mAdapter.notifyDataSetChanged();
          break;
        case SwipeDirections.DIRECTION_NORMAL_RIGHT:
          pinsList.remove(undoMatch);
          disLikeProfile(undoMatch);
          mAdapter.notifyDataSetChanged();
          break;
      }

    }
  }

  private void disLikeProfile(final MatchesModel matchesModel) {
    mApp.show_PDialog(context, "Please wait", false);
    ParseQuery<ParseObject> query = new ParseQuery<>("PinnedProfile");
    query.whereEqualTo("profileId", ParseObject.createWithoutData("Profile", Prefs.getProfileId(context)));
    query.whereEqualTo("pinnedProfileId", ParseObject.createWithoutData("Profile", matchesModel.getProfileId()));
    query.getFirstInBackground(new GetCallback<ParseObject>() {
      @Override
      public void done(ParseObject parseObject, ParseException e) {
        if (e == null) {
          parseObject.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
              if (e == null) {
                ParseObject dislikeParseObject = new ParseObject("DislikeProfile");
                dislikeParseObject.put("dislikeProfileId", ParseObject.createWithoutData("Profile", matchesModel.getProfileId()));
                dislikeParseObject.put("profileId", ParseObject.createWithoutData("Profile", Prefs.getProfileId(context)));
                dislikeParseObject.saveInBackground(new SaveCallback() {
                  @Override
                  public void done(ParseException e) {
                    showSnakeBar("PROFILE DISLIKED", false);
                  }
                });

              } else {
                mApp.showToast(context, e.getMessage());
              }
              mApp.dialog.dismiss();
            }
          });
        } else {
          mApp.dialog.dismiss();
          mApp.showToast(context, e.getMessage());
        }
      }
    });
  }

  public void likeProfile(final MatchesModel matchesModel) {
    if (mApp.isNetworkAvailable(context))
      try {
        mApp.show_PDialog(context, "Please wait", false);
        HashMap<String, Object> params = new HashMap<>();
        params.put("userProfileId", userProfileObject.getObjectId());
        params.put("likeProfileId", matchesModel.getProfileId());
        params.put("userName", userProfileObject.getString("name"));

        ParseCloud.callFunctionInBackground("likeAndFind", params, new FunctionCallback<Object>() {
          @Override
          public void done(Object o, ParseException e) {
            if (e == null) {
              mApp.dialog.dismiss();
              if (o != null) {
                try {
                  if (o instanceof ParseObject) {
                    ParseObject parseObject = ParseObject.createWithoutData("Profile", matchesModel.getProfileId());
                    String religion = parseObject.fetchIfNeeded().getParseObject("religionId").fetchIfNeeded().getString("name");
                    String caste = parseObject.fetchIfNeeded().getParseObject("casteId").fetchIfNeeded().getString("name");
                    MatchesModel model = new MatchesModel();
                    model.setName(parseObject.fetchIfNeeded().getString("name"));
                    model.setProfileId(parseObject.getObjectId());
                    model.setReligion(religion + ", " + caste);
                    model.setWork(parseObject.getString("designation"));
                    model.setUrl(parseObject.fetchIfNeeded().getParseFile("profilePic").getUrl());
                    Intent intent = new Intent(context, MatchedProfileActivity.class);
                    intent.putExtra("profile", model);
                    startActivity(intent);
                  } else {
                    showSnakeBar("PROFILE LIKED", true);
                  }
                } catch (Exception e1) {
                  e1.printStackTrace();
                }
              }
            } else {
              mApp.dialog.dismiss();
              e.printStackTrace();
              mApp.showToast(context, e.getMessage());
            }
          }
        });

      } catch (Exception e) {
        e.printStackTrace();
        mApp.showToast(context, "Error while liking profile");
      }
  }

  private void showSnakeBar(String msg, final Boolean isLiked) {
    Snackbar snackbar = Snackbar
        .make(rootView, msg, Snackbar.LENGTH_LONG)
        .setAction("Undo", new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            undoAction(isLiked);
          }
        });
    snackbar.setActionTextColor(context.getResources().getColor(R.color.indigo_900));
    View snackbarView = snackbar.getView();
    snackbarView.setBackgroundColor(context.getResources().getColor(R.color.red_400));//change Snackbar's background color;
    TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
    textView.setTextColor(Color.WHITE);
    snackbar.show();
  }

  private void undoAction(Boolean isLiked) {
    mApp.show_PDialog(context, "Please wait..");
    pinsList.add(undoMatch);
    mAdapter.notifyDataSetChanged();
    if (isLiked) {
      undoLike();
    } else undoDislike();
  }

  private void undoLike() {
    ParseObject dislikeParseObject = new ParseObject("PinnedProfile");
    dislikeParseObject.put("pinnedProfileId", ParseObject.createWithoutData("Profile", undoMatch.getProfileId()));
    dislikeParseObject.put("profileId", userProfileObject);
    dislikeParseObject.saveInBackground(new SaveCallback() {
      @Override
      public void done(ParseException e) {
        if (e == null) {
          ParseQuery<ParseObject> parseQuery = new ParseQuery<>("LikedProfile");
          parseQuery.whereEqualTo("likeProfileId", ParseObject.createWithoutData("Profile", undoMatch.getProfileId()));
          parseQuery.whereEqualTo("profileId", userProfileObject);
          parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
              if (e == null) {
                parseObject.deleteInBackground(new DeleteCallback() {
                  @Override
                  public void done(ParseException e) {
                    mApp.dialog.dismiss();
                  }
                });
              } else {
                mApp.showToast(context, e.getMessage());
                mApp.dialog.dismiss();
              }
            }
          });
        } else {
          mApp.dialog.dismiss();
          mApp.showToast(context, e.getMessage());
        }
      }
    });
  }

  private void undoDislike() {
    ParseObject dislikeParseObject = new ParseObject("PinnedProfile");
    dislikeParseObject.put("pinnedProfileId", ParseObject.createWithoutData("Profile", undoMatch.getProfileId()));
    dislikeParseObject.put("profileId", userProfileObject);
    dislikeParseObject.saveInBackground(new SaveCallback() {
      @Override
      public void done(ParseException e) {
        if (e == null) {
          ParseQuery<ParseObject> parseQuery = new ParseQuery<>("DislikeProfile");
          parseQuery.whereEqualTo("dislikeProfileId", ParseObject.createWithoutData("Profile", undoMatch.getProfileId()));
          parseQuery.whereEqualTo("profileId", userProfileObject);
          parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
              if (e == null) {
                parseObject.deleteInBackground(new DeleteCallback() {
                  @Override
                  public void done(ParseException e) {
                    mApp.dialog.dismiss();
                  }
                });
              } else {
                mApp.dialog.dismiss();
                mApp.showToast(context, e.getMessage());
              }
            }
          });
        } else {
          mApp.dialog.dismiss();
          mApp.showToast(context, e.getMessage());
        }
      }
    });

  }

}
