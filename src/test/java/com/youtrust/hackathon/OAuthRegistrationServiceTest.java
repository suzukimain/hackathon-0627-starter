package com.youtrust.hackathon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.youtrust.hackathon.adapter.GitHubOAuthProvider;
import com.youtrust.hackathon.adapter.InMemoryUserRepository;
import com.youtrust.hackathon.exception.DuplicateEmailException;
import com.youtrust.hackathon.model.RegisterResult;
import com.youtrust.hackathon.model.User;
import com.youtrust.hackathon.oauth.OAuthProvider;
import com.youtrust.hackathon.oauth.OAuthProviderRegistry;
import com.youtrust.hackathon.port.EmailSender;
import com.youtrust.hackathon.port.UserRepository;
import com.youtrust.hackathon.registrar.UserRegistrar;
import java.util.List;
import org.junit.jupiter.api.Test;

class OAuthRegistrationServiceTest {

    /** 確認メールが送られたかを記録する spy。 */
    private static final class SpyEmailSender implements EmailSender {
        private User confirmed;

        @Override
        public void sendConfirmation(User user) {
            this.confirmed = user;
        }
    }

    private OAuthRegistrationService service(UserRepository repo, EmailSender email) {
        UserRegistrar registrar = new UserRegistrar(repo, email);
        OAuthProviderRegistry registry =
                new OAuthProviderRegistry(List.of(new GitHubOAuthProvider()));
        return new OAuthRegistrationService(registry, registrar);
    }

    @Test
    void githubSignup_runsSharedDownstream_sendsWelcomeEmailAndPersists() {
        UserRepository repo = new InMemoryUserRepository();
        SpyEmailSender email = new SpyEmailSender();
        OAuthRegistrationService service = service(repo, email);

        RegisterResult result = service.register(OAuthProvider.GITHUB, "code-123");

        assertTrue(result.isSuccess());
        // パスワード登録と同じ後続処理（ウェルカムメール）を通っている
        assertEquals("gh_code-123@users.noreply.github.com", email.confirmed.getEmail());
        assertEquals("GITHUB", email.confirmed.getProvider());
        // OAuth ユーザーはパスワードを持たない
        assertNull(email.confirmed.getPassword());
        assertTrue(repo.findByEmail("gh_code-123@users.noreply.github.com").isPresent());
    }

    @Test
    void githubSignup_duplicate_throws() {
        UserRepository repo = new InMemoryUserRepository();
        OAuthRegistrationService service = service(repo, new SpyEmailSender());
        service.register(OAuthProvider.GITHUB, "same-code");

        assertThrows(DuplicateEmailException.class,
                () -> service.register(OAuthProvider.GITHUB, "same-code"));
    }

    @Test
    void unsupportedProvider_throws() {
        OAuthRegistrationService service =
                service(new InMemoryUserRepository(), new SpyEmailSender());

        assertThrows(RuntimeException.class,
                () -> service.register(OAuthProvider.GOOGLE, "code"));
    }
}
