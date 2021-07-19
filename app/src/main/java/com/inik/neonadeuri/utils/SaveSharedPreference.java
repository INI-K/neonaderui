package com.inik.neonadeuri.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {

    static final String PREF_USER_NAME = "userid";
    static final String TOKEN = "token";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    // 계정 정보 저장
    public static void setUserId(Context ctx, String userName) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.commit();
    }

    // 저장된 정보 가져오기
    public static String getUserid(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }

    // 로그아웃
    public static void clearUserName(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear();
        editor.commit();
    }

    public static void setUserToken(Context ctx, String token) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(TOKEN, token);
        editor.commit();
    }

    // 저장된 정보 가져오기
    public static String getUserToken(Context ctx) {
        return getSharedPreferences(ctx).getString(TOKEN, "");
    }
}