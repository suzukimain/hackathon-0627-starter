package com.youtrust.hackathon.exception;

/**
 * ユーザー登録ユースケースの例外の基底。呼び出し側はこれを catch-all として扱える。
 * unchecked（RuntimeException）にして全シグネチャから throws Exception を排除する。
 */
public class RegistrationException extends RuntimeException {

    public RegistrationException(String message) {
        super(message);
    }

    public RegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
