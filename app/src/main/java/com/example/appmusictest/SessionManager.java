package com.example.appmusictest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.appmusictest.activity.LoginActivity;

public class SessionManager {
    private final Context context;
    private final SharedPreferences sp;

    private final String PREF_FILE_NAME = "Music_App";
    private final String KEY_NAME = "key_session_name";
    private final String KEY_ID = "key_session_id";
    private final String KEY_EMAIL = "key_session_email";
    private final String KEY_IF_LOGGED_IN = "key_session_if_logged_in";
    private final int PRIVATE_MODE = 0;
    SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        this.context = context;
        sp = context.getSharedPreferences(PREF_FILE_NAME, PRIVATE_MODE);
        editor = sp.edit();
    }

    public void createSession(String name, String email, String id) {
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ID, id);
        editor.putBoolean(KEY_IF_LOGGED_IN, true);
        editor.commit();
    }

    public boolean checkSession() {
        return sp.contains(KEY_IF_LOGGED_IN);
    }

    public String getIdUser() {
        return sp.getString(KEY_ID, null);
    }
    public void logOutSession() {
        editor.clear();
        editor.commit();
        context.startActivity(new Intent(context, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
