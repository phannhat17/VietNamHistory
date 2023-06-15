package com.vietnam.history.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;


public abstract class HistoricalEntity {
    private String id;
    private String label;
    private String overview;
    private List<String> aliases;
    private JsonNode references;
    private JsonNode claims;
    private String description;

    public HistoricalEntity() {
    }

    public HistoricalEntity(String id, String label, String overview, List<String> aliases, JsonNode references, JsonNode claims, String description) {
        this.id = id;
        this.label = label;
        this.overview = overview;
        this.aliases = aliases;
        this.references = references;
        this.claims = claims;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getOverview() {
        return overview;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public JsonNode getReferences() {
        return references;
    }

    public JsonNode getClaims() {
        return claims;
    }

    public String getDescription() {
        return description;
    }
}

