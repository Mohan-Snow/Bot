package com.itmo.bot;

import com.itmo.bot.entities.Location;
import com.itmo.bot.entities.User;
import com.itmo.bot.services.WeatherAccess;
import com.itmo.bot.services.database.MappingUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {

    @Autowired
    private MappingUserService service;

    @Override
    public void onUpdateReceived(Update update) {

        // retrieve user request
        if (update.hasMessage()) {
            new Thread(() -> {
                Message message = update.getMessage();

                if (message.hasLocation()) {

                    User user = new User("Test Name#1",
                            new Location(message.getLocation().getLatitude(), message.getLocation().getLatitude()),
                            message.getChatId());


                    try {
                        service.save(user);
                    } catch (NullPointerException e) {
                        System.out.println("Service null...again T_T " + e.getMessage());
                    }

                    try {
                        sendMsg(message, WeatherAccess.getInstance().getWeather(message));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else if (message.hasText()) {

                    switch (message.getText()) {
                        case "/help":
                            sendMsg(message, "Ready to help");
                            break;
                        case "/settings":
                            sendMsg(message, "Let's tweak something");
                            break;
                        default:
                            try {
                                sendMsg(message, WeatherAccess.getInstance().getWeather(message));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    }
                } else {
                    sendMsg(message, "Something went wrong");
                }
            }).start();
        }
    }

    @Override
    public String getBotToken() {
        return BotConfig.WEATHER_TOKEN;
    }

    @Override
    public String getBotUsername() {
        return BotConfig.WEATHER_NAME;
    }

    // send response back to user
    public void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();

        sendMessage.enableMarkdown(true)
                .setChatId("")
                .setChatId(message.getChatId().toString())
                .setReplyToMessageId(message.getMessageId())
                .setText(text);

        try {
            // invoking the setButtons method to attach the keyboard
            setButtons(sendMessage);

            // sends the pre-defined text to the user
            sendApiMethod(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // create keyboard schema for the user
    public void setButtons(SendMessage sMessage) {
        // initializing the keyboard
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        // setting the markup for the keyboard with the messages
        sMessage.setReplyMarkup(replyKeyboardMarkup);
        // make the bot reveal the keyboard to the specified users
        replyKeyboardMarkup.setSelective(true);
        // the markup of the keyboard relates to the quantity of function buttons
        replyKeyboardMarkup.setResizeKeyboard(true);
        // provides the ability to hide the keyboard
        replyKeyboardMarkup.setOneTimeKeyboard(false);


        // creating the rows of buttons
        List<KeyboardRow> listOfButtonRows = new ArrayList<>();
        // create the first row of buttons
        KeyboardRow firstRowOfButtons = new KeyboardRow();
        // adding a button to the first row
        firstRowOfButtons.add(new KeyboardButton("/help"));
        firstRowOfButtons.add(new KeyboardButton("/settings"));

        KeyboardRow secondRowOfButtons = new KeyboardRow();
        secondRowOfButtons.add(new KeyboardButton("Saint Petersburg"));
        secondRowOfButtons.add(new KeyboardButton("Moscow"));
        secondRowOfButtons.add(new KeyboardButton("London"));

        // adding the first row to the keyboard rows list
        listOfButtonRows.add(firstRowOfButtons);
        listOfButtonRows.add(secondRowOfButtons);

        // attach created keyboard to the markup
        replyKeyboardMarkup.setKeyboard(listOfButtonRows);
    }
}
