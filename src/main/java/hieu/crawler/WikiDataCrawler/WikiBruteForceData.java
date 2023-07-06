package crawler.WikiDataCrawler;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import crawler.DataManage.BruteForceData;
import crawler.DataManage.DataHandling;

public class WikiBruteForceData extends BruteForceData {

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

    protected HashSet<String> allQFile = DataHandling.listAllFiles(ENTITY_JSON_PATH);
    protected HashSet<String> allPFile = DataHandling.listAllFiles(ENTITY_PROPERTIES_PATH);

    protected HashMap<String, String> urlToEntityHashMap = new HashMap<>();

    public WikiBruteForceData(){
        throw new IllegalArgumentException("File path must be provided.");
    }

    public WikiBruteForceData(String folderPath) throws Exception{
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
        getVietnamRelatedEntity();
        return;
    }

    @Override
    protected void entityAnalys(String urlString, int depth, boolean forceRelated) throws Exception {
        // Check if urlString is a valid Wikipedia URL .
        urlString = DataHandling.urlDecode(urlString.replace("\n", ""));
        if (WikiDataHandling.checkURL(urlString, false) == false){
            return;
        }
        if (forceRelated == false && existInAnalysedURL(urlString)){
            return;
        }

        if (!urlToEntityHashMap.containsKey(urlString))
        {
            // Get page data from Wiki API
            String wikiPageData = DataHandling.getDataFromURL(urlString).toString();
            String qID = "";
            if (wikiPageData.isEmpty()) return;
            Document doc = Jsoup.parse(wikiPageData);
            qID = WikiDataHandling.getEntityIdFromHtml(doc);
            if (!qID.isEmpty()){
                urlToEntityHashMap.put(urlString, qID);
            }
            // Write the entity data to "EntityJson" and "WebHtml" folder if there's exist a qID
            if (!checkRelated(qID, wikiPageData, forceRelated)){
                if (forceRelated == false)
                    addToFailedURL(urlString);
                return;
            }

            /*
            * Get related URL for this entity.
            * The related URLs is in "EntityReference" folder. 
            */
            // Parse the HTML using Jsoup
            StringBuffer s = new StringBuffer("");
            for (String craftURL: WikiDataHandling.getAllWikiHref(wikiPageData)){
                s.append(craftURL + '\n');
                addToCrafedURL(craftURL, depth);
            }
            DataHandling.writeFile(ENTITY_REFERENCE_PATH + qID + ".txt", s.toString(), true);
            addToAnalysedURL(urlString);
        }
        return;
    }

    public boolean checkRelated(String qID, String wikiPageData, boolean forceRelated) throws Exception {
        if (!qID.isEmpty()){
            DataHandling.writeFile(HTML_PATH + qID + ".html", wikiPageData, false);
        }
        else {
            return false;
        }

        String entityURL = "https://www.wikidata.org/wiki/Special:EntityData/" + qID + ".json";
        String content = DataHandling.getDataFromURL(entityURL).toString();
        if (forceRelated == false){
            if (content.isEmpty())
                return false;
            JSONObject json = new JSONObject(content);
        
            if (WikiDataHandling.jsonAnalysis(json, vietnamEntityHashSet) == false)
                return false;
            if (WikiDataHandling.getWikiEntityViLabel(json, qID).isEmpty())
                return false;
            
            boolean check = false;
            for (String vnWord: WikiDataHandling.VIETNAM_WORD) {
                if (content.contains(vnWord)){
                    check = true;
                    break;
                }
            }
            if (check == false) return false;

            JSONObject entitiyJson = json.getJSONObject("entities").getJSONObject(qID);
            // If an entity has no sitelinks to Wikipedia then that entity is virtual. We will put it into the ENTITY_PROPERTIES_PATH
            if (WikiDataHandling.getWikiSitelink(entitiyJson, qID, "viwiki").isEmpty()) {
                DataHandling.writeFile(ENTITY_PROPERTIES_PATH + qID +".json", content , false);
                return false;
            }
            
            // If an entity is a human (Q5) and there exist at least one year, it must be less than 1962.
            if (WikiDataHandling.getWikiEntityInstance(entitiyJson).equals("Q5")){
                int entityMinYear = WikiDataHandling.getMinYear(entitiyJson);
                if (entityMinYear > 1962 && entityMinYear != 100000) {
                    DataHandling.writeFile(ENTITY_PROPERTIES_PATH + qID +".json", content , false);
                }
            }
        }
        DataHandling.writeFile(ENTITY_JSON_PATH + qID +".json", content , false);
        return true;
    }


    @Override
    public void getDataCallBack() throws Exception
    {
        analyzeBruteForceData();
        return;
    }

    private void analyzeBruteForceData() throws Exception{
        urlToEntities();
        getWikiProperties();
        entityRefFinal();
        entityFinal();
    }

    @Override
    public void getVietnamRelatedEntity() throws Exception{

        if (!DataHandling.fileExist(INITIALIZE_PATH + "FromVietnam.json")){
            throw new FileNotFoundException("Please create file FromVietnam.json that contains entities related to Vietnam in this folder: " + INITIALIZE_PATH);
        }
        JSONArray myJsonArray = new JSONArray(DataHandling.readFileAll(INITIALIZE_PATH + "/FromVietnam.json"));
        vietnamEntityHashSet.clear();

        for (int i = 0; i < myJsonArray.length(); i++) {
            JSONObject tmpJsonObject = myJsonArray.getJSONObject(i);
            String tmpEntity = tmpJsonObject.getString("item");
            tmpEntity = tmpEntity.replace("http://www.wikidata.org/entity/", "");
            vietnamEntityHashSet.add(tmpEntity);
        }

        DataHandling.writeFile(INITIALIZE_PATH + "/VietnamRelated.json", new JSONArray(vietnamEntityHashSet).toString(), false);

        return;
    }


    public final void urlToEntities() throws Exception
    {
        if (DataHandling.fileExist(LOGS_PATH + "URLToEntities.json"))
        {
            JSONObject jsonContent = DataHandling.getJSONFromFile(LOGS_PATH + "URLToEntities.json");
            for (String key: DataHandling.getAllKeys(jsonContent)){
                urlToEntityHashMap.put(key,jsonContent.getString(key));
            }
            return;
        }
        for (String fileName: DataHandling.listAllFiles(ENTITY_JSON_PATH))
        {
            String qID = fileName.replace(".json", "");
            JSONObject entity = DataHandling.getJSONFromFile(ENTITY_JSON_PATH + fileName).getJSONObject("entities").getJSONObject(qID);
            JSONObject sitelinks = entity.getJSONObject(qID).getJSONObject("sitelinks");
            if (sitelinks.has("viwiki")){
                urlToEntityHashMap.put(DataHandling.urlDecode(sitelinks.getJSONObject("viwiki").getString("url")), qID);
            }
            if (sitelinks.has("enwiki")){
                urlToEntityHashMap.put(sitelinks.getJSONObject("enwiki").getString("url"), qID);
            }
        }
        DataHandling.writeFile(LOGS_PATH +  "URLToEntities.json" , (new JSONObject(urlToEntityHashMap)).toString(), false);
    }

    public void entityRefFinal() throws Exception
    {
        HashSet<String> allQRefFile = DataHandling.listAllFiles(ENTITY_REFERENCE_PATH);
        HashMap<String, HashSet<String> > refList = new HashMap<String, HashSet<String>>();
        for (String fileName: allQRefFile)
        {
            String qID = fileName.replace(".txt", "");
            List<String> qRef = DataHandling.readFileAllLine(ENTITY_REFERENCE_PATH + fileName);
            for (String urlString: qRef)
            {
                urlString = DataHandling.urlDecode(urlString);
                if (urlString.isEmpty()) continue;
                if (urlToEntityHashMap.containsKey(urlString))
                {
                    String qID1 = urlToEntityHashMap.get(urlString);
                    if (!refList.containsKey(qID))
                    {
                        HashSet<String> h = new HashSet<>();
                        h.add(qID1);
                        refList.put(qID, h);
                    }
                    else
                    {
                        refList.get(qID).add(qID1);
                    }
                    if (!refList.containsKey(qID1))
                    {
                        HashSet<String> h = new HashSet<>();
                        h.add(qID);
                        refList.put(qID1, h);
                    }
                    else
                    {
                        refList.get(qID1).add(qID);
                    }
                }
            }
        }
        refList.forEach((key, value) -> {
            try {
                DataHandling.writeFile(ENTITY_REF_FINAL_PATH + key + ".json", (new JSONArray(value)).toString() , false);
            } catch (Exception e) {
                DataHandling.print("Can not write to file: " + ENTITY_REF_FINAL_PATH + key + ".json", "content: ", (new JSONArray(value)).toString());
            }
        });
    }

    public final void entityFinal() throws Exception
    {
        //HashSet<String> allPFile = listAllFiles(ENTITY_PROPERTIES_PATH);
        allQFile = DataHandling.listAllFiles(ENTITY_JSON_PATH);
        allPFile = DataHandling.listAllFiles(ENTITY_PROPERTIES_PATH);

        for (String fileName: allQFile)
        {
            if (DataHandling.fileExist(ENTITY_FINAL_PATH + fileName)) {
                continue;
            }
            String qID = fileName.replace(".json", "");
            JSONObject json = WikiDataHandling.getVietnameseWikiReadable(qID, allQFile, allPFile, ENTITY_JSON_PATH, ENTITY_PROPERTIES_PATH, ENTITY_REF_FINAL_PATH, HTML_PATH);
            String writePath = ENTITY_FINAL_PATH + fileName;
            String writeContent = json.toString();
            DataHandling.writeFile(writePath, writeContent, false);
        }
    }

    public final void resetEntityRef() throws Exception
    {
        //HashSet<String> allPFile = listAllFiles(ENTITY_PROPERTIES_PATH);
        allQFile = DataHandling.listAllFiles(ENTITY_JSON_PATH);
        for (String fileName: allQFile)
        {
            JSONObject json = DataHandling.getJSONFromFile(ENTITY_FINAL_PATH + fileName);
            json.put("references", WikiDataHandling.getWikiEntityReferences(fileName, ENTITY_REF_FINAL_PATH, ENTITY_JSON_PATH, ENTITY_PROPERTIES_PATH));
            String writePath = ENTITY_FINAL_PATH + fileName;
            String writeContent = json.toString();
            DataHandling.writeFile(writePath, writeContent, false);
        }
    }

    /**
     * Analyze a JSON Object and add all properties into propertyHashSet
     * @param entityJSON A Wikidata JSON Object.
     * @param entityJSONFileList A list of files in "EntityJSON" folder.
     */
    private void jsonGetPropertiesFromEntity(Object entityJSON, HashSet<String> entityJSONFileList)
    {
        WikiDataHandling.jsonGetPropertiesFromEntity(entityJSON, entityJSONFileList, propertyHashSet);
    }

    protected void getPropertiesInJson(String root, String fileName, HashSet<String> entityJSONFileList) throws Exception
    {
        String content = DataHandling.readFileAll(ENTITY_JSON_PATH + fileName);
        int last = 0;
        while(true) {
            int start = content.indexOf("http://www.wikidata.org/entity/", last);
            if (start == -1) break;
            int end = content.indexOf("\"", start);
            String qID = (content.substring(start, end)).replace("http://www.wikidata.org/entity/", "");
            propertyHashSet.add(qID);
            last = end;
        }
        JSONObject entityJSON = new JSONObject(DataHandling.readFileAll(ENTITY_JSON_PATH + fileName));
        JSONObject claims = entityJSON.getJSONObject("entities").getJSONObject(fileName.replace(".json","")).getJSONObject("claims");
        jsonGetPropertiesFromEntity(claims, entityJSONFileList);
    }

    /**
     * Get all properties of all entities and save it to folder "Properties".
     * @throws Exception
     */
    protected void getWikiProperties() throws Exception
    {
        if (DataHandling.fileExist(LOGS_PATH + "PropertiesList.json")) {
            JSONArray myJsonArray = new JSONArray(DataHandling.readFileAll(LOGS_PATH + "PropertiesList.json"));
            for (int i = 0; i < myJsonArray.length(); i++) {
                String pID = myJsonArray.getString(i);
                propertyHashSet.add(pID);
            }
        }
        else{
            HashSet<String> entityFileList = DataHandling.listAllFiles(ENTITY_JSON_PATH);
            for (String fileName: entityFileList){
                if (DataHandling.fileExist(ENTITY_JSON_PATH + fileName)) {
                    getPropertiesInJson(ENTITY_JSON_PATH, fileName, entityFileList);
                }
            }
            HashSet<String> propertyFileList = DataHandling.listAllFiles(ENTITY_PROPERTIES_PATH);
            List<String> removePID = new ArrayList<>();
            for (String pID: propertyHashSet) {
                if (!propertyFileList.contains(pID + ".json")) {
                    String data = DataHandling.getDataFromURL("https://www.wikidata.org/wiki/Special:EntityData/" + pID + ".json").toString();
                    if (!WikiDataHandling.getWikiEntityViLabel(new JSONObject(data), pID).isEmpty()) {
                        DataHandling.writeFile(ENTITY_PROPERTIES_PATH + pID + ".json", data, false);
                    }
                    else{
                        removePID.add(pID);
                    }
                }
            }
            for (String pID: removePID){
                propertyHashSet.remove(pID);
            }
            DataHandling.writeFile(LOGS_PATH + "PropertiesList.json", (new JSONArray(propertyHashSet)).toString(), false);
        }
    }

}
