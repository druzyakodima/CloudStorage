package com.example.cloudst.client;

import com.example.cloudst.server.models.AbstractFile;
import com.example.cloudst.server.models.MyMessage;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Network {
    private final String HOST = "localhost";
    private final int PORT = 8080;
    private ObjectEncoderOutputStream out;
    private ObjectDecoderInputStream in;
    private Socket socket;

    private String username;

    public void connect() {
        try {
            socket = new Socket(HOST, PORT);
            out = new ObjectEncoderOutputStream(socket.getOutputStream());
            in = new ObjectDecoderInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMsg(AbstractFile myFile) {
        try {
            out.writeObject(myFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendNameDir(MyMessage nameDir) {
        try {
            out.writeObject(nameDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public AbstractFile readObject() throws IOException, ClassNotFoundException {
        Object obj = in.readObject();
        return (AbstractFile) obj;
    }

    public void addFile(String name, byte[] data, String nameDir) {
        try {
            Files.write(Paths.get(nameDir + "/" + name), data, StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFile(String name, String nameDir) {
        try {
            Files.delete(Paths.get(nameDir + name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshFilesList(String nameDir, ListView<String> file) {

        Path dir = Path.of(nameDir);
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (Platform.isFxApplicationThread()) {
            try {
                file.getItems().clear();
                Files.list(dir).map(p -> p.getFileName().toString()).forEach(o -> file.getItems().add(o));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Platform.runLater(() -> {
                try {
                    file.getItems().clear();
                    Files.list(Paths.get(nameDir)).map(p -> p.getFileName().toString()).forEach(o -> file.getItems().add(o));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
