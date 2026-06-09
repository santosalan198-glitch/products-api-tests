package com.alan.qa.utils;

public class TokenManager {
    private static String token;

    public static void setToken(String authToken) {
        token = authToken;
    }

    public static String getToken() {
        return token;
    }

    public static void clearToken() {
        token = null;
    }
}
