import static org.junit.jupiter.api.Assertions.*;

import Server.Message;
import Server.MessageType;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class MessageTest {

    @Mock
    MessageType type;

    @Test
    public void getDataTest() {
        Message message = new Message(type, "data");

        assertEquals("data", message.getData());
    }

    @Test
    public void getTypeTest() {
        MessageType messageType = MessageType.TEXT;
        Message message  = new Message(messageType, "data");

        assertEquals(messageType, message.getType());
    }
}
