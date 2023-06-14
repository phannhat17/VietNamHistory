package com.vietnam.history.model.loader;

import com.vietnam.history.model.Place;

public class PlaceLoader extends DataLoader<Place>{
    private static final String FOLDER_PATH = "src/data/historical-place";

    public PlaceLoader() {
        super(FOLDER_PATH);
    }
    @Override
    protected Class<Place> getType() {
        return Place.class;
    }
}
