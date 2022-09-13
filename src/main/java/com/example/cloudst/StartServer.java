package com.example.cloudst;

import com.example.cloudst.server.CloudServer;
import org.apache.log4j.PropertyConfigurator;

public class StartServer {
    public static void main(String[] args) {
        PropertyConfigurator.configure("src/main/resources/logs/configs/log4j.properties");

        CloudServer cloudServer = new CloudServer();
        try {
            cloudServer.run();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
