package glebio;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * @author Gleb Danichev
 */
public class AnonChatRoomApp {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new AnonChatRoomBot());
            System.out.println("Started " + AnonChatRoomBot.class.getSimpleName());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
