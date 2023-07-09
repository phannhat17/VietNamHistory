package com.vietnam.history.gui.loader;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.vietnam.history.gui.model.HistoricalEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A base class for loading data from JSON files.
 *
 * @param <T> the type of object to load
 */
public abstract class DataLoader<T extends HistoricalEntity> {
    private final String folderPath;

    /**
     * Constructs a new {@code DataLoader} with the specified folder path.
     *
     * @param folderPath the path to the folder containing the JSON files
     */
    protected DataLoader(String folderPath) {
        this.folderPath = folderPath;
    }

    /**
     * Loads the data from the JSON files and returns an observable list of objects.
     *
     * @return an observable list of objects
     */
    public ObservableList<T> loadData() {
        ObservableList<T> list = FXCollections.observableArrayList();

        ObjectReader reader = new ObjectMapper()
                .configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true)
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

    /**
     * Returns the class of the objects to load.
     *
     * @return the class of the objects to load
     */
    protected abstract Class<T> getType();
}
