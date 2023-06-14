package com.vietnam.history.controller.figure;

import com.vietnam.history.App;
import com.vietnam.history.model.Figure;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

public class FiguresController {

    @FXML
    private TableColumn<Figure, String> colFDescription;

    @FXML
    private TableColumn<Figure, String> colFName;

    @FXML
    private TableView<Figure> tblFigure;

    @FXML
    private Label totalNum;

    @FXML
    void aboutClick(ActionEvent event) throws IOException {
        App.openAbout("About");
    }

    @FXML
    void initialize() {
        totalNum.setText(Integer.toString(App.figures.size()));

        colFDescription.setCellValueFactory(
            new PropertyValueFactory<Figure, String>("overview")
        );
        colFName.setCellValueFactory(
            new PropertyValueFactory<Figure, String>("label")
        );

        tblFigure.setItems(App.figures);
        tblFigure.setRowFactory(tableView -> {
            TableRow<Figure> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && (!row.isEmpty())){
                    Figure figure = row.getItem();
                    try {
                        App.setRootWithObject("figure/FiguresDetailsScene", figure);
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            });
            return row;
        });
    }

}
