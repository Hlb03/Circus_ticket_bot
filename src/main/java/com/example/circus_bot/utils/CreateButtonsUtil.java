package com.example.circus_bot.utils;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Arrays;
import java.util.List;

public class CreateButtonsUtil {

    public static ReplyKeyboard createButtons(String... buttonTexts) {
        KeyboardRow row = new KeyboardRow();
        Arrays.stream(buttonTexts).forEach(row::add);
        return new ReplyKeyboardMarkup(List.of(row));
    }
}
