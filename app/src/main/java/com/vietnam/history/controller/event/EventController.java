package com.vietnam.history.controller.event;

import com.vietnam.history.App;
import com.vietnam.history.model.HistoricalEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

public class EventController {
    @FXML
    private TableColumn<HistoricalEvent, String> colFDescription;

    @FXML
    private TableColumn<HistoricalEvent, String> colFName;

    @FXML
    private TableView<HistoricalEvent> tblFigure;

    @FXML
    private Label totalNum;

    @FXML
    void aboutClick(ActionEvent event) throws IOException {
        App.openAbout("About");
    }

    @FXML
    void initialize() {
        totalNum.setText(Integer.toString(App.historicalEvents.size()));

        colFDescription.setCellValueFactory(
            new PropertyValueFactory<HistoricalEvent, String>("overview")
        );
        colFName.setCellValueFactory(
            new PropertyValueFactory<HistoricalEvent, String>("label")
        );

        tblFigure.setItems(App.historicalEvents);
        tblFigure.setRowFactory(tableView -> {
            TableRow<HistoricalEvent> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && (!row.isEmpty())){
                    HistoricalEvent historicalEvent = row.getItem();
                    try {
                        App.setRootWithEntity("event/EventDetailsScene", historicalEvent);
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            });
            return row;
        });
    }

}
