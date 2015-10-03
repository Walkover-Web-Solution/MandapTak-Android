package com.mandaptak.android.Matches;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.utils.Prefs;

public class PinsFragment extends Fragment {
  Common mApp;
  ListView listViewMatches;
  ArrayList<MatchesModel> pinsList = new ArrayList<>();
  TextView empty;
  ProgressBar progressBar;
  private View rootView;
  private Context context;

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
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
      }
    });
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
              listViewMatches.setAdapter(new PinsAdapter(PinsFragment.this, pinsList, context));
              progressBar.setVisibility(View.GONE);
              listViewMatches.setVisibility(View.VISIBLE);
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
}
