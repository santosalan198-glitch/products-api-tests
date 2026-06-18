package com.alan.qa.utils;

public class TokenManager {

    // Así cada prueba tiene SU PROPIO token aunque corran en paralelo
    private static final ThreadLocal<String> token = new ThreadLocal<>();

    // Guarda el token de forma segura
    public static void setToken(String authToken) {
        token.set(authToken);
    }

    public static String getToken() {

        if (token.get() == null) {
            throw new IllegalStateException(
                    "No hay token disponible. " +
                            "¿Olvidaste hacer login antes de la prueba?"
            );
        }
        return token.get();
    }

    public static void clearToken() {
        token.remove();
    }
}