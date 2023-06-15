package com.vietnam.history.controller.festival;

import com.vietnam.history.App;
import com.vietnam.history.controller.DetailScene;
import com.vietnam.history.model.Festival;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class FestivalDetailsController extends DetailScene<Festival> {

    @FXML
    void btnBackClick(ActionEvent event) throws IOException {
        App.setRoot("festival/FestivalScene");
    }

    @Override
    public void setData(Festival festival) {
        super.setData(festival);
    }
}