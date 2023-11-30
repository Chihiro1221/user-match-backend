package com.haonan.context;

import com.haonan.model.entity.User;

public class BaseContext {

    public static ThreadLocal<User> threadLocal = new ThreadLocal<>();

    public static void setCurrentUser(User id) {
        threadLocal.set(id);
    }

    public static User getCurrentUser() {
        return threadLocal.get();
    }

    public static void removeCurrentUser() {
        threadLocal.remove();
    }

}