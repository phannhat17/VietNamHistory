package com.vietnam.history.gui.loader;

import com.vietnam.history.gui.model.Figure;

/**
 * A class for loading historical figure data from JSON files.
 */
public class FigureLoader extends DataLoader<Figure> {
    private static final String FOLDER_PATH = "src/data/nhân vật lịch sử";

    /**
     * Constructs a new {@code FigureLoader}.
     */
    public FigureLoader() {
        super(FOLDER_PATH);
    }

    /**
     * Returns the class of the {@code Figure} objects to load.
     *
     * @return the class of the {@code Figure} objects to load
     */
    @Override
    protected Class<Figure> getType() {
        return Figure.class;
    }
}
