package com.mandaptak.android.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mandaptak.android.Matches.PinsFragment;
import com.mandaptak.android.Models.MatchesModel;
import com.mandaptak.android.R;
import com.mandaptak.android.Utils.Common;
import com.mandaptak.android.Views.CircleImageView;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import me.iwf.photopicker.utils.Prefs;

public class PinsAdapter extends BaseAdapter {
  Context ctx;
  ArrayList<MatchesModel> list;
  PinsFragment pinsFragment;
  Common mApp;

  public PinsAdapter(PinsFragment pinsFragment, ArrayList<MatchesModel> paramArrayList, Context paramContext) {
    this.list = paramArrayList;
    this.ctx = paramContext;
    this.pinsFragment = pinsFragment;
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
      paramView = LayoutInflater.from(ctx).inflate(R.layout.pins_row, null);
      viewholder = new ViewHolder();
      viewholder.tvName = (TextView) paramView.findViewById(R.id.title);
      viewholder.unpin = (ImageView) paramView.findViewById(R.id.unpin);
      viewholder.dislikeprofile = (ImageView) paramView.findViewById(R.id.dislike);
      viewholder.tvReligion = ((TextView) paramView.findViewById(R.id.religion));
      viewholder.tvWork = (TextView) paramView.findViewById(R.id.work);
      viewholder.profilePic = (CircleImageView) paramView.findViewById(R.id.thumbnail);
      paramView.setTag(viewholder);
    } else {
      viewholder = (ViewHolder) paramView.getTag();
    }
    final MatchesModel matchesModel = list.get(paramInt);
    viewholder.tvName.setText(matchesModel.getName());
    viewholder.tvReligion.setText(matchesModel.getReligion());
    viewholder.tvWork.setText(matchesModel.getWork());
    Picasso.with(ctx)
        .load(matchesModel.getUrl())
        .error(R.drawable.com_facebook_profile_picture_blank_square)
        .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
        .into(viewholder.profilePic);
    viewholder.unpin.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        toggleClick(view, false);
        mApp.show_PDialog(ctx, "Unpinning profile",false);
        ParseQuery<ParseObject> query = new ParseQuery<>("PinnedProfile");
        query.whereEqualTo("profileId", ParseObject.createWithoutData("Profile", Prefs.getProfileId(ctx)));
        query.whereEqualTo("pinnedProfileId", ParseObject.createWithoutData("Profile", matchesModel.getProfileId()));
        query.getFirstInBackground(new GetCallback<ParseObject>() {
          @Override
          public void done(ParseObject parseObject, ParseException e) {
            if (e == null) {
              parseObject.deleteInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                  if (e == null) {
                    pinsFragment.getParseData();
                  } else {
                    mApp.showToast(ctx, e.getMessage());
                  }
                  mApp.dialog.dismiss();
                }
              });
            } else {
              mApp.dialog.dismiss();
              mApp.showToast(ctx, e.getMessage());
            }
          }
        });
        toggleClick(view, true);
      }
    });
    viewholder.dislikeprofile.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        toggleClick(view, false);
        mApp.show_PDialog(ctx, "Please wait",false);
        ParseQuery<ParseObject> query = new ParseQuery<>("PinnedProfile");
        query.whereEqualTo("profileId", ParseObject.createWithoutData("Profile", Prefs.getProfileId(ctx)));
        query.whereEqualTo("pinnedProfileId", ParseObject.createWithoutData("Profile", matchesModel.getProfileId()));
        query.getFirstInBackground(new GetCallback<ParseObject>() {
          @Override
          public void done(ParseObject parseObject, ParseException e) {
            if (e == null) {
              parseObject.deleteInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                  if (e == null) {
                    ParseObject dislikeParseObject = new ParseObject("DislikeProfile");
                    dislikeParseObject.put("dislikeProfileId", ParseObject.createWithoutData("Profile", matchesModel.getProfileId()));
                    dislikeParseObject.put("profileId", ParseObject.createWithoutData("Profile", Prefs.getProfileId(ctx)));
                    dislikeParseObject.saveInBackground(new SaveCallback() {
                      @Override
                      public void done(ParseException e) {
                        pinsFragment.getParseData();
                      }
                    });

                  } else {
                    mApp.showToast(ctx, e.getMessage());
                  }
                  mApp.dialog.dismiss();
                }
              });
            } else {
              mApp.dialog.dismiss();
              mApp.showToast(ctx, e.getMessage());
            }
          }
        });
        toggleClick(view, true);
      }


    });
    return paramView;
  }

  private void toggleClick(View view, boolean enabled) {
    view.setEnabled(enabled);
    view.setClickable(enabled);
  }

  static class ViewHolder {
    public TextView tvName;
    public TextView tvReligion;
    public TextView tvWork;
    public CircleImageView profilePic;
    public ImageView unpin;
    public ImageView dislikeprofile;
  }
}