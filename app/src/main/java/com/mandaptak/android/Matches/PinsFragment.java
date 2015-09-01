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

import com.mandaptak.android.Adapter.MatchesAdapter;
import com.mandaptak.android.Models.MatchesModel;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.utils.Prefs;

public class PinsFragment extends Fragment {
    Common mApp;
    ListView listViewMatches;
    ArrayList<MatchesModel> matchList = new ArrayList<>();
    private View rootView;
    private Context context;

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
        listViewMatches = (ListView) rootView.findViewById(R.id.list);

        listViewMatches.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), MatchedProfileActivity.class);
                intent.putExtra("profile", matchList.get(i));
                startActivity(intent);
            }
        });
    }

    private ArrayList<MatchesModel> getParseData() {
        mApp.show_PDialog(context, "Loading..");
        ParseQuery<ParseObject> q1 = new ParseQuery<>("Profile");
        q1.getInBackground(Prefs.getProfileId(context), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    ParseQuery<ParseObject> query = new ParseQuery<>("PinnedProfile");
                    query.whereEqualTo("profileId", object);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> list, ParseException e) {
                            if (e == null)
                                if (list.size() > 0) {
                                    for (ParseObject parseObject : list) {
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
                                                model.setReligion(religion);
                                            String url = parseObject.fetchIfNeeded().getParseObject("pinnedProfileId").fetchIfNeeded().getParseFile("profilePic").getUrl();
                                            if (url != null) {
                                                model.setUrl(url);
                                            }
                                            model.setProfileId(parseObject.fetchIfNeeded().getParseObject("pinnedProfileId").getObjectId());
                                            model.setUserId(parseObject.fetchIfNeeded().getParseObject("pinnedProfileId").fetchIfNeeded().getParseObject("userId").getObjectId());
                                            matchList.add(model);
                                        } catch (ParseException e1) {
                                            e1.printStackTrace();
                                        }

                                    }
                                    listViewMatches.setAdapter(new MatchesAdapter(matchList, context));
                                } else
                                    mApp.showToast(context, "No pins");
                        }
                    });
                }
            }
        });

        mApp.dialog.dismiss();
        return matchList;
    }
}
