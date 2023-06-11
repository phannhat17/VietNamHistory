package com.vietnam.history.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class Dynasty {
    @JsonProperty("overview")
    private String overview;

    @JsonProperty("aliases")
    private List<String> aliases;

    @JsonProperty("claims")
    private Map<String, List<Map<String, Object>>> claims;

    @JsonProperty("description")
    private String description;

    @JsonProperty("id")
    private String id;

    @JsonProperty("label")
    private String label;

    // Getters and setters

    public String getLabel() {
        return label;
    }

    public String getOverview() {
        return overview;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public Map<String, List<Map<String, Object>>> getClaims() {
        return claims;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }


}
