package com.example.cloudst;

public class StartServer {
    public static void main(String[] args) {
        CloudServer cloudServer = new CloudServer();
        try {
            cloudServer.run();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
