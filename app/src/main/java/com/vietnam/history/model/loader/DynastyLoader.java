package com.vietnam.history.model.loader;

import com.vietnam.history.model.Dynasty;

public class DynastyLoader extends DataLoader<Dynasty> {
    private static final String FOLDER_PATH = "src/data/dynasty";

    public DynastyLoader() {
        super(FOLDER_PATH);
    }

    @Override
    protected Class<Dynasty> getType() {
        return Dynasty.class;
    }
}
