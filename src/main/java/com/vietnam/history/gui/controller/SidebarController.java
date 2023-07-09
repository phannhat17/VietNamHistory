package com.vietnam.history.gui.controller;

import com.vietnam.history.gui.App;
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
        App.setRootWithEntity(SCENE_NAME, App.DYNASTIES, "TRIỀU ĐẠI LỊCH SỬ");
    }

    @FXML
    void switchToEvent(ActionEvent event) throws IOException {
        // Return to Event scene
        App.setRootWithEntity(SCENE_NAME, App.HISTORICAL_EVENTS, "SỰ KIỆN LỊCH SỬ");
    }

    @FXML
    void switchToFestival(ActionEvent event) throws IOException {
        // Return to Festival scene
        App.setRootWithEntity(SCENE_NAME, App.FESTIVALS, "LỄ HỘI VĂN HOÁ");
    }

    @FXML
    void switchToLandmark(ActionEvent event) throws IOException {
        // Return to Landmark scene
        App.setRootWithEntity(SCENE_NAME, App.LANDMARKS, "ĐỊA ĐIỂM, DI TÍCH");
    }

    @FXML
    void switchToHistoricalFigures(ActionEvent event) throws IOException {
        // Return to Figure scene
        App.setRootWithEntity(SCENE_NAME, App.FIGURES, "NHÂN VẬT LỊCH SỬ");
    }

}
