package com.mandaptak.android.Matches;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.layer.sdk.messaging.ConversationOptions;
import com.mandaptak.android.Adapter.MatchesAdapter;
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
    ArrayList<String> mTargetParticipants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matched_profile);
        context = this;
        mTargetParticipants = new ArrayList<>();
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
                Intent intent = new Intent();

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
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageActivity.class);
                ConversationOptions options = new ConversationOptions().distinct(true);
                intent.putExtra("conversation-id", LayerImpl.getLayerClient().newConversation(options, mTargetParticipants).getId());
                startActivity(intent);
            }
        });
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
        q1.getInBackground(Prefs.getProfileId(context), new GetCallback<ParseObject>() {
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
                                    for (ParseObject parseObject : list) {
                                        try {
                                            mTargetParticipants.add(parseObject.fetchIfNeeded().getString("userId"));

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
