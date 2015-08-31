package com.mandaptak.android.Matches;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.layer.sdk.LayerClient;
import com.mandaptak.android.Adapter.MatchesAdapter;
import com.mandaptak.android.Layer.ConversationViewController;
import com.mandaptak.android.Layer.MyAuthenticationListener;
import com.mandaptak.android.Layer.MyConnectionListener;
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
import java.util.Arrays;
import java.util.List;

public class ChatsFragment extends Fragment {
    Common mApp;
    private View rootView;
    private Context context;
    public static final String LAYER_APP_ID = "layer:///apps/staging/3ffe495e-45e8-11e5-9685-919001005125";
    public static final String GCM_PROJECT_NUMBER = "00000";
    private LayerClient layerClient;
    private ConversationViewController conversationView;
    //Layer connection and authentication callback listeners
    private MyConnectionListener connectionListener;
    private MyAuthenticationListener authenticationListener;
    LayoutInflater inflater;
    ViewGroup container;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;
        init(inflater, container);
        return rootView;
    }

    private void init(LayoutInflater inflater, ViewGroup container) {
        context = getActivity();
        mApp = (Common) context.getApplicationContext();
        rootView = inflater.inflate(R.layout.activity_loading, container, false);
        //Create the callback listeners
        if (connectionListener == null)
            connectionListener = new MyConnectionListener(this);

        if (authenticationListener == null)
            authenticationListener = new MyAuthenticationListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();


        loadLayerClient();

        //Every time the app is brought to the foreground, register the typing indicator
        if (layerClient != null && conversationView != null)
            layerClient.registerTypingIndicator(conversationView);
    }

    @Override
    public void onPause() {
        super.onPause();
        //When the app is moved to the background, unregister the typing indicator
        if (layerClient != null && conversationView != null)
            layerClient.unregisterTypingIndicator(conversationView);
    }

    private void loadLayerClient() {

        // Check if Sample App is using a valid app ID.
        if (isValidAppID()) {
            if (layerClient == null) {
                //Used for debugging purposes ONLY. DO NOT include this option in Production Builds.
                LayerClient.enableLogging(getActivity().getApplicationContext());

                // Initializes a LayerClient object with the Google Project Number
                LayerClient.Options options = new LayerClient.Options();
                options.googleCloudMessagingSenderId(GCM_PROJECT_NUMBER);
                layerClient = LayerClient.newInstance(context, LAYER_APP_ID, options);

                //Register the connection and authentication listeners
                layerClient.registerConnectionListener(connectionListener);
                layerClient.registerAuthenticationListener(authenticationListener);
            }

            //Check the current state of the SDK. The client must be CONNECTED and the user must
            // be AUTHENTICATED in order to send and receive messages. Note: it is possible to be
            // authenticated, but not connected, and vice versa, so it is a best practice to check
            // both states when your app launches or comes to the foreground.
            if (!layerClient.isConnected()) {

                //If Layer is not connected, make sure we connect in order to send/receive messages.
                // MyConnectionListener.java handles the callbacks associated with Connection, and
                // will start the Authentication process once the connection is established
                layerClient.connect();

            } else if (!layerClient.isAuthenticated()) {

                //If the client is already connected, try to authenticate a user on this device.
                // MyAuthenticationListener.java handles the callbacks associated with Authentication
                // and will start the Conversation View once the user is authenticated
                layerClient.authenticate();

            } else {

                // If the client is to Layer and the user is authenticated, start the Conversation
                // View. This will be called when the app moves from the background to the foreground,
                // for example.
                onUserAuthenticated();
            }
        }
    }

    //If you haven't replaced "LAYER_APP_ID" with your App ID, send a message
    private boolean isValidAppID() {
        if (LAYER_APP_ID.equalsIgnoreCase("LAYER_APP_ID")) {

            // Instantiate an AlertDialog.Builder with its constructor
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            // Chain together various setter methods to set the dialog characteristics
            builder.setMessage("To correctly use this project you need to replace LAYER_APP_ID in MainActivity.java (line 39) with your App ID from developer.layer.com.")
                    .setTitle(":-(");

            // Get the AlertDialog from create() and then show() it
            AlertDialog dialog = builder.create();
            dialog.show();

            return false;
        }

        return true;
    }

    public static String getUserID() {
        if (Build.FINGERPRINT.startsWith("generic"))
            return "Simulator";

        return "Device";
    }

    //By default, create a conversationView between these 3 participants
    public static List<String> getAllParticipants() {
        return Arrays.asList("Device", "Simulator", "Dashboard");
    }

    //Once the user has successfully authenticated, begin the conversationView
    public void onUserAuthenticated() {

        if (conversationView == null) {

            conversationView = new ConversationViewController(rootView, layerClient,inflater,container);

            if (layerClient != null) {
                layerClient.registerTypingIndicator(conversationView);
            }
        }
    }

}
