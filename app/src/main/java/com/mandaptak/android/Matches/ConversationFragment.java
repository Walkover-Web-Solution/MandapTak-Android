package com.mandaptak.android.Matches;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.layer.atlas.Atlas;
import com.layer.atlas.AtlasConversationsList;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.query.Predicate;
import com.layer.sdk.query.Query;
import com.layer.sdk.query.SortDescriptor;
import com.mandaptak.android.Layer.LayerImpl;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;


public class ConversationFragment extends android.support.v4.app.Fragment {
    Common mApp;
    private View rootView;
    private Context context;
    static public LayerClient layerClient;
    static public Atlas.ParticipantProvider participantProvider;

    private AtlasConversationsList myConversationList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        init(inflater, container);
        return rootView;
    }

    private void init(LayoutInflater inflater, ViewGroup container) {
        context = getActivity();
        layerClient = LayerImpl.getLayerClient();
        mApp = (Common) context.getApplicationContext();
        rootView = inflater.inflate(R.layout.conversation_list, container, false);
        myConversationList = (AtlasConversationsList) rootView.findViewById(R.id.conversationlist);
        if (!layerClient.isAuthenticated()) {
            layerClient.authenticate();
        } else {
            onUserAuthenticated();
        }
    }

    public void onUserAuthenticated() {
            long monthBeforeMs = System.currentTimeMillis() - (1L * 30 * 24 * 3600 * 1000);
            Query<Conversation> query = Query.builder(Conversation.class)
                    .predicate(new Predicate(Conversation.Property.LAST_MESSAGE_RECEIVED_AT, Predicate.Operator.GREATER_THAN, monthBeforeMs))
                    .sortDescriptor(new SortDescriptor(Conversation.Property.LAST_MESSAGE_RECEIVED_AT, SortDescriptor.Order.DESCENDING))
                    .build();
            myConversationList.setQuery(query);

        }

    }



