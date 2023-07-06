package crawler.DataManage;

import crawler.WikiDataCrawler.WikiDataHandling;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class BruteForceData extends DataFolder {
    class Pair
    {
        public String first;
        public int second;
        public Pair(String a, int b)
        {
            first = a;
            second = b;
        }
    }

    protected HashSet<String> vietnamEntityHashSet = new HashSet<>();
    protected HashSet<String> propertyHashSet = new HashSet<>();
    //root path:
    protected final String ROOT_PATH;

    //folders:
    protected final String DATA_PATH;
    protected final String INITIALIZE_PATH;
    protected final String LOGS_PATH;
    protected final String ENTITY_JSON_PATH;
    //files
    protected final String BEGIN_URLS_PATH;
    protected final String CRAFTED_URLS_PATH;
    protected final String ANALYSED_URLS_PATH;  // Đổi tên thành AcceptedURL
    protected final String FAILED_URLS_PATH;    // Đổi tên thành RejectedURL

    private Deque<Pair> deque = new ArrayDeque<>();
    private HashSet<String> failedURLsHashSet;
    private HashSet<String> analysedURLsHashSet;
    private HashMap<String, Integer> craftedURLsHashMap = new HashMap<>();
    private int totalAnalysed;
    private int limitAmountAnalysis = 150000;


    /**
     * Set up an environment for saving data.
     * @param path Your path where the crawled data will be stored.
     */
    public BruteForceData(String path)
    {
        super(path);
        if (path.charAt(path.length()-1) != (char)('/') && path.charAt(path.length()-1) != (char)('\\'))
        {
            path = path + "/";
        }
        ROOT_PATH = path;
        DATA_PATH = path + "data/";
        DataHandling.createFolder(DATA_PATH);
        INITIALIZE_PATH = path + "initialize/";
        DataHandling.createFolder(INITIALIZE_PATH);
        LOGS_PATH = path + "logs/";
        DataHandling.createFolder(LOGS_PATH);
        ENTITY_JSON_PATH = LOGS_PATH + "EntityJson/";
        DataHandling.createFolder(ENTITY_JSON_PATH);
        BEGIN_URLS_PATH = LOGS_PATH + "BeginURLs.txt";
        CRAFTED_URLS_PATH = LOGS_PATH + "CraftedURLs.txt";
        ANALYSED_URLS_PATH = LOGS_PATH + "AnalysedURLs.txt";
        FAILED_URLS_PATH = LOGS_PATH + "FailedURLs.txt";
    }

    public BruteForceData()
    {
        throw new IllegalArgumentException("File path must be provided");
    }


    /**
     * This method is used to scrape data.
     * @apiNote The getData() function merely retrieves the raw data. Override the getDataCallBack() function located at the end of the getData() function so that, if you need to, you can choose how to process the data to give the best final data.
     * @throws Exception
     */
    public final void getBruteForceData() throws Exception
    {
        getVietnamRelatedEntity();
        failedURLsHashSet = new HashSet<>(DataHandling.readFileAllLine(FAILED_URLS_PATH));
        analysedURLsHashSet = new HashSet<>(DataHandling.readFileAllLine(ANALYSED_URLS_PATH));
        totalAnalysed += failedURLsHashSet.size() + analysedURLsHashSet.size();
        if (totalAnalysed < limitAmountAnalysis)
        {
            List<String> craftedURLsList = DataHandling.readFileAllLine(CRAFTED_URLS_PATH);
            if (craftedURLsList.size()==0)
            {
                String beginURLs = DataHandling.readFileAll(BEGIN_URLS_PATH);
                DataHandling.writeFile(CRAFTED_URLS_PATH, beginURLs + "\n0\n", false);
                deque.addLast(new Pair(beginURLs, 0));
                craftedURLsHashMap.put(beginURLs, 0);
            }
            else
            {
                for (int i = 0; i < craftedURLsList.size(); i+=2)
                {
                    String urlString = craftedURLsList.get(i);
                    urlString = filterURL(urlString);
                    int depth = Integer.parseInt(craftedURLsList.get(i+1));
                    if (WikiDataHandling.checkURL(urlString, false) == false) continue;
                    if (existInAnalysedURL(urlString)) continue;
                    craftedURLsHashMap.put(urlString, depth);
                    deque.addLast(new Pair(urlString, depth));
                }
            }

            while(deque.size()!=0)
            {
                int depth = deque.getFirst().second;
                String urlString = deque.getFirst().first;
                if ( depth <= 3 && totalAnalysed <= limitAmountAnalysis)
                {
                    entityAnalys(urlString, depth, true);
                    totalAnalysed++;
                }
                deque.removeFirst();
            }
        }
        getDataCallBack();
    }

    /**
     * Set the limitation of the number of entities to analyze.
     * @param newLimit
     */
    public void setBruteForceAnalyseLimit(int newLimit)
    {
        limitAmountAnalysis = newLimit;
    }

    /**
     * This method is a filter for URLs to remove unnecessary things. 
     * @param urlString
     * @return String after applying the filter.
     * @throws Exception
     * @apiNote Default method does not change the URL. Should be overwritten in a subclass.
     */
    public String filterURL(String urlString) throws Exception
    {
        return urlString;
    }

    /**
     Add URL and its depth to crafed URL list.
     */
    protected final void addToCrafedURL(String urlString, int depth) throws Exception
    {
        if (craftedURLsHashMap.containsKey(urlString) == false) {
            if (depth < 3)
            {
                deque.add(new Pair(urlString, depth + 1));
                String content = urlString + '\n' + String.valueOf(depth+1)+ '\n';
                DataHandling.writeFile(CRAFTED_URLS_PATH, content, true);
                craftedURLsHashMap.put(urlString, depth + 1);
            }
        }
        return;
    }

    /**
     * Check if {@code urlString} has been processed before.
     * @return If {@code urlString} has been processed, return {@code true}; otherwise, return {@code false}.
     */
    protected final boolean existInAnalysedURL(String urlString)
    {
        if (failedURLsHashSet.contains(urlString)) return true;
        if (analysedURLsHashSet.contains(urlString)) return true;
        return false;
    }

    /**
     * Add {@code urlString} to {@code ANALYSED_URLS_PATH}.
     */
    protected final void addToAnalysedURL(String urlString) throws Exception
    {
        if (!existInAnalysedURL(urlString))
        {
            analysedURLsHashSet.add(urlString);
            DataHandling.writeFile(ANALYSED_URLS_PATH, urlString + '\n', true);
        }
    }

    /**
     * Add {@code urlString} to {@code FAILED_URLS_PATH}.
     */
    protected final void addToFailedURL(String urlString) throws Exception
    {
        if (!existInAnalysedURL(urlString))
        {
            failedURLsHashSet.add(urlString);
            DataHandling.writeFile(FAILED_URLS_PATH, urlString + '\n', true);
        }
    }

    /**
     * A callback fucntion for getData
     * @apiNote 
     * @throws Exception
     */
    public void getDataCallBack() throws Exception
    {
        return;
    }

    /**
     * Check if the URL is valid.
     * @return {@code true} if the URL is valid; otherwise, return {@code false}.
     */
    public boolean checkURL(String urlString) throws Exception {
        throw new UnsupportedOperationException("Unimplemented method 'checkURL' from EntityHandling. Must be overriden in subclasss.");
    }

    /**
     * This method helps to gather information about entities that have a connection to Vietnam.
     */
    protected void getVietnamRelatedEntity() throws Exception {
        throw new UnsupportedOperationException("Unimplemented method 'getVietnamRelatedEntity' from EntityHandling. Must be overriden in subclasss.");
    }

    /**
     * Analize an entity to make sure it is related to Vietnam and write it into logs.
     */
    /*
    protected boolean checkRelated(String data, boolean forceRelated) throws Exception {
        throw new UnsupportedOperationException("Unimplemented method 'checkRelated' from EntityHandling. Must be overriden in subclasss.");
    }
    */

    protected void entityAnalys(String url, int depth, boolean forceRelated) throws Exception {
        throw new UnsupportedOperationException("Unimplemented method 'entityAnalys' from EntityHandling. Must be overriden in subclasss.");
    }

    protected void getWikiProperties() throws Exception{
        throw new UnsupportedOperationException("Unimplemented method 'entityAnalys' from EntityHandling. Must be overriden in subclasss.");
    }

}
