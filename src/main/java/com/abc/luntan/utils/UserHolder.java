package com.abc.luntan.utils;

import com.abc.luntan.dto.UserDTO;

public class UserHolder {

    private static final ThreadLocal<UserDTO> tl = new ThreadLocal<>();

    public static UserDTO getUser() {
        return tl.get();
    }
    public static void setUser(UserDTO user) {
        tl.set(user);
    }

    public static void clear() {
        tl.remove();
    }
}
