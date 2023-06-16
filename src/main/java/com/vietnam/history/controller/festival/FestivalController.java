package com.vietnam.history.controller.festival;

import com.vietnam.history.App;
import com.vietnam.history.model.Festival;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

public class FestivalController {

    @FXML
    private TableColumn<Festival, String> colFDescription;

    @FXML
    private TableColumn<Festival, String> colFName;

    @FXML
    private TableView<Festival> tblFigure;

    @FXML
    private Label totalNum;

    @FXML
    void aboutClick(ActionEvent event) throws IOException {
        App.openAbout("About");
    }

    @FXML
    void initialize() {
        totalNum.setText(Integer.toString(App.festivals.size()));

        colFDescription.setCellValueFactory(
            new PropertyValueFactory<Festival, String>("overview")
        );
        colFName.setCellValueFactory(
            new PropertyValueFactory<Festival, String>("label")
        );

        tblFigure.setItems(App.festivals);
        tblFigure.setRowFactory(tableView -> {
            TableRow<Festival> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && (!row.isEmpty())){
                    Festival festival = row.getItem();
                    try {
                        App.setRootWithEntity("festival/FestivalDetailsScene", festival);
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            });
            return row;
        });
    }

}
