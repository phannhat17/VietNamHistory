package com.vietnam.history.controller;

import java.io.IOException;

import com.vietnam.history.App;
import com.vietnam.history.model.Dynasty;
import com.vietnam.history.model.collect.DynastyCollection;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class DynastyController {

    private DynastyCollection dynastyCollection = new DynastyCollection();
    private ObservableList<Dynasty> allDynasties = dynastyCollection.getDynasties();

    @FXML
    private TableColumn<Dynasty, String> colFDescription;

    @FXML
    private TableColumn<Dynasty, String> colFName;

    @FXML
    private TableView<Dynasty> tblFigure;

    @FXML
    void initialize() {
        assert colFDescription != null : "fx:id=\"colFDescription\" was not injected: check your FXML file 'FiguresScene.fxml'.";
        assert colFName != null : "fx:id=\"colFName\" was not injected: check your FXML file 'FiguresScene.fxml'.";
        assert tblFigure != null : "fx:id=\"tblFigure\" was not injected: check your FXML file 'FiguresScene.fxml'.";
        colFDescription.setCellValueFactory(
            new PropertyValueFactory<Dynasty, String>("overview")
        );
        colFName.setCellValueFactory(
            new PropertyValueFactory<Dynasty, String>("label")
        );

        tblFigure.setItems(allDynasties);
        tblFigure.setRowFactory(tableView -> {
            TableRow<Dynasty> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && (!row.isEmpty())){
                    Dynasty dynasty = row.getItem();
                    try {
                        App.setRootWithObject("DynastyDetailsScene", dynasty);
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            });
            return row;
        });
    }

}
