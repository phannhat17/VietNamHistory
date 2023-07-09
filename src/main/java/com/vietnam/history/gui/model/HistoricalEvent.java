package com.vietnam.history.gui.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Represents a historical event, such as a war, revolution, treaty, or ceremony.
 */
public class HistoricalEvent extends HistoricalEntity{
    public HistoricalEvent() {
    }

    public HistoricalEvent(String id, String label, String overview, List<String> aliases, JsonNode references, JsonNode claims, String description) {
        super(id, label, overview, aliases, references, claims, description);
    }
}
