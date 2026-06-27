package com.youtrust.hackathon.adapter;

import com.youtrust.hackathon.model.User;
import com.youtrust.hackathon.port.UserRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> usersByEmail = new HashMap<>();

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(usersByEmail.get(email));
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId("user_" + System.currentTimeMillis());
        }
        usersByEmail.put(user.getEmail(), user);
        return user;
    }
}
