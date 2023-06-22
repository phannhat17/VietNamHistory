package com.vietnam.history.controller;

import com.vietnam.history.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

/**
 * Controller for the Sidebar
 */
public class SidebarController {

    private final String SCENE_NAME = "ListEntityScene";

    @FXML
    void returnStart(ActionEvent event) throws IOException {
        // Return to Start scene
        App.setRoot("StartScene");
    }

    @FXML
    void switchToDynasty(ActionEvent event) throws IOException {
        // Return to Dynasty scene
        App.setRootWithEntity(SCENE_NAME, App.dynasties, "TRIỀU ĐẠI LỊCH SỬ");
    }

    @FXML
    void switchToEvent(ActionEvent event) throws IOException {
        // Return to Event scene
        App.setRootWithEntity(SCENE_NAME, App.historicalEvents, "SỰ KIỆN LỊCH SỬ");
    }

    @FXML
    void switchToFestival(ActionEvent event) throws IOException {
        // Return to Festival scene
        App.setRootWithEntity(SCENE_NAME, App.festivals, "LỄ HỘI VĂN HOÁ");
    }

    @FXML
    void switchToPlace(ActionEvent event) throws IOException {
        // Return to Place scene
        App.setRootWithEntity(SCENE_NAME, App.places, "ĐỊA ĐIỂM, DI TÍCH");
    }

    @FXML
    void switchToHistoricalFigures(ActionEvent event) throws IOException {
        // Return to Figure scene
        App.setRootWithEntity(SCENE_NAME, App.figures, "NHÂN VẬT LỊCH SỬ");
    }

}
