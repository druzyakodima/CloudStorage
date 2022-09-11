package com.example.cloudst.authentication;
import lombok.Data;

@Data
public class User {
    String login;
    String password;
    String username;

    public User(String login, String password, String username) {
        this.login = login;
        this.password = password;
        this.username = username;
    }
}
