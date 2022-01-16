package com.ldnhat.model;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public class FileModel implements Serializable {

    private List<File> files;

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }
}
