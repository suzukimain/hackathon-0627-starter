package com.youtrust.hackathon.adapter;

import com.youtrust.hackathon.exception.InfrastructureException;
import com.youtrust.hackathon.port.PasswordEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SHA-256 によるハッシュ化。元の偽ハッシュ（raw + "_hashed"）を置き換える。
 *
 * 注: 本番ではソルト付きの bcrypt / argon2 を使うべき。ここでは外部依存を増やさず
 * JDK 標準の MessageDigest に留め、その判断は DECISIONS.md に「次の一手」として記録する。
 */
public class Sha256PasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                hex.append(Character.forDigit((b >> 4) & 0xF, 16));
                hex.append(Character.forDigit(b & 0xF, 16));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new InfrastructureException("SHA-256 が利用できません", e);
        }
    }
}
