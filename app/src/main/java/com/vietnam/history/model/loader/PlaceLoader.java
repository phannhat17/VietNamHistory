package com.vietnam.history.model.loader;

import com.vietnam.history.model.Place;

/**
 * A class for loading place data from JSON files.
 */
public class PlaceLoader extends DataLoader<Place>{
    private static final String FOLDER_PATH = "src/data/địa điểm du lịch, di tích lịch sử";

    /**
     * Constructs a new {@code PlaceLoader}.
     */
    public PlaceLoader() {
        super(FOLDER_PATH);
    }

    /**
     * Returns the class of the {@code Place} objects to load.
     *
     * @return the class of the {@code Place} objects to load
     */
    @Override
    protected Class<Place> getType() {
        return Place.class;
    }
}
