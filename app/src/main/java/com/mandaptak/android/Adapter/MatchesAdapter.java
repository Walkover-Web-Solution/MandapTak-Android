package com.mandaptak.android.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.query.Predicate;
import com.layer.sdk.query.Query;
import com.mandaptak.android.Layer.LayerImpl;
import com.mandaptak.android.Matches.MessageScreen;
import com.mandaptak.android.Models.MatchesModel;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.utils.Prefs;

public class MatchesAdapter extends BaseAdapter {
  Context ctx;
  ArrayList<MatchesModel> list;
  ArrayList<String> mTargetParticipants = new ArrayList<>();
  Common mApp;

  public MatchesAdapter(ArrayList<MatchesModel> paramArrayList, Context paramContext) {
    this.list = paramArrayList;
    this.ctx = paramContext;
    mApp = (Common) ctx.getApplicationContext();
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
      viewholder = new ViewHolder();
      paramView = LayoutInflater.from(ctx).inflate(R.layout.matches_row, null);
      viewholder.tvName = (TextView) paramView.findViewById(R.id.title);
      viewholder.tvReligion = ((TextView) paramView.findViewById(R.id.religion));
      viewholder.tvWork = (TextView) paramView.findViewById(R.id.work);
      viewholder.chatButton = (ImageView) paramView.findViewById(R.id.chat_button);
      viewholder.profilePic = (SimpleDraweeView) paramView.findViewById(R.id.thumbnail);
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
    viewholder.profilePic.setImageURI(Uri.parse(matchesModel.getUrl()));
    return paramView;
  }

  private void getChatMembers(String profileId, final String name) {
    mApp.show_PDialog(ctx, "Please wait", false);
    ParseQuery<ParseObject> query = new ParseQuery<>("UserProfile");
    query.include("userId");
    query.whereEqualTo("profileId", ParseObject.createWithoutData("Profile", Prefs.getProfileId(ctx)));
    query.whereEqualTo("profileId", ParseObject.createWithoutData("Profile", profileId));
    query.whereNotEqualTo("relation", "Agent");
    query.findInBackground(new FindCallback<ParseObject>() {
      @Override
      public void done(List<ParseObject> list, ParseException e) {
        if (e == null) {
          mApp.dialog.dismiss();
          if (list.size() > 0) {
            mTargetParticipants.add(LayerImpl.getLayerClient().getAuthenticatedUserId());
            for (ParseObject parseObject : list) {
              mTargetParticipants.add(parseObject.getParseObject("userId").getObjectId());
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
                try {
                  intent.putExtra("title-conv", name + " " + ParseObject.createWithoutData("Profile", Prefs.getProfileId(ctx)).fetchIfNeeded().getString("name"));
                } catch (ParseException e1) {
                  e1.printStackTrace();
                }
              }
              ctx.startActivity(intent);
            }
          }
        } else {
          mApp.showToast(ctx, e.getMessage());
          mApp.dialog.dismiss();
        }
      }
    });

  }

  static class ViewHolder {
    public TextView tvName;
    public TextView tvReligion;
    public TextView tvWork;
    public ImageView chatButton;
    private SimpleDraweeView profilePic;
  }
}