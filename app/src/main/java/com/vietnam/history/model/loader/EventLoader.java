package com.vietnam.history.model.loader;


import com.vietnam.history.model.HistoryEvent;

public class EventLoader extends DataLoader<HistoryEvent>{
    private static final String FOLDER_PATH = "src/data/event";

    public EventLoader() {
        super(FOLDER_PATH);
    }

    @Override
    protected Class<HistoryEvent> getType() {
        return HistoryEvent.class;
    }
}
