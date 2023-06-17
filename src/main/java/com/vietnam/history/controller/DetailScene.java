package com.vietnam.history.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.vietnam.history.App;
import com.vietnam.history.model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
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
import java.util.List;
import java.util.Map;

public class DetailScene {

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
        // Open the "About" page
        App.openAbout("About");
    }

    /**
     * Set the data of the detail scene with the given entity.
     *
     * @param entity the entity to display the data for
     */
    public void setData(HistoricalEntity entity) {

        // Set the name and overview text
        lbName.setText(entity.getLabel());
        tOver.setText(entity.getOverview());

        // Create a VBox to contain the HBox containers
        claimsContainer = new VBox();
        scrollPane.setContent(claimsContainer);

        // Process information and references
        JsonNode claims = entity.getClaims();
        JsonNode refs = entity.getReferences();

        processData("THÔNG TIN", claims, claimsContainer, entity);
        processData("LIÊN QUAN", refs, claimsContainer);
    }

    /**
     * Processes and displays data of a given type in a VBox container.
     *
     * @param type          the type of data (either "information" or "reference")
     * @param jsonNode      the JSON node containing the data
     * @param vbox          the VBox container to display the processed data
     */
    private void processData(String type, JsonNode jsonNode, VBox vbox) {
        processData(type, jsonNode, vbox, null);
    }

    /**
     * Processes and displays data of a given type and entity in a VBox container.
     *
     * @param type          the type of data (either "information" or "reference")
     * @param jsonNode      the JSON node containing the data
     * @param vbox          the VBox container to display the processed data
     * @param entity        the entity associated with the data (optional)
     */
    private void processData(String type, JsonNode jsonNode, VBox vbox, HistoricalEntity entity) {

        VBox claimsContainer = new VBox(); // Create a new VBox for the claimsContainer

        // Label for "THÔNG TIN" or "LIÊN QUAN"
        if (type != null) {
            Label claimsLabel = new Label(type);
            claimsLabel.setStyle("-fx-font-size: 20px;-fx-padding: 10px 10px 10px 10px; -fx-font-weight: bold;-fx-text-fill: #4b867e");
            claimsContainer.getChildren().add(claimsLabel);
        }

        // Display entity-specific information if an entity is provided
        if (entity != null) {
            // Add the description
            Label descriptionLabel = new Label("Mô tả ngắn:");
            descriptionLabel.setPrefWidth(300);
            descriptionLabel.setWrapText(true);
            descriptionLabel.setStyle("-fx-font-size: 16px;-fx-padding: 0px 10px 0px 0px; -fx-font-weight: bold;");
            Text descriptionText = new Text(entity.getDescription());
            descriptionText.setWrappingWidth(700);
            descriptionText.setStyle("-fx-font-size: 16px;");
            HBox descriptionH = new HBox();
            descriptionH.getChildren().addAll(descriptionLabel, descriptionText);
            descriptionH.setStyle("-fx-padding: 10px 0px 0px 10px");
            claimsContainer.getChildren().add(descriptionH);

            // Add aliases
            Label aliasLabel = new Label("Tên gọi khác:");
            aliasLabel.setPrefWidth(300);
            aliasLabel.setWrapText(true);
            aliasLabel.setStyle("-fx-font-size: 16px;-fx-padding: 0px 10px 0px 0px; -fx-font-weight: bold;");
            List<String> aliases = entity.getAliases();
            String aliasTextString = String.join(", \n", aliases);
            Text aliasText = new Text(aliasTextString);
            aliasText.setStyle("-fx-font-size: 16px;");
            HBox aliasH = new HBox();
            aliasH.getChildren().addAll(aliasLabel, aliasText);
            aliasH.setStyle("-fx-padding: 10px 0px 0px 10px");
            claimsContainer.getChildren().add(aliasH);
        }

        // No information yet
        if (jsonNode == null) {
            Label nullLabel = new Label("Chưa có thông tin");
            nullLabel.setStyle("-fx-font-size: 16px;-fx-padding: 10px 10px 0px 10px; -fx-font-weight: bold;-fx-text-fill: red");
            claimsContainer.getChildren().add(nullLabel);
            vbox.getChildren().add(claimsContainer);
            return;
        }

        Iterator<Map.Entry<String, JsonNode>> properties = jsonNode.fields();
        while (properties.hasNext()) {
            Map.Entry<String, JsonNode> property = properties.next();
            String propertyName = StringUtils.capitalize(property.getKey());
            // Add label for holding key value
            Label keyLabel = new Label(propertyName + ":");
            keyLabel.setPrefWidth(300);
            keyLabel.setWrapText(true);
            keyLabel.setStyle("-fx-font-size: 16px;-fx-padding: 0px 10px 0px 0px; -fx-font-weight: bold;");

            JsonNode propertyArr = property.getValue();

            TextFlow valueTextFlow = new TextFlow(); // Use TextFlow for styling individual Text nodes

            int count = 0;
            for (JsonNode propertyDetail : propertyArr) {
                if (count > 0) {
                    valueTextFlow.getChildren().add(new Text("\n"));
                }


                String value = propertyDetail.get("value").asText();
                Text valueText = new Text(value);
                // If there is an ID, it proves to be associated with another entity
                if (propertyDetail.get("id")!= null) {
                    valueText.setStyle("-fx-underline: true;");
                    valueText.setCursor(Cursor.HAND);
                    valueText.setFill(Color.web("#056df5")); // Set color and underline
                    valueText.setOnMouseClicked(mouseEvent -> { // Click to go to another entity
                        String id = propertyDetail.get("id").asText();
                        HistoricalEntity obj = getObjectById(id);
                        try {
                            App.setRootWithEntity("DetailScene", obj);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
                valueTextFlow.getChildren().add(valueText);

                if (propertyDetail.has("qualifiers") && propertyDetail.has("source")) {
                    valueTextFlow.getChildren().add(new Text(" ("));
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
                            qualifierTextFlow.getChildren().add(subQualifierPropertyValueText);
                            subSubCount++;
                        }
                        subCount++;
                    }
                    qualifierTextFlow.getChildren().add(new Text(")"));

                    String source = propertyDetail.get("source").asText();
                    if (!source.equals("both")) {
                        Text sourceText = new Text(" (Nguồn: " + source + ")");
                        sourceText.setFill(Color.web("#3498db"));
                        qualifierTextFlow.getChildren().add(sourceText);

                    }

                    valueTextFlow.getChildren().add(qualifierTextFlow); // Add the qualifierTextFlow TextFlow to the valueTextFlow TextFlow
                } else if (!propertyDetail.has("qualifiers") && propertyDetail.has("source")) {
                    String source = propertyDetail.get("source").asText();
                    if (!source.equals("both")) {
                        Text sourceText = new Text(" (Nguồn: " + source + ")");
                        sourceText.setFill(Color.web("#3498db"));
                        valueTextFlow.getChildren().add(sourceText);
                    }
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

    /**
     * Retrieves a HistoricalEntity object based on its ID.
     *
     * @param id the ID of the HistoricalEntity object
     * @return the HistoricalEntity object with the matching ID, or null if not found
     */
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
        return null;
    }

}
