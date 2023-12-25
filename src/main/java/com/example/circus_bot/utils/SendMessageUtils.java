package com.example.circus_bot.utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

/**
 * Build message with or without keyboards
 */
public class SendMessageUtils {

    /**
     * Forms the most common message with only plain text
     *
     * @param chatId of user who writes to the bot
     * @param messageText that should be delivered to a user
     * @return message that could be sent a user
     */
    public static SendMessage createMessage(long chatId, String messageText) {
        return buildMessage(chatId, messageText);
    }

    /**
     * Produces a message with additional keyboards configs
     *
     * @param chatId of user who writes to the bot
     * @param messageText that should be delivered to a user
     * @param keyboardMarkup type of keyboard will be displayed on a user device
     * @return message with certain reply markups
     */
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
