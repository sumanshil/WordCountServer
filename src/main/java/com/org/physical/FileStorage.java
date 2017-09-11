package com.org.physical;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileStorage {
    private final static String fileLocation = "/tmp/numbers.log";
    private static File file;
    private static FileStorage fileStorage = new FileStorage();

    public static FileStorage getInstance() {
        return fileStorage;
    }

    private FileStorage(){

    }

    public void initialize(){
        cleanup();
        FileStorage.file = new File(fileLocation);
        try {
            FileStorage.file.createNewFile();
        } catch (IOException e) {
            System.err.println("Failed to create file "+fileLocation);
            e.printStackTrace();
        }
    }

    public static void cleanup() {
        File file = new File(fileLocation);
        if (file.exists()){
            file.delete();
        }
    }

    public void writeContent(String content){
        try {
            File file = new File(fileLocation);
            if (file.exists()) {
                Files.write(Paths.get(fileLocation), content.getBytes(), StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            System.err.println("Failed to write content "+content);
        } catch (Exception e){
            System.err.println("Failed to write content in file "+content);
        }
    }

}
