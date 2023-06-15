package com.vietnam.history.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.vietnam.history.App;
import com.vietnam.history.model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public abstract class DetailScene<T extends HistoricalEntity> {

    @FXML
    private Label lbName;

    @FXML
    private Text tOver;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox claimsContainer;

    @FXML
    void aboutClick(ActionEvent event) throws IOException {
        App.openAbout("About");
    }

    public void setData(T entity) {
        lbName.setText(entity.getLabel());
        tOver.setText(entity.getOverview());

        // Create a VBox to contain the HBox containers
        claimsContainer = new VBox();
        scrollPane.setContent(claimsContainer);

        JsonNode claims = entity.getClaims();
       JsonNode refs = entity.getReferences();

        processData("THÔNG TIN", claims, claimsContainer);
        processData("LIÊN QUAN", refs, claimsContainer);
    }

    private void processData(String type, JsonNode claimsNode, VBox vbox) {

        VBox claimsContainer = new VBox(); // Create a new VBox for the claimsContainer

        Label claimsLabel = new Label(type);
        claimsLabel.setStyle("-fx-font-size: 20px;-fx-padding: 10px 10px 10px 10px; -fx-font-weight: bold;-fx-text-fill: #4b867e");
        claimsContainer.getChildren().add(claimsLabel);

        if (claimsNode == null) {
            Label nullLabel = new Label("Chưa có thông tin");
            nullLabel.setStyle("-fx-font-size: 16px;-fx-padding: 10px 10px 0px 10px; -fx-font-weight: bold;-fx-text-fill: red");
            claimsContainer.getChildren().add(nullLabel);
            vbox.getChildren().add(claimsContainer);
            return;
        }
        Iterator<Map.Entry<String, JsonNode>> properties = claimsNode.fields();
        while (properties.hasNext()) {
            Map.Entry<String, JsonNode> property = properties.next();
            String propertyName = StringUtils.capitalize(property.getKey());
            Label keyLabel = new Label(propertyName + ":");
            keyLabel.setPrefWidth(250);
            keyLabel.setWrapText(true);
            keyLabel.setStyle("-fx-font-size: 16px;-fx-padding: 0px 10px 0px 0px; -fx-font-weight: bold;");

            JsonNode propertyArr = property.getValue();

            TextFlow valueTextFlow = new TextFlow(); // Use TextFlow to allow for styling individual Text nodes

            int count = 0;
            for (JsonNode propertyDetail : propertyArr) {
                if (count > 0) {
                    valueTextFlow.getChildren().add(new Text(", \n"));
                }

                String value = propertyDetail.get("value").asText();
                Text valueText = new Text(value);
                if (propertyDetail.get("id")!= null) {
                    valueText.setFill(Color.web("#3498db"));
                    valueText.setOnMouseClicked(mouseEvent -> {
                        String id = propertyDetail.get("id").asText();
                        HistoricalEntity obj = getObjectById(id);
                        try {
                            App.setRootWithEntity("ReferenceDetail", obj);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
                valueTextFlow.getChildren().add(valueText);

                if (propertyDetail.has("qualifiers")) {
                    valueTextFlow.getChildren().add(new Text("("));
                    JsonNode qualifiersObj = propertyDetail.get("qualifiers");
                    Iterator<Map.Entry<String, JsonNode>> qualifierKeys = qualifiersObj.fields();

                    TextFlow qualifierTextFlow = new TextFlow(); // Use TextFlow to allow for styling individual Text nodes within qualifiers

                    int subCount = 0;
                    while (qualifierKeys.hasNext()) {
                        if (subCount != 0) {
                            qualifierTextFlow.getChildren().add(new Text(", \n"));
                        }
                        Map.Entry<String, JsonNode> qualifierProperty = qualifierKeys.next();
                        String qualifierPropertyName = qualifierProperty.getKey();
                        qualifierTextFlow.getChildren().add(new Text(qualifierPropertyName + ": "));
                        JsonNode qualifierPropertyArr = qualifierProperty.getValue();
                        int subSubCount = 0;
                        for (JsonNode ele : qualifierPropertyArr) {
                            if (subSubCount != 0) {
                                qualifierTextFlow.getChildren().add(new Text(", \n"));
                            }
                            String subQualifierPropertyValue = ele.get("value").asText();
                            Text subQualifierPropertyValueText = new Text(subQualifierPropertyValue);
                            if (ele.get("id")!=null) {
                                subQualifierPropertyValueText.setFill(Color.web("#3498db"));
                                subQualifierPropertyValueText.setOnMouseClicked(mouseEvent -> {
                                    String id = ele.get("id").asText();
                                    HistoricalEntity obj = getObjectById(id);
                                    try {
                                        App.setRootWithEntity("ReferenceDetail", obj);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                            }
                            qualifierTextFlow.getChildren().add(subQualifierPropertyValueText);
                            subSubCount++;
                        }
                        subCount++;
                    }
                    valueTextFlow.getChildren().add(qualifierTextFlow); // Add the qualifierTextFlow TextFlow to the valueTextFlow TextFlow
                    valueTextFlow.getChildren().add(new Text(")"));
                }
                count++;
            }

            valueTextFlow.setStyle("-fx-font-size: 16px;");
            HBox keyValuePair = new HBox();
            keyValuePair.getChildren().addAll(keyLabel, valueTextFlow);
            keyValuePair.setStyle("-fx-padding: 10px 0px 0px 10px");
            // Add the key-value pair HBox container to the VBox container
            claimsContainer.getChildren().add(keyValuePair);
        }

        vbox.getChildren().add(claimsContainer); // Add the claimsContainer VBox to the main VBox (vbox)
    }

    private HistoricalEntity getObjectById(String id) {
        // Search through the appropriate list of objects for the object with the matching id
        for (Dynasty dynasty : App.dynasties) {
            if (dynasty.getId().equals(id)) {
                return dynasty;
            }
        }
        for (Figure figure : App.figures) {
            if (figure.getId().equals(id)) {
                return figure;
            }
        }
        for (HistoricalEvent event : App.historicalEvents) {
            if (event.getId().equals(id)) {
                return event;
            }
        }
        for (Festival festival : App.festivals) {
            if (festival.getId().equals(id)) {
                return festival;
            }
        }
        for (Place place : App.places) {
            if (place.getId().equals(id)) {
                return place;
            }
        }
        return null; // Object with the given id not found
    }

}
