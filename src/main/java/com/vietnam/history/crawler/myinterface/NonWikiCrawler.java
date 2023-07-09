package com.vietnam.history.crawler.myinterface;

public interface NonWikiCrawler {
    void getData() throws Exception;

    void syncData(String wikiPath) throws Exception;
}
