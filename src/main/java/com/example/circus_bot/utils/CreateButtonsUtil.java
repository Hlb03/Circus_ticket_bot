package com.example.circus_bot.utils;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public class CreateButtonsUtil {

    /**
     * Creates button(s) that would be displayed on a user devise
     *
     * @param buttonTexts that should be displayed on a user device
     * @return keyboard with multiple buttons that were received as an input param
     */
    public static ReplyKeyboard createButtons(List<String> buttonTexts) {
        KeyboardRow row = new KeyboardRow();
        buttonTexts.forEach(row::add);
        return new ReplyKeyboardMarkup(List.of(row));
    }
}
