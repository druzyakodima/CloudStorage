package com.example.cloudst.alert;

import javafx.scene.control.Alert;

public class AlertEx {

    public void showErrorAlert(String title, String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(error);
        alert.show();
    }
}
