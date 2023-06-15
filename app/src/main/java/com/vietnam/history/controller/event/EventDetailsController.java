package com.vietnam.history.controller.event;

import com.vietnam.history.App;
import com.vietnam.history.controller.DetailScene;
import com.vietnam.history.model.HistoryEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class EventDetailsController extends DetailScene<HistoryEvent> {

    @FXML
    void btnBackClick(ActionEvent event) throws IOException {
        App.setRoot("event/EventScene");
    }

    @Override
    public void setData(HistoryEvent historyEvent) {
        super.setData(historyEvent);
    }
}
