package com.vietnam.history.model.loader;

import com.vietnam.history.model.Landmark;

/**
 * A class for loading place data from JSON files.
 */
public class PlaceLoader extends DataLoader<Landmark>{
    private static final String FOLDER_PATH = "src/data/địa điểm du lịch, di tích lịch sử";

    /**
     * Constructs a new {@code PlaceLoader}.
     */
    public PlaceLoader() {
        super(FOLDER_PATH);
    }

    /**
     * Returns the class of the {@code Landmark} objects to load.
     *
     * @return the class of the {@code Landmark} objects to load
     */
    @Override
    protected Class<Landmark> getType() {
        return Landmark.class;
    }
}
