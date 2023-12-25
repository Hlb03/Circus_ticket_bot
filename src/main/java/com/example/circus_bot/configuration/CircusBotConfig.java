package com.example.circus_bot.configuration;

import com.example.circus_bot.bot.CircusTickerBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


@Configuration
public class CircusBotConfig {

    /**
     * As long as Spring Boot 3 doesn't automatically configure a bot declared with @Component
     * it should be done manually.
     *
     * @param bot that should be registered
     * @return TelegramBotsApi bots manager
     * @throws TelegramApiException in case bot is null or bot token/username are invalid
     */
    @Bean
    protected TelegramBotsApi registerBot(CircusTickerBot bot) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(bot);
        return botsApi;
    }
}
