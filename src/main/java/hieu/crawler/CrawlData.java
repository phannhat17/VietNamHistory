package crawler;

import crawler.DBPediaDataCrawler.DBPediaData;
import crawler.DataManage.DataHandling;
import crawler.DataManage.Merge;
import crawler.DataManage.ModifyData;
import crawler.WikiDataCrawler.*;

public class CrawlData {
    public static void main(String[] args) throws Exception {
        String wikiPath = "E:/Code/Java/OOP_Project/saveddata/Wikipedia/";
        String dbpediaPath = "E:/Code/Java/OOP_Project/saveddata/DBPedia/";

        WikiData wikiData = new WikiData(wikiPath);
        wikiData.setBruteForceLimit(100000);
        wikiData.getData();

        DBPediaData dbpediaData = new DBPediaData(dbpediaPath);
        dbpediaData.getBruteForceData();
        dbpediaData.syncData();
        DataHandling.print("Done dbpediaData");

        Merge mergeData = new Merge();
        mergeData.merge("data", wikiPath, dbpediaPath, Merge.createSource("Wikipedia"), Merge.createSource("DBPedia"));
        DataHandling.print("Done merge");

        ModifyData md = new ModifyData();
        md.removeEntity();
        DataHandling.print("Finish");
    }

}
