package com.example.cloudst.server.models;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MyFile implements AbstractFile {
    private byte[] data;
    private String name;

    public String getFileName() {
        return name;
    }

    public byte[] getData() {
        return data;
    }

    public MyFile(Path path) throws IOException {
        this.name = path.getFileName().toString();
        this.data = Files.readAllBytes(path);
    }
}
