package com.example.circus_bot.utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

public class SendMessageUtils {

    public static SendMessage createMessage(long chatId, String messageText) {
        return buildMessage(chatId, messageText);
    }

    public static SendMessage createMessage(long chatId, String messageText, ReplyKeyboard keyboardMarkup) {
        SendMessage message = buildMessage(chatId, messageText);
        message.setReplyMarkup(keyboardMarkup);
        return message;
    }

    private static SendMessage buildMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.enableMarkdown(true);
        return message;
    }
}
