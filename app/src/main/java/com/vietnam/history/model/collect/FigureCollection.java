package com.vietnam.history.model.collect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietnam.history.model.Figure;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;

public class FigureCollection {
    private static final String FOLDER_PATH = "src/data/historical-figures";

    public ObservableList<Figure> getFigures() {
        ObservableList<Figure> figures = FXCollections.observableArrayList();
        ObjectMapper objectMapper = new ObjectMapper();
        File folder = new File(FOLDER_PATH);

        if (folder.isDirectory()) {
            File[] files = folder.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".json")) {
                        try {
                            Figure figure = objectMapper.readValue(file, Figure.class);
                            figures.add(figure);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return figures;
    }
}
