package com.mandaptak.android.Matches;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.query.Predicate;
import com.layer.sdk.query.Query;
import com.mandaptak.android.Layer.LayerImpl;
import com.mandaptak.android.Models.MatchesModel;
import com.mandaptak.android.R;
import com.mandaptak.android.Views.CircleImageView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.utils.Prefs;

public class MatchedProfileActivity extends AppCompatActivity {
  Context context;
  MatchesModel model = new MatchesModel();
  TextView name, age, religion, designation, traits;
  Button chatButton;
  CircleImageView image;
  ArrayList<String> mTargetParticipants = new ArrayList<>();
  String myName;

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
    ParseQuery<ParseObject> q1 = new ParseQuery<>("Profile");
    q1.getInBackground(Prefs.getProfileId(context), new GetCallback<ParseObject>() {
      @Override
      public void done(ParseObject parseObject, ParseException e) {
        try {
          myName = parseObject.fetchIfNeeded().getString("name");
        } catch (ParseException e1) {
          e1.printStackTrace();
        }
        getChatMembers(parseObject);

      }
    });
//        Query query = Query.builder(Conversation.class)
//                .build();
//        List<Conversation> results = LayerImpl.getLayerClient().executeQuery(query, Query.ResultType.OBJECTS);
//        if (results.size() > 0) {
//            for (int i = 0; i < results.size(); i++) {
//                Conversation conversation = results.get(0);
//                conversation.delete(LayerClient.DeletionMode.ALL_PARTICIPANTS);
//            }
//
//        }
    chatButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (mTargetParticipants.size() > 0) {
          Intent intent = new Intent(context, MessageScreen.class);
          Query query = Query.builder(Conversation.class)
              .predicate(new Predicate(Conversation.Property.PARTICIPANTS, Predicate.Operator.EQUAL_TO, mTargetParticipants))
              .build();
          List<Conversation> results = LayerImpl.getLayerClient().executeQuery(query, Query.ResultType.OBJECTS);
          if (results.size() > 0) {
            intent.putExtra("conversation-id", results.get(0).getId());
          } else {
            intent.putExtra("participant-map", mTargetParticipants);
            intent.putExtra("title-conv", model.getName() + " " + myName);
          }
          startActivity(intent);

        }
      }
    });

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

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      onBackPressed();
    }
    return super.onOptionsItemSelected(item);
  }

  private void getChatMembers(final ParseObject myProfile) {
    ParseQuery<ParseObject> q1 = new ParseQuery<>("Profile");
    q1.getInBackground(model.getProfileId(), new GetCallback<ParseObject>() {
      @Override
      public void done(ParseObject object, ParseException e) {
        if (e == null) {
          ParseQuery<ParseObject> query = new ParseQuery<>("UserProfile");
          query.whereEqualTo("profileId", myProfile);
          query.whereEqualTo("profileId", object);
          query.whereNotEqualTo("relation", "Agent");
          query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
              if (e == null)
                if (list.size() > 0) {
                  mTargetParticipants.add(LayerImpl.getLayerClient().getAuthenticatedUserId());
                  for (ParseObject parseObject : list) {
                    try {
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
//    private void getChatMembers(String profileId, final ParseObject myProfile) {
//        ParseQuery<ParseObject> q1 = new ParseQuery<>("Profile");
//        q1.getInBackground(profileId, new GetCallback<ParseObject>() {
//            @Override
//            public void done(ParseObject object, ParseException e) {
//                if (e == null) {
//                    ParseQuery<ParseObject> query = new ParseQuery<>("UserProfile");
//                    query.whereEqualTo("profileId", object).whereEqualTo("profileId", myProfile);
//                    query.findInBackground(new FindCallback<ParseObject>() {
//                        @Override
//                        public void done(List<ParseObject> list, ParseException e) {
//                            if (e == null)
//                                if (list.size() > 0) {
//                                    mTargetParticipants.add(LayerImpl.getLayerClient().getAuthenticatedUserId());
//                                    for (ParseObject parseObject : list) {
//                                        try {
//                                            if (!parseObject.fetchIfNeeded().getString("relation").equalsIgnoreCase("Agent"))
//                                                mTargetParticipants.add(parseObject.fetchIfNeeded().getParseObject("userId").getObjectId());
//                                        } catch (ParseException e1) {
//                                            e1.printStackTrace();
//                                        }
//                                    }
//                                }
//
//                        }
//                    });
//                }
//            }
//        });
//    }


}
