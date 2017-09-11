package com.org.runnables;

import com.org.physical.FileStorage;

public class FileWriter implements Runnable {

    private String content;

    public FileWriter(String content){
        this.content = content;
    }

    @Override
    public void run() {
        FileStorage.getInstance().writeContent(this.content+"\n");
    }
}
