package Server;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConsoleHelper {

    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static String readString() {
        String str;
        while (true) {
            try {
                str = reader.readLine();
                break;
            } catch (IOException e) {
                System.out.println("Произошла ошибка при попытке ввода текста. Попробуйте еще раз.");
            }
        }
        return str;
    }

    public static int readInt() {
        String num;
        while (true) {
            try {
                num = readString();
                break;
            } catch (NumberFormatException e) {
                System.out.println("Введено не число. Введите число.");
            }
        }
        return Integer.parseInt(num);
    }

    public static void writeLogFile(String msg) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.now();
        String formatDateTime = formatter.format(dateTime);
        String message = formatDateTime + " " + msg;
        System.out.println(message);
        try (FileWriter logger = new FileWriter("server.log", true)) {
            logger.append(message)
                    .append('\n')
                    .flush();
        } catch (IOException e) {
            System.out.println("Отсутствует лог файл");
        }
    }
}
