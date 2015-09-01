package com.mandaptak.android.Matches;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.layer.sdk.messaging.Conversation;
import com.mandaptak.android.Adapter.ConversationQueryAdapter;
import com.mandaptak.android.Adapter.MatchesAdapter;
import com.mandaptak.android.Adapter.QueryAdapter;
import com.mandaptak.android.Layer.LayerImpl;
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

public class ChatsFragment extends Fragment implements ConversationQueryAdapter.ConversationClickHandler {
    Common mApp;
    ListView listViewMatches;
    private View rootView;
    private Context context;
    private  RecyclerView conversationsView;
    private ConversationQueryAdapter mConversationsAdapter;
    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        init(inflater, container);
        return rootView;
    }

    private void init(LayoutInflater inflater, ViewGroup container) {
        context = getActivity();
        mApp = (Common) context.getApplicationContext();
        rootView = inflater.inflate(R.layout.fragment_chat_layout, container, false);
        listViewMatches = (ListView) rootView.findViewById(R.id.list);
        //Grab the Recycler View and list all conversation objects in a vertical list
         conversationsView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        conversationsView.setLayoutManager(layoutManager);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!LayerImpl.isAuthenticated()) {
            LayerImpl.authenticateUser();
        }
        mConversationsAdapter = new ConversationQueryAdapter(context.getApplicationContext(), LayerImpl.getLayerClient(), this, new QueryAdapter.Callback() {
            @Override
            public void onItemInserted() {
                Log.d("Activity", "Conversation Adapter, new conversation inserted");
            }
        });

        //Attach the Query Adapter to the Recycler View
        conversationsView.setAdapter(mConversationsAdapter);

        //Execute the Query
        mConversationsAdapter.refresh();

    }

    @Override
    public void onConversationClick(Conversation conversation) {
    /*    if (conversation != null && conversation.getId() != null && !conversation.isDeleted()) {
            Intent intent = new Intent(context, MessageActivity.class);
            intent.putExtra("conversation-id", conversation.getId());
            startActivity(intent);
        }*/
    }

    @Override
    public boolean onConversationLongClick(Conversation conversation) {
        return false;
    }
}
