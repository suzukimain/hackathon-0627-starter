package com.youtrust.hackathon.exception;

/**
 * 入力が不正なときに投げる（HTTP 400 相当）。クライアント側で修正可能なエラー。
 */
public class ValidationException extends RegistrationException {

    public ValidationException(String message) {
        super(message);
    }
}
