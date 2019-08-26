package glebio;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


/**
 * @author Gleb Danichev
 */
public class AnonChatRoomBot extends TelegramLongPollingBot {

    private final Map<Long, Long> chats = new HashMap<>();

    private final Set<Long> pending = new HashSet<>(2);

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(Thread.currentThread().getName());
        logUpdate(update);
        Long chatId = update.getMessage().getChatId();
        if (update.getMessage().getText().equals("/changemate")) {
            if (chats.containsKey(chatId)) {
                chats.remove(chats.remove(chatId));
            }
            return;
        }

        if (!chats.containsKey(chatId)) {
            pending.add(chatId);
            if (pending.size() == 2) {
                Long[] objects = pending.toArray(Long[]::new);
                chats.put(objects[0], objects[1]);
                chats.put(objects[1], objects[0]);
                pending.clear();
            } else {
                execute(chatId, "У вас пока еще нет собеседника, ждем...");
                return;
            }
        }

        execute(chats.get(chatId), update.getMessage().getText());
    }

    private void execute(Long chatId, String text) {
        try {
            SendMessage message = new SendMessage(chatId, text);
            execute(message);
            System.out.println("Sent " + message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void logUpdate(Update update) {
        System.out.println(update);
        Message message = update.getMessage();
        System.out.println(String.format(
            "%s: %s",
            message.getFrom().getUserName(),
            message.getText()
        ));
        System.out.println("chats = " + chats);
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
