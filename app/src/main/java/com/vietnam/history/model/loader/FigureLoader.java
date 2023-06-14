package com.vietnam.history.model.loader;

import com.vietnam.history.model.Figure;

public class FigureLoader extends DataLoader<Figure> {
    private static final String FOLDER_PATH = "src/data/historical-figures";

    public FigureLoader() {
        super(FOLDER_PATH);
    }

    @Override
    protected Class<Figure> getType() {
        return Figure.class;
    }
}
