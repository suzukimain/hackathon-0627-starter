package com.youtrust.hackathon.exception;

/**
 * メールアドレスが既に登録済みのときに投げる（HTTP 409 相当）。
 * 入力エラー（ValidationException）とは区別し、呼び出し側が別々に対応できるようにする。
 */
public class DuplicateEmailException extends RegistrationException {

    public DuplicateEmailException(String message) {
        super(message);
    }
}
