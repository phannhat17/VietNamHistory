package crawler.WikiDataCrawler;

import crawler.DataManage.DataHandling;

/**
 *  The "WikiData" class provides useful methods for analyzing Wikipedia pages and extracting relevant information related to entities in Vietnam.
 */


public class WikiData{
    
    public static void main(String[] args) throws Exception {
    }

    private String wikiPath = "";
    private int bruteForceLimit = -1;

    public WikiData(String path) throws Exception{
        wikiPath = path;
    }
    public WikiData(){
        throw new IllegalArgumentException("File path must be provided.");
    }

    public void getData() throws Exception{
        WikiBruteForceData wikiBruteForceData = new WikiBruteForceData(wikiPath);
        if (bruteForceLimit != -1){
            wikiBruteForceData.setBruteForceAnalyseLimit(bruteForceLimit);
        }
        wikiBruteForceData.getBruteForceData();
        DataHandling.print("Done brute force Wiki data");

        WikiSelectiveData wikiSelectiveData = new WikiSelectiveData(wikiPath);
        wikiSelectiveData.selectiveDataQueries();
        wikiSelectiveData.analyzeSelectiveData();
        DataHandling.print("Done get selective Wiki data");
        
        WikiTableData wikiTableData = new WikiTableData(wikiPath);
        wikiTableData.tableDataQueries();
        DataHandling.print("Done get table Wiki data");

        wikiBruteForceData.entityRefFinal();
        wikiBruteForceData.resetEntityRef();
        DataHandling.print("Done rewrite ref of Wiki data");

        WikiDataExport wikiDataExport = new WikiDataExport(wikiPath);
        wikiDataExport.export();
        DataHandling.print("Done Wiki data export");
    }

    public void setBruteForceLimit(int newLimit){
        bruteForceLimit = newLimit;
    }
}   