package com.vietnam.history.crawler;

import com.vietnam.history.crawler.datamanage.DataHandling;
import com.vietnam.history.crawler.datamanage.Merge;
import com.vietnam.history.crawler.datamanage.ModifyData;
import com.vietnam.history.crawler.dbpediadatacrawler.DBPediaData;
import com.vietnam.history.crawler.wikidatacrawler.WikiData;

public class CrawlData {
    public static void main(String[] args) throws Exception {
        String wikiPath = "raw/Wikipedia/";
        String dbpediaPath = "raw/DBPedia/";

        WikiData wikiData = new WikiData(wikiPath);
        wikiData.setBruteForceLimit(0);
        wikiData.getData();

        DBPediaData dbpediaData = new DBPediaData(dbpediaPath);
        dbpediaData.getBruteForceData();
        dbpediaData.syncData(wikiPath);
        DataHandling.print("Done dbpediaData");

        Merge mergeData = new Merge();
        mergeData.merge("data/", wikiPath, dbpediaPath, Merge.createSource("Wikipedia"), Merge.createSource("DBPedia"));
        DataHandling.print("Done merge");

        ModifyData md = new ModifyData("data/", "src/text-modify/");
        md.removeEntity();
        DataHandling.print("Finish");
    }
}
