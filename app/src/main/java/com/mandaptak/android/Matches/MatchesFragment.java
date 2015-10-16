package com.mandaptak.android.Matches;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mandaptak.android.Adapter.MatchesAdapter;
import com.mandaptak.android.Models.MatchesModel;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;

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
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
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
                  listViewMatches.setAdapter(new MatchesAdapter(matchList, context));
                  listViewMatches.setVisibility(View.VISIBLE);
                } else {
                  progressBar.setVisibility(View.GONE);
                  empty.setVisibility(View.VISIBLE);
                }
              } else {
                progressBar.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
              }
            }
            else {
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
}
