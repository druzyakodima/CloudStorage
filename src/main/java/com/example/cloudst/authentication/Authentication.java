package com.example.cloudst.authentication;

public interface Authentication {
    String getUsernameByLoginAndPassword(String login, String password);

    void createUser(String login, String password, String username, byte[] salt);
    Boolean checkLoginByFree(String login);

    void startAuthentication();

    void endAuthentication();

}
