package com.youtrust.hackathon.registrar;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.youtrust.hackathon.adapter.InMemoryUserRepository;
import com.youtrust.hackathon.exception.DuplicateEmailException;
import com.youtrust.hackathon.model.RegisterResult;
import com.youtrust.hackathon.model.User;
import com.youtrust.hackathon.port.EmailSender;
import com.youtrust.hackathon.port.UserRepository;
import org.junit.jupiter.api.Test;

class UserRegistrarTest {

    private User user(String email, String name) {
        User u = new User();
        u.setEmail(email);
        u.setName(name);
        u.setPassword("hashed");
        return u;
    }

    @Test
    void register_whenEmailSendFails_stillSucceeds() {
        UserRepository repo = new InMemoryUserRepository();
        EmailSender throwingSender = u -> {
            throw new RuntimeException("SMTP down");
        };
        UserRegistrar registrar = new UserRegistrar(repo, throwingSender);

        RegisterResult result = registrar.register(user("alice@example.com", "Alice"));

        assertTrue(result.isSuccess());
        assertTrue(repo.findByEmail("alice@example.com").isPresent());
    }

    @Test
    void register_duplicateEmail_throws() {
        UserRepository repo = new InMemoryUserRepository();
        UserRegistrar registrar = new UserRegistrar(repo, u -> { });
        registrar.register(user("dup@example.com", "Alice"));

        assertThrows(DuplicateEmailException.class,
                () -> registrar.register(user("dup@example.com", "Bob")));
    }
}
