package com.youtrust.hackathon.registrar;

import com.youtrust.hackathon.exception.DuplicateEmailException;
import com.youtrust.hackathon.model.RegisterResult;
import com.youtrust.hackathon.model.User;
import com.youtrust.hackathon.port.EmailSender;
import com.youtrust.hackathon.port.UserRepository;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 登録の共通後続処理。パスワード登録と OAuth 登録の両入口が必ずこのコアを通ることで、
 * ウェルカムメール送信・ログ記録を一箇所に集約する。
 *
 * 順序: 重複チェック → 保存 → 確認メール送信 → ログ。
 * 保存後にメール送信が失敗しても、登録自体は成立しているため成功扱いとし、警告ログを残す
 * （確認メールは再送可能な best-effort と位置づける）。
 */
public class UserRegistrar {

    private static final Logger logger = Logger.getLogger(UserRegistrar.class.getName());

    private final UserRepository userRepository;
    private final EmailSender emailSender;

    public UserRegistrar(UserRepository userRepository, EmailSender emailSender) {
        this.userRepository = userRepository;
        this.emailSender = emailSender;
    }

    public RegisterResult register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateEmailException("このメールアドレスはすでに登録されています");
        }

        userRepository.save(user);

        try {
            emailSender.sendConfirmation(user);
        } catch (RuntimeException e) {
            logger.log(Level.WARNING,
                    "確認メール送信に失敗しました（登録は成功扱い）: " + user.getEmail(), e);
        }

        logger.info("ユーザー登録完了: " + user.getEmail());

        return new RegisterResult(true, user.getId(), "登録が完了しました");
    }
}
