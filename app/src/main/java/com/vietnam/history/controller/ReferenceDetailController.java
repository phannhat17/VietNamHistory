package com.vietnam.history.controller;

import com.vietnam.history.App;
import com.vietnam.history.model.HistoricalEntity;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class ReferenceDetailController extends DetailScene<HistoricalEntity> {

    @FXML
    void btnBackClick(ActionEvent event) throws IOException {
        App.setRoot("StartScene");
    }

    @Override
    public void setData(HistoricalEntity historicalEntity) {
        super.setData(historicalEntity);
    }
}
