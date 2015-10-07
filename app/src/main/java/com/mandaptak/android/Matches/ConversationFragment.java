package com.mandaptak.android.Matches;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.layer.atlas.AtlasConversationsList;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.mandaptak.android.Layer.LayerImpl;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;

public class ConversationFragment extends android.support.v4.app.Fragment {
  static public LayerClient layerClient;
  Common mApp;
  private View rootView;
  private Context context;
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
    myConversationList.setLongClickListener(new AtlasConversationsList.ConversationLongClickListener() {
      @Override
      public void onItemLongClick(final Conversation conversation) {
        new AlertDialog.Builder(context)
            .setTitle("Delete chat")
            .setMessage("Are you sure you want to delete this conversation?")
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                conversation.delete(LayerClient.DeletionMode.ALL_PARTICIPANTS);
              }
            })
            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                // do nothing
              }
            })
            .setIcon(R.drawable.ic_delete_red)
            .show();
      }
    });
  }

  public void onUserAuthenticated() {
    myConversationList.init(layerClient, Common.getIdentityProvider());
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



