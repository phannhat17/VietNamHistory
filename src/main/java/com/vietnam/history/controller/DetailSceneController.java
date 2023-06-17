package com.vietnam.history.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.vietnam.history.App;
import com.vietnam.history.model.HistoricalEntity;
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

public class DetailSceneController {

    @FXML
    private Label nameLabel;

    @FXML
    private Text overviewText;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox claimsContainer;

    @FXML
    void initialize() {
        claimsContainer = new VBox();
        scrollPane.setContent(claimsContainer);
    }

    @FXML
    void aboutClick(ActionEvent event) throws IOException {
        App.openAbout("About");
    }

    public void setData(HistoricalEntity entity) {
        nameLabel.setText(entity.getLabel());
        overviewText.setText(entity.getOverview());

        displayData("THÔNG TIN", entity.getClaims(), entity);
        displayData("LIÊN QUAN", entity.getReferences(), null);
    }

    private void displayData(String type, JsonNode jsonNode, HistoricalEntity entity) {
        if (type != null) {
            Label claimsLabel = new Label(type);
            claimsLabel.getStyleClass().add("section-label");
            claimsContainer.getChildren().add(claimsLabel);
        }

        if (entity != null) {
            addDescriptionSection(entity.getDescription());
            addAliasesSection(entity.getAliases());
        }

        if (jsonNode == null) {
            Label nullLabel = new Label("Chưa có thông tin");
            nullLabel.getStyleClass().add("null-label");
            claimsContainer.getChildren().add(nullLabel);
            return;
        }

        Iterator<Map.Entry<String, JsonNode>> properties = jsonNode.fields();
        while (properties.hasNext()) {
            Map.Entry<String, JsonNode> property = properties.next();
            String propertyName = StringUtils.capitalize(property.getKey());
            Label keyLabel = new Label(propertyName + ":");
            keyLabel.setPrefWidth(300);
            keyLabel.setWrapText(true);
            keyLabel.getStyleClass().add("key-label");

            JsonNode propertyArr = property.getValue();

            TextFlow valueTextFlow = new TextFlow();

            int count = 0;
            for (JsonNode propertyDetail : propertyArr) {
                if (count > 0) {
                    valueTextFlow.getChildren().add(new Text("\n"));
                }

                String value = propertyDetail.get("value").asText();
                Text valueText = new Text(value.trim());

                if (propertyDetail.has("id")) {
                    configureLinkedValueText(valueText, propertyDetail.get("id").asText());
                }

                valueTextFlow.getChildren().add(valueText);

                if (propertyDetail.has("qualifiers")) {
                    addQualifiersAndSource(propertyDetail, valueTextFlow);
                } else if (!propertyDetail.has("qualifiers") && propertyDetail.has("source")) {
                    String source = propertyDetail.get("source").asText();
                    if (!source.equals("both")) {
                        Text sourceText = new Text(" (Nguồn: " + source.trim() + ")");
                        sourceText.setFill(Color.web("#9b59b6"));
                        valueTextFlow.getChildren().add(sourceText);
                    }
                }

                count++;
            }

            valueTextFlow.getStyleClass().add("value-label");
            HBox keyValuePair = new HBox(keyLabel, valueTextFlow);
            keyValuePair.getStyleClass().add("key-value-pair");
            claimsContainer.getChildren().add(keyValuePair);
        }
    }

    private void addDescriptionSection(String description) {
        if (StringUtils.isNotEmpty(description)) {
            Label descriptionLabel = new Label("Mô tả:");
            descriptionLabel.setPrefWidth(300);
            descriptionLabel.setWrapText(true);
            descriptionLabel.getStyleClass().add("key-label");

            Text descriptionText = new Text(description);
            descriptionText.setWrappingWidth(700);
            descriptionText.getStyleClass().add("value-label");
            HBox descriptionSection = new HBox(descriptionLabel, descriptionText);
            descriptionSection.getStyleClass().add("key-value-pair");
            claimsContainer.getChildren().add(descriptionSection);
        }
    }

    private void addAliasesSection(List<String> aliases) {
        if (aliases != null && !aliases.isEmpty()) {
            Label aliasesLabel = new Label("Tên gọi khác:");
            aliasesLabel.setPrefWidth(300);
            aliasesLabel.setWrapText(true);
            aliasesLabel.getStyleClass().add("key-label");
            TextFlow aliasesTextFlow = new TextFlow();
            for (String alias : aliases) {
                aliasesTextFlow.getChildren().add(new Text(alias.trim() + "\n"));
            }
            aliasesTextFlow.getStyleClass().add("value-label");
            HBox aliasesSection = new HBox(aliasesLabel, aliasesTextFlow);
            aliasesSection.getStyleClass().add("key-value-pair");
            claimsContainer.getChildren().add(aliasesSection);
        }
    }

    private void addQualifiersAndSource(JsonNode propertyDetail, TextFlow valueTextFlow) {
        valueTextFlow.getChildren().add(new Text(" ("));
        JsonNode qualifiersObj = propertyDetail.get("qualifiers");
        Iterator<Map.Entry<String, JsonNode>> qualifierKeys = qualifiersObj.fields();

        TextFlow qualifierTextFlow = new TextFlow(); 

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
                Text subQualifierPropertyValueText = new Text(subQualifierPropertyValue.trim());
                qualifierTextFlow.getChildren().add(subQualifierPropertyValueText);
                subSubCount++;
            }
            subCount++;
        }
        qualifierTextFlow.getChildren().add(new Text(")"));
        

        if (propertyDetail.has("source")) {
            JsonNode source = propertyDetail.get("source");
            String sourceValue = source.asText();
            if (!sourceValue.equals("both")) {
                Text sourceText = new Text(" (Nguồn: " + sourceValue + ")");
                sourceText.setFill(Color.web("#9b59b6"));
                qualifierTextFlow.getChildren().add(sourceText);
            }
        }

        valueTextFlow.getChildren().add(qualifierTextFlow); 
    }

    private void configureLinkedValueText(Text valueText, String entityId) {
        valueText.setFill(Color.web("#3498db"));
        valueText.setCursor(Cursor.HAND);
        valueText.getStyleClass().add("linked-text");
        valueText.setOnMouseClicked(event -> {
            try {
                HistoricalEntity entity = App.fetchEntity(entityId);
                if (entity != null) {
                    App.setRootWithEntity("DetailScene", entity);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
