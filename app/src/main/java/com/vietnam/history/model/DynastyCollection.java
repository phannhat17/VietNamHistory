package com.vietnam.history.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;

public class DynastyCollection {
    private static final String FOLDER_PATH = "C:\\Users\\f1rst\\OneDrive - opotato1\\Desktop\\VietNamHistory\\app\\src\\main\\java\\com\\vietnam\\history\\data\\dynasty";

    public ObservableList<Dynasty> getDynasties() {
        ObservableList<Dynasty> dynasties = FXCollections.observableArrayList();
        ObjectMapper objectMapper = new ObjectMapper();
        File folder = new File(FOLDER_PATH);

        if (folder.isDirectory()) {
            File[] files = folder.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".json")) {
                        try {
                            Dynasty dynasty = objectMapper.readValue(file, Dynasty.class);
                            dynasties.add(dynasty);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        return dynasties;
    }

    public static void main(String[] args) {
        DynastyCollection dynastyCollection = new DynastyCollection();
        ObservableList<Dynasty> allDynasties = dynastyCollection.getDynasties();

        // Use the list of dynasties as needed
        for (Dynasty dynasty : allDynasties) {
            System.out.println(dynasty.getLabel());
        }
    }
}
