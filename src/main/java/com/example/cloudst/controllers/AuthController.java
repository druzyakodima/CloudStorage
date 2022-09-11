package com.example.cloudst.controllers;

import com.example.cloudst.StartAuth;
import com.example.cloudst.authentication.DBBaseAuthentication;
import com.example.cloudst.client.Network;
import com.example.cloudst.server.handler.ServerHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.security.SecureRandom;

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

    @lombok.Setter
    private Network network;
    private DBBaseAuthentication authentication = new DBBaseAuthentication();

    ServerHandler serverHandler = new ServerHandler();

    private String username;

    public String getUsername() {
        return username;
    }

    @FXML
    void auth() {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();
        String hashPassword = serverHandler.hashPassword(password,authentication.getSalt(login));
        checkAuth(login, hashPassword);
    }

    public void checkAuth(String login,  String password) {

        if (login.length() == 0 || password.length() == 0) {
            startAuth.showErrorAlert("Ошибка ввода", "Поля не должны быть пустыми");
            return;
        }

        authentication.startAuthentication();
        String user = authentication.getUsernameByLoginAndPassword(login, password);
        if (user != null) {
            setUsername(user);
            try {
                startAuth.createChatDialog();
            } catch (IOException e) {
                e.printStackTrace();
            }
            startAuth.openChatDialog();
        } else {
            startAuth.showErrorAlert("Ошибка аутентификации", "Не правильный пароль или логин");
        }

    }

    @FXML
    void close(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void registration(ActionEvent event) {
        String username = usernameRegister.getText();
        String login = loginFieldRegister.getText();
        String password = passwordRegister.getText();

        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        String hashPassword =  serverHandler.hashPassword(password,salt);
        authentication.createUser(login, hashPassword, username, salt);

        checkAuth(login, hashPassword);

    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setStartClient(StartAuth startAuth) {
        this.startAuth = startAuth;
    }

}