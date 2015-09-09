package com.mandaptak.android.Matches;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.layer.atlas.Atlas;
import com.layer.atlas.AtlasMessageComposer;
import com.layer.atlas.AtlasMessagesList;
import com.layer.atlas.AtlasParticipantPicker;
import com.layer.atlas.AtlasTypingIndicator;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Message;
import com.mandaptak.android.Layer.LayerImpl;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;

import java.util.ArrayList;
import java.util.Set;

public class MessageScreen extends AppCompatActivity {

    private AtlasMessagesList messagesList;
    private AtlasParticipantPicker participantPicker;
    private AtlasMessageComposer atlasComposer;
    private Conversation conversation;
    private LayerClient layerClient;
    private ArrayList<String> userIds;
    String tittleConversation = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_view);
        layerClient = LayerImpl.getLayerClient();
        Uri id = getIntent().getParcelableExtra("conversation-id");
        if (id != null)
            conversation = layerClient.getConversation(id);
        userIds = getIntent().getStringArrayListExtra("participant-map");
        tittleConversation = getIntent().getStringExtra("tittle-conv");
        messagesList = (AtlasMessagesList) findViewById(R.id.messageslist);
        messagesList.init(layerClient, Common.getIdentityProvider());
        messagesList.setConversation(conversation);

        participantPicker = (AtlasParticipantPicker) findViewById(R.id.participantpicker);
        String[] currentUser = {layerClient.getAuthenticatedUserId()};
        participantPicker.init(currentUser, Common.getIdentityProvider());
        if (conversation != null)
            participantPicker.setVisibility(View.GONE);
        AtlasTypingIndicator typingIndicator = (AtlasTypingIndicator) findViewById(R.id.typingindicator);
        typingIndicator.init(conversation, new AtlasTypingIndicator.Callback() {
            public void onTypingUpdate(AtlasTypingIndicator indicator, Set<String> typingUserIds) {
            }
        });

        atlasComposer = (AtlasMessageComposer) findViewById(R.id.textinput);
        atlasComposer.init(layerClient, conversation);
        atlasComposer.setListener(new AtlasMessageComposer.Listener() {
            public boolean beforeSend(Message message) {
                if (conversation == null) {
                    //  String[] participants = (String[]) userIds.toArray();
                    if (userIds.size() > 0) {
                        participantPicker.setVisibility(View.GONE);
                        conversation = layerClient.newConversation(userIds);
                        Atlas.setTitle(conversation, tittleConversation);
                      /*  Metadata metadata1=conversation.getMetadata();
                        if (metadata1==null){
                            Metadata metadata = Metadata.newInstance();
                            metadata.put("title", "My Conversation");
                            Metadata theme = Metadata.newInstance();
                            theme.put("background_color", "333333");
                            theme.put("text_color", "F8F8EC");
                            theme.put("link_color", "21AAE1");
                            metadata.put("theme", theme);
                            metadata.put("created_at", "Dec, 01, 2014");
                            metadata.put("img_url", "/path/to/img/url");
                          //  mConversation.putMetadata(metadata, false);
                        }*/
                        messagesList.setConversation(conversation);
                        atlasComposer.setConversation(conversation);
                    } else {
                        return false;
                    }
                }
                return true;
            }
        });
    }

    protected void onResume() {
        super.onResume();
        layerClient.registerEventListener(messagesList);
    }

    protected void onPause() {
        super.onPause();
        layerClient.unregisterEventListener(messagesList);
    }

}
