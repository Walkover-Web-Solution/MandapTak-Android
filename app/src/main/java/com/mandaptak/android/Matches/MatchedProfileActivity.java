package com.mandaptak.android.Matches;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mandaptak.android.Models.MatchesModel;
import com.mandaptak.android.R;
import com.mandaptak.android.Views.CircleImageView;
import com.squareup.picasso.Picasso;

public class MatchedProfileActivity extends AppCompatActivity {
    Context context;
    MatchesModel model = new MatchesModel();
    TextView name, age, religion, designation, traits;
    Button chatButton;
    CircleImageView image;

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
                Intent intent = new Intent();

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
}
