package com.example.cloudst.controllers;

import com.example.cloudst.StartAuth;
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
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
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
    String nameDir;

    @FXML
    void initialize() {
        selectNameFileClickMouse(localFile);
        selectNameFileClickMouse(serverFile);
    }

    public void refreshFilesListAndNameDir() {
        setNameServerDir(nameServerDir = "server_storage_" + getNameDir());
        setNameLocalDir(nameLocalDir = "client_storage_" + getNameDir());

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
    void selectFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Добавить");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Файлы", "*.jpg", "*.png", "*.gif", "*.bmp", "*.txt", "*.pdf");
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showOpenDialog(StartAuth.javaFXC);
        if (file != null) {
            localFile.getItems().add(file.getName());
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                network.addFile(file.getName(), fileInputStream.readAllBytes(), getNameLocalDir());
            }
        }
    }
    @FXML
    void sendFile() {
        if (selectedRecipient.length() > 0) {
            try {
                if (Files.exists(Paths.get(getNameLocalDir() + "/" + selectedRecipient))) {
                    MyFile myFile = new MyFile(Paths.get(getNameLocalDir() + "/" + selectedRecipient));
                    network.sendMsg(myFile);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                serverFile.getItems().add(selectedRecipient);
            }
        }
    }

    public void setUsernameTitle(String username) {
        this.username.setText(username);
    }

    @FXML
    public void removeFile() {

        if (localFile.getItems().indexOf(selectedRecipient) == index) {
            localFile.getItems().remove(selectedRecipient);
            network.deleteFile(selectedRecipient, getNameLocalDir());
        }
        if (serverFile.getItems().indexOf(selectedRecipient) == index) {
            serverFile.getItems().remove(selectedRecipient);
            ServerHandler.deleteFile(selectedRecipient, getNameServerDir());
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
            selectedRecipient = null;
        }
    }
}

