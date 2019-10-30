package ru.kiselev.igor.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;


public class ClientHandler {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private MainServer server;
    private String nick;
    private ArrayList<String> blackList;

    public String getNick() {
        return nick;
    }

    public boolean checkBlackList(String nick) {
        return blackList.contains(nick);
    }

    //String nick;

    public ClientHandler(Socket socket, MainServer server) {

        try {
            this.blackList = new ArrayList<>();
            this.socket = socket;
            this.server = server;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());


            Thread t1 = new Thread(() -> {

                    try {
                        while (true) {  // цикл для авторизации клиентов
                            String str = in.readUTF();
                            if (str.startsWith("/auth ")) {
                                String[] tokens = str.split(" ");
                                String newNick = DbService.getNickByLoginAndPass(tokens[1], tokens[2]);

                                //// проверка правильности пары логин/пароль и того, что с таким ником пользователя нет в чате
                                if (newNick != null) {
                                    if (!server.nickIsBusy(newNick)) {
                                        sendMsg("/authok " + newNick); ////
                                        nick = newNick;
                                        server.subscribe(ClientHandler.this);

                                        //System.out.println("Клиент " + nick + " подключился");
                                        MainServer.logger.log(Level.INFO, "Клиент: " + nick + " подключился");


                                        break;
                                    } else {
                                        sendMsg("Пользователь с этим именем уже подключён");
                                    }
                                } else {
                                    sendMsg("Wrong Login/Password");
                                }
                            }
                            //server.broadCastMsg(ru.kiselev.igor.server.ClientHandler.this, nick + ": " + str);
                        }

                        /////

                        while (true) {  // цикл для работы со служебными сообщениями
                            String str = in.readUTF();
                            if (str.startsWith("/")) {
                                if (str.equals("/end")) {
                                    out.writeUTF("/serverclosed");
                                    break;
                                }

                                if (str.startsWith("/history")) {
                                    StringBuilder stringBuilder = DbService.getHistoryChat();
                                    out.writeUTF(stringBuilder.toString());

                                }
                                if (str.startsWith("/w ")) {
                                    String[] elements = str.split(" ", 3);

                                    server.personalMsg(ClientHandler.this, elements[1], elements[2]);
                                }
                                if (str.startsWith("/blacklist")) {
                                    String[] elements = str.split(" ");
                                    blackList.add(elements[1]);
                                    sendMsg("Вы добавили пользователя " + elements[1] + " в чёрный список");
                                }
                            } else {
                                DbService.saveHistory(nick, str);
                                server.broadCastMsg(ClientHandler.this, nick + ": " + str);
                            }
                            System.out.println("Client: " + str);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();

                    } finally {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        server.unsubscribe(ClientHandler.this);
                        server.broadCastMsg(ClientHandler.this, "Пользователь " + nick + " покинул чат");
                        MainServer.logger.log(Level.INFO, "Пользователь " + nick + " покинул чат");////

                    }

            });
            t1.setDaemon(true);
            t1.start();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendMsg(String msg) {

        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
