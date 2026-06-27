package com.youtrust.hackathon;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.youtrust.hackathon.exception.DuplicateEmailException;
import com.youtrust.hackathon.exception.ValidationException;
import com.youtrust.hackathon.model.RegisterInput;
import com.youtrust.hackathon.model.RegisterResult;
import org.junit.jupiter.api.Test;

/**
 * 挙動保存（characterization）テスト。S1 のリファクタリングで既存の振る舞いが
 * 変わっていないことを示す。注: JDK/Maven 未導入のためローカル実行はしない。
 */
class UserRegistrationServiceTest {

    private RegisterInput input(String email, String password, String name) {
        RegisterInput in = new RegisterInput();
        in.setEmail(email);
        in.setPassword(password);
        in.setName(name);
        return in;
    }

    @Test
    void register_happyPath_returnsSuccessWithUserId() {
        UserRegistrationService service = UserRegistrationService.createDefault();

        RegisterResult result = service.register(input("alice@example.com", "password123", "Alice"));

        assertTrue(result.isSuccess());
        assertNotNull(result.getUserId());
    }

    @Test
    void register_invalidEmail_throwsValidationException() {
        UserRegistrationService service = UserRegistrationService.createDefault();

        assertThrows(ValidationException.class,
                () -> service.register(input("no-at-mark", "password123", "Alice")));
    }

    @Test
    void register_shortPassword_throwsValidationException() {
        UserRegistrationService service = UserRegistrationService.createDefault();

        assertThrows(ValidationException.class,
                () -> service.register(input("bob@example.com", "short", "Bob")));
    }

    @Test
    void register_duplicateEmail_throwsDuplicateEmailException() {
        UserRegistrationService service = UserRegistrationService.createDefault();
        service.register(input("dup@example.com", "password123", "Alice"));

        assertThrows(DuplicateEmailException.class,
                () -> service.register(input("dup@example.com", "password123", "Bob")));
    }
}
