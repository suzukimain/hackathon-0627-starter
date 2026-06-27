package com.youtrust.hackathon.adapter;

import com.youtrust.hackathon.model.User;
import com.youtrust.hackathon.port.EmailSender;

public class ConsoleEmailSender implements EmailSender {

    @Override
    public void sendConfirmation(User user) {
        String subject = "【ハッカソン】登録完了のお知らせ";
        String body = user.getName() + " 様\n\nご登録ありがとうございます。";
        System.out.println("Email sent to: " + user.getEmail()
                + " | subject: " + subject + " | body: " + body);
    }
}
