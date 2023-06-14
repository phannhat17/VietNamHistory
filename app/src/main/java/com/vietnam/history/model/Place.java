package com.vietnam.history.model;

import java.util.List;
import java.util.Map;

public class Place extends HistoricalEntity{
    public Place() {
    }

    public Place(String id, String label, String overview, List<String> aliases, Map<String, Object> references, Map<String, Object> claims, String description) {
        super(id, label, overview, aliases, references, claims, description);
    }
}
