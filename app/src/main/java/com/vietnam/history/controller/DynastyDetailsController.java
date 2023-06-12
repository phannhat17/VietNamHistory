package com.vietnam.history.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.vietnam.history.App;
import com.vietnam.history.model.Dynasty;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class DynastyDetailsController {

    @FXML
    private Button btnBack;

    @FXML
    private Label lbName;
    @FXML
    private Label lbName1;

    @FXML
    private Text tOver;
    @FXML
    private ScrollPane scrollPane;

    private VBox claimsContainer;

    @FXML
    void aboutClick(ActionEvent event) throws IOException {
        App.openAbout("About");
    }

    @FXML
    void btnBackClick(ActionEvent event) throws IOException {
        App.setRoot("DynastyScene");
    }

    public void setData(Dynasty dynasty) {
        lbName.setText(dynasty.getLabel());
        tOver.setText(dynasty.getOverview());
        // Create a VBox to contain the HBox containers
        claimsContainer = new VBox();
        scrollPane.setContent(claimsContainer);

        // Assuming you have an instance of Dynasty with the retrieved data
        Map<String, Object> claims = dynasty.getClaims();

        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // Get the value string
            String valueString = getValueString(value);

            // Create a label to display the key
            Label keyLabel = new Label(key + ":");
            keyLabel.setStyle("-fx-font-size: 14px;-fx-padding: 0px 10px 0px 0px; -fx-font-weight: bold;");

            // Create a text node to display the value string
            Text valueText = new Text(valueString);
            valueText.setStyle("-fx-font-size: 14px;");

            // Create an HBox container to hold the label and text nodes
            HBox keyValuePair = new HBox();
            keyValuePair.getChildren().addAll(keyLabel, valueText);
            keyValuePair.setStyle("-fx-padding: 10px 0px 0px 10px");
            // Add the key-value pair HBox container to the VBox container
            claimsContainer.getChildren().add(keyValuePair);
        }
    }

    private static String getValueString(Object value) {
        if (value instanceof List) {
            // If the value is a list, recursively process each element and concatenate the
            // results
            List<?> list = (List<?>) value;
            StringBuilder sb = new StringBuilder();
            for (Object item : list) {
                sb.append(getValueString(item));
            }
            return sb.toString();
        } else if (value instanceof Map) {
            // If the value is a map, recursively process each entry and concatenate the
            // results
            Map<?, ?> map = (Map<?, ?>) value;
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                Object entryValue = entry.getValue();
                if (entryValue instanceof Map || entryValue instanceof List) {
                    // If the entry value is a complex object, recursively process it
                    if (!entry.getKey().equals("qualifiers")) {
                        sb.append(entry.getKey()).append(": ");
                        sb.append(getValueString(entryValue)).append("\n");
                    }
                } else if (entry.getKey().equals("value")) {
                    // If the key is "value", append the value to the string
                    sb.append(entryValue).append("; ");
                }
            }
            return sb.toString();
        } else {
            // If the value is a simple object, just call its toString() method
            return value.toString();
        }
    }


}
