package com.vietnam.history.gui.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Represents a historical figure, such as a king, queen, emperor, or politician.
 */
public class Figure extends HistoricalEntity{
    public Figure() {
    }

    public Figure(String id, String label, String overview, List<String> aliases, JsonNode references, JsonNode claims, String description) {
        super(id, label, overview, aliases, references, claims, description);
    }
}
