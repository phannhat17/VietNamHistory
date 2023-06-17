package com.vietnam.history.controller;

import com.vietnam.history.App;
import com.vietnam.history.model.HistoricalEntity;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

public class ListEntityScene<T extends HistoricalEntity> {

    @FXML
    private Label entityType;

    @FXML
    private TableColumn<T, String> colFDescription;

    @FXML
    private TableColumn<T, String> colFName;

    @FXML
    private TableView<T> tblFigure;

    @FXML
    private Label totalNum;

    @FXML
    private TextField tfFilter;

    @FXML
    void aboutClick(ActionEvent event) throws IOException {
        App.openAbout("About");
    }


    public void setData(ObservableList<T> entityList, String type) {

        entityType.setText(type);

        totalNum.setText(Integer.toString(entityList.size()));
        tblFigure.setItems(entityList);

        colFDescription.setCellValueFactory(new PropertyValueFactory<T, String>("overview"));
        colFName.setCellValueFactory(new PropertyValueFactory<T, String>("label"));


        tblFigure.setRowFactory(tableView -> {
            TableRow<T> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    HistoricalEntity entity = row.getItem();
                    try {
                        App.setRootWithEntity("DetailScene", entity);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            return row;
        });

        FilteredList<T> filteredList = new FilteredList<>(entityList, p -> true);

        tfFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(entity -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String filterText = newValue.toLowerCase();
                return entity.getLabel().toLowerCase().contains(filterText);
            });
        });
        tblFigure.setItems(filteredList);

    }
}