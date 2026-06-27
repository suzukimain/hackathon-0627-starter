package com.youtrust.hackathon.port;

import com.youtrust.hackathon.model.User;

public interface EmailSender {

    void sendConfirmation(User user);
}
