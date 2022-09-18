package Client;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

import Server.Connection;
import Server.ConsoleHelper;
import Server.Message;
import Server.MessageType;

public class Client {

    protected Connection connection;
    private volatile boolean clientConnected;

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }

    public void run() {
        SocketThread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();

        try {
            synchronized (this) {
                wait();
            }
        } catch (InterruptedException e) {
            ConsoleHelper.writeLogFile("Произошла ошибка во время работы клиента.");
            return;
        }

        if (clientConnected)
            ConsoleHelper.writeLogFile("Соединение установлено. Для выхода наберите команду 'exit'.");
        else
            ConsoleHelper.writeLogFile("Произошла ошибка во время работы клиента.");

        while (clientConnected) {
            String text = ConsoleHelper.readString();
            if (text.equalsIgnoreCase("exit"))
                break;

            if (shouldSendTextFromConsole())
                sendTextMessage(text);
        }
    }


    protected String getServerAddress() {
        return "localhost";
    }

    protected int getServerPort() throws IOException {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("settings.txt"));
        }catch (IOException e) {
            ConsoleHelper.writeLogFile("Не найден файл settings.txt");
        }
        return Integer.parseInt(properties.getProperty("SERVER_PORT"));
    }

    protected String getUserName() {
        ConsoleHelper.writeLogFile("Введите свое имя: ");
        return ConsoleHelper.readString();
    }

    protected boolean shouldSendTextFromConsole() {
        return true;
    }

    protected SocketThread getSocketThread() {
        return new SocketThread();
    }

    protected void sendTextMessage(String text) {
        try {
            connection.send(new Message(MessageType.TEXT, text));
        } catch (IOException e) {
            ConsoleHelper.writeLogFile("Возникла проблема с отправкой сообщения.");
            clientConnected = false;
        }
    }

    public class SocketThread extends Thread {

        public void run() {
            super.run();

            String serverAddress = getServerAddress();
            int serverPort = 0;
            try {
                serverPort = getServerPort();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                Socket socket = new Socket(serverAddress, serverPort);
                connection = new Connection(socket);
                clientHandshake();
                clientMainLoop();
            } catch (IOException | ClassNotFoundException e) {
                notifyConnectionStatusChanged(false);
            }
        }

        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            while (currentThread().isAlive()) {
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT) {
                    processIncomingMessage(message.getData());
                } else if (message.getType() == MessageType.USER_ADDED) {
                    informAboutAddingNewUser(message.getData());
                } else if (message.getType() == MessageType.USER_REMOVED) {
                    informAboutDeletingNewUser(message.getData());
                } else {
                    throw new IOException("Неизвестный тип сообщения");
                }
            }
        }

        protected void clientHandshake() throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.NAME_REQUEST) {
                    String name = getUserName();
                    Message nameMessage = new Message(MessageType.USER_NAME, name);
                    connection.send(nameMessage);
                } else if (message.getType() == MessageType.NAME_ACCEPTED) {
                    notifyConnectionStatusChanged(true);
                    break;
                } else {
                    throw new IOException("Неизвестный тип сообщения");
                }
            }
        }

        protected void notifyConnectionStatusChanged(boolean clientConnected) {
            Client.this.clientConnected = clientConnected;

            synchronized (Client.this) {
                Client.this.notify();
            }
        }

        protected void informAboutDeletingNewUser(String userName) {
            ConsoleHelper.writeLogFile(String.format("Участник с именем %s покинул чат.", userName));
        }

        protected void informAboutAddingNewUser(String userName) {
            ConsoleHelper.writeLogFile(String.format("Участник с именем %s присоединился к чату", userName));
        }

        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeLogFile(message);
        }
    }
}
