package com.mandaptak.android.Matches;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.layer.atlas.Atlas;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Oleg Orlov
 * @since 17 Jul 2015
 */
public class AtlasIdentityProvider implements Atlas.ParticipantProvider {
    private final static String TAG = AtlasIdentityProvider.class.getSimpleName();
    private static final boolean debug = false;
    private static final int REFRESH_TIMEOUT_MILLIS = 60 * 1000;

    private final Map<String, Participant> participantsMap = new HashMap<>();
    private final Context context;
    private final Object refreshLock = new Object();
    Participant model = new Participant();
    private String appId;
    private Thread refresher;
    private boolean refreshRequired = false;
    private long lastRefreshMs = System.currentTimeMillis();

    public AtlasIdentityProvider(Context context) {
        this.context = context;

    }

    @Override
    public Map<String, Atlas.Participant> getParticipants(String filter, Map<String, Atlas.Participant> result) {
        if (result == null) {
            result = new HashMap<>();
        }

        // With no filter, return all Participants
        if (filter == null) {
            result.putAll(participantsMap);
            return result;
        }

        // Filter participants by substring matching first- and last- names
        for (Participant p : participantsMap.values()) {
            boolean matches = false;
            if (p.firstName != null && p.firstName.toLowerCase().contains(filter)) matches = true;
            if (!matches && p.lastName != null && p.lastName.toLowerCase().contains(filter))
                matches = true;
            if (matches) {
                result.put(p.getId(), p);
            } else {
                result.remove(p.getId());
            }
        }
        return result;
    }

    @Override
    public Atlas.Participant getParticipant(final String userId) {
        ParseQuery<ParseObject> q1 = new ParseQuery<>("_User");
        q1.getInBackground(userId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    ParseQuery<ParseObject> query = new ParseQuery<>("Profile");
                    query.whereEqualTo("userId", object);
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            if (e == null) {
                                try {

                                    String name = parseObject.fetchIfNeeded().getString("name");
                                    if (name != null)
                                        model.firstName = (name);
                                    model.userId = userId;

                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                            }

                        }
                    });

                }
            }
        });
        Participant participant = model;
        return participant;
    }

  /*  */

    /**
     * @return String[] { indentityToken (may be null), status/error description }
     *//*
    public String[] getIdentityToken(String nonce, String userName) {
        if (appId == null) return new String[] {null, "App ID is not set!"};
        return refreshContacts(true, nonce, userName);
    }*/
    public void requestRefresh() {
        synchronized (refreshLock) {
            refreshRequired = true;
            refreshLock.notifyAll();
        }
    }

    public void setAppId(String appId) {
        try {
            UUID.fromString(appId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("appId must be valid UUID value. appId: " + appId, e);
        }
        synchronized (refreshLock) {
            this.appId = appId;
            refreshRequired = true;
            refreshLock.notifyAll();
        }
    }

/*    private String[] refreshContacts(boolean requestIdentityToken, String nonce, String userName) {
        try {
            String url = "https://layer-identity-provider.herokuapp.com/apps/" + appId + "/atlas_identities";
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Accept", "application/json");
            post.setHeader("X_LAYER_APP_ID", appId);

            JSONObject rootObject = new JSONObject();
            if (requestIdentityToken) {
                rootObject.put("nonce", nonce);
                rootObject.put("name", userName);
            } else {
                rootObject.put("name", "Web");  // name must be specified to make entiry valid
            }
            StringEntity entity = new StringEntity(rootObject.toString(), "UTF-8");
            entity.setContentType("application/json");
            post.setEntity(entity);

            HttpResponse response = (new DefaultHttpClient()).execute(post);
            if (HttpStatus.SC_OK != response.getStatusLine().getStatusCode() && HttpStatus.SC_CREATED != response.getStatusLine().getStatusCode()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Got status ").append(response.getStatusLine().getStatusCode()).append(" [").append(response.getStatusLine())
                        .append("] when logging in. Request: ").append(url);
                if (requestIdentityToken) sb.append(" login: ").append(userName).append(", nonce: ").append(nonce);
                Log.e(TAG, sb.toString());
                return new String[] {null, sb.toString()};
            }

            String responseString = EntityUtils.toString(response.getEntity());
            JSONObject jsonResp = new JSONObject(responseString);

            JSONArray atlasIdentities = jsonResp.getJSONArray("atlas_identities");
            List<Participant> participants = new ArrayList<Participant>(atlasIdentities.length());
            for (int i = 0; i < atlasIdentities.length(); i++) {
                JSONObject identity = atlasIdentities.getJSONObject(i);
                Participant participant = new Participant();
                participant.firstName = identity.getString("name");
                participant.userId = identity.getString("id");
                participants.add(participant);
            }
            if (participants.size() > 0) {
                setParticipants(participants);
                save();
                if (debug) Log.d(TAG, "refreshContacts() contacts: " + atlasIdentities);
            }

            if (requestIdentityToken) {
                String error = jsonResp.optString("error", null);
                String identityToken = jsonResp.optString("identity_token");
                return new String[] {identityToken, error};
            }
            return new String[] {null, "Refreshed " + participants.size() + " contacts"};
        } catch (Exception e) {
            Log.e(TAG, "Error when fetching identity token", e);
            return new String[] {null, "Cannot obtain identity token. " + e};
        }
    }*/

    /**
     * Overwrites the current list of Contacts with the provided list.
     *
     * @param participants New list of Contacts to apply.
     */
    private void setParticipants(List<Participant> participants) {
        synchronized (participantsMap) {
            participantsMap.clear();
            for (Participant participant : participants) {
                participantsMap.put(participant.userId, participant);
            }
        }
    }



/*    private boolean save() {
        Collection<Participant> participants;
        synchronized (participantsMap) {
            participants = participantsMap.values();
        }

        JSONArray contactsJson;
        try {
            contactsJson = new JSONArray();
            for (Participant participant : participants) {
                JSONObject contactJson = new JSONObject();
                contactJson.put("id", participant.userId);
                contactJson.put("first_name", participant.firstName);
                contactJson.put("last_name", participant.lastName);
                contactsJson.put(contactJson);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error while saving", e);
            return false;
        }

        SharedPreferences.Editor editor = context.getSharedPreferences("contacts", Context.MODE_PRIVATE).edit();
        editor.putString("json", contactsJson.toString());
        editor.commit();

        return true;
    }*/

    public class Participant implements Atlas.Participant {
        public String userId;
        public String firstName;
        public String lastName;

        public String getId() {
            return userId;
        }

        @Override
        public String getFirstName() {
            return firstName;
        }

        @Override
        public String getLastName() {
            return lastName;
        }

        @Override
        public Drawable getAvatarDrawable() {
            return null;
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Contact [userId: ").append(userId).append(", firstName: ").append(firstName).append(", lastName: ").append(lastName).append("]");
            return builder.toString();
        }
    }

}