package com.vietnam.history.controller.dynasty;

import com.vietnam.history.App;
import com.vietnam.history.controller.DetailScene;
import com.vietnam.history.model.Dynasty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;


public class DynastyDetailsController extends DetailScene<Dynasty> {

    @FXML
    void btnBackClick(ActionEvent event) throws IOException {
        App.setRoot("dynasty/DynastyScene");
    }

    @Override
    public void setData(Dynasty dynasty) {
        super.setData(dynasty);
    }
}
