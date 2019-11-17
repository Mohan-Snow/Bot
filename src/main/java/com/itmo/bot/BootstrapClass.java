package com.itmo.bot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BootstrapClass {

    public static void main(String[] args) {

        // initializing the API
        ApiContextInitializer.init();
        // creating the Telegram API object
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            Bot bot = new Bot();
            try (InputStream input = BootstrapClass.class.getClassLoader().getResourceAsStream("config.properties")) {

                Properties properties = new Properties();
                properties.load(input);

                bot.setName(properties.getProperty("bot.username"));
                bot.setToken(properties.getProperty("bot.token"));
                bot.setAppid(properties.getProperty("weather.appid"));
            } catch (IOException e) {
                e.printStackTrace();
            }

//            System.out.println(bot.getBotUsername());
//            System.out.println(bot.getBotToken());

            // then register the bot
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage().toUpperCase());
            e.printStackTrace();
        }
    }
}
