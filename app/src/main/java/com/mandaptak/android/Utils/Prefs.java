package com.mandaptak.android.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mandaptak.android.Models.MatchesModel;
import com.mandaptak.android.Models.Participant;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Prefs {


    public static String CHAT_USERS = "chat_users";
    public static String MATCHES = "matches";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences("mandapTak", Context.MODE_MULTI_PROCESS);
    }

    public static HashMap<String, Participant> getChatUsers(Context context) {
        String json = getPrefs(context).getString(CHAT_USERS, null);
        Type type = new TypeToken<HashMap<String, Participant>>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }

    public static void setChatUsers(Context context, Map<String, Participant> list) {
        getPrefs(context).edit().putString(CHAT_USERS, new Gson().toJson(list)).commit();
    }

    public static ArrayList<MatchesModel> getMatches(Context context) {
        String json = getPrefs(context).getString(MATCHES, null);
        Type type = new TypeToken<ArrayList<MatchesModel>>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }

    public static void setMatches(Context context, ArrayList<MatchesModel> list) {
        getPrefs(context).edit().putString(MATCHES, new Gson().toJson(list)).commit();
    }

}

