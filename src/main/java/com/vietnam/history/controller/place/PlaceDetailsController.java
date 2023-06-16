package com.vietnam.history.controller.place;

import com.vietnam.history.App;
import com.vietnam.history.controller.DetailScene;
import com.vietnam.history.model.Place;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class PlaceDetailsController extends DetailScene<Place> {

    @FXML
    void btnBackClick(ActionEvent event) throws IOException {
        App.setRoot("place/PlaceScene");
    }

    @Override
    public void setData(Place place) {
        super.setData(place);
    }
}
