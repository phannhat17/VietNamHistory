package com.vietnam.history.controller.event;

import com.vietnam.history.App;
import com.vietnam.history.controller.DetailScene;
import com.vietnam.history.model.HistoricalEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class EventDetailsController extends DetailScene<HistoricalEvent> {

    @FXML
    void btnBackClick(ActionEvent event) throws IOException {
        App.setRoot("event/EventScene");
    }

    @Override
    public void setData(HistoricalEvent historicalEvent) {
        super.setData(historicalEvent);
    }
}
