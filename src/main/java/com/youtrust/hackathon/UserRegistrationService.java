package com.youtrust.hackathon;

import com.youtrust.hackathon.adapter.ConsoleEmailSender;
import com.youtrust.hackathon.adapter.InMemoryUserRepository;
import com.youtrust.hackathon.adapter.Sha256PasswordEncoder;
import com.youtrust.hackathon.model.RegisterInput;
import com.youtrust.hackathon.model.RegisterResult;
import com.youtrust.hackathon.model.User;
import com.youtrust.hackathon.port.EmailSender;
import com.youtrust.hackathon.port.PasswordEncoder;
import com.youtrust.hackathon.port.UserRepository;
import com.youtrust.hackathon.registrar.UserRegistrar;
import com.youtrust.hackathon.validation.UserRegistrationValidator;

/**
 * パスワード登録の入口（ファサード）。
 *
 * 担当はパスワード固有の前処理（バリデーション・ハッシュ化・User 組み立て）のみ。
 * 重複チェック・保存・確認メール・ログという後続処理は {@link UserRegistrar} に委譲し、
 * OAuth 登録と完全に共通化する。
 */
public class UserRegistrationService {

    private final UserRegistrationValidator validator;
    private final UserRegistrar userRegistrar;
    private final PasswordEncoder passwordEncoder;

    public UserRegistrationService(UserRegistrationValidator validator,
                                   UserRegistrar userRegistrar,
                                   PasswordEncoder passwordEncoder) {
        this.validator = validator;
        this.userRegistrar = userRegistrar;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * デモ・動作確認用の標準構成を組み立てる簡易合成ルート。
     */
    public static UserRegistrationService createDefault() {
        UserRepository userRepository = new InMemoryUserRepository();
        EmailSender emailSender = new ConsoleEmailSender();
        UserRegistrar registrar = new UserRegistrar(userRepository, emailSender);
        return new UserRegistrationService(
                new UserRegistrationValidator(), registrar, new Sha256PasswordEncoder());
    }

    public RegisterResult register(RegisterInput input) {
        validator.validate(input);

        User user = new User();
        user.setEmail(input.getEmail());
        user.setName(input.getName());
        user.setPassword(passwordEncoder.encode(input.getPassword()));

        return userRegistrar.register(user);
    }
}
