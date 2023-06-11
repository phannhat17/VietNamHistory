package com.vietnam.history.controller;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class FiguresController {

    @FXML
    private TableColumn<?, ?> colFDescription;

    @FXML
    private TableColumn<?, ?> colFName;

    @FXML
    private TableView<?> tblFigure;

    @FXML
    void initialize() {
        assert colFDescription != null : "fx:id=\"colFDescription\" was not injected: check your FXML file 'FiguresScene.fxml'.";
        assert colFName != null : "fx:id=\"colFName\" was not injected: check your FXML file 'FiguresScene.fxml'.";
        assert tblFigure != null : "fx:id=\"tblFigure\" was not injected: check your FXML file 'FiguresScene.fxml'.";

    }

}
