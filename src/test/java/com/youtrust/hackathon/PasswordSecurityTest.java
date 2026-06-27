package com.youtrust.hackathon;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.youtrust.hackathon.adapter.ConsoleEmailSender;
import com.youtrust.hackathon.adapter.Sha256PasswordEncoder;
import com.youtrust.hackathon.model.RegisterInput;
import com.youtrust.hackathon.model.User;
import com.youtrust.hackathon.port.UserRepository;
import com.youtrust.hackathon.registrar.UserRegistrar;
import com.youtrust.hackathon.validation.UserRegistrationValidator;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * 生パスワードが永続化されないことを保証する。保存された User を捕捉する fake を注入して検証。
 */
class PasswordSecurityTest {

    /** save された User を捕捉するだけの fake。 */
    private static final class CapturingUserRepository implements UserRepository {
        private User saved;

        @Override
        public Optional<User> findByEmail(String email) {
            return Optional.empty();
        }

        @Override
        public User save(User user) {
            if (user.getId() == null) {
                user.setId("user_test");
            }
            this.saved = user;
            return user;
        }
    }

    @Test
    void register_neverPersistsRawPassword() {
        CapturingUserRepository repo = new CapturingUserRepository();
        UserRegistrar registrar = new UserRegistrar(repo, new ConsoleEmailSender());
        UserRegistrationService service = new UserRegistrationService(
                new UserRegistrationValidator(), registrar, new Sha256PasswordEncoder());

        RegisterInput input = new RegisterInput();
        input.setEmail("alice@example.com");
        input.setPassword("password123");
        input.setName("Alice");

        service.register(input);

        assertNotNull(repo.saved);
        assertNotEquals("password123", repo.saved.getPassword());
    }
}
