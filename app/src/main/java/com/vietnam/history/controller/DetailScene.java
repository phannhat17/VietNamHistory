package com.vietnam.history.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.vietnam.history.App;
import com.vietnam.history.model.HistoricalEntity;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
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
        processClaims(claims, claimsContainer);
    }

    private void processClaims(JsonNode claimsNode, VBox vbox) {
        Iterator<Map.Entry<String, JsonNode>> properties = claimsNode.fields();
        VBox claimsContainer = new VBox(); // Create a new VBox for the claimsContainer

        while (properties.hasNext()) {
            Map.Entry<String, JsonNode> property = properties.next();
            String propertyName = StringUtils.capitalize(property.getKey());
            Label keyLabel = new Label(propertyName + ":");
            keyLabel.setStyle("-fx-font-size: 16px;-fx-padding: 0px 10px 0px 0px; -fx-font-weight: bold;");

            JsonNode propertyArr = property.getValue();
            StringBuilder sb = new StringBuilder(); // Use StringBuilder for efficient string concatenation

            int count = 0;
            for (JsonNode propertyDetail : propertyArr) {
                if (count > 0) {
                    sb.append(", ");
                }
                String value = propertyDetail.get("value").asText();
                sb.append(value).append(" ");

                if (propertyDetail.has("qualifiers")) {
                    sb.append("(");
                    JsonNode qualifiersObj = propertyDetail.get("qualifiers");
                    Iterator<Map.Entry<String, JsonNode>> qualifierKeys = qualifiersObj.fields();

                    StringBuilder qualifierSB = new StringBuilder(); // Use StringBuilder for efficient string concatenation

                    int subCount = 0;
                    while (qualifierKeys.hasNext()) {
                        if (subCount != 0) {
                            qualifierSB.append(", ");
                        }
                        Map.Entry<String, JsonNode> qualifierProperty = qualifierKeys.next();
                        String qualifierPropertyName = qualifierProperty.getKey();
                        qualifierSB.append(qualifierPropertyName).append(": ");
                        JsonNode qualifierPropertyArr = qualifierProperty.getValue();
                        int subSubCount = 0;
                        for (JsonNode ele : qualifierPropertyArr) {
                            if (subSubCount != 0) {
                                qualifierSB.append(", ");
                            }
                            String subQualifierPropertyValue = ele.get("value").asText();
                            qualifierSB.append(subQualifierPropertyValue);
                            subSubCount++;
                        }
                        subCount++;
                    }
                    sb.append(qualifierSB.toString()); // Append the qualifiers string
                    sb.append(")");
                }
                count++;
            }

            Text valueText = new Text(sb.toString());
            valueText.setStyle("-fx-font-size: 16px;");

            HBox keyValuePair = new HBox();
            keyValuePair.getChildren().addAll(keyLabel, valueText);
            keyValuePair.setStyle("-fx-padding: 10px 0px 0px 10px");
            // Add the key-value pair HBox container to the VBox container
            claimsContainer.getChildren().add(keyValuePair);
        }

        vbox.getChildren().add(claimsContainer); // Add the claimsContainer VBox to the main VBox (vbox)
    }
}
