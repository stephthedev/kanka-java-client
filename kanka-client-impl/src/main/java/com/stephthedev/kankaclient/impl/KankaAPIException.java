package com.stephthedev.kankaclient.impl;

public class KankaAPIException extends Exception {
    int statusCode;
    public KankaAPIException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}
