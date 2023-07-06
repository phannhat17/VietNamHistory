package crawler.WikiDataCrawler;

import crawler.DataManage.DataFolder;
import crawler.DataManage.DataHandling;

import java.io.FileNotFoundException;
import java.util.HashMap;

import org.json.JSONObject;

public class WikiFolder extends DataFolder{
    protected final HashMap<String, String> urlToEntityHashMap = new HashMap<>();

    protected final String ENTITY_PROPERTIES_PATH = LOGS_PATH + "EntityProperties/";
    protected final String HTML_PATH = LOGS_PATH + "WebHtml/";
    protected final String SCARLARLY_PATH = LOGS_PATH + "Scarlarly/";

    protected final String ENTITY_REFERENCE_PATH = LOGS_PATH + "EntityReference/";;
    protected final String EVENT_PATH = DATA_PATH + "sự kiện lịch sử";
    protected final String PLACE_PATH = DATA_PATH + "địa điểm du lịch, di tích lịch sử/";
    protected final String HUMAN_PATH = DATA_PATH + "nhân vật lịch sử/";
    protected final String DYNASTY_PATH = DATA_PATH + "triều đại lịch sử/";
    protected final String FESTIVAL_PATH = DATA_PATH + "lễ hội văn hóa/";

    protected final String ENTITY_FINAL_PATH = LOGS_PATH + "EntityFinal/";
    protected final String ENTITY_REF_FINAL_PATH = LOGS_PATH + "EntityRefFinal/";
    
    public WikiFolder(){
        throw new IllegalArgumentException("File path must be provided.");
    }

    public WikiFolder(String folderPath) throws Exception{
        super(folderPath);
        if (!DataHandling.folderExist(folderPath)) {
            throw new FileNotFoundException("Folder doesn't exist: " + folderPath);
        }
        DataHandling.createFolder(ENTITY_PROPERTIES_PATH);
        DataHandling.createFolder(ENTITY_REFERENCE_PATH);
        DataHandling.createFolder(SCARLARLY_PATH);

        DataHandling.createFolder(EVENT_PATH);
        DataHandling.createFolder(HUMAN_PATH);
        DataHandling.createFolder(PLACE_PATH);
        DataHandling.createFolder(DYNASTY_PATH);
        DataHandling.createFolder(FESTIVAL_PATH);

        DataHandling.createFolder(ENTITY_FINAL_PATH);
        DataHandling.createFolder(ENTITY_REF_FINAL_PATH);

        if(DataHandling.fileExist(LOGS_PATH + "URLToEntities.json"))
        {
            JSONObject jsonContent = DataHandling.getJSONFromFile(LOGS_PATH + "URLToEntities.json");
            for (String key: DataHandling.getAllKeys(jsonContent)){
                urlToEntityHashMap.put(key,(jsonContent).getString(key));
            }
        }
        return;
    }

}
