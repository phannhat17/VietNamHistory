package com.vietnam.history.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class SearchController {

    @FXML
    private TextField tfFilter;

    @FXML
    void initialize() {
        assert tfFilter != null : "fx:id=\"tfFilter\" was not injected: check your FXML file 'SearchBox.fxml'.";

        tfFilter.textProperty().addListener(
            new ChangeListener<String>() {

                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    showFilteredMedia(newValue);
                }
            }
        );
    }

    public void showFilteredMedia(String keyword) {

        }
}
