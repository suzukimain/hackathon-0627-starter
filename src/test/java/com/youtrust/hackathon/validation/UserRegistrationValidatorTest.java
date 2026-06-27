package com.youtrust.hackathon.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.youtrust.hackathon.exception.ValidationException;
import com.youtrust.hackathon.model.RegisterInput;
import org.junit.jupiter.api.Test;

class UserRegistrationValidatorTest {

    private final UserRegistrationValidator validator = new UserRegistrationValidator();

    private RegisterInput input(String email, String password, String name) {
        RegisterInput in = new RegisterInput();
        in.setEmail(email);
        in.setPassword(password);
        in.setName(name);
        return in;
    }

    @Test
    void validInput_passes() {
        assertDoesNotThrow(() -> validator.validate(input("a@example.com", "password1", "Alice")));
    }

    @Test
    void password7Chars_isRejected() {
        assertThrows(ValidationException.class,
                () -> validator.validate(input("a@example.com", "pass123", "Alice")));
    }

    @Test
    void password8Chars_isAccepted() {
        assertDoesNotThrow(() -> validator.validate(input("a@example.com", "pass1234", "Alice")));
    }

    @Test
    void emailWithoutAtMark_isRejected() {
        assertThrows(ValidationException.class,
                () -> validator.validate(input("no-at-mark", "password1", "Alice")));
    }

    @Test
    void blankName_isRejected() {
        assertThrows(ValidationException.class,
                () -> validator.validate(input("a@example.com", "password1", "   ")));
    }
}
