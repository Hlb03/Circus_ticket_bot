package com.example.circus_bot.bot;

import com.example.circus_bot.response_handler.ResponseHandler;
import com.example.circus_bot.services.CheckRequiredDataService;
import com.example.circus_bot.services.DataInsertionService;
import com.example.circus_bot.services.ResponseUserMessageService;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

/**
 * This class represents the bot itself,
 * which has some basic behaviour patterns
 * gained from extended class - AbilityBot
 **/
@Component
public class CircusTickerBot extends AbilityBot {

    private final ResponseHandler handler;

    public CircusTickerBot(Environment env, DataInsertionService insertionService,
                           CheckRequiredDataService requiredDataService, ResponseUserMessageService responseUserMessageService) {
        super(env.getProperty("telegram.bot.token"), "CircusTicketBot");
        this.handler = new ResponseHandler(silent, db(), insertionService, requiredDataService, responseUserMessageService);
    }

    @Override
    public long creatorId() {
        return 1L;
    }

    /**
     * Handles all responses of bot users.
     * Calls ResponseHandler method to provide certain reactor to user messages.
     *
     * @param update the update received by Telegram's API
     */
    @Override
    public void onUpdateReceived(Update update) {
        handler.replyToUserRequest(getChatId(update), update.getMessage());
    }
}
