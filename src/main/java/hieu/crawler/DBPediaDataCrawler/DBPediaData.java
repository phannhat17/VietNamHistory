package crawler.DBPediaDataCrawler;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

import crawler.DataManage.BruteForceData;
import crawler.DataManage.DataHandling;
import crawler.WikiDataCrawler.WikiDataHandling;

public class DBPediaData extends BruteForceData {
    
    public DBPediaData(String path) throws Exception
    {
        super(path);
        DataHandling.changeRequestRate(100);
    }

    public DBPediaData() throws Exception
    {
        throw new IllegalArgumentException("File path must be provided.");
    }

    public static void main(String[] args) throws Exception {
        DBPediaData dbpediaData = new DBPediaData("E:/Code/Java/OOP_Project/saveddata/DBPedia/");
        dbpediaData.getBruteForceData();
        dbpediaData.syncData();
    }

    /**
     * Convert the URL to their true form to be accessed by the Internet.
     */
    @Override
    public String filterURL(String urlString) throws Exception
    {
        int start = 0;
        int id = 0;
        for ( int i = 0; i < 4; i++ )
        {
            id = urlString.indexOf("/",start);
            start = id + 1;
        }
        String rootURL = urlString.substring(0, start);
        String name = DataHandling.unicodeDecode(urlString.replace(rootURL, ""));
        return rootURL + DataHandling.unicodeDecode(name);
    }

    /**
     * Analize an entity to make sure it is related to Vietnam and write it to logs.<p>
     * Get the JSON content of this URL.
     * @apiNote The entity data is in "EntityJson" folder. 
     */
    @Override
    protected void entityAnalys(String url, int depth, boolean forceRelated) throws Exception {
        if (WikiDataHandling.checkURL(url, false)==false) return;
        url = filterURL(url);
        url = url.replace("http:", "https:");
        if (url.contains("/resource/"))
        {
            url = url.replace("/resource/","/data/");
            url = url + ".json";
        }

        String entityName = url.replace("https://dbpedia.org/data/", "");
        String content;
        if (DataHandling.fileExist(ENTITY_JSON_PATH + entityName) == true)
        {
            if (!existInAnalysedURL(url))
            {
                DataHandling.writeFile(ANALYSED_URLS_PATH, url + '\n', true);
            }
        }
        else
        {
            content = DataHandling.getDataFromURL(url).toString();
            // Check related
            if (checkRelated(content) == false)
            {
                DataHandling.writeFile(FAILED_URLS_PATH, url + '\n', true);
                return;
            }

            DataHandling.writeFile(ENTITY_JSON_PATH + entityName, content , false);
            if (!existInAnalysedURL(url))
            {
                DataHandling.writeFile(ANALYSED_URLS_PATH, url + '\n', true);
            }
        }

        int strBegin = 0;
        int strEnd = 0;
        
        content = DataHandling.readFileAll(ENTITY_JSON_PATH + entityName);
        while(true)
        {
            strBegin = content.indexOf("http://dbpedia.org/resource/", strEnd);
            if (strBegin == -1) break;
            strEnd = content.indexOf("\"", strBegin);
            if (strEnd == -1 ) break;
            String refURL = content.substring(strBegin, strEnd);
            refURL.replace("http:", "https:");
            if (checkURL(refURL)==false) continue;
            refURL = refURL.replace("http:", "https:");
            if (refURL.contains("/resource/"))
            {
                refURL = refURL.replace("/resource/","/data/");
                refURL = refURL + ".json";
            }
            refURL = filterURL(refURL);
            addToCrafedURL(refURL, depth);
        }
        return;
    }

    private static final char[] BANNED_CHRS = {'/', '/', '?', '*', ':', '>', '<', '|', '\"'};
    
    @Override
    public boolean checkURL(String url) throws Exception {
        url = url.replace("http:", "https:");
        if (!url.contains("https://dbpedia.org/resource/") && !url.contains("https://dbpedia.org/data/")){
            return false;
        }
        if (url.chars().filter(ch -> ch == ':').count() > 1) {
            return false;
        }
        int index = 0;
        for ( int i = 0; i < 4; i++ )
        {
            index = url.indexOf("/",index) + 1;
        }
        String name = DataHandling.unicodeDecode(url.replace(url.substring(0, index), ""));

        for (char c: BANNED_CHRS){
            if (name.contains(Character.toString(c))){
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the entity is related to Vietnam.
     * @param data String content of DBPedia JSON item.
     * @return Return {@code true} if it is related; otherwise return {@code false}.
     */
    public boolean checkRelated(String data) throws Exception {
        for (String vietnamEntity: vietnamEntityHashSet)
        {
            if (((String) data).contains(vietnamEntity))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void getVietnamRelatedEntity() throws Exception {
        vietnamEntityHashSet.clear();
        vietnamEntityHashSet.add("http://dbpedia.org/resource/Vietnam");
    }
    
    /**
     * Get all properties of all entities and save it to folder "Properties".
     * @throws Exception
     */
    @Override
    protected void getWikiProperties() throws Exception
    {
        return;
    }

    public static String convertCamelCase(String input) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                output.append(" ");
            }
            output.append(Character.toLowerCase(c));
        }
        return output.toString();
    }

    public void syncData() throws Exception
    {
        String[] bigCategories = {"địa điểm du lịch, di tích lịch sử", "lễ hội văn hóa", "nhân vật lịch sử", "sự kiện lịch sử", "triều đại lịch sử"};
        String wikiEntityPath = "E:/Code/Java/OOP_Project/saveddata/Wikipedia/logs/EntityJson";
        String wikiPropPath = "E:/Code/Java/OOP_Project/saveddata/Wikipedia/logs/EntityProperties";
        String wikiDataPath = "E:/Code/Java/OOP_Project/saveddata/Wikipedia/data/";

        HashSet<String> qIDHashSet = new HashSet<>();
        for (String bigCategory: bigCategories)
        {
            String path = wikiDataPath + bigCategory;
            HashSet<String> fileList = DataHandling.listAllFiles(path);
            for (String fileName: fileList)
            {
                qIDHashSet.add(fileName.replaceAll(".json",""));
            }
        }
        String dbEntityFolder = ENTITY_JSON_PATH;

        JSONObject wikiUrlMapped = new JSONObject();
        JSONObject rawWikiUrlMapped = DataHandling.getJSONFromFile("E:/Code/Java/OOP_Project/saveddata/Wikipedia/logs/URLToEntities.json");
        Iterator<String> URLs = ((JSONObject) rawWikiUrlMapped).keys();
        while (URLs.hasNext()) {
            String url = URLs.next();
            wikiUrlMapped.put(DataHandling.urlDecode(url), rawWikiUrlMapped.getString(url));
        }

        JSONObject selected = new JSONObject(); 
        JSONObject selectedP = new JSONObject();

        if (!DataHandling.fileExist(LOGS_PATH + "wikiMapped.json") || !DataHandling.fileExist(LOGS_PATH + "wikiMappedProp.json"))
        {
            HashSet<String> files = DataHandling.listAllFiles(dbEntityFolder);

            for (String fileName: files)
            {
                String filePath = dbEntityFolder + fileName;
                String key1 = "", key2 = "";
                JSONObject json = DataHandling.getJSONFromFile(filePath);
                Iterator<String> keys = ((JSONObject) json).keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONObject value = json.getJSONObject(key);
                    if (value.has("http://xmlns.com/foaf/0.1/primaryTopic"))
                    {
                        key1 = key;
                    }
                    if (value.has("http://xmlns.com/foaf/0.1/isPrimaryTopicOf"))
                    {
                        key2 = value.getJSONArray("http://xmlns.com/foaf/0.1/isPrimaryTopicOf").getJSONObject(0).getString("value");
                    }
                }
                if (!key1.equals(key2))
                {
                    DataHandling.print("Something wrong", key1, key2);
                    break;
                }
                key1 = DataHandling.unicodeDecode(key1).replace("http:", "https:");
                if (wikiUrlMapped.has(key1))
                {
                    String qID = wikiUrlMapped.getString(key1);
                    if (qIDHashSet.contains(qID))
                    {
                        selected.put(fileName, qID);
                    }
                    else{
                        String label = WikiDataHandling.getWikiEntityViLabel(qID, wikiEntityPath, wikiPropPath);
                        if (!label.isEmpty())
                        {
                            selectedP.put(fileName, label);
                        }
                    }
                }   
            }
            DataHandling.writeFile(LOGS_PATH + "wikiMapped.json", selected.toString(), false);
            DataHandling.writeFile(LOGS_PATH + "wikiMappedProp.json", selectedP.toString(), false);
        }
        else
        {
            selected = DataHandling.getJSONFromFile(LOGS_PATH + "wikiMapped.json");
            selectedP = DataHandling.getJSONFromFile(LOGS_PATH + "wikiMappedProp.json");
        }
                
        JSONObject mappedWikiProp = new JSONObject();
        if (!DataHandling.fileExist(LOGS_PATH + "MappedWikiProp.json"))
        {
            HashSet<String> files = DataHandling.listAllFiles(wikiPropPath);
            for (String fileName: files)
            {
                if (!fileName.contains("P")) continue;
                String pID = fileName.replace(".json", "");
                String propViLabel = WikiDataHandling.getWikiEntityViLabel(pID, wikiPropPath, wikiPropPath);
                if (propViLabel.isEmpty()) continue;
                JSONObject json = DataHandling.getJSONFromFile(wikiPropPath + "/" + fileName);
                JSONObject entity = json.getJSONObject("entities").getJSONObject(pID);
                String propEngLabel = entity.getJSONObject("labels").getJSONObject("en").getString("value");
                mappedWikiProp.put(propEngLabel, propViLabel);
                JSONObject aliases = entity.getJSONObject("aliases");
                if (!aliases.has("en")) continue;
                JSONArray propEngAliases = aliases.getJSONArray("en");
                for (Object propEngAlias: propEngAliases)
                {
                    JSONObject propAliasObj = (JSONObject)propEngAlias;
                    String alias = propAliasObj.getString("value");
                    mappedWikiProp.put(alias, propViLabel);
                }
            }
            DataHandling.writeFile(LOGS_PATH + "MappedWikiProp.json", mappedWikiProp.toString(), false);
        }
        else
        {
            mappedWikiProp = DataHandling.getJSONFromFile(LOGS_PATH + "MappedWikiProp.json");
        }

        JSONObject dbpediaPropertyTranslate = new JSONObject();
        if (!DataHandling.fileExist(LOGS_PATH + "DBPediaPropertyTranslate.json"))
        {
            if (!DataHandling.fileExist(LOGS_PATH + "AllProperties.txt"))
            {
                Iterator<String> keys = selected.keys();
                while(keys.hasNext())
                {
                    JSONObject json = DataHandling.getJSONFromFile(dbEntityFolder + keys.next());
                    Iterator<String> firstFloorKeys = json.keys();
                    while(firstFloorKeys.hasNext())
                    {
                        String firstFloorKey = firstFloorKeys.next();
                        JSONObject firstFloorJson = json.getJSONObject(firstFloorKey);
                        Iterator<String> secondFloorKeys = firstFloorJson.keys();
                        while(secondFloorKeys.hasNext())
                        {
                            String propertyStr = secondFloorKeys.next();
                            if (propertyStr.contains("wiki")||propertyStr.contains("Wiki")) continue;
                            String p;
                            if (propertyStr.contains("http://dbpedia.org/property/"))
                            {
                                p = propertyStr.replace("http://dbpedia.org/property/", "");
                            }
                            else if (propertyStr.contains("http://dbpedia.org/ontology/"))
                            {
                                p = propertyStr.replace("http://dbpedia.org/ontology/", "");
                            }
                            else continue;
                            if (p.matches(".*[0-9].*")) continue;
                            if (p.length()<=2) continue;
                            String pConvert = convertCamelCase(p);
                            dbpediaPropertyTranslate.put(pConvert, "");
                        }
                    }
                }
                Iterator<String> propKeys = dbpediaPropertyTranslate.keys();
                while(propKeys.hasNext())
                {
                    DataHandling.writeFile(LOGS_PATH + "AllProperties.txt", propKeys.next() + "\n", true);
                }
            }
            else
            {
                List<String> lines = DataHandling.readFileAllLine(LOGS_PATH + "AllProperties.txt");
                List<String> trans = DataHandling.readFileAllLine(LOGS_PATH + "Translate.txt");

                for (int i = 0; i < lines.size(); i++)
                {
                    String propertyName = lines.get(i);
                    if (mappedWikiProp.has(propertyName))
                    {
                        dbpediaPropertyTranslate.put(lines.get(i), mappedWikiProp.getString(propertyName));
                    }
                    else {

                        dbpediaPropertyTranslate.put(lines.get(i), trans.get(i));
                    }
                }
                DataHandling.writeFile(LOGS_PATH + "DBPediaPropertyTranslate.json", dbpediaPropertyTranslate.toString(), false);
            }
        }
        else
        {
            dbpediaPropertyTranslate = DataHandling.getJSONFromFile(LOGS_PATH + "DBPediaPropertyTranslate.json");
        }

        /*
         * Iterate all selected files
         */
        Iterator<String> keys = selected.keys();
        while (keys.hasNext()) {
            String fileName = keys.next();
            JSONObject analizedJSON = new JSONObject();
            JSONObject claims = new JSONObject();
            JSONObject json = DataHandling.getJSONFromFile(dbEntityFolder + fileName);
            Iterator<String> firstFloorKeys = json.keys();
            String mainKey = "http://dbpedia.org/resource/" + fileName.replace(".json", "");
            while(firstFloorKeys.hasNext())
            {
                String firstFloorKey = firstFloorKeys.next();
                if (firstFloorKey.equals(mainKey))
                {
                    JSONObject mainJSON = json.getJSONObject(mainKey);
                    Iterator<String> secondFloorKeys = mainJSON.keys();
                    while(secondFloorKeys.hasNext())
                    {
                        String secondFloorKey = secondFloorKeys.next();
                        String propertyName = convertCamelCase(secondFloorKey.replace("http://dbpedia.org/ontology/", "").replace("http://dbpedia.org/property/", ""));
                        if (!dbpediaPropertyTranslate.has(propertyName)) continue;
                        if (!mappedWikiProp.has(propertyName)) continue;
                        propertyName = dbpediaPropertyTranslate.getString(propertyName);
                        JSONArray secondFloorArray = mainJSON.getJSONArray(secondFloorKey);
                        JSONArray analizedJsonArray = new JSONArray();
                        for (int i=0;i < secondFloorArray.length() ; ++i){
                            JSONObject thirdFloorProp = secondFloorArray.getJSONObject(i);
                            if (thirdFloorProp.has("lang")) continue;
                            if (thirdFloorProp.getString("type").equals("uri"))
                            {
                                String value = thirdFloorProp.getString("value");
                                if (!value.contains("http://dbpedia.org/resource/")) continue;
                                value = value.replace("http://dbpedia.org/resource/", "") + ".json";
                                if (selected.has(value))
                                {
                                    JSONObject info = new JSONObject();
                                    info.put("type", "wikibase-item");
                                    String id = selected.getString(value);
                                    info.put("id", id);
                                    info.put("value", WikiDataHandling.getWikiEntityViLabel(id, wikiEntityPath, wikiPropPath));
                                    analizedJsonArray.put(info);
                                }
                                else if (selectedP.has(value))
                                {
                                    JSONObject info = new JSONObject();
                                    info.put("type", "string");
                                    info.put("value", selectedP.getString(value));
                                    analizedJsonArray.put(info);
                                }
                            }
                            else if (thirdFloorProp.has("datatype"))
                            {
                                String datatype = thirdFloorProp.getString("datatype");
                                if (datatype.equals("http://www.w3.org/2001/XMLSchema#date"))
                                {
                                    JSONObject info = new JSONObject();
                                    info.put("type", "string");
                                    String dateStr = thirdFloorProp.getString("value");
                                    LocalDate date = LocalDate.parse(dateStr);
                                    String formattedDate = date.format(DateTimeFormatter.ofPattern("'ngày' dd 'tháng' MM 'năm' yyyy"));
                                    info.put("value", formattedDate);
                                    analizedJsonArray.put(info);
                                }
                            }
                        }
                        if (analizedJsonArray.length()>0)
                        {
                            claims.put(propertyName, analizedJsonArray);
                        }
                    }
                }
                else{
                    if (!firstFloorKey.contains("http://dbpedia.org/resource/")) continue;
                    String key = firstFloorKey.replace("http://dbpedia.org/resource/", "") + ".json";
                    if (!selected.has(key) && !selectedP.has(key)) continue;
                    JSONObject info = new JSONObject();
                    if (selected.has(key))
                    {
                        info.put("type", "wikibase-item");
                        String id = selected.getString(key);
                        info.put("id", id);
                        info.put("value", WikiDataHandling.getWikiEntityViLabel(id, wikiEntityPath, wikiPropPath));
                    }
                    else
                    {
                        info.put("type", "string");
                        info.put("value", selectedP.getString(key));
                    }

                    JSONObject passiveJSON = json.getJSONObject(firstFloorKey);
                    Iterator<String> secondFloorKeys = passiveJSON.keys();
                    while(secondFloorKeys.hasNext())
                    {
                        String propertyName = convertCamelCase(secondFloorKeys.next().replace("http://dbpedia.org/ontology/", "").replace("http://dbpedia.org/property/", ""));
                        if (!dbpediaPropertyTranslate.has(propertyName)) continue;
                        propertyName = dbpediaPropertyTranslate.getString(propertyName) + " của";

                        if (!claims.has(propertyName))
                        {
                            JSONArray jsonArr = new JSONArray();
                            jsonArr.put(info);
                            claims.put(propertyName, jsonArr);
                        }
                        else
                        {
                            claims.getJSONArray(propertyName).put(info);
                        }
                    }
                }
            }
            Iterator<String> claimKeys = claims.keys();
            while(claimKeys.hasNext())
            {
                String propertyName = claimKeys.next();
                JSONArray jsonArr = claims.getJSONArray(propertyName);
                HashSet<String> nameSet = new HashSet<>();
                List<Integer> eraseList = new ArrayList<>();
                for (int i = 0; i < jsonArr.length(); i++)
                {
                    String value = jsonArr.getJSONObject(i).getString("value");
                    if (nameSet.contains(value))
                    {
                        eraseList.add(i);
                    }
                    else
                    {
                        nameSet.add(value);
                    }
                }
                for (int i = eraseList.size() - 1; i >= 0 ; i--)
                {
                    jsonArr.remove(eraseList.get(i));
                }
            }
            if (claims.length() == 0) continue;
            analizedJSON.put("claims", claims);
            String qID = selected.getString(fileName);
            String writePath = DATA_PATH + qID + ".json";
            DataHandling.writeFile(writePath,  analizedJSON.toString(), false);
        }

    }
}
