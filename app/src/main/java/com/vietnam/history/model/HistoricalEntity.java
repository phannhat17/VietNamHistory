package com.vietnam.history.model;

public abstract class HistoricalEntity {
    private String name;
    private String overview;

    public HistoricalEntity() {
    }

    public HistoricalEntity(String name, String overview) {
        this.name = name;
        this.overview = overview;
    }

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    // Check for a string is in name or not
    public boolean isStringInName(String searchName) {
        if (name != null && searchName != null) {
            return name.toLowerCase().contains(searchName.toLowerCase());
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "HistoricalEntity{" +
                "name='" + name + '\'' +
                ", overview='" + overview + '\'' +
                '}';
    }
}

