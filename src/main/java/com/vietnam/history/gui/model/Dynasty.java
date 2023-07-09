package com.vietnam.history.gui.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Represents a historical dynasty, such as a ruling family or dynasty.
 */
public class Dynasty extends HistoricalEntity{
    public Dynasty() {
    }

    public Dynasty(String id, String label, String overview, List<String> aliases, JsonNode references, JsonNode claims, String description) {
        super(id, label, overview, aliases, references, claims, description);
    }
}
