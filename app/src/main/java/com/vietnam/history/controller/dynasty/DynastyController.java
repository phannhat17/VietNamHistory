package com.vietnam.history.controller.dynasty;

import com.vietnam.history.App;
import com.vietnam.history.model.Dynasty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

public class DynastyController {

    @FXML
    private TableColumn<Dynasty, String> colFDescription;

    @FXML
    private TableColumn<Dynasty, String> colFName;

    @FXML
    private TableView<Dynasty> tblFigure;

    @FXML
    private Label totalNum;

    @FXML
    void aboutClick(ActionEvent event) throws IOException {
        App.openAbout("About");
    }

    @FXML
    void initialize() {
        totalNum.setText(Integer.toString(App.dynasties.size()));

        colFDescription.setCellValueFactory(
            new PropertyValueFactory<Dynasty, String>("overview")
        );
        colFName.setCellValueFactory(
            new PropertyValueFactory<Dynasty, String>("label")
        );

        tblFigure.setItems(App.dynasties);

        tblFigure.setRowFactory(tableView -> {
            TableRow<Dynasty> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && (!row.isEmpty())){
                    Dynasty dynasty = row.getItem();
                    try {
                        App.setRootWithEntity("dynasty/DynastyDetailsScene", dynasty);
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            });
            return row;
        });
    }

}
