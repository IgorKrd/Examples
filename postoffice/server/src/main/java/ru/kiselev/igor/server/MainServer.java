package ru.kiselev.igor.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.*;


public class MainServer {

    private Vector<ClientHandler> clients;

    static final Logger logger = Logger.getLogger(MainServer.class.getName());

    public MainServer() throws SQLException {
        clients = new Vector<>();
        ServerSocket server = null;
        Socket socket = null;


        try {
            DbService.connect();

            server = new ServerSocket(8189);


            Handler handler = new FileHandler("logfile.txt", true);
            handler.setLevel(Level.ALL);
            handler.setFormatter(new SimpleFormatter());
            logger.addHandler(handler);

            logger.log(Level.INFO, "Сервер запущен. Ожидаем клиентов...");


            while (true) {
                socket = server.accept();

                logger.log(Level.INFO, "Канал к клиенту установлен. Этап авторизации клиента.");
                new ClientHandler(socket, this);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            DbService.disconnect();
            System.out.println("Клиент отключился");
            logger.log(Level.INFO, "Клиент отключился");
        }
    }

    // метод широковещательной рассылки

    public void broadCastMsg(ClientHandler from, String msg) {
        for (ClientHandler o : clients) {
            if (!o.checkBlackList(from.getNick())) {
                o.sendMsg(msg);
            }
        }
    }

    // метод проверки, что запрашиваемый nick занят/свободен

    public boolean nickIsBusy(String nick) {

        for (ClientHandler c : clients) {
            if (c.getNick().equals(nick)) {
                return true;
            }
        }
        return false;
    }
    /////
    // метод персонифицированной отправки сообщений

    public void personalMsg(ClientHandler from, String nickTo, String msg) {
        for (ClientHandler o : clients) {
            if (o.getNick().equals(nickTo)) {
                o.sendMsg("from " + from.getNick() + ": " + msg);
                from.sendMsg("to " + nickTo + ": " + msg);
                return;
            }
        }
        from.sendMsg("Клиент с ником " + nickTo + " не найден в чате");
    }

    public void broadcastClientList() {

        StringBuilder sb = new StringBuilder();
        sb.append("/clientlist ");
        for (ClientHandler c : clients) {
            sb.append(c.getNick() + " ");
        }
        String out = sb.toString();
        for (ClientHandler c : clients) {
            c.sendMsg(out);

        }
    }

    // подписываем клиента на рассылку
    public void subscribe(ClientHandler client) {

        clients.add(client);
        broadcastClientList();
    }

    // отписываем клиента от рассылки сообщений
    public void unsubscribe(ClientHandler client) {

        clients.remove(client);
        broadcastClientList();
    }
}




