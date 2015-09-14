package me.iwf.photopicker.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import me.iwf.photopicker.entity.Profile;

public class Prefs {

    public static String PROFILE_ID = "profile_id";
    public static String PROFILE = "profile";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences("mandapTak", Context.MODE_MULTI_PROCESS);
    }

    // -----------------------------------------------------------------------------------------------------------------------------------

    public static Profile getProfile(Context context) {
        String json = getPrefs(context).getString(PROFILE, null);
        Type type = new TypeToken<Profile>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }

    public static void setProfile(Context context, Profile profile) {
        getPrefs(context).edit().putString(PROFILE, new Gson().toJson(profile)).commit();
    }

    public static String getProfileId(Context context) {
        return getPrefs(context).getString(PROFILE_ID, "");
    }

    public static void setProfileId(Context context, String value) {
        getPrefs(context).edit().putString(PROFILE_ID, value).commit();
    }
}

