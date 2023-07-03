package com.vietnam.history.controller;

import com.vietnam.history.App;
import com.vietnam.history.model.HistoricalEntity;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

public class ListEntityScene<T extends HistoricalEntity> extends MainController {

    @FXML
    private Label entityType;

    @FXML
    private TableColumn<T, String> colFID;

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

    /**
     * Sets the root of the scene to the specified FXML file.
     *
     * @param entityList the list of entity
     * @param type the type of that entity for display
     */
    public void setData(ObservableList<T> entityList, String type) {

        // Set the entity type label
        entityType.setText(type);

        // Display the total number of entities in the table
        totalNum.setText(Integer.toString(entityList.size()));

        // Set the items to display in the table
        tblFigure.setItems(entityList);

        // Configure the columns to display the entity label aka name and overview
        colFID.setCellValueFactory(new PropertyValueFactory<T, String>("id"));
        colFDescription.setCellValueFactory(new PropertyValueFactory<T, String>("overview"));
        colFName.setCellValueFactory(new PropertyValueFactory<T, String>("label"));

        // Set the behavior for double-clicking a row to open the entity's detail view
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

        // Update the filtered list when the filter text changes
        tfFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(entity -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true; // If the filter text is empty, show all entities
                }

                String filterText = newValue.toLowerCase();
                return entity.getLabel().toLowerCase().contains(filterText); // Show entities that its label contains the filter text
            });
        });
        tblFigure.setItems(filteredList); // Update the table to display the filtered entities

    }
}