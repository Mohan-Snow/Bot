package com.itmo.bot.prop;

import com.itmo.bot.BootstrapClass;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

//TODO modify
public class BotProfile {

    private String botUsername;
    private String botToken;
    private static volatile BotProfile botInstance;

    private BotProfile() {
        try (InputStream input = BootstrapClass.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties properties = new Properties();
            properties.load(input);

            this.botUsername = (properties.getProperty("bot.username"));
            this.botToken = (properties.getProperty("bot.token"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BotProfile getInstance() {
        BotProfile localBotInstance = botInstance;

        if (localBotInstance == null) {
            synchronized (BotProfile.class) {
                localBotInstance = botInstance;

                if (localBotInstance == null) {
                    botInstance = localBotInstance = new BotProfile();
                }
            }
        }
        return localBotInstance;
    }

    public String getBotUsername() {
        return botUsername;
    }

    public String getBotToken() {
        return botToken;
    }
}
