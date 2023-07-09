package com.vietnam.history.gui.controller;

import com.vietnam.history.gui.App;
import com.vietnam.history.gui.model.HistoricalEntity;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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

        // Create a filtered list based on the entity list
        FilteredList<T> filteredList = new FilteredList<>(entityList);

        // Bind the filter predicate to the text property of tfFilter
        tfFilter.textProperty().addListener((observable, oldValue, newValue) ->
                filteredList.setPredicate(entity -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true; // Display all entities when filter is empty
                    }
                    String filterText = newValue.toLowerCase();
                    String entityText = entity.getLabel().toLowerCase();
                    if (entity.getAliases() != null) {
                        entityText += " " + String.join(" ", entity.getAliases()).toLowerCase();
                    }
                    return entityText.contains(filterText); // Show entities that have the filter text in their label or aliases
                }));

        // Create a sorted list based on the filtered list
        SortedList<T> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(tblFigure.comparatorProperty());

        // Set the entity type label
        entityType.setText(type);

        // Display the total number of entities in the table
        totalNum.setText(Integer.toString(entityList.size()));

        // Set the items to display in the table
        tblFigure.setItems(sortedList);

        // Configure the columns to display the entity label aka name and overview
        colFID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFDescription.setCellValueFactory(cellData -> {
            T entity = cellData.getValue();
            String description = entity.getOverview();
            if (description == null || description.isEmpty()) {
                description = entity.getDescription();
            }
            return new SimpleStringProperty(description);
        });
        colFName.setCellValueFactory(new PropertyValueFactory<>("label"));

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
    }
}