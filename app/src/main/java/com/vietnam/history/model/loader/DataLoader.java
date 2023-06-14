package com.vietnam.history.model.loader;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class DataLoader<T> {
    private final String folderPath;

    public DataLoader(String folderPath) {
        this.folderPath = folderPath;
    }

    public ObservableList<T> loadData() {
        ObservableList<T> list = FXCollections.observableArrayList();

        ObjectReader reader = new ObjectMapper()
                .configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false)
                .configure(JsonParser.Feature.STRICT_DUPLICATE_DETECTION, true)
                .readerFor(getType());

        File folder = new File(folderPath);

        if (folder.isDirectory()) {
            File[] files = folder.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".json")) {
                        try (InputStream in = new FileInputStream(file)) {
                            T obj = reader.readValue(in);
                            list.add(obj);
                        } catch (IOException e) {
                            System.err.println("Error reading file " + file.getName() + ": " + e.getMessage());
                        }
                    }
                }
            }
        }

        return list;
    }

    protected abstract Class<T> getType();
}
