package com.vietnam.history.gui.loader;


import com.vietnam.history.gui.model.Festival;

/**
 * A class for loading festival data from JSON files.
 */
public class FestivalLoader extends DataLoader<Festival> {
    private static final String FOLDER_PATH = "src/data/lễ hội văn hóa";

    /**
     * Constructs a new {@code FestivalLoader}.
     */
    public FestivalLoader() {
        super(FOLDER_PATH);
    }

    /**
     * Returns the class of the {@code Festival} objects to load.
     *
     * @return the class of the {@code Festival} objects to load
     */
    @Override
    protected Class<Festival> getType() {
        return Festival.class;
    }
}
