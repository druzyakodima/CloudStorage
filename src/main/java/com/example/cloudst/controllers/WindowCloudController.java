package com.example.cloudst.controllers;

import com.example.cloudst.alert.AlertEx;
import com.example.cloudst.check_size.CheckSize;
import com.example.cloudst.client.Network;
import com.example.cloudst.server.handler.ServerHandler;
import com.example.cloudst.server.models.AbstractFile;
import com.example.cloudst.server.models.FileRequest;
import com.example.cloudst.server.models.MyFile;
import com.example.cloudst.server.models.MyMessage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.input.MouseEvent;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class WindowCloudController {

    @FXML
    private Label username;
    @FXML
    private ListView<String> serverFile;

    @FXML
    private ListView<String> localFile;

    private String selectedRecipient;

    private final long MAX_SIZE_DIR = 5368709120L;

    @lombok.Setter
    private Network network;
    private int index;
    @lombok.Setter
    @lombok.Getter
    private String nameLocalDir;
    @lombok.Setter
    @lombok.Getter
    private String nameServerDir;

    @lombok.Setter
    @lombok.Getter
    private String userNameAndNameDir;

    private final Logger file = Logger.getLogger("file");
    @FXML
    void initialize() {
        selectNameFileClickMouse(localFile);
        selectNameFileClickMouse(serverFile);
    }

    public void refreshFilesListAndNameDir() {
        setNameServerDir(nameServerDir = "server_storage_" + getUserNameAndNameDir());
        setNameLocalDir(nameLocalDir = "client_storage_" + getUserNameAndNameDir());

        MyMessage message = new MyMessage(getNameServerDir());
        network.sendNameDir(message);

        network.refreshFilesList(getNameServerDir(), serverFile);
        network.refreshFilesList(getNameLocalDir(), localFile);
    }

    private void selectNameFileClickMouse(ListView<String> file) {
        file.setCellFactory(lv -> {
            MultipleSelectionModel<String> selectionModel = file.getSelectionModel();
            ListCell<String> cell = new ListCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                file.requestFocus();
                if (!cell.isEmpty()) {
                    index = cell.getIndex();
                    if (selectionModel.getSelectedIndices().contains(index)) {
                        selectionModel.clearSelection(index);
                        selectedRecipient = null;
                    } else {
                        selectionModel.select(index);
                        selectedRecipient = cell.getItem();
                    }
                    event.consume();
                }
            });
            return cell;
        });
    }

    @FXML
    void selectFile()  {
        network.chooseFile(localFile, getNameLocalDir());
    }

    @FXML
    void sendFile() {
        long newFileSize = CheckSize.size(Paths.get(getNameLocalDir() + "/" + selectedRecipient));
        long fullOfMemoryDir = CheckSize.size(Paths.get(getNameServerDir()));

        if (fullOfMemoryDir + newFileSize <  MAX_SIZE_DIR ) {
            if (selectedRecipient.length() > 0) {
                try {
                    if (Files.exists(Paths.get(getNameLocalDir() + "/" + selectedRecipient))) {
                        MyFile myFile = new MyFile(Paths.get(getNameLocalDir() + "/" + selectedRecipient));
                        network.sendMsg(myFile);
                        file.info("Пользователь " + getUserNameAndNameDir() + " отправил файл на сервер");
                    }
                } catch (IOException e) {
                    file.error("Произошла ошибка отправки файла у пользователя " + getUserNameAndNameDir());
                    throw new RuntimeException(e);
                } finally {
                    serverFile.getItems().add(selectedRecipient);
                }
            }
        } else {
            AlertEx alert = new AlertEx();
            alert.showErrorAlert("Ошибка записи файла", "В облачном хранилище не осталось места");
        }
    }

    public void setUsernameTitle(String username) {
        this.username.setText(username);
    }

    @FXML
    public void removeLocalFile() {
        if (localFile.getItems().indexOf(selectedRecipient) == index) {
            localFile.getItems().remove(selectedRecipient);
            network.deleteFile(selectedRecipient, getNameLocalDir());
            file.info("Пользователь " + getNameServerDir() + " удалил файл из локального хранилища " + selectedRecipient);
        }
    }

    @FXML
    void removeServerFile() {
        if (serverFile.getItems().indexOf(selectedRecipient) == index) {
            serverFile.getItems().remove(selectedRecipient);
            ServerHandler.deleteFile(selectedRecipient, getNameServerDir());
            file.info("Пользователь " + getNameServerDir() + " удалил файл из облака " + selectedRecipient);
        }
    }

    @FXML
    void downloadFile() {
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    AbstractFile abstractFile = network.readObject();
                    if (abstractFile instanceof MyFile myFile) {
                        Files.write(Paths.get(getNameLocalDir() + "/" + myFile.getFileName()), myFile.getData(), StandardOpenOption.CREATE);
                        network.refreshFilesList(getNameLocalDir(), localFile);
                        network.refreshFilesList(getNameServerDir(), serverFile);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {

                throw new RuntimeException(e);
            } finally {
                network.stop();
            }
        });
        t.setDaemon(true);
        t.start();

        if (selectedRecipient.length() > 0) {
            network.sendMsg(new FileRequest(selectedRecipient));
            localFile.getItems().add(selectedRecipient);
            file.info("Пользователь " + getNameServerDir() + " скачал файл из облака " + selectedRecipient);
            selectedRecipient = null;
        }
    }
}

