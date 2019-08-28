package glebio;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


/**
 * @author Gleb Danichev
 */
public class AnonChatRoomBot extends TelegramLongPollingBot {

    private final static Logger logger = LogManager.getLogger();

    private final Map<Long, Long> chats = new HashMap<>();

    private final Set<Long> pending = new HashSet<>(2);

    @Override
    public void onUpdateReceived(Update update) {
        logUpdate(update);
        Long chatId = update.getMessage().getChatId();
        if (update.getMessage().getText().equals("/changemate")) {
            if (chats.containsKey(chatId)) {
                chats.remove(chats.remove(chatId));
            }
        }

        if (!chats.containsKey(chatId)) {
            pending.add(chatId);
            if (pending.size() == 2) {
                Long[] objects = pending.toArray(Long[]::new);
                chats.put(objects[0], objects[1]);
                chats.put(objects[1], objects[0]);
                pending.clear();
                execute(objects[0], "Мы нашил вам собеседника, напишите ему(ей)!");
                execute(objects[1], "Мы нашил вам собеседника, напишите ему(ей)!");
                return;
            } else {
                execute(chatId, "У вас пока еще нет собеседника, ждем...");
                return;
            }
        }

        execute(chats.get(chatId), update.getMessage().getText());
    }

    void execute(Long chatId, String text) {
        try {
            SendMessage message = new SendMessage(chatId, text);
            execute(message);
            logger.info("Sent " + message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void logUpdate(Update update) {
        logger.info(update);
        Message message = update.getMessage();
        logger.info(String.format(
            "User %s Text: %s",
            message.getFrom().getUserName(),
            message.getText()
        ));
        logger.info("chats = " + chats);
    }

    @Override
    public String getBotUsername() {
        return "AnonChatRoomBot";
    }

    @Override
    public String getBotToken() {
        return "921479453:AAHOzWRC7leY-wOBRUoCfeP-WGoaNRKfJ9c";
    }
}
