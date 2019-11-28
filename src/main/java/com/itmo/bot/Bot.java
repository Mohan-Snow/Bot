package com.itmo.bot;

import com.itmo.bot.entities.Location;
import com.itmo.bot.entities.User;
import com.itmo.bot.services.NotificationService;
import com.itmo.bot.services.UserService;
import com.itmo.bot.services.WeatherAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
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

@Component
@PropertySource("classpath:telegram.properties")
public class Bot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String botToken;

    private User user;
    private Location location;

    private UserService service;
    private WeatherAccess<Object> weatherAccess;

    public Bot() {
    }

    @Autowired
    public Bot(UserService userService, WeatherAccess<Object> weatherAccess) {
        this.service = userService;
        this.weatherAccess = weatherAccess;
    }

    @Override
    public void onUpdateReceived(Update update) {

        // retrieve user request
        if (update.hasMessage()) {
            new Thread(() -> {
                Message message = update.getMessage();

                if (message.hasLocation()) {

                    location = new Location(message.getLocation().getLatitude(),
                            message.getLocation().getLatitude());

                    // check user in database >>
                    registerUser(message);

                    // processing user request >>
                    try {
                        sendMsg(message, weatherAccess.getCurrentForecast(location));
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
                        case "subscribe": //TODO: fix saving user boolean type
                            user = getUserByChatId(message.getChatId());

                            if (user != null) {
                                System.out.println(user);
                                if (user.isSubscriber()) {
                                    sendMsg(message, "You're already a subscriber");
                                    break;
                                }
                                user.setSubscriber(true);
                                System.out.println("AFTER SET TRUE: " + user);
                                sendMsg(message, "User Subscribed");
                            } else {
                                sendMsg(message, "You need to send your current location first");
                            }
                            break;
                        default:
                            try {
                                sendMsg(message, weatherAccess.getCurrentForecast(message));
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

    private void registerUser(Message message) {
        if (getUserByChatId(message.getChatId()) == null) {

            user = new User(message.getChat().getUserName(),
                    location, message.getChatId());
            try {
                service.save(user);
            } catch (NullPointerException e) {
                System.out.println("\nService null...again T_T\n" + e.getMessage());
            }
        }
    }

    private User getUserByChatId(Long chatId) {
        for (User u : service.getAll()) {
            if (u.getChatId() == chatId) {
                System.out.println("\ngetUserByChatId >> " + u);
                System.out.println("U EXIST");
                return u;
            }
        }
        return null;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botName;
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
        firstRowOfButtons.add(new KeyboardButton("subscribe"));

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

//    @Scheduled(fixedRate = 50000)
    @Scheduled(cron = "0 0 9 * * *")
    public void sendForecast() throws IOException {
        List<User> users = service.getAll();

        if (users.isEmpty()) return;

        for (User u : users) {
            if (!u.isSubscriber()) continue;

            String forecast = weatherAccess.getForecastForThisDay(u.getLocation());
            SendMessage sendMessage = new SendMessage();

            sendMessage.enableMarkdown(true)
                    .setChatId("")
                    .setChatId(u.getChatId())
                    .setText(forecast);

            try {
                sendApiMethod(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
