package com.example.cloudst.server.models;

import java.io.Serializable;

public class MyMessage implements Serializable {
    @lombok.Getter
    private String nameDir;

    public MyMessage(String nameDir) {
        this.nameDir = nameDir;
    }
}
