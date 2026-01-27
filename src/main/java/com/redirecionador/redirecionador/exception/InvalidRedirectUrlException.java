package com.redirecionador.redirecionador.exception;

public class InvalidRedirectUrlException extends RuntimeException {
    private final String url;

    public InvalidRedirectUrlException(String url) {
        super("Invalid redirect URL: " + url);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
