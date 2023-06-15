package com.vietnam.history.controller.event;

import com.vietnam.history.App;
import com.vietnam.history.model.HistoryEvent;
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
    private TableColumn<HistoryEvent, String> colFDescription;

    @FXML
    private TableColumn<HistoryEvent, String> colFName;

    @FXML
    private TableView<HistoryEvent> tblFigure;

    @FXML
    private Label totalNum;

    @FXML
    void aboutClick(ActionEvent event) throws IOException {
        App.openAbout("About");
    }

    @FXML
    void initialize() {
        totalNum.setText(Integer.toString(App.historyEvents.size()));

        colFDescription.setCellValueFactory(
            new PropertyValueFactory<HistoryEvent, String>("overview")
        );
        colFName.setCellValueFactory(
            new PropertyValueFactory<HistoryEvent, String>("label")
        );

        tblFigure.setItems(App.historyEvents);
        tblFigure.setRowFactory(tableView -> {
            TableRow<HistoryEvent> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && (!row.isEmpty())){
                    HistoryEvent historyEvent = row.getItem();
                    try {
                        App.setRootWithEntity("event/EventDetailsScene", historyEvent);
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            });
            return row;
        });
    }

}
