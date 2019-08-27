package glebio;

import org.junit.Test;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Gleb Danichev
 */
public class AnonChatRoomBotTest {

    @Test
    public void regularScenarioWith2UsersTest() {
        AnonChatRoomBot bot = spy(new AnonChatRoomBot());
        //Миролюб connects to bot
        bot.onUpdateReceived(buildUpdate("/start", 1));
        //he has to wait
        verify(bot).execute(1L, "У вас пока еще нет собеседника, ждем...");
        //Мирослава connects to bot
        bot.onUpdateReceived(buildUpdate("/start", 2));
        //Миролюб and Мирослава notified about each other
        verify(bot).execute(1L, "Мы нашил вам собеседника, напишите ему(ей)!");
        verify(bot).execute(2L, "Мы нашил вам собеседника, напишите ему(ей)!");
        //then Мирослава and Миролюб start to talk
        bot.onUpdateReceived(buildUpdate("Привет, Мирослава", 1));
        verify(bot).execute(2L, "Привет, Мирослава");
    }

    @Test
    public void regularScenarioWith3UsersTest() {
        AnonChatRoomBot bot = spy(new AnonChatRoomBot());
        //Миролюб connects to bot
        bot.onUpdateReceived(buildUpdate("/start", 1));
        //he has to wait
        verify(bot).execute(1L, "У вас пока еще нет собеседника, ждем...");
        //Мирослава connects to bot
        bot.onUpdateReceived(buildUpdate("/start", 2));
        //then Мирослава and Миролюб start to talk
        bot.onUpdateReceived(buildUpdate("Привет, Мирослава", 1));
        verify(bot).execute(2L, "Привет, Мирослава");
        //Пересвет connects to bot and waits
        bot.onUpdateReceived(buildUpdate("ну чо там?", 3));
        verify(bot).execute(3L, "У вас пока еще нет собеседника, ждем...");
    }

    @Test
    public void changeMateTest()  {
        AnonChatRoomBot bot = spy(new AnonChatRoomBot());
        //Миролюб connects to bot
        bot.onUpdateReceived(buildUpdate("/start", 1));
        //he has to wait
        verify(bot).execute(1L, "У вас пока еще нет собеседника, ждем...");
        //Мирослава connects to bot
        bot.onUpdateReceived(buildUpdate("/start", 2));
        //Миролюб and Мирослава notified about each other
        verify(bot).execute(1L, "Мы нашил вам собеседника, напишите ему(ей)!");
        verify(bot).execute(2L, "Мы нашил вам собеседника, напишите ему(ей)!");
        //then Мирослава and Миролюб start to talk
        bot.onUpdateReceived(buildUpdate("Привет, Мирослава", 1));
        verify(bot).execute(2L, "Привет, Мирослава");
        bot.onUpdateReceived(buildUpdate("Hi, Миролюб", 2));
        verify(bot).execute(1L, "Hi, Миролюб");
        //Миролюб tired of Мирослава and changes chat
        bot.onUpdateReceived(buildUpdate("/changemate", 1));
        //Мирослава can't connect to Миролюб
        bot.onUpdateReceived(buildUpdate("Как дела, Миролюб?", 2));
        verify(bot).execute(2L, "У вас пока еще нет собеседника, ждем...");
        //Миролюб is looking for new mate
        bot.onUpdateReceived(buildUpdate("ну чо", 1));
        verify(bot).execute(1L, "У вас пока еще нет собеседника, ждем...");
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
