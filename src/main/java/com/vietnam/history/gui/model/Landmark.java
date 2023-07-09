package com.vietnam.history.gui.model;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Map;

/**
 * Represents a historical place, such as a city, province, or region.
 */
public class Landmark extends HistoricalEntity{
    public Landmark() {
    }

    public Landmark(String id, String label, String overview, List<String> aliases, Map<String, Object> references, Map<String, Object> claims, String description) {
        super(id, label, overview, aliases, (ObjectNode) references, (ObjectNode) claims, description);
    }
}
