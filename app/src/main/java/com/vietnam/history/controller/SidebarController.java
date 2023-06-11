package com.vietnam.history.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import com.vietnam.history.App;

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
        App.setRoot("DynastyScene");
    }

    @FXML
    void switchToEvent(ActionEvent event) throws IOException {
        // Return to Event scene
        App.setRoot("EventScene");
    }

    @FXML
    void switchToFestival(ActionEvent event) throws IOException {
        // Return to Festival scene
        App.setRoot("FestivalScene");
    }

    @FXML
    void switchToHistoricalFigures(ActionEvent event) throws IOException {
        App.setRoot("FiguresScene");
    }

    @FXML
    void initialize() {

    }

}
