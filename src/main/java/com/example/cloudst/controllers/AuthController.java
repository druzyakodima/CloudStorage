package com.example.cloudst.controllers;

import com.example.cloudst.StartAuth;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AuthController {
    @FXML
    private TextField loginField;

    @FXML
    private TextField loginFieldRegister;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField passwordRegister;

    @FXML
    private TextField usernameRegister;

    private StartAuth startAuth;

    @FXML
    void checkAuth(ActionEvent event) {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();

        if (login.length() == 0 || password.length() == 0) {
            startAuth.showErrorAlert("Ошибка ввода", "Поля не должны быть пустыми");
            return;
        }


    }

    @FXML
    void close(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void registration(ActionEvent event) {

    }

    public void setStartClient(StartAuth startAuth) {
        this.startAuth = startAuth;
    }
}