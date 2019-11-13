package com.itmo.bot;

import com.itmo.bot.services.WeatherAccessService;
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

    private String token;
    private String name;
    private String appid;

    public void setToken(String token) {
        this.token = token;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // persist user response
        Message message = update.getMessage();


        if (message != null && message.hasText()) {
            switch (message.getText()) {
                case "/help":
                    sendMsg(message, "Ready to help");
                    break;
                case "/settings":
                    sendMsg(message, "Let's tweak something");
                    break;
                default:
                    try {
                        sendMsg(message, new WeatherAccessService(message, appid).getWeather());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        } else {
            try {
//                sendMsg(message, Weather.getWeather(message, appid));
                sendMsg(message, new WeatherAccessService(message, appid).getWeather());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotToken() {
        return this.token;
    }

    @Override
    public String getBotUsername() {
        return this.name;
    }

    // send response back to user
    public void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();

        sendMessage.enableMarkdown(true)
                .setChatId(message.getChatId().toString())
                .setReplyToMessageId(message.getMessageId())
                .setText(text);

        try {
            // invoking the setButtons method to attach the keyboard
//            setButtons(sendMessage);

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
