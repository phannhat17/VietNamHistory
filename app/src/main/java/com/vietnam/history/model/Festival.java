package com.vietnam.history.model;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Map;

public class Festival extends HistoricalEntity{
    public Festival() {
    }

    public Festival(String id, String label, String overview, List<String> aliases, Map<String, Object> references, Map<String, Object> claims, String description) {
        super(id, label, overview, aliases, (ObjectNode) references, (ObjectNode) claims, description);
    }
}
