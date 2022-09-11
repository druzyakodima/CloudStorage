package com.example.cloudst;

import com.example.cloudst.server.CloudServer;

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
