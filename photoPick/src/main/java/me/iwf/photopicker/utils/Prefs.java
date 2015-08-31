package me.iwf.photopicker.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Prefs {

    public static String PROFILE_ID = "profile_id";
    public static String IMAGE_LIST = "image_list";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences("mandapTak", Context.MODE_MULTI_PROCESS);
    }

    // -----------------------------------------------------------------------------------------------------------------------------------

    public static ArrayList<ImageModel> getImageList(Context context) {
        String json = getPrefs(context).getString(IMAGE_LIST, null);
        Type type = new TypeToken<ArrayList<ImageModel>>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }

    public static void setImageList(Context context, ArrayList<ImageModel> list) {
        getPrefs(context).edit().putString(IMAGE_LIST, new Gson().toJson(list)).commit();
    }

    public static String getProfileId(Context context) {
        return getPrefs(context).getString(PROFILE_ID, "");
    }

    public static void setProfileId(Context context, String value) {
        getPrefs(context).edit().putString(PROFILE_ID, value).commit();
    }
}

