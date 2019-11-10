import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
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

    public static void main(String[] args) {

        // initializing the API
        ApiContextInitializer.init();
        // creating the Telegram API object
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        // then register the bot
        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void onUpdateReceived(Update update) {
        WeatherModel model = new WeatherModel();
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
                        sendMsg(message, Weather.getWeather(message.getText(), model));
                    } catch (IOException e) {
                        sendMsg(message, "This city wasn't found");
                    }
            }
        }
    }

    // Возвращает токен бота
    public String getBotToken() {
        return "1026962314:AAFsHLZBFi6SULRPVjsJv_vucMFQNJ9MqRY";
    }

    // Метод для того, чтобы вернуть имя бота указанного при регистрации
    public String getBotUsername() {
        return "WeatherBot";
    }

    public void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();

        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);

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
//        firstRowOfButtons.add(new KeyboardButton("/help"));
//        firstRowOfButtons.add(new KeyboardButton("/settings"));
        firstRowOfButtons.add(new KeyboardButton("saint petersburg"));
        firstRowOfButtons.add(new KeyboardButton("moscow"));
        firstRowOfButtons.add(new KeyboardButton("london"));

        // adding the first row to the keyboard rows list
        listOfButtonRows.add(firstRowOfButtons);

        // attach created keyboard to the markup
        replyKeyboardMarkup.setKeyboard(listOfButtonRows);
    }
}
