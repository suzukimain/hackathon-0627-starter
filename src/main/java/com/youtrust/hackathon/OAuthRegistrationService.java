package com.youtrust.hackathon;

import com.youtrust.hackathon.adapter.ConsoleEmailSender;
import com.youtrust.hackathon.adapter.GitHubOAuthProvider;
import com.youtrust.hackathon.adapter.InMemoryUserRepository;
import com.youtrust.hackathon.model.OAuthProfile;
import com.youtrust.hackathon.model.RegisterResult;
import com.youtrust.hackathon.model.User;
import com.youtrust.hackathon.oauth.OAuthProvider;
import com.youtrust.hackathon.oauth.OAuthProviderRegistry;
import com.youtrust.hackathon.port.EmailSender;
import com.youtrust.hackathon.port.UserRepository;
import com.youtrust.hackathon.registrar.UserRegistrar;
import java.util.List;

/**
 * OAuth 登録の入口。プロバイダ固有の前処理（認可コード→正規化プロフィール→User 組み立て）を担い、
 * 重複チェック・保存・確認メール・ログはパスワード登録と同じ {@link UserRegistrar} に委譲する。
 */
public class OAuthRegistrationService {

    private final OAuthProviderRegistry providerRegistry;
    private final UserRegistrar userRegistrar;

    public OAuthRegistrationService(OAuthProviderRegistry providerRegistry,
                                    UserRegistrar userRegistrar) {
        this.providerRegistry = providerRegistry;
        this.userRegistrar = userRegistrar;
    }

    /**
     * デモ・動作確認用の標準構成。現状は GitHub のみ登録（Google/LINE はアダプタ追加で有効化）。
     */
    public static OAuthRegistrationService createDefault() {
        UserRepository userRepository = new InMemoryUserRepository();
        EmailSender emailSender = new ConsoleEmailSender();
        UserRegistrar registrar = new UserRegistrar(userRepository, emailSender);
        OAuthProviderRegistry registry =
                new OAuthProviderRegistry(List.of(new GitHubOAuthProvider()));
        return new OAuthRegistrationService(registry, registrar);
    }

    public RegisterResult register(OAuthProvider provider, String authorizationCode) {
        OAuthProfile profile = providerRegistry.resolve(provider).fetchProfile(authorizationCode);

        User user = new User();
        user.setEmail(profile.getEmail());
        user.setName(profile.getName());
        user.setProvider(profile.getProvider().name());
        user.setProviderUserId(profile.getProviderUserId());

        return userRegistrar.register(user);
    }
}
