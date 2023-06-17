package com.vietnam.history.controller;

import com.vietnam.history.App;
import com.vietnam.history.model.HistoricalEntity;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ListEntityScene<T extends HistoricalEntity> {

    public VBox sideBar;
    public Label entityType;
    public HBox searchBox;
    @FXML
    private TableColumn<T, String> colFDescription;

    @FXML
    private TableColumn<T, String> colFName;

    @FXML
    private TableView<T> tblFigure;

    @FXML
    private Label totalNum;

    @FXML
    private  SearchController searchController;

    @FXML
    void aboutClick(ActionEvent event) throws IOException {
        App.openAbout("About");
    }


    public void setData(ObservableList<T> entityList, String type) {
//        searchController.healthCheck();
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
    }
}