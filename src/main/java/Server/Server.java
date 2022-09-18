package Server;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {

        Properties properties = new Properties();
        properties.load(new FileInputStream("settings.txt"));
        int serverPort = Integer.parseInt(properties.getProperty("SERVER_PORT"));

        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            ConsoleHelper.writeLogFile("Сервер запущен...");

            while (true) {
                Socket socket = serverSocket.accept();
                new Handler(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendBroadcastMessage(Message message) {
        for (Map.Entry<String, Connection> pair : connectionMap.entrySet()) {
            try {
                Connection value = pair.getValue();
                value.send(message);
            } catch (IOException e) {
                ConsoleHelper.writeLogFile("Не удалось отправить сообщение.");
            }
        }

    }

    private static class Handler extends Thread {

        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            super.run();

            ConsoleHelper.writeLogFile("Установлено соединение с адресом " + socket.getRemoteSocketAddress());
            String userName = null;
            try (Connection connection = new Connection(socket)) {
                userName = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
                notifyUsers(connection, userName);
                serverMainLoop(connection, userName);
            } catch (IOException | ClassNotFoundException e) {
                ConsoleHelper.writeLogFile("При обмене данными возникла непредвиденная ошибка.");
            }

            if (userName != null) {
                connectionMap.remove(userName);
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));
            }
            ConsoleHelper.writeLogFile(String.format("Потльзователь %s был удален", userName));
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {

            while (true) {
                Message message = connection.receive();
                if (message.getType() == (MessageType.TEXT)) {
                    String msg = userName + ": " + message.getData();
                    Message messageOut = new Message(MessageType.TEXT, msg);
                    sendBroadcastMessage(messageOut);
                } else {
                    ConsoleHelper.writeLogFile("ERROR");
                }
            }
        }

        private void notifyUsers(Connection connection, String userName) throws IOException {
            for (Map.Entry<String, Connection> pair : connectionMap.entrySet()) {
                String kay = pair.getKey();
                if (!kay.equals(userName)) {
                    connection.send(new Message(MessageType.USER_ADDED, kay));
                }
            }
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            while (true) {
                connection.send(new Message(MessageType.NAME_REQUEST));
                Message message = connection.receive();

                if (message.getType() == MessageType.USER_NAME) {
                    if (!message.getData().isEmpty()) {
                        if (connectionMap.get(message.getData()) == null) {
                            connectionMap.put(message.getData(), connection);
                            connection.send((new Message(MessageType.NAME_ACCEPTED)));
                            return message.getData();
                        }
                    }

                }
            }
        }
    }
}
