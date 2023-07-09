package com.vietnam.history.crawler.wikidatacrawler;

import com.vietnam.history.crawler.datamanage.DataHandling;
import com.vietnam.history.crawler.myinterface.WikiCrawler;

/**
 * The "WikiData" class provides useful methods for analyzing Wikipedia pages and extracting relevant information related to entities in Vietnam.
 */


public class WikiData implements WikiCrawler {

    private String wikiPath = "";
    private int bruteForceLimit = -1;

    public WikiData(String path) {
        wikiPath = path;
    }

    public WikiData() {
        throw new IllegalArgumentException("File path must be provided.");
    }

    @Override
    public void getData() throws Exception {
        getBruteForceData();
        getSelectiveData();
        getTableData();
        rewriteReferences();
        export();
    }

    public void setBruteForceLimit(int newLimit) {
        bruteForceLimit = newLimit;
    }

    @Override
    public void getBruteForceData() throws Exception {
        WikiBruteForceData wikiBruteForceData = new WikiBruteForceData(wikiPath);
        if (bruteForceLimit != -1) {
            wikiBruteForceData.setBruteForceAnalyseLimit(bruteForceLimit);
        }

        wikiBruteForceData.getBruteForceData();
        DataHandling.print("Done brute force Wiki data");
    }

    @Override
    public void getSelectiveData() throws Exception {
        WikiSelectiveData wikiSelectiveData = new WikiSelectiveData(wikiPath);
        wikiSelectiveData.selectiveDataQueries();
        wikiSelectiveData.analyzeSelectiveData();
        DataHandling.print("Done get selective Wiki data");
    }

    @Override
    public void getTableData() throws Exception {
        WikiTableData wikiTableData = new WikiTableData(wikiPath);
        wikiTableData.tableDataQueries();
        DataHandling.print("Done get table Wiki data");
    }

    @Override
    public void rewriteReferences() throws Exception {
        WikiBruteForceData wikiBruteForceData = new WikiBruteForceData(wikiPath);
        wikiBruteForceData.entityRefFinal();
        wikiBruteForceData.resetEntityRef();
        DataHandling.print("Done rewrite ref of Wiki data");
    }

    @Override
    public void export() throws Exception {
        WikiDataExport wikiDataExport = new WikiDataExport(wikiPath);
        wikiDataExport.export();
        DataHandling.print("Done Wiki data export");
    }
}   