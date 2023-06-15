package com.vietnam.history.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public class Festival extends HistoricalEntity{
    public Festival() {
    }

    public Festival(String id, String label, String overview, List<String> aliases, JsonNode references, JsonNode claims, String description) {
        super(id, label, overview, aliases, references, claims, description);
    }
}
