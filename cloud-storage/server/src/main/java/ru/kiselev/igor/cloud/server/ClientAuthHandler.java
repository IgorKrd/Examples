package ru.kiselev.igor.cloud.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.*;

import ru.kiselev.igor.cloud.common.RefreshServerStorageMessage;
import ru.kiselev.igor.cloud.common.ServiceMessage;
import ru.kiselev.igor.cloud.common.ServiceRequest;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;


public class ClientAuthHandler extends ChannelInboundHandlerAdapter {

    private int existingUserId;

    private String loginCurrentUser;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            while (true) {
                if (msg instanceof ServiceRequest) {
                    ServiceRequest sr = (ServiceRequest) msg;

                    if (sr.getServiceRequest().startsWith("/auth ")) {
                        String[] tokens = sr.getServiceRequest().split(" ");
                        existingUserId = DbService.getIdByLoginAndPass(tokens[1], tokens[2]);
                        loginCurrentUser = tokens[1];
                        if (existingUserId != 0) {
                            ServiceMessage sm = new ServiceMessage("/authok " + existingUserId + " " + tokens[1]);
                            ctx.writeAndFlush(sm);
                            // сразу вместе с подтверждением аутентификации отправляем список файлов в каталоге клиента на стороне сервера
                            ArrayList<String> findFiles = new ArrayList<>();
                            Files.walk(Paths.get(MainHandler.fileCatalogServerSide + loginCurrentUser)).filter(Files::isRegularFile).forEach(filePath -> {
                                findFiles.add(filePath.getFileName().toString());
                            });
                            RefreshServerStorageMessage rssm = new RefreshServerStorageMessage(findFiles);
                            ctx.writeAndFlush(rssm);
                            //
                            MainServer.logger.log(Level.INFO, "Клиент ID:" + existingUserId + "; " + tokens[1] + " подключился");
                            break;
                        } else {
                            ServiceMessage sm = new ServiceMessage("/wrong ");
                            ctx.writeAndFlush(sm);
                            break;
                        }
                    } else if (sr.getServiceRequest().startsWith("/end ")) {
                        String[] tokens = sr.getServiceRequest().split(" ");
                        ctx.close();
                        MainServer.logger.log(Level.INFO, "Клиент ID:" + tokens[1] + "; " + tokens[2] + " отключился.");
                        break;

                    } else if (sr.getServiceRequest().startsWith("/check ")) {
                        String[] tokens = sr.getServiceRequest().split(" ");

                        if (!DbService.checkNewLogin(tokens[1])) {
                            ServiceMessage sm = new ServiceMessage("/loginFalse");
                            ctx.writeAndFlush(sm);
                            break;
                        } else {
                            ServiceMessage sm = new ServiceMessage("/loginTrue");
                            ctx.writeAndFlush(sm);
                            break;
                        }
                    } else if (sr.getServiceRequest().startsWith("/createUser ")) {
                        String[] tokens = sr.getServiceRequest().split(" ");

                        if (!DbService.tryToCreateNewUsers(tokens[1], tokens[2])) {
                            ServiceMessage sm = new ServiceMessage("/createUserFalse");
                            ctx.writeAndFlush(sm);
                            break;
                        } else {
                            ServiceMessage sm = new ServiceMessage("/createUserTrue");
                            File file = new File("server_storage/" + tokens[1]);
                            file.mkdirs();   // создание папки пользователя для хранения его файлов
                            loginCurrentUser = tokens[1];
                            ctx.writeAndFlush(sm);
                            break;
                        }
                    }
                } else {
                    ctx.fireChannelRead(msg);  //если полученное сообщение не является ServiceRequest, то отправляем его далее по конвееру
                    break;
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}


