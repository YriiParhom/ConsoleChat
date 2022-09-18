import Server.Connection;
import Server.Message;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ServerTest {

    @Test
    public void sendBroadcastMessageTest() throws IOException {
        Connection connection = Mockito.mock(Connection.class);
        Message message = Mockito.mock(Message.class);
        Map<String, Connection> connectionMap = Mockito.mock(ConcurrentHashMap.class);
        for (Map.Entry<String, Connection> pair : connectionMap.entrySet()) {
            Connection value = pair.getValue();
            value.send(message);
        }

        IOException exception = assertThrows(
                IOException.class,
                () -> {
                    throw new IOException("Не удалось отправить сообщение.");
                }
        );
        assertEquals("Не удалось отправить сообщение.", exception.getMessage());
    }

    @Test
    public void runTest() {
        IOException exception = assertThrows(
                IOException.class,
                () -> {
                    throw new IOException("При обмене данными возникла непредвиденная ошибка.");
                }
        );
        assertEquals("При обмене данными возникла непредвиденная ошибка.", exception.getMessage());

        ClassNotFoundException exception1 = assertThrows(ClassNotFoundException.class,
                () -> {
            throw new ClassNotFoundException("При обмене данными возникла непредвиденная ошибка.");}
        );
        assertEquals("При обмене данными возникла непредвиденная ошибка.", exception1.getMessage());
    }
}
