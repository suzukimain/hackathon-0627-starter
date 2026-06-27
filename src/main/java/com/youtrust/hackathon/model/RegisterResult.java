package com.youtrust.hackathon.model;

public class RegisterResult {

    private final boolean success;
    private final String userId;
    private final String message;

    public RegisterResult(boolean success, String userId, String message) {
        this.success = success;
        this.userId = userId;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }
}
