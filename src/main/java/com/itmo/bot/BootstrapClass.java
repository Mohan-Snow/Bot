package com.itmo.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@SpringBootApplication
public class BootstrapClass {

    public static void main(String[] args) {

        // initializing the API
        ApiContextInitializer.init();
        ConfigurableApplicationContext context = SpringApplication.run(BootstrapClass.class, args);

        // creating the Telegram API object
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            // then register the bot
//            new TelegramBotsApi()
//                    .registerBot(SpringApplication.run(BootstrapClass.class, args)
//                            .getBean(Bot.class));

            Bot bot = context.getBean(Bot.class);
            System.out.println("HASH FROM MAIN: " + bot.hashCode());
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
