package com.vietnam.history.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.vietnam.history.App;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * Controller for the Sidebar
 */
public class SidebarController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    void returnStart(ActionEvent event) throws IOException {
        // Return to Start scene
        App.setRoot("StartScene");
    }

    @FXML
    void switchToDynasty(ActionEvent event) throws IOException {
        // Return to Dynasty scene
        App.setRootWithEntity("ListEntityScene", App.dynasties, "TRIỀU ĐẠI LỊCH SỬ");
    }

    @FXML
    void switchToEvent(ActionEvent event) throws IOException {
        // Return to Event scene
        App.setRootWithEntity("ListEntityScene", App.historicalEvents, "SỰ KIỆN LỊCH SỬ");
    }

    @FXML
    void switchToFestival(ActionEvent event) throws IOException {
        // Return to Festival scene
        App.setRootWithEntity("ListEntityScene", App.festivals, "LỄ HỘI VĂN HOÁ");
    }

    @FXML
    void switchToPlace(ActionEvent event) throws IOException {
        // Return to Place scene
        App.setRootWithEntity("ListEntityScene", App.places, "ĐỊA ĐIỂM");
    }

    @FXML
    void switchToHistoricalFigures(ActionEvent event) throws IOException {
        // Return to Figure scene
        App.setRootWithEntity("ListEntityScene", App.figures, "NHÂN VẬT LỊCH SỬ");
    }

}
