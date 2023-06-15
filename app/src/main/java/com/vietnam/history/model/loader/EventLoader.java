package com.vietnam.history.model.loader;


import com.vietnam.history.model.HistoricalEvent;

public class EventLoader extends DataLoader<HistoricalEvent>{
    private static final String FOLDER_PATH = "src/data/event";

    public EventLoader() {
        super(FOLDER_PATH);
    }

    @Override
    protected Class<HistoricalEvent> getType() {
        return HistoricalEvent.class;
    }
}
