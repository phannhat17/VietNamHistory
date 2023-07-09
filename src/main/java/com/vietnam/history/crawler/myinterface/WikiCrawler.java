package com.vietnam.history.crawler.myinterface;

public interface WikiCrawler {
    void getData() throws Exception;

    void getBruteForceData() throws Exception;

    void getSelectiveData() throws Exception;

    void getTableData() throws Exception;

    void rewriteReferences() throws Exception;

    void export() throws Exception;
}