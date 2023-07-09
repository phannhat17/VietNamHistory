package com.vietnam.history.gui.loader;

import com.vietnam.history.gui.model.Dynasty;

/**
 * A class for loading dynasty data from JSON files.
 */
public class DynastyLoader extends DataLoader<Dynasty> {
    private static final String FOLDER_PATH = "src/data/triều đại lịch sử";

    /**
     * Constructs a new {@code DynastyLoader}.
     */
    public DynastyLoader() {
        super(FOLDER_PATH);
    }

    /**
     * Returns the class of the {@code Dynasty} objects to load.
     *
     * @return the class of the {@code Dynasty} objects to load
     */
    @Override
    protected Class<Dynasty> getType() {
        return Dynasty.class;
    }
}
