package com.mandaptak.android.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.query.Predicate;
import com.layer.sdk.query.Query;
import com.mandaptak.android.Layer.LayerImpl;
import com.mandaptak.android.Matches.MessageScreen;
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

public class MatchesAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<MatchesModel> list;
    MatchesModel model = new MatchesModel();
    ArrayList<String> mTargetParticipants = new ArrayList<>();

    public MatchesAdapter(ArrayList<MatchesModel> paramArrayList, Context paramContext) {
        this.list = paramArrayList;
        this.ctx = paramContext;
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int paramInt) {
        return this.list.get(paramInt);
    }

    public long getItemId(int paramInt) {
        return paramInt;
    }

    public View getView(final int paramInt, View paramView, ViewGroup paramViewGroup) {
        ViewHolder viewholder;
        if (paramView == null) {
            paramView = LayoutInflater.from(ctx).inflate(R.layout.matches_row, null);
            viewholder = new ViewHolder();
            viewholder.tvName = (TextView) paramView.findViewById(R.id.title);
            viewholder.tvReligion = ((TextView) paramView.findViewById(R.id.religion));
            viewholder.tvWork = (TextView) paramView.findViewById(R.id.work);
            viewholder.chatButton = (ImageView) paramView.findViewById(R.id.chat_button);
            viewholder.profilePic = (CircleImageView) paramView.findViewById(R.id.thumbnail);
            paramView.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) paramView.getTag();
        }
        MatchesModel matchesModel = list.get(paramInt);
        viewholder.tvName.setText(matchesModel.getName());
        viewholder.tvReligion.setText(matchesModel.getReligion());
        viewholder.tvWork.setText(matchesModel.getWork());
        viewholder.chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getChatMembers(list.get(paramInt).getProfileId(), list.get(paramInt).getName());
            }
        });
        Picasso.with(ctx)
                .load(matchesModel.getUrl())
                .into(viewholder.profilePic);

        return paramView;
    }

    static class ViewHolder {
        public TextView tvName;
        public TextView tvReligion;
        public TextView tvWork;
        public ImageView chatButton;
        public CircleImageView profilePic;
    }

    private void getChatMembers(String profileId, final String name) {
        ParseQuery<ParseObject> q1 = new ParseQuery<>("Profile");
        q1.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        q1.getInBackground(profileId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    ParseQuery<ParseObject> query = new ParseQuery<>("UserProfile");
                    query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
                    query.whereEqualTo("profileId", object);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> list, ParseException e) {
                            if (e == null)
                                if (list.size() > 0) {
                                    mTargetParticipants.add(LayerImpl.getLayerClient().getAuthenticatedUserId());
                                    for (ParseObject parseObject : list) {
                                        try {
                                            if (!parseObject.fetchIfNeeded().getString("relation").equalsIgnoreCase("Agent"))
                                                mTargetParticipants.add(parseObject.fetchIfNeeded().getParseObject("userId").getObjectId());
                                        } catch (ParseException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                    if (mTargetParticipants.size() > 0) {
                                        Intent intent = new Intent(ctx, MessageScreen.class);
                                        Query query = Query.builder(Conversation.class)
                                                .predicate(new Predicate(Conversation.Property.PARTICIPANTS, Predicate.Operator.EQUAL_TO, mTargetParticipants))
                                                .build();
                                        List<Conversation> results = LayerImpl.getLayerClient().executeQuery(query, Query.ResultType.OBJECTS);
                                        if (results.size() > 0) {
                                            intent.putExtra("conversation-id", results.get(0).getId());
                                        } else {
                                            intent.putExtra("participant-map", mTargetParticipants);
                                            intent.putExtra("tittle-conv", name);
                                        }
                                        ctx.startActivity(intent);

                                    }

                                }

                        }
                    });
                }
            }
        });
    }
}