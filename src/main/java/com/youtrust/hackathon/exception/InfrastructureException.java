package com.youtrust.hackathon.exception;

/**
 * 永続化・ハッシュ化・外部送信など基盤処理が失敗したときに投げる（HTTP 500 相当）。
 * 原因例外を cause として保持する。入力エラーや衝突とは区別する。
 */
public class InfrastructureException extends RegistrationException {

    public InfrastructureException(String message, Throwable cause) {
        super(message, cause);
    }
}
