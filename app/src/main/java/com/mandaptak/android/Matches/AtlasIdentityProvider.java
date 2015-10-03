package com.mandaptak.android.Matches;

import android.content.Context;

import com.layer.atlas.Atlas;
import com.mandaptak.android.Models.MatchesModel;
import com.mandaptak.android.Models.Participant;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.iwf.photopicker.utils.Prefs;

public class AtlasIdentityProvider implements Atlas.ParticipantProvider {

  private final Context context;
  ArrayList<ParseObject> profileObjs;
  ArrayList<MatchesModel> matchList = new ArrayList<>();
  String name;
  private Map<String, Participant> participantsMap = new HashMap<>();

  public AtlasIdentityProvider(Context context) {
    this.context = context;
    getMatchesFromFunction();

  }

  @Override
  public Map<String, Atlas.Participant> getParticipants(String filter, Map<String, Atlas.Participant> result) {
    if (result == null) {
      result = new HashMap<>();
    }

    // With no filter, return all Participants
    if (filter == null || filter == "") {
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
    return participantsMap.get(userId);
  }

  private void getMatchesFromFunction() {
    HashMap<String, Object> params = new HashMap<>();
    params.put("profileId", Prefs.getProfileId(context));
    ParseCloud.callFunctionInBackground("getMatchedProfile", params, new FunctionCallback<ArrayList<ParseObject>>() {
      @Override
      public void done(ArrayList<ParseObject> o, ParseException e) {
        if (e == null) {
          if (o != null) {
            profileObjs = o;
            if (profileObjs.size() > 0) {
              for (ParseObject parseObject : profileObjs) {
                try {
                  MatchesModel model = new MatchesModel();
                  model.setProfileId(parseObject.getObjectId());
                  model.setUserId(parseObject.fetchIfNeeded().getParseObject("userId").getObjectId());
                  String name = parseObject.fetchIfNeeded().getString("name");
                  if (name != null)
                    model.setName(name);
                  String work = parseObject.fetchIfNeeded().getString("designation");
                  if (work != null)
                    model.setWork(work);
                  String religion = parseObject.fetchIfNeeded().getParseObject("religionId").fetchIfNeeded().getString("name");
                  if (religion != null)
                    model.setReligion(religion);
                  String url = parseObject.fetchIfNeeded().fetchIfNeeded().getParseFile("profilePic").getUrl();
                  if (url != null) {
                    model.setUrl(url);
                  }
                  matchList.add(model);
                } catch (ParseException e1) {
                  e1.printStackTrace();
                }
              }
              com.mandaptak.android.Utils.Prefs.setMatches(context, matchList);
              ParseQuery<ParseObject> q1 = new ParseQuery<>("Profile");
              q1.getInBackground(Prefs.getProfileId(context), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                  if (e == null) {
                    profileObjs.add(object);
                    saveMatches();
                  }
                }
              });
            }
          }
        } else {
          e.printStackTrace();
        }
      }
    });
  }

  private void saveMatches() {
    final ArrayList<ParseObject> relatives = new ArrayList<>();
    for (int i = 0; i < profileObjs.size(); i++) {
      ParseQuery<ParseObject> query = new ParseQuery<>("UserProfile");
      query.whereEqualTo("profileId", profileObjs.get(i));
      final int finalI = i;
      query.findInBackground(new FindCallback<ParseObject>() {
        @Override
        public void done(List<ParseObject> list, ParseException e) {
          if (e == null)
            if (list.size() > 0) {
              relatives.addAll(list);
              if (finalI == profileObjs.size() - 1) {
                saveinfo(relatives);
              }
            }
        }
      });
    }

  }

  void saveinfo(ArrayList<ParseObject> relatives) {
    for (ParseObject parseObject : relatives) {
      try {
        name = parseObject.fetchIfNeeded().getParseObject("profileId").fetchIfNeeded().getString("name");
        //    name = parseObjectPro.getString("name");
        Participant participant = new Participant();
        participant.userId = parseObject.fetchIfNeeded().getParseObject("userId").getObjectId();
        String relation = parseObject.fetchIfNeeded().getString("relation");
        if (relation.equalsIgnoreCase("Bachelor")) {
          participant.firstName = name;
        } else {
          participant.firstName = relation + " (" + name + ")";
        }
        if (!participantsMap.containsKey(participant.userId))
          participantsMap.put(participant.userId, participant);
      } catch (ParseException e1) {
        e1.printStackTrace();
      }
    }
    com.mandaptak.android.Utils.Prefs.setChatUsers(context, participantsMap);
  }

}