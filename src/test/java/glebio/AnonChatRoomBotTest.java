package glebio;

import org.junit.Test;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Gleb Danichev
 */
public class AnonChatRoomBotTest {

    @Test
    public void regularScenarioTestTest() {
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
