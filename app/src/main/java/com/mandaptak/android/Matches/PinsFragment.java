package com.mandaptak.android.Matches;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mandaptak.android.Adapter.MatchesAdapter;
import com.mandaptak.android.Models.MatchesModel;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class PinsFragment extends Fragment {
    Common mApp;
    private View rootView;
    private Context context;
    ListView listViewMatches;
    ArrayList<MatchesModel> matchList = new ArrayList<>();

    public PinsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        init(inflater, container);
        if (mApp.isNetworkAvailable(context)) {
            getParseData();
        }
        return rootView;
    }

    private void init(LayoutInflater inflater, ViewGroup container) {
        context = getActivity();
        mApp = (Common) context.getApplicationContext();
        rootView = inflater.inflate(R.layout.fragment_matches, container, false);
        listViewMatches = (ListView) rootView.findViewById(R.id.matches_list);
    }

    private ArrayList<MatchesModel> getParseData() {
        mApp.show_PDialog(context, "Loading..");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("PinnedProfile");
        query.whereEqualTo("profileId", ParseUser.getCurrentUser().getParseObject("profileId"));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list.size() > 0) {
                    for (final ParseObject parseObject : list) {
                        try {
                            MatchesModel model = new MatchesModel();
                            String name = parseObject.fetchIfNeeded().getParseObject("pinnedProfileId").fetchIfNeeded().getString("name");
                            if (name != null)
                                model.setName(name);
                            String work = parseObject.fetchIfNeeded().getParseObject("pinnedProfileId").fetchIfNeeded().getString("designation");
                            if (work != null)
                                model.setWork(work);
                            String religion = parseObject.fetchIfNeeded().getParseObject("pinnedProfileId").fetchIfNeeded().getParseObject("religionId").fetchIfNeeded().getString("name");
                            if (religion != null)
                                model.setCaste(religion);
                            String url = parseObject.fetchIfNeeded().getParseObject("pinnedProfileId").fetchIfNeeded().getParseFile("profilePic").getUrl();
                            if (url != null) {
                                model.setUrl(url);
                            }
                            matchList.add(model);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }

                    }
                    listViewMatches.setAdapter(new MatchesAdapter(matchList, context));


                }
            }
        });


        mApp.dialog.dismiss();
        return matchList;

    }

}