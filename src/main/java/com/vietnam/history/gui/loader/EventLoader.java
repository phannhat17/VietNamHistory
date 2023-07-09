package com.vietnam.history.gui.loader;


import com.vietnam.history.gui.model.HistoricalEvent;

/**
 * A class for loading historical event data from JSON files.
 */
public class EventLoader extends DataLoader<HistoricalEvent>{
    private static final String FOLDER_PATH = "src/data/sự kiện lịch sử";

    /**
     * Constructs a new {@code EventLoader}.
     */
    public EventLoader() {
        super(FOLDER_PATH);
    }

    /**
     * Returns the class of the {@code HistoricalEvent} objects to load.
     *
     * @return the class of the {@code HistoricalEvent} objects to load
     */
    @Override
    protected Class<HistoricalEvent> getType() {
        return HistoricalEvent.class;
    }
}
