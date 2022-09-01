package com.example.cloudst.controllers;

import com.example.cloudst.StartAuth;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class WindowCloudController {

    @FXML
    private ResourceBundle resources;
    @FXML
    private ListView<String> localFile;

    @FXML
    private URL location;
    private ObservableList<String> nameFile = FXCollections.observableArrayList();
    private String selectedRecipient;

    @FXML
    void initialize() {
        localFile.setCellFactory(lv -> {
            MultipleSelectionModel<String> selectionModel = localFile.getSelectionModel();
            ListCell<String> cell = new ListCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                localFile.requestFocus();
                if (!cell.isEmpty()) {
                    int index = cell.getIndex();
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
    void selectFile(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Добавить");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Картинки", "*.jpg", "*.png", "*.gif", "*.bmp", "*.txt", "*.pdf");
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showOpenDialog(StartAuth.javaFXC);
        if (file != null) {
            nameFile.add(file.getName());
            localFile.setItems(nameFile);
        }
    }


    @FXML
    void removeFile(ActionEvent event) {
        localFile.getItems().remove(selectedRecipient);
    }
}
