package com.vietnam.history.controller.figure;

import com.vietnam.history.App;
import com.vietnam.history.controller.DetailScene;
import com.vietnam.history.model.Figure;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class FiguresDetailsController extends DetailScene<Figure> {

    @FXML
    void btnBackClick(ActionEvent event) throws IOException {
        App.setRoot("figure/FiguresScene");
    }

    @Override
    public void setData(Figure figure) {
        super.setData(figure);
    }
}
