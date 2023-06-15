package com.vietnam.history.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public class HistoricalEvent extends HistoricalEntity{
    public HistoricalEvent() {
    }

    public HistoricalEvent(String id, String label, String overview, List<String> aliases, JsonNode references, JsonNode claims, String description) {
        super(id, label, overview, aliases, references, claims, description);
    }
}
