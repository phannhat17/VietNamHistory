package com.vietnam.history.gui.loader;

import com.vietnam.history.gui.model.Landmark;

/**
 * A class for loading place data from JSON files.
 */
public class LandmarkLoader extends DataLoader<Landmark>{
    private static final String FOLDER_PATH = "src/data/địa điểm du lịch, di tích lịch sử";

    /**
     * Constructs a new {@code LandmarkLoader}.
     */
    public LandmarkLoader() {
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
