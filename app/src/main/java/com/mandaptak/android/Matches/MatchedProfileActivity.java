package com.mandaptak.android.Matches;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.layer.atlas.Atlas;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.query.Predicate;
import com.layer.sdk.query.Query;
import com.mandaptak.android.Layer.LayerImpl;
import com.mandaptak.android.Models.MatchesModel;
import com.mandaptak.android.Models.Participant;
import com.mandaptak.android.R;
import com.mandaptak.android.Views.CircleImageView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchedProfileActivity extends AppCompatActivity {
    static public Atlas.ParticipantProvider participantProvider;
    Context context;
    MatchesModel model = new MatchesModel();
    TextView name, age, religion, designation, traits;
    Button chatButton;
    CircleImageView image;
    ArrayList<String> mTargetParticipants = new ArrayList<>();
    HashMap<String, Participant> users = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matched_profile);
        context = this;
        if (getIntent() != null) {
            if (getIntent().hasExtra("profile")) {
                model = (MatchesModel) getIntent().getSerializableExtra("profile");
            }
        }
        init();
        if (model.getName() != null) {
            Picasso.with(context)
                    .load(model.getUrl())
                    .into(image);
            name.setText(model.getName());
            religion.setText(model.getReligion());
            designation.setText(model.getWork());
        }
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTargetParticipants.size() > 0) {
                    initParti();
                    Intent intent = new Intent(context, MessageScreen.class);
                    Query query = Query.builder(Conversation.class)
                            .predicate(new Predicate(Conversation.Property.PARTICIPANTS, Predicate.Operator.EQUAL_TO, mTargetParticipants))
                            .build();
                    List<Conversation> results = LayerImpl.getLayerClient().executeQuery(query, Query.ResultType.OBJECTS);
                    if (results.size() > 0) {
                        intent.putExtra("conversation-id", results.get(0).getId());
                    } else {
                        intent.putExtra("participant-map", mTargetParticipants);
                    }
                    startActivity(intent);

                }
            }
        });
        getChatMembers();
    }

    void init() {
        image = (CircleImageView) findViewById(R.id.image);
        name = (TextView) findViewById(R.id.display_name);
        age = (TextView) findViewById(R.id.age);
        religion = (TextView) findViewById(R.id.religion);
        designation = (TextView) findViewById(R.id.designation);
        traits = (TextView) findViewById(R.id.matching_traits);
        chatButton = (Button) findViewById(R.id.chat_button);
    }

    public void initParti() {
        participantProvider = new Atlas.ParticipantProvider() {
            @Override
            public Map<String, Atlas.Participant> getParticipants(String filter, Map<String, Atlas.Participant> result) {
                if (result == null) {
                    result = new HashMap<String, Atlas.Participant>();
                }

                // With no filter, return all Participants
                if (filter == null) {
                    result.putAll(users);
                    return result;
                }

                // Filter participants by substring matching first- and last- names
                for (Participant p : users.values()) {
                    boolean matches = false;
                    if (p.firstName != null && p.firstName.toLowerCase().contains(filter))
                        matches = true;
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
            public Atlas.Participant getParticipant(String userId) {
                Participant participant = users.get(userId);
                return participant;
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getChatMembers() {
        ParseQuery<ParseObject> q1 = new ParseQuery<>("Profile");
        q1.getInBackground(model.getProfileId(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    ParseQuery<ParseObject> query = new ParseQuery<>("UserProfile");
                    query.whereEqualTo("profileId", object);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> list, ParseException e) {
                            if (e == null)
                                if (list.size() > 0) {
                                    mTargetParticipants.add(LayerImpl.getLayerClient().getAuthenticatedUserId());
                                    //mTargetParticipants.add(model.getUserId());
                                    Participant participant1 = new Participant();
                                    participant1.setUserId(model.getUserId());
                                    participant1.setLastName("");
                                    participant1.setFirstName(model.getName());
                                    participant1.setAvtatar(image.getDrawable());
                                    users.put(model.getUserId(), participant1);
                                    for (ParseObject parseObject : list) {
                                        try {
                                            Participant participant = new Participant();
                                            participant.setFirstName(parseObject.fetchIfNeeded().getParseObject("profileId").getString("name"));
                                            participant.setUserId(parseObject.fetchIfNeeded().getParseObject("userId").getObjectId());
                                            participant.setAvtatar(image.getDrawable());
                                            participant.setLastName(parseObject.fetchIfNeeded().getString("relation"));
                                            users.put(participant.getId(), participant);
                                            mTargetParticipants.add(parseObject.fetchIfNeeded().getParseObject("userId").getObjectId());

                                        } catch (ParseException e1) {
                                            e1.printStackTrace();
                                        }
                                    }

                                }

                        }
                    });
                }
            }
        });
    }
}
