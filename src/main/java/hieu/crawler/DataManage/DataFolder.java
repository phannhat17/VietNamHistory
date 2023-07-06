package crawler.DataManage;

public class DataFolder {
    //root path:
    protected final String ROOT_PATH;

    protected final String DATA_PATH;
    protected final String INITIALIZE_PATH;
    protected final String LOGS_PATH;
    protected final String ENTITY_JSON_PATH;
    //files
    protected final String BEGIN_URLS_PATH;
    protected final String CRAFTED_URLS_PATH;
    protected final String ANALYSED_URLS_PATH;  // Đổi tên thành AcceptedURL
    protected final String FAILED_URLS_PATH;    // Đổi tên thành RejectedURL

    /**
     * Set up an environment for saving data.
     * @param path Your path where the crawled data will be stored.
     */
    public DataFolder(String path)
    {
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

    public DataFolder()
    {
        throw new IllegalArgumentException("File path must be provided");
    }


}
