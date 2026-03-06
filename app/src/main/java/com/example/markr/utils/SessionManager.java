package com.example.markr.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.markr.models.User;
import com.google.gson.Gson;

public class SessionManager {
    private static final String PREF_NAME = "MarkrSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER = "user";
    private static final String KEY_TOKEN = "token";
    
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    
    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }
    
    public void createLoginSession(User user, String token) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_TOKEN, token);
        
        // Store user object as JSON
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        editor.putString(KEY_USER, userJson);
        
        editor.commit();
    }
    
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    public User getCurrentUser() {
        String userJson = pref.getString(KEY_USER, null);
        if (userJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }
    
    public String getToken() {
        return pref.getString(KEY_TOKEN, null);
    }
    
    public String getAuthHeader() {
        String token = getToken();
        return token != null ? "Bearer " + token : null;
    }
    
    public void logoutUser() {
        editor.clear();
        editor.commit();
    }
    
    public void updateUser(User user) {
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        editor.putString(KEY_USER, userJson);
        editor.commit();
    }
}
