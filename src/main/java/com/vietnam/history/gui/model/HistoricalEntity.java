package com.vietnam.history.gui.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * The base class representing a historical entity.
 * This abstract class provides common properties and methods for historical entities.
 */
public abstract class HistoricalEntity {
    private String id;                  // The ID of the historical entity
    private String label;               // The label or name of the historical entity
    private String overview;            // An overview or summary of the historical entity
    private List<String> aliases;       // A list of alternative names or aliases for the historical entity
    private JsonNode references;        // References or sources related to the historical entity
    private JsonNode claims;            // Claims or specific information about the historical entity
    private String description;         // The description of the historical entity

    /**
     * Default constructor for the HistoricalEntity class.
     */
    protected HistoricalEntity() {
    }

    /**
     * Constructs a new {@code HistoricalEntity} instance with the specified values.
     *
     * @param id          the ID of the entity
     * @param label       the label or name of the entity
     * @param overview    the overview of the entity
     * @param aliases     the aliases of the entity
     * @param references  the references of the entity
     * @param claims      the claims of the entity
     * @param description the description of the entity
     */
    protected HistoricalEntity(String id, String label, String overview, List<String> aliases, JsonNode references, JsonNode claims, String description) {
        this.id = id;
        this.label = label;
        this.overview = overview;
        this.aliases = aliases;
        this.references = references;
        this.claims = claims;
        this.description = description;
    }

    // Getter

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

