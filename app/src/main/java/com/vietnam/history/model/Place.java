package com.vietnam.history.model;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Map;

/**
 * Represents a historical place, such as a city, province, or region.
 */
public class Place extends HistoricalEntity{
    public Place() {
    }

    public Place(String id, String label, String overview, List<String> aliases, Map<String, Object> references, Map<String, Object> claims, String description) {
        super(id, label, overview, aliases, (ObjectNode) references, (ObjectNode) claims, description);
    }
}
