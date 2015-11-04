package com.mandaptak.android.Matches;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.query.Predicate;
import com.layer.sdk.query.Query;
import com.mandaptak.android.Adapter.MatchesAdapter;
import com.mandaptak.android.Layer.LayerImpl;
import com.mandaptak.android.Models.MatchesModel;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.iwf.photopicker.utils.Prefs;

public class MatchesFragment extends Fragment {
  Common mApp;
  ListView listViewMatches;
  ArrayList<MatchesModel> matchList = new ArrayList<>();
  TextView empty;
  ProgressBar progressBar;
  private View rootView;
  private Context context;
  ArrayList<ParseObject> profileObjs = new ArrayList<>();
  MatchesAdapter matchesAdapter;

  public MatchesFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    init(inflater, container);
    if (mApp.isNetworkAvailable(context)) {
      listViewMatches.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
          Intent intent = new Intent(getActivity(), ViewProfilePage.class);
          intent.putExtra("profile", matchList.get(i));
          startActivity(intent);
        }
      });
    }
    return rootView;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  private void init(LayoutInflater inflater, ViewGroup container) {
    context = getActivity();
    mApp = (Common) context.getApplicationContext();
    rootView = inflater.inflate(R.layout.fragment_list, container, false);
    listViewMatches = (ListView) rootView.findViewById(R.id.list);
    empty = (TextView) rootView.findViewById(R.id.empty);
    progressBar = (ProgressBar) rootView.findViewById(R.id.progress);
    empty.setText("No Matches Found");
    progressBar.setVisibility(View.VISIBLE);
    listViewMatches.setVisibility(View.GONE);
    // matchList = com.mandaptak.android.Utils.Prefs.getMatches(context);
    listViewMatches.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
        new AlertDialog.Builder(context)
            .setTitle("Remove from matches")
            .setMessage("Are you sure you want to Unmatch ?")
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                removeMatch((MatchesModel) parent.getItemAtPosition(position));
              }
            })
            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                // do nothing
              }
            })
            .setIcon(R.drawable.ic_delete_red)
            .show();
        return true;
      }
    });
    getMatchesFromFunction();
  }

  private void getMatchesFromFunction() {
    HashMap<String, Object> params = new HashMap<>();
    params.put("profileId", Prefs.getProfileId(context));
    ParseCloud.callFunctionInBackground("getMatchedProfile", params, new FunctionCallback<ArrayList<ParseObject>>() {
      @Override
      public void done(ArrayList<ParseObject> o, ParseException e) {
        if (e == null) {
          if (o != null) {
            profileObjs = o;
            if (profileObjs.size() > 0) {
              for (ParseObject parseObject : profileObjs) {
                try {
                  MatchesModel model = new MatchesModel();
                  model.setProfileId(parseObject.getObjectId());
                  model.setUserId(parseObject.getParseObject("userId").getObjectId());
                  String name = parseObject.getString("name");
                  if (name != null)
                    model.setName(name);
                  String work = parseObject.getString("designation");
                  if (work != null)
                    model.setWork(work);
                  String religion = parseObject.getParseObject("religionId").getString("name");
                  if (religion != null)
                    model.setReligion(religion);
                  String url = parseObject.getParseFile("profilePic").getUrl();
                  if (url != null) {
                    model.setUrl(url);
                  }
                  matchList.add(model);
                } catch (Exception e1) {
                  e1.printStackTrace();
                }
              }
              if (matchList != null) {
                if (matchList.size() > 0) {
                  progressBar.setVisibility(View.GONE);
                  matchesAdapter = new MatchesAdapter(matchList, context);
                  listViewMatches.setAdapter(matchesAdapter);
                  listViewMatches.setVisibility(View.VISIBLE);
                } else {
                  progressBar.setVisibility(View.GONE);
                  empty.setVisibility(View.VISIBLE);
                }
              } else {
                progressBar.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
              }
            } else {
              progressBar.setVisibility(View.GONE);
              empty.setVisibility(View.VISIBLE);
            }

          } else {
            progressBar.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
          }
        } else {
          progressBar.setVisibility(View.GONE);
          empty.setVisibility(View.VISIBLE);
          e.printStackTrace();
        }
      }
    });
  }

  private void removeMatch(final MatchesModel matchesModel) {
    mApp.show_PDialog(context, "Please wait", false);
    ParseQuery<ParseObject> removeQuery = new ParseQuery<>("LikedProfile");
    removeQuery.whereEqualTo("likeProfileId", ParseObject.createWithoutData("Profile", matchesModel.getProfileId()));
    removeQuery.whereEqualTo("profileId", ParseObject.createWithoutData("Profile", Prefs.getProfileId(context)));
    removeQuery.getFirstInBackground(new GetCallback<ParseObject>() {
      @Override
      public void done(ParseObject object, ParseException e) {
        if (e == null) {
          if (object != null) {
            object.deleteInBackground(new DeleteCallback() {
              @Override
              public void done(ParseException e) {
                if (e == null) {
                  //  mApp.dialog.dismiss();
                  matchList.remove(matchesModel);
                  matchesAdapter.notifyDataSetChanged();
                  getChatMembers(matchesModel.getProfileId());
                  mApp.showToast(context, "Match removed");
                } else {
                  mApp.dialog.dismiss();
                  mApp.showToast(context, e.getMessage());
                }
              }
            });
          }
        } else {
          mApp.dialog.dismiss();
          mApp.showToast(context, e.getMessage());
        }
      }
    });

  }

  private void getChatMembers(String profileId) {
    ParseQuery<ParseObject> query = new ParseQuery<>("UserProfile");
    query.include("userId");
    query.whereEqualTo("profileId", ParseObject.createWithoutData("Profile", Prefs.getProfileId(context)));
    query.whereEqualTo("profileId", ParseObject.createWithoutData("Profile", profileId));
    query.whereNotEqualTo("relation", "Agent");
    query.findInBackground(new FindCallback<ParseObject>() {
      @Override
      public void done(List<ParseObject> list, ParseException e) {
        if (e == null) {
          mApp.dialog.dismiss();
          if (list.size() > 0) {
            ArrayList<String> mTargetParticipants = new ArrayList<>();
            mTargetParticipants.add(LayerImpl.getLayerClient().getAuthenticatedUserId());
            for (ParseObject parseObject : list) {
              mTargetParticipants.add(parseObject.getParseObject("userId").getObjectId());
            }
            if (mTargetParticipants.size() > 0) {
              Query query = Query.builder(Conversation.class)
                  .predicate(new Predicate(Conversation.Property.PARTICIPANTS, Predicate.Operator.EQUAL_TO, mTargetParticipants))
                  .build();
              List<Conversation> results = LayerImpl.getLayerClient().executeQuery(query, Query.ResultType.OBJECTS);
              if (results.size() > 0) {
                for (String user : mTargetParticipants) {
                  results.get(0).removeParticipants(user);
                }
                mApp.dialog.dismiss();
              } else {
                mApp.dialog.dismiss();
              }
            }
          }
        } else {
          mApp.dialog.dismiss();
          mApp.showToast(context, e.getMessage());
        }
      }
    });

  }
}
