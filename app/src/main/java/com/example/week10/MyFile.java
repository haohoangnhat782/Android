package com.example.week10;

public class MyFile {
    private final String name;
    private final String path;
    private final boolean isDirectory;

    public MyFile(String name, String path, boolean isDirectory) {
        this.name = name;
        this.path = path;
        this.isDirectory = isDirectory;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public String getExtension() {
        if (!isDirectory) {
            int dotIndex = name.lastIndexOf(".");
            if (dotIndex >= 0) {
                return name.substring(dotIndex);
            }
        }
        return null;
    }
}