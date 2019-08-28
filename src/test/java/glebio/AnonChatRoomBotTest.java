package glebio;

import java.util.HashMap;
import java.util.HashSet;

import org.junit.Test;
import org.mockito.internal.util.reflection.FieldSetter;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Gleb Danichev
 */
public class AnonChatRoomBotTest {

    @Test
    public void regularScenarioWith2UsersTest() {
        AnonChatRoomBot bot = buildAnonChatRoomBot();
        //Миролюб connects to bot
        bot.onUpdateReceived(buildUpdate("/start", 1));
        //he has to wait
        verify(bot).execute(1L, "У вас пока еще нет собеседника, ждем...");
        clearInvocations(bot);
        //Мирослава connects to bot
        bot.onUpdateReceived(buildUpdate("/start", 2));
        //Миролюб and Мирослава notified about each other
        verify(bot).execute(1L, "Мы нашил вам собеседника, напишите ему(ей)!");
        verify(bot).execute(2L, "Мы нашил вам собеседника, напишите ему(ей)!");
        clearInvocations(bot);
        //then Мирослава and Миролюб start to talk
        bot.onUpdateReceived(buildUpdate("Привет, Мирослава", 1));
        verify(bot).execute(2L, "Привет, Мирослава");
    }

    @Test
    public void regularScenarioWith3UsersTest() {
        AnonChatRoomBot bot = buildAnonChatRoomBot();
        //Миролюб connects to bot
        bot.onUpdateReceived(buildUpdate("/start", 1));
        //he has to wait
        verify(bot).execute(1L, "У вас пока еще нет собеседника, ждем...");
        clearInvocations(bot);
        //Мирослава connects to bot
        bot.onUpdateReceived(buildUpdate("/start", 2));
        //then Мирослава and Миролюб start to talk
        bot.onUpdateReceived(buildUpdate("Привет, Мирослава", 1));
        verify(bot).execute(2L, "Привет, Мирослава");
        clearInvocations(bot);
        //Пересвет connects to bot and waits
        bot.onUpdateReceived(buildUpdate("ну чо там?", 3));
        verify(bot).execute(3L, "У вас пока еще нет собеседника, ждем...");
    }

    @Test
    public void changeMate2UsersTest() {
        AnonChatRoomBot bot = buildAnonChatRoomBot();
        //Миролюб connects to bot
        bot.onUpdateReceived(buildUpdate("/start", 1));
        //he has to wait
        verify(bot).execute(1L, "У вас пока еще нет собеседника, ждем...");
        clearInvocations(bot);
        //Мирослава connects to bot
        bot.onUpdateReceived(buildUpdate("/start", 2));
        //Миролюб and Мирослава notified about each other
        verify(bot).execute(1L, "Мы нашил вам собеседника, напишите ему(ей)!");
        verify(bot).execute(2L, "Мы нашил вам собеседника, напишите ему(ей)!");
        clearInvocations(bot);
        //then Мирослава and Миролюб start to talk
        bot.onUpdateReceived(buildUpdate("Привет, Мирослава", 1));
        verify(bot).execute(2L, "Привет, Мирослава");
        clearInvocations(bot);
        bot.onUpdateReceived(buildUpdate("Hi, Миролюб", 2));
        verify(bot).execute(1L, "Hi, Миролюб");
        clearInvocations(bot);
        //Миролюб tired of Мирослава and changes chat
        bot.onUpdateReceived(buildUpdate("/changemate", 1));
        verify(bot).execute(1L, "У вас пока еще нет собеседника, ждем...");
    }

    @Test
    public void changeMate3UsersTest() {
        AnonChatRoomBot bot = buildAnonChatRoomBot();
        //Миролюб connects to bot
        bot.onUpdateReceived(buildUpdate("/start", 1));
        //he has to wait
        verify(bot).execute(1L, "У вас пока еще нет собеседника, ждем...");
        clearInvocations(bot);
        //Мирослава connects to bot
        bot.onUpdateReceived(buildUpdate("/start", 2));
        //Миролюб and Мирослава notified about each other
        verify(bot).execute(1L, "Мы нашил вам собеседника, напишите ему(ей)!");
        verify(bot).execute(2L, "Мы нашил вам собеседника, напишите ему(ей)!");
        clearInvocations(bot);
        //then Мирослава and Миролюб start to talk
        bot.onUpdateReceived(buildUpdate("Привет, Мирослава", 1));
        verify(bot).execute(2L, "Привет, Мирослава");
        clearInvocations(bot);
        bot.onUpdateReceived(buildUpdate("Hi, Миролюб", 2));
        verify(bot).execute(1L, "Hi, Миролюб");
        clearInvocations(bot);
        //Евпатий connects to bot
        bot.onUpdateReceived(buildUpdate("/start", 3));
        //he has to wait
        verify(bot).execute(3L, "У вас пока еще нет собеседника, ждем...");
        clearInvocations(bot);
        //Миролюб tired of Мирослава and changes chat
        bot.onUpdateReceived(buildUpdate("/changemate", 1));
        //Миролюб connects to Евпатий
        verify(bot).execute(1L, "Мы нашил вам собеседника, напишите ему(ей)!");
        verify(bot).execute(3L, "Мы нашил вам собеседника, напишите ему(ей)!");
        clearInvocations(bot);
        //then Миролюб and Евпатий start to talk
        bot.onUpdateReceived(buildUpdate("Привет, Евпатий", 1));
        verify(bot).execute(3L, "Привет, Евпатий");
        clearInvocations(bot);
        //and Мирослава has to wait
        bot.onUpdateReceived(buildUpdate("Есть кто?", 2));
        verify(bot).execute(2L, "У вас пока еще нет собеседника, ждем...");
        clearInvocations(bot);
    }

    private AnonChatRoomBot buildAnonChatRoomBot() {
        AnonChatRoomBot bot = mock(AnonChatRoomBot.class);
        doCallRealMethod().when(bot).onUpdateReceived(any());
        try {
            FieldSetter.setField(bot, AnonChatRoomBot.class.getDeclaredField("chats"), new HashMap<>());
            FieldSetter.setField(bot, AnonChatRoomBot.class.getDeclaredField("pending"), new HashSet<>());
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        return bot;
    }

    private Update buildUpdate(String text, long chatId) {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.getMessage()).thenReturn(message);
        when(message.getText()).thenReturn(text);
        when(message.getFrom()).thenReturn(new User());
        when(message.getChatId()).thenReturn(chatId);
        return update;
    }
}
