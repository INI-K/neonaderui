package com.inik.neonadeuri.utils;

import com.inik.neonadeuri.models.User;

public class CurrentUserManager {

    private CurrentUserManager() {}

    private static User user;

    private static class CurrentUserManagerHolder {
        private static final CurrentUserManager uniqueInstance = new CurrentUserManager();
    }

    public static User getCurrentUser() {
        CurrentUserManager currentUserManager = CurrentUserManagerHolder.uniqueInstance;
        user = user == null ? new User() : user;

        return currentUserManager.getUser();
    }

    public static void setCurrentUser(User setUser) {
        CurrentUserManager currentUserManager = CurrentUserManagerHolder.uniqueInstance;
        user = user == null ? new User() : user;
        user = setUser;
    }

    public User getUser() {
        return user;
    }
}
