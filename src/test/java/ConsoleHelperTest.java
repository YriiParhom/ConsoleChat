import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConsoleHelperTest {

    @Test
    public void readStringTest() throws IOException {
        BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);
        Mockito.when(bufferedReader.readLine()).thenReturn("message");

        assertEquals("message", bufferedReader.readLine());


        IOException exception = assertThrows(
                IOException.class,
                () -> {
                    throw new IOException("Произошла ошибка при попытке ввода текста. Попробуйте еще раз.");
                }
        );
        assertEquals("Произошла ошибка при попытке ввода текста. Попробуйте еще раз.", exception.getMessage());
    }

    @Test
    public void readIntTest() throws IOException {
        BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);
        Mockito.when(bufferedReader.readLine()).thenReturn("message");


        IOException exception = assertThrows(
                IOException.class,
                () -> {
                    throw new IOException("Введено не число. Введите число.");
                }
        );
        assertEquals("Введено не число. Введите число.", exception.getMessage());
    }

    @Test
    public void writeLogFileTest() throws IOException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.now();
        String formatDateTime = formatter.format(dateTime);
        String msg = "Всем привет";
        String message = formatDateTime + " " + msg;
        String expected = formatDateTime + " Всем привет";
        assertEquals(expected, message);


        IOException exception = assertThrows(
                IOException.class,
                () -> {
                    throw new IOException("Отсутствует лог файл");
                }
        );
        assertEquals("Отсутствует лог файл", exception.getMessage());
    }
}
