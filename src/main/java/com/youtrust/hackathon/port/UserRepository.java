package com.youtrust.hackathon.port;

import com.youtrust.hackathon.model.User;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findByEmail(String email);

    User save(User user);
}
