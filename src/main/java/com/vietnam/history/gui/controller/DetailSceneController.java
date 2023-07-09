package com.vietnam.history.gui.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.vietnam.history.gui.App;
import com.vietnam.history.gui.model.HistoricalEntity;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

public class DetailSceneController extends MainController {

    @FXML
    public ButtonBar backBtnBar;

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
        // Initialize the claims container and set it as the content of the scroll pane
        claimsContainer = new VBox();
        scrollPane.setContent(claimsContainer);

        if (App.getEntityStack().size()>1){
            backBtnBar.setDisable(false); // Disable the back button if there's only one entity in the stack
        }
    }

    @FXML
    void goBackPress(ActionEvent actionEvent) throws IOException {
        if (App.getEntityStack().size()>1) {
            App.popEntityStack(); // Pop the current entity from the stack
            if (!App.getEntityStack().isEmpty()) {
                HistoricalEntity previousEntity = App.popEntityStack();
                App.setRootWithEntity("DetailScene", previousEntity); // If there's a previous entity, retrieve it from the stack and display
            }
        }
    }

    /**
     * Set data for the detail scene based on the given historical entity
     * @param entity the entity to display data
     */
    public void setData(HistoricalEntity entity) {

        nameLabel.setText(entity.getLabel());
        overviewText.setText(entity.getOverview());

        // Display information and related data sections
        displayData("THÔNG TIN", entity.getClaims(), entity);
        displayData("LIÊN QUAN", entity.getReferences(), entity);
    }

    /**
     * Display all data for a specific section (type) of the entity
     *
     * @param type type of the data "THÔNG TIN" or "LIÊN QUAN"
     * @param jsonNode the data to display
     * @param entity the entity contain these data
     */
    private void displayData(String type, JsonNode jsonNode, HistoricalEntity entity) {
        if (type != null) {
            // Create and display a label for the section
            Label claimsLabel = new Label(type);
            claimsLabel.getStyleClass().add("section-label");
            claimsContainer.getChildren().add(claimsLabel);
            if (type.equals("THÔNG TIN")) {
                // Add description and aliases for "THÔNG TIN"
                addDescriptionSection(entity.getDescription());
                addAliasesSection(entity.getAliases());
            }
        }

        // Display a label indicating that there is no information available
        if (jsonNode == null) {
            Label nullLabel = new Label("Chưa có thông tin");
            nullLabel.getStyleClass().add("null-label");
            claimsContainer.getChildren().add(nullLabel);
            return;
        }

        // Iterate through the properties of the JSON nodes
        Iterator<Map.Entry<String, JsonNode>> properties = jsonNode.fields();
        while (properties.hasNext()) {
            Map.Entry<String, JsonNode> property = properties.next();
            String propertyName = StringUtils.capitalize(property.getKey());

            // Create and display a label for the key
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
                    // Configure linked value text for clickable entities
                    configureLinkedValueText(valueText, propertyDetail.get("id").asText());
                }

                valueTextFlow.getChildren().add(valueText);

                if (propertyDetail.has("qualifiers")) {
                    addQualifiersAndSource(propertyDetail, valueTextFlow);
                } else if (!propertyDetail.has("qualifiers") && propertyDetail.has("source")) {
                    // Add source information for properties without qualifiers
                    ArrayNode sourceArray = (ArrayNode) propertyDetail.get("source");
                    int numSources = sourceArray.size();
                    Set<String> uniqueSources = new HashSet<>();

                    // Check if all elements in the array are the same
                    for (JsonNode source : sourceArray) {
                        uniqueSources.add(source.asText());
                    }
                    boolean allSame = uniqueSources.size() == 1;

                    if (numSources > 1 && !allSame) {
                        // Display all elements separated by a comma
                        StringBuilder sourcesText = new StringBuilder();
                        for (JsonNode source : sourceArray) {
                            sourcesText.append(source.asText()).append(", ");
                        }
                        sourcesText.delete(sourcesText.length() - 2, sourcesText.length()); // Remove trailing comma and space

                        Text sourceText = new Text(" (Nguồn: " + sourcesText + ")");
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

    /**
     * Add description section for the entity
     * @param description description of that entity
     */
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

    /**
     * Add aliases section for the entity
     * @param aliases aliases of that entity
     */
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

    // Add qualifiers and source information to the value text flow
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
            // Add source information for properties without qualifiers
            ArrayNode sourceArray = (ArrayNode) propertyDetail.get("source");
            int numSources = sourceArray.size();
            Set<String> uniqueSources = new HashSet<>();

            // Check if all elements in the array are the same
            for (JsonNode source : sourceArray) {
                uniqueSources.add(source.asText());
            }
            boolean allSame = uniqueSources.size() == 1;

            if (numSources > 1 && !allSame) {
                // Display all elements separated by a comma
                StringBuilder sourcesText = new StringBuilder();
                for (JsonNode source : sourceArray) {
                    sourcesText.append(source.asText()).append(", ");
                }
                sourcesText.delete(sourcesText.length() - 2, sourcesText.length()); // Remove trailing comma and space

                Text sourceText = new Text(" (Nguồn: " + sourcesText + ")");
                sourceText.setFill(Color.web("#9b59b6"));
                valueTextFlow.getChildren().add(sourceText);
            }
        }

        valueTextFlow.getChildren().add(qualifierTextFlow); 
    }

    // Configure linked value text for clickable entities
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
