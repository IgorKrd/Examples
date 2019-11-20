package ru.kiselev.igor.cloud.client;

import ru.kiselev.igor.cloud.common.*;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.IOException;
import java.net.Socket;

class Network {
    static Socket socket;
    private static ObjectEncoderOutputStream out;
    private static ObjectDecoderInputStream in;

    private static final String IP_ADRESS = "localhost";
    private static final int PORT = 8199;

    static void start() {

        try {
            socket = new Socket(IP_ADRESS, PORT);
            in = new ObjectDecoderInputStream(socket.getInputStream(), 50 * 1024 * 1024);
            out = new ObjectEncoderOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void stop() {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean sendMsg(AbstractMessage msg) {
        try {
            out.writeObject(msg);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    static boolean sendFileDeleteMessage(String fileDelete, String userFolder){
        try{
            out.writeObject(new FileDeleteMessage(fileDelete, userFolder));
            out.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    static AbstractMessage readObject() throws ClassNotFoundException, IOException {
            Object obj = in.readObject();
            return (AbstractMessage) obj;
    }
}