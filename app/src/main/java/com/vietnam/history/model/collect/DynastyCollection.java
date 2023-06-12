package com.vietnam.history.model.collect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietnam.history.model.Dynasty;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;

public class DynastyCollection {
    private static final String FOLDER_PATH = "src/data/dynasty";

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
}
