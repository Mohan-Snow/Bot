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
    }
}
