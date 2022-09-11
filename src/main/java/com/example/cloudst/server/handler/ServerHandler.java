package com.example.cloudst.server.handler;

import com.example.cloudst.server.models.FileRequest;
import com.example.cloudst.server.models.Hash;
import com.example.cloudst.server.models.MyFile;
import com.example.cloudst.server.models.MyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    @lombok.Getter
    @lombok.Setter
    String nameServerDir;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {
        try {
            if (msg == null) {
                return;
            }
            if (msg instanceof FileRequest) {
                sendFile(ctx, msg);
            }
            if (msg instanceof MyFile) {
                readFile(msg);
            } else {
                if (msg instanceof  MyMessage) {
                    MyMessage message = (MyMessage) msg;
                    setNameServerDir(message.getNameDir());
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void sendFile(ChannelHandlerContext ctx, Object msg) throws IOException {
        FileRequest fr = (FileRequest) msg;
        if (Files.exists(Paths.get(getNameServerDir() + "/" + fr.getName()))) {
            MyFile myFile = new MyFile(Paths.get(getNameServerDir() + "/" + fr.getName()));
            ctx.writeAndFlush(myFile);
        }
    }

    private void readFile(Object msg) throws IOException {
        MyFile file = (MyFile) msg;
        Files.write(Paths.get(getNameServerDir() + "/" + file.getFileName()), file.getData(), StandardOpenOption.CREATE);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }

    public static void deleteFile(String name, String nameDir) {
        try {
            Files.delete(Paths.get(nameDir + "/" + name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String hashPassword(String password, byte[] salt) {
        Hash hash = new Hash();
        return hash.hash(password.toCharArray(), salt);
    }

}
