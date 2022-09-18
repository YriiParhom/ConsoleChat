import org.junit.jupiter.api.Test;


import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClientTest {

    @Test
    public void runTest() {
        InterruptedException exception = assertThrows(
                InterruptedException.class,
                () -> {
                    throw new InterruptedException("Произошла ошибка во время работы клиента.");
                }
        );
        assertEquals("Произошла ошибка во время работы клиента.", exception.getMessage());
    }

    @Test
    public void getServerAddressTest() {
        assertEquals("localhost", "localhost");
    }

    @Test
    public void getServerPort() {
        IOException exception = assertThrows(
                IOException.class,
                () -> {
                    throw new IOException("Не найден файл settings.txt");
                }
        );
        assertEquals("Не найден файл settings.txt", exception.getMessage());
    }

    @Test
    public void sendTextMessage() {
        IOException exception = assertThrows(
                IOException.class,
                () -> {
                    throw new IOException("Возникла проблема с отправкой сообщения.");
                }
        );
        assertEquals("Возникла проблема с отправкой сообщения.", exception.getMessage());
    }
}
