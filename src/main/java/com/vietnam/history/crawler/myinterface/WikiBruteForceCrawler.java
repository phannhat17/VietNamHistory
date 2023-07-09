package com.vietnam.history.crawler.myinterface;

public interface WikiBruteForceCrawler {
    void getVietnamRelatedEntity() throws Exception;

    void getBruteForceData() throws Exception;

    void entityAnalys(String url, int depth, boolean forceRelated) throws Exception;

    void analyzeBruteForceData() throws Exception;

    void urlToEntities() throws Exception;

    void getWikiProperties() throws Exception;

    void entityRefFinal() throws Exception;

    void entityFinal() throws Exception;

    void resetEntityRef() throws Exception;
}
