import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {
    public static void main(String[] args) {

//        System.getProperties().put("proxySet", "true");
//        System.getProperties().put("socksProxyHost", "127.0.0.1");
//        System.getProperties().put("socksProxyPort", "9150");

        // инициализируем API
        ApiContextInitializer.init();
        // создаем объект Telegram API
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        // далее нужно зарегистрировать бота
        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    // Метод для приема сообщений. Он используется для приема обновлений через long pool
    // (существует несколько вариантов обновлений - web hooks или long pool)
    public void onUpdateReceived(Update update) {
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
            sendApiMethod(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
