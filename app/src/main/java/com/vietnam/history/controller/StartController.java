package com.vietnam.history.controller;

import com.vietnam.history.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class StartController {
    @FXML
    void aboutClick(ActionEvent event) throws IOException {
        App.openAbout("About");
    }
}
