package com.inik.neonadeuri.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.inik.neonadeuri.models.Feed;
import com.inik.neonadeuri.models.User;

/*
 * 꼭 싱글톤 패턴으로 수정해볼것
 */
public class SharedPreferencesManager {

    public static final String SHARED_PREFERENCES_NAME = "neonadeuri_shared_preferences";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    // 사용자 정보 저장
    public static void saveUser(Context context, User user) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("idx", user.getIdx());
        editor.putString("email", user.getEmail());
        editor.putString("password", user.getPassword());
        editor.putString("name", user.getName());
        editor.putString("nickname", user.getNickname());
        editor.putString("phone", user.getPhone());
        editor.putString("webSite", user.getWebSite());
        editor.putString("introduction", user.getIntroduction());
    }

    // 사용자 정보 로드
    public static User loadUser(Context context, String idx) {
        User user = new User();

        return user;
    }

    // 피드 정보 저장
    public static void saveFeed(Context context, Feed feed) {

    }

    // 피드 정보 로드
    public static Feed loadFeed(Context context, String idx) {
        Feed feed = new Feed();

        return feed;
    }
}

