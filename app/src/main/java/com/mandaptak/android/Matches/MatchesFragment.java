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

import java.util.ArrayList;

public class MatchesFragment extends Fragment {
    Common mApp;
    ListView listViewMatches;
    ArrayList<MatchesModel> matchList = new ArrayList<>();
    private View rootView;
    private Context context;

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
                    Intent intent = new Intent(getActivity(), MatchedProfileActivity.class);
                    intent.putExtra("profile", matchList.get(i));
                    startActivity(intent);
                }
            });
        }
        return rootView;
    }

    private void init(LayoutInflater inflater, ViewGroup container) {
        context = getActivity();
        mApp = (Common) context.getApplicationContext();
        rootView = inflater.inflate(R.layout.fragment_matches, container, false);
        listViewMatches = (ListView) rootView.findViewById(R.id.list);
        matchList = com.mandaptak.android.Utils.Prefs.getMatches(context);
        if (matchList != null)
            if (matchList.size() > 0)
                listViewMatches.setAdapter(new MatchesAdapter(matchList, context));
            else
                mApp.showToast(context, "No matching results found.");
    }

   /* private void getMatchesFromFunction() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("profileId", Prefs.getProfileId(context));
        ParseCloud.callFunctionInBackground("getMatchedProfile", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object o, ParseException e) {

                if (e == null) {
                    if (o != null) {
                        profileObjs = (ArrayList<ParseObject>) o;
                        if (profileObjs.size() > 0) {
                            for (ParseObject parseObject : profileObjs) {
                                try {
                                    MatchesModel model = new MatchesModel();
                                    String name = parseObject.fetchIfNeeded().getString("name");
                                    if (name != null)
                                        model.setName(name);
                                    String work = parseObject.fetchIfNeeded().getString("designation");
                                    if (work != null)
                                        model.setWork(work);
                                    String religion = parseObject.fetchIfNeeded().getParseObject("religionId").fetchIfNeeded().getString("name");
                                    if (religion != null)
                                        model.setReligion(religion);
                                    String url = parseObject.fetchIfNeeded().fetchIfNeeded().getParseFile("profilePic").getUrl();
                                    if (url != null) {
                                        model.setUrl(url);
                                    }
                                    matchList.add(model);
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                            }
                            listViewMatches.setAdapter(new MatchesAdapter(matchList, context));
                            ParseQuery<ParseObject> q1 = new ParseQuery<>("Profile");
                            q1.getInBackground(Prefs.getProfileId(context), new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject object, ParseException e) {
                                    if (e == null) {
                                        currentProfile = object;
                                        saveMatches();
                                    }
                                }
                            });
                        } else {
                            mApp.showToast(context, "No matching results found.");
                        }
                    } else {
                        mApp.showToast(context, "No matching results found.");
                    }
                } else {
                    mApp.showToast(context, "No matching results found.");
                    e.printStackTrace();
                }
            }
        });

    }*/

/*    private void saveMatches() {
        final HashMap<String, Participant> usersMap = new HashMap<>();
        profileObjs.add(currentProfile);
        for (ParseObject parseObjectPro : profileObjs) {
            name = parseObjectPro.getString("name");
            ParseQuery<ParseObject> query = new ParseQuery<>("UserProfile");
            query.whereEqualTo("profileId", parseObjectPro);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null)
                        if (list.size() > 0) {
                            for (ParseObject parseObject : list) {
                                try {
                                    Participant participant = new Participant();
                                    participant.userId = parseObject.fetchIfNeeded().getParseObject("userId").getObjectId();
                                    String relation = parseObject.fetchIfNeeded().getString("relation");
                                    if (relation.equalsIgnoreCase("Bachelor")) {
                                        participant.firstName = name;
                                    } else {
                                        participant.firstName = relation + " (" + name + ")";
                                    }
                                    usersMap.put(participant.userId, participant);

                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                            }
                            com.mandaptak.android.Utils.Prefs.setChatUsers(context, usersMap);

                        }

                }
            });


        }


    }*/

}
