package com.vietnam.history.gui.controller;

import com.vietnam.history.gui.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MainController {

	public VBox sideBar;

	//  Handle the click event on the "About" menu
    @FXML
    void aboutClick(ActionEvent event) throws IOException {
        App.openAbout("About");
    }

}
