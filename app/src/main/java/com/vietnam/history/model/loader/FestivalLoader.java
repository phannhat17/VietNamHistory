package com.vietnam.history.model.loader;


import com.vietnam.history.model.Festival;

public class FestivalLoader extends DataLoader<Festival> {
    private static final String FOLDER_PATH = "src/data/festival";

    public FestivalLoader() {
        super(FOLDER_PATH);
    }

    @Override
    protected Class<Festival> getType() {
        return Festival.class;
    }
}
