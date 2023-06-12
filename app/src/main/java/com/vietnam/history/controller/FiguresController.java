package com.vietnam.history.controller;

import java.io.IOException;

import com.vietnam.history.App;
import com.vietnam.history.model.Figure;
import com.vietnam.history.model.collect.FigureCollection;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class FiguresController {

    private FigureCollection figureCollection = new FigureCollection();
    private ObservableList<Figure> allFigures = figureCollection.getFigures();

    @FXML
    private TableColumn<Figure, String> colFDescription;

    @FXML
    private TableColumn<Figure, String> colFName;

    @FXML
    private TableView<Figure> tblFigure;

    @FXML
    void aboutClick(ActionEvent event) throws IOException {
        App.openAbout("About");
    }

    @FXML
    void initialize() {
        colFDescription.setCellValueFactory(
            new PropertyValueFactory<Figure, String>("overview")
        );
        colFName.setCellValueFactory(
            new PropertyValueFactory<Figure, String>("label")
        );

        tblFigure.setItems(allFigures);
        tblFigure.setRowFactory(tableView -> {
            TableRow<Figure> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && (!row.isEmpty())){
                    Figure figure = row.getItem();
                    try {
                        App.setRootWithObject("FiguresDetailsScene", figure);
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            });
            return row;
        });
    }

}
