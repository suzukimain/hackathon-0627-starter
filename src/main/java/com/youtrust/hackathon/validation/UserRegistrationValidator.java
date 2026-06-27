package com.youtrust.hackathon.validation;

import com.youtrust.hackathon.exception.ValidationException;
import com.youtrust.hackathon.model.RegisterInput;

/**
 * パスワード登録の入力バリデーション。ルールを明示的なメソッドに分け、単体でテスト可能にする。
 *
 * 値オブジェクト（Email/Password 型）は今回採用しない。時間制約に対して得られる安全性が
 * 小さく過剰設計になりやすいため。判断は DECISIONS.md に記録し、将来の選択肢として残す。
 */
public class UserRegistrationValidator {

    private static final int MIN_PASSWORD_LENGTH = 8;

    public void validate(RegisterInput input) {
        validateEmail(input.getEmail());
        validatePassword(input.getPassword());
        validateName(input.getName());
    }

    private void validateEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new ValidationException("メールアドレスが無効です");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new ValidationException("パスワードは8文字以上必要です");
        }
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("名前は必須です");
        }
    }
}
