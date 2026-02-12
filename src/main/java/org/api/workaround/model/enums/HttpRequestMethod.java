package org.api.workaround.model.enums;

public enum HttpRequestMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE");

    private final String value;

    HttpRequestMethod(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
