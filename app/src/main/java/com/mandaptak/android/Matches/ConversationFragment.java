package com.mandaptak.android.Matches;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.layer.atlas.Atlas;
import com.layer.atlas.AtlasConversationsList;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.mandaptak.android.Layer.LayerImpl;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;

import java.util.HashMap;
import java.util.Map;


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
        participantProvider = new Atlas.ParticipantProvider() {
            Map<String, Atlas.Participant> users = new HashMap<>();

            public Map<String, Atlas.Participant> getParticipants(String filter,
                                                                  Map<String, Atlas.Participant> result) {

                for (Map.Entry<String, Atlas.Participant> entry : users.entrySet()) {
                    if (entry.getValue().getFirstName().indexOf(filter) > -1)
                        result.put(entry.getKey(), entry.getValue());
                }

                return result;
            }

            public Atlas.Participant getParticipant(String userId) {
                return users.get(userId);
            }
        };

        myConversationList = (AtlasConversationsList) rootView.findViewById(R.id.conversationlist);
        myConversationList.init(layerClient, participantProvider);
        myConversationList.setClickListener(new AtlasConversationsList.ConversationClickListener() {
            public void onItemClick(Conversation conversation) {
                startMessagesActivity(conversation);
            }
        });

        layerClient.registerEventListener(myConversationList);

    }

    private void startMessagesActivity(Conversation c) {
        Intent intent = new Intent(context, MessageScreen.class);
        if (c != null)
            intent.putExtra("conversation-id", c.getId());
        startActivity(intent);
    }
}



