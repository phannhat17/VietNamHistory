package com.vietnam.history.controller;

import java.io.IOException;

import com.vietnam.history.App;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class StartController {
    @FXML
    void aboutClick(ActionEvent event) throws IOException {
        App.openAbout("About");
    }
}
