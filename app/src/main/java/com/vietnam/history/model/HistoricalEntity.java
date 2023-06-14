package com.vietnam.history.model;

import java.util.List;
import java.util.Map;

public abstract class HistoricalEntity {
    private String id;
    private String label;
    private String overview;
    private List<String> aliases;
    private Map<String, Object> references;
    private Map<String, Object> claims;
    private String description;

    public HistoricalEntity() {
    }

    public HistoricalEntity(String id, String label, String overview, List<String> aliases, Map<String, Object> references, Map<String, Object> claims, String description) {
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

    public Map<String, Object> getReferences() {
        return references;
    }

    public Map<String, Object> getClaims() {
        return claims;
    }

    public String getDescription() {
        return description;
    }
}

