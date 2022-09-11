package com.example.cloudst.server.models;

public class FileRequest implements AbstractFile {
    private String name;

    public String getName() {
        return name;
    }

    public FileRequest(String name) {
        this.name = name;
    }
}
