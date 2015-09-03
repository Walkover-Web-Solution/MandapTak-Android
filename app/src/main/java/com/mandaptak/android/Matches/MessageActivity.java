package com.mandaptak.android.Matches;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;
import com.layer.sdk.query.Query;
import com.mandaptak.android.Adapter.MessageQueryAdapter;
import com.mandaptak.android.Adapter.QueryAdapter;
import com.mandaptak.android.Layer.LayerImpl;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/*
 * MessageActivity.java
 * Handles displaying all Messages in a specific Conversation, or allows the user to specify the
 *  participants in a new Conversation. Uses the MessageQueryAdapter to drive the view.
 */

public class MessageActivity extends ActivityBase implements MessageQueryAdapter.MessageClickHandler {

    //The owning conversation
    private Conversation mConversation;

    //The Query Adapter that grabs all Messages and displays them based on their Position
    private MessageQueryAdapter mMessagesAdapter;
    //This is the actual view that contains all the messages
    private RecyclerView mMessagesView;

    //When starting a new Conversation, we keep a list of all target participants. The Conversation
    // is only created when the first message is sent
    private ArrayList<String> mTargetParticipants;
    //Grab all the view objects on the message_screen layout when the Activity starts
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_screen);
        mTargetParticipants = new ArrayList<>();
        //View containing all messages in the target Conversastion
        mMessagesView = (RecyclerView) findViewById(R.id.mRecyclerView);

        //Check to see when the locally Authenticated user is trying to send a message
        Button sendButton = (Button) findViewById(R.id.sendButton);
        if (sendButton != null)
            sendButton.setOnClickListener(this);


        //If the soft keyboard changes the size of the mMessagesView, we want to force the scroll to
        // the bottom of the view so the latest message is always displayed
        attachKeyboardListeners(mMessagesView);
    }

    //Checks the state of the LayerClient and whether this is an existing Conversation or a new
    // Conversation
    public void onResume() {
        super.onResume();

        //If the user is not Authenticated, check to see if we need to return to the Login Screen,
        // or if the User can be Authenticated silently in the background
        if (!LayerImpl.isAuthenticated()) {

            Log.e("Not Authenticated ", "Not Authenticated in message");

        } else {
            mTargetParticipants = getIntent().getStringArrayListExtra("targetLists");
            //Now check to see if this is a new Conversation, or if the Activity needs to render an
            // existing Conversation
            Uri conversationURI = getIntent().getParcelableExtra("conversation-id");
            if (conversationURI != null)
                mConversation = LayerImpl.getLayerClient().getConversation(conversationURI);
            //This is an existing Conversation, display the messages, otherwise, allow the user to
            // add/remove participants and create a new Conversation
            if (mConversation != null)
                setupMessagesView();
            else
                createNewConversationView();
        }
    }

    //Existing Conversation, so render the messages in the RecyclerView
    private void setupMessagesView() {

        Log.d("Activity", "Conversation exists, setting up view");

        //Create the appropriate RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mMessagesView.setLayoutManager(layoutManager);

        //And attach it to the appropriate QueryAdapter, which will automatically update the view
        // when a new Message is added to the Conversation
        createMessagesAdapter();

        //Grab all the Participants and add them at the top of the screen (the "To:" field)
        populateToField(mConversation.getParticipants());
    }

    //Takes a String Array of user IDs, finds the display name, and adds them to the "To:" field
    // at the top of the Messages screen
    private void populateToField(List<String> participantIds) {
        //We will not include the Authenticated user in the "To:" field, since they know they are
        // already part of the Conversation
        TextView[] participantList = new TextView[participantIds.size() - 1];
        int idx = 0;
        for (String id : participantIds) {
            if (!id.equals(LayerImpl.getLayerClient().getAuthenticatedUserId())) {

                //Create a new stylized text view
                TextView tv = new TextView(this);
                tv.setText(Common.getUsername(id));
                tv.setTextSize(16);
                tv.setPadding(5, 5, 5, 5);
                tv.setBackgroundColor(Color.LTGRAY);
                participantList[idx] = tv;

                idx++;
            }
        }

        //Uses the helper function to make sure all participant names are appropriately displayed
        // and not cut off due to size constraints
    }

    //If a Conversation ID was not passed into this Activity, we assume that a new Conversation is
    // being created
    private void createNewConversationView() {

        Log.d("Activity", "Creating a new Conversation");

        //Create the appropriate RecyclerView which will be attached to the QueryController when it
        // is created (after the first message is sent and the Conversation is actually created)
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mMessagesView.setLayoutManager(layoutManager);
    }

    //This is called when there is a valid Conversation to attach the RecyclerView to the appropriate
    // QueryAdapter. Whenever a new Message is sent to the Conversation, the RecyclerView will be updated
    private void createMessagesAdapter() {

        //The Query Adapter drives the RecyclerView, and handles all the heavy lifting of checking 
        // for new Messages, and updating the RecyclerView 
        mMessagesAdapter = new MessageQueryAdapter(getApplicationContext(), LayerImpl.getLayerClient(), mMessagesView, mConversation, this, new QueryAdapter.Callback() {

            public void onItemInserted() {
                //When a new item is inserted into the RecyclerView, scroll to the bottom so the
                // most recent Message is always displayed
                mMessagesView.smoothScrollToPosition(Integer.MAX_VALUE);
            }
        });
        mMessagesView.setAdapter(mMessagesAdapter);

        //Execute the Query
        mMessagesAdapter.refresh();

        //Start by scrolling to the bottom (newest Message)
        mMessagesView.smoothScrollToPosition(Integer.MAX_VALUE);
    }

    //You can choose to present additional options when a Message is tapped
    public void onMessageClick(Message message) {

    }

    //You can choose to present additional options when a Message is long tapped
    public boolean onMessageLongClick(Message message) {
        return false;
    }


    //Handle the sendButtona nd Add/Remove Participants button (if displayed)
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.sendButton:
                Log.d("Activity", "Send button pressed");
                sendMessage();
                break;

        }
    }

    //The Authenticated User is actually sending a Message to this Conversation
    private void sendMessage() {
        //First Check to see if we have a valid Conversation object
        if (mConversation == null) {
            //Make sure there are valid participants. Since the Authenticated user will always be
            // included in a new Conversation, we check to see if there is more than one target participant
            if (mTargetParticipants.size() > 1) {

                //Create a new conversation, and tie it to the QueryAdapter
                mConversation = LayerImpl.getLayerClient().newConversation(mTargetParticipants);
                createMessagesAdapter();

                //Once the Conversation object is created, we don't allow changing the Participant List
                // Note: this is an implementation choice. It is always possible to add/remove participants
                // after a Conversation has been created

            } else {
                showAlert("Send Message Error", "You need to specify at least one participant before sending a message.");
                return;
            }
        }

        //Grab the user's input
        EditText input = (EditText) findViewById(R.id.textInput);
        String text = getTextAsString(input);

        //If the input is valid, create a new Message and send it to the Conversation
        if (mConversation != null && text != null && text.length() > 0) {

            MessagePart part = LayerImpl.getLayerClient().newMessagePart(text);
            Message msg = LayerImpl.getLayerClient().newMessage(part);
            mConversation.send(msg);
            input.setText("");

        } else {
            showAlert("Send Message Error", "You cannot send an empty message.");
        }
    }

    //When the RecyclerView changes size because of the Soft Keyboard, force scroll to the bottom
    // in order to always show the latest message
    protected void onShowKeyboard(int keyboardHeight) {
        mMessagesView.smoothScrollToPosition(Integer.MAX_VALUE);
    }

    protected void onHideKeyboard() {
        mMessagesView.smoothScrollToPosition(Integer.MAX_VALUE);
    }
}