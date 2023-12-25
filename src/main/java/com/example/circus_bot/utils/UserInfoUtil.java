package com.example.circus_bot.utils;

import org.telegram.telegrambots.meta.api.objects.Message;

public class UserInfoUtil {

    public static String getUsernameOrFullName(Message message) {
        if (message.getFrom().getUserName() == null)
            return String.format("%s %s", message.getFrom().getFirstName(), message.getFrom().getLastName());
        return String.format("@%s", message.getFrom().getUserName());
    }
}
