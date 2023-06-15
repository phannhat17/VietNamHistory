package com.vietnam.history.controller.place;

import com.vietnam.history.App;
import com.vietnam.history.model.Place;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

public class PlaceController {

    @FXML
    private TableColumn<Place, String> colFDescription;

    @FXML
    private TableColumn<Place, String> colFName;

    @FXML
    private TableView<Place> tblFigure;

    @FXML
    private Label totalNum;

    @FXML
    void aboutClick(ActionEvent event) throws IOException {
        App.openAbout("About");
    }

    @FXML
    void initialize() {
        totalNum.setText(Integer.toString(App.places.size()));

        colFDescription.setCellValueFactory(
            new PropertyValueFactory<Place, String>("overview")
        );
        colFName.setCellValueFactory(
            new PropertyValueFactory<Place, String>("label")
        );

        tblFigure.setItems(App.places);
        tblFigure.setRowFactory(tableView -> {
            TableRow<Place> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && (!row.isEmpty())){
                    Place place = row.getItem();
                    try {
                        App.setRootWithEntity("place/PlaceDetailsScene", place);
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            });
            return row;
        });
    }

}
