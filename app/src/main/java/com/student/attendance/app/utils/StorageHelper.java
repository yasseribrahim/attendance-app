package com.student.attendance.app.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.student.attendance.app.CustomApplication;
import com.student.attendance.app.models.User;

public class StorageHelper {
    private static SharedPreferences preferences;
    private static User currentUser;
    private static final String KEY_CURRENT_USER = "current-user";

    static {
        preferences = PreferenceManager.getDefaultSharedPreferences(CustomApplication.getApplication());
    }

    public static synchronized User getCurrentUser() {
        if (currentUser == null) {
            try {
                String json = preferences.getString(KEY_CURRENT_USER, "");
                currentUser = new Gson().fromJson(json, new TypeToken<User>() {
                }.getType());
            } catch (Exception ex) {
            }
        }
        return currentUser;
    }

    public static synchronized void clearCurrentUser() {
        try {
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(KEY_CURRENT_USER);
            editor.apply();
            currentUser = null;
        } catch (Exception ex) {
        }
    }

    public static synchronized void setCurrentUser(User user) {
        if (user != null) {
            try {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(KEY_CURRENT_USER, new Gson().toJson(user));
                editor.apply();
            } catch (Exception ex) {
            }

            getCurrentUser();
        }
    }
}
