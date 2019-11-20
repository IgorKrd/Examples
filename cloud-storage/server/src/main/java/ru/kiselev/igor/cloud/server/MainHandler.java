package ru.kiselev.igor.cloud.server;

import ru.kiselev.igor.cloud.common.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;


public class MainHandler extends ChannelInboundHandlerAdapter {

   static String fileCatalogServerSide = "server_storage/";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof FileRequest) {
                FileRequest fr = (FileRequest) msg;
                File file = new File(fileCatalogServerSide + fr.getUserFolder() + "/" + fr.getFilename());
                int bufferSize = 1024 * 1024 * 10;
                int partsCount = new Long(file.length() / bufferSize).intValue();
                if (file.length() % bufferSize != 0) {
                    partsCount++;
                }
                FileMessage fm = new FileMessage(Paths.get(fileCatalogServerSide + fr.getUserFolder() + "/" + fr.getFilename()),
                        fr.getUserFolder(), new byte[bufferSize], partsCount, -1);

                try (FileInputStream inputStream = new FileInputStream(file)) {
                    for (int i = 0; i < partsCount; i++) {
                        int readBytes = inputStream.read(fm.getData());
                        int partNumber = i + 1;
                        fm.setPartNumber(partNumber);
                        if (readBytes < bufferSize) {
                            fm.setData(Arrays.copyOfRange(fm.getData(), 0, readBytes));
                        }
                        ctx.writeAndFlush(fm);
                        System.out.println("Отправлена часть №: " + (i + 1));
                    }
                }
            } else if (msg instanceof FileMessage) {
                FileMessage fm = (FileMessage) msg;
                boolean append = true;
                if (fm.getPartNumber() == 1) {
                    append = false;
                }
                System.out.println(fm.getPartNumber() + "/" + fm.getPartsCount());
                try (FileOutputStream fos = new FileOutputStream(fileCatalogServerSide + fm.getUserFolder() + "/" + fm.getFilename(), append)) {
                    fos.write(fm.getData());
                }
                // после приёма файла целиком на сервер выполняем обновление списка файлов и направляем клменту новый список
                ArrayList<String> findFiles = new ArrayList<>();
                Files.walk(Paths.get(fileCatalogServerSide + fm.getUserFolder())).filter(Files::isRegularFile).forEach(filePath -> {
                    findFiles.add(filePath.getFileName().toString());
                });
                RefreshServerStorageMessage rssm = new RefreshServerStorageMessage(findFiles);
                ctx.writeAndFlush(rssm);

            } else  if (msg instanceof FileDeleteMessage) {
                FileDeleteMessage fdm = (FileDeleteMessage)msg;
                Files.delete(Paths.get(fileCatalogServerSide + fdm.getUserFolder() + "/" + fdm.getFileDeleteName()));
                // после удаления файла на сервере выполняем обновление списка файлов и направляем клменту новый список
                ArrayList<String> findFiles = new ArrayList<>();
                Files.walk(Paths.get(fileCatalogServerSide + fdm.getUserFolder())).filter(Files::isRegularFile).forEach(filePath -> {
                    findFiles.add(filePath.getFileName().toString());
                });
                RefreshServerStorageMessage rssm = new RefreshServerStorageMessage(findFiles);
                ctx.writeAndFlush(rssm);
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
