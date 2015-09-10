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

import java.util.ArrayList;

public class MatchesFragment extends Fragment {
    Common mApp;
    ListView listViewMatches;
    ArrayList<MatchesModel> matchList = new ArrayList<>();
    TextView empty;
    ProgressBar progressBar;
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
        rootView = inflater.inflate(R.layout.fragment_list, container, false);
        listViewMatches = (ListView) rootView.findViewById(R.id.list);
        empty = (TextView) rootView.findViewById(R.id.empty);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress);
        empty.setText("No Matches Found");
        progressBar.setVisibility(View.VISIBLE);
        listViewMatches.setVisibility(View.GONE);
        matchList = com.mandaptak.android.Utils.Prefs.getMatches(context);
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
}
