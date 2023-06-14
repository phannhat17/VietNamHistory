package com.vietnam.history.controller;

import com.vietnam.history.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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
        App.setRoot("dynasty/DynastyScene");
    }

    @FXML
    void switchToEvent(ActionEvent event) throws IOException {
        // Return to Event scene
        App.setRoot("event/EventScene");
    }

    @FXML
    void switchToFestival(ActionEvent event) throws IOException {
        // Return to Festival scene
        App.setRoot("festival/FestivalScene");
    }

    @FXML
    void switchToPlace(ActionEvent event) throws IOException {
        // Return to Place scene
        App.setRoot("place/PlaceScene");
    }

    @FXML
    void switchToHistoricalFigures(ActionEvent event) throws IOException {
        App.setRoot("figure/FiguresScene");
    }

}
