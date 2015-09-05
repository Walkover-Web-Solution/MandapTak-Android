package com.mandaptak.android.Matches;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.layer.atlas.Atlas;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.Map;

public class AtlasIdentityProvider implements Atlas.ParticipantProvider {

    private final Map<String, Participant> participantsMap = new HashMap<>();
    private final Context context;
    Participant model = new Participant();

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
    /*    ParseQuery<ParseObject> q1 = new ParseQuery<>("_User");
        q1.getInBackground(userId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {


                }
            }
        });*/
        ParseQuery<ParseObject> query = new ParseQuery<>("Profile");
        ParseUser pu = new ParseUser();
        pu.setObjectId(userId);
        query.whereEqualTo("userId", pu);
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
        //   if (!participantsMap.containsKey(userId))
        //    participantsMap.put(userId, model);
        Participant participant = model;
        return participant;
    }

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