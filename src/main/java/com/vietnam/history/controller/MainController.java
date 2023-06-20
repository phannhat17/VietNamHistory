package com.vietnam.history.controller;

import com.vietnam.history.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class MainController {

    //  Handle the click event on the "About" menu
    @FXML
    void aboutClick(ActionEvent event) throws IOException {
        App.openAbout("About");
    }

}
