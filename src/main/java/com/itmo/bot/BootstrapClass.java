package com.itmo.bot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class BootstrapClass {

    public static void main(String[] args) {

        // initializing the API
        ApiContextInitializer.init();
        // creating the Telegram API object
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            // then register the bot
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
