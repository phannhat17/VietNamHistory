package com.vietnam.history.crawler.dbpediadatacrawler;

import com.vietnam.history.crawler.datamanage.BruteForceData;
import com.vietnam.history.crawler.datamanage.DataHandling;
import com.vietnam.history.crawler.myinterface.NonWikiCrawler;
import com.vietnam.history.crawler.wikidatacrawler.WikiDataHandling;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class DBPediaData extends BruteForceData implements NonWikiCrawler {

    public static final String[] BIG_CATEGORIES = {"địa điểm du lịch, di tích lịch sử", "lễ hội văn hóa", "nhân vật lịch sử", "sự kiện lịch sử", "triều đại lịch sử"};
    private static final char[] BANNED_CHRS = {'/', '/', '?', '*', ':', '>', '<', '|', '\"'};
    private HashSet<String> qIDHashSet = new HashSet<>();
    private JSONObject wikiUrlMapped = new JSONObject();
    private JSONObject selectedQ = new JSONObject();
    private JSONObject selectedP = new JSONObject();
    private JSONObject dbpediaPropertyTranslate = new JSONObject();
    private JSONObject mappedWikiProp = new JSONObject();

    public DBPediaData(String path) throws Exception {
        super(path);
        DataHandling.changeRequestRate(100);
    }

    public DBPediaData() throws Exception {
        throw new IllegalArgumentException("File path must be provided.");
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

    public static HashSet<String> getAllQid(String dataPath) {
        HashSet<String> qIDHashSet = new HashSet<>();
        for (String bigCategory : BIG_CATEGORIES) {
            String path = dataPath + bigCategory;
            HashSet<String> fileList = DataHandling.listAllFiles(path);
            for (String fileName : fileList) {
                qIDHashSet.add(fileName.replaceAll(".json", ""));
            }
        }
        return qIDHashSet;
    }

    public static JSONObject getWikiUrlToEntity(String wikiPath) throws Exception {
        JSONObject wikiUrlMapped = new JSONObject();
        JSONObject rawWikiUrlMapped = DataHandling.getJSONFromFile(wikiPath + "logs/URLToEntities.json");
        for (String url : DataHandling.getAllKeys(rawWikiUrlMapped)) {
            wikiUrlMapped.put(DataHandling.urlDecode(url), rawWikiUrlMapped.getString(url));
        }
        return wikiUrlMapped;
    }

    /**
     * Convert the URL to their true form to be accessed by the Internet.
     */
    @Override
    public String filterURL(String urlString) throws Exception {
        int start = 0;
        int id = 0;
        for (int i = 0; i < 4; i++) {
            id = urlString.indexOf("/", start);
            start = id + 1;
        }
        String rootURL = urlString.substring(0, start);
        String name = DataHandling.unicodeDecode(urlString.replace(rootURL, ""));
        return rootURL + DataHandling.unicodeDecode(name);
    }

    /**
     * Analize an entity to make sure it is related to Vietnam and write it to logs.<p>
     * Get the JSON content of this URL.
     *
     * @apiNote The entity data is in "EntityJson" folder.
     */
    @Override
    protected void entityAnalys(String url, int depth, boolean forceRelated) throws Exception {
        if (!WikiDataHandling.checkURL(url, false)) return;
        url = filterURL(url);
        url = url.replace("http:", "https:");
        if (url.contains("/resource/")) {
            url = url.replace("/resource/", "/data/");
            url = url + ".json";
        }

        String entityName = url.replace("https://dbpedia.org/data/", "");
        String content;
        if (DataHandling.fileExist(ENTITY_JSON_PATH + entityName)) {
            if (!existInAnalysedURL(url)) {
                DataHandling.writeFile(ANALYSED_URLS_PATH, url + '\n', true);
            }
        } else {
            content = DataHandling.getDataFromURL(url).toString();
            // Check related
            if (!checkRelated(content)) {
                DataHandling.writeFile(FAILED_URLS_PATH, url + '\n', true);
                return;
            }

            DataHandling.writeFile(ENTITY_JSON_PATH + entityName, content, false);
            if (!existInAnalysedURL(url)) {
                DataHandling.writeFile(ANALYSED_URLS_PATH, url + '\n', true);
            }
        }

        int strBegin = 0;
        int strEnd = 0;

        content = DataHandling.readFileAll(ENTITY_JSON_PATH + entityName);
        while (true) {
            strBegin = content.indexOf("http://dbpedia.org/resource/", strEnd);
            if (strBegin == -1) break;
            strEnd = content.indexOf("\"", strBegin);
            if (strEnd == -1) break;
            String refURL = content.substring(strBegin, strEnd);
            refURL.replace("http:", "https:");
            if (!checkURL(refURL)) continue;
            refURL = refURL.replace("http:", "https:");
            if (refURL.contains("/resource/")) {
                refURL = refURL.replace("/resource/", "/data/");
                refURL = refURL + ".json";
            }
            refURL = filterURL(refURL);
            addToCrafedURL(refURL, depth);
        }
    }

    @Override
    public boolean checkURL(String url) throws Exception {
        url = url.replace("http:", "https:");
        if (!url.contains("https://dbpedia.org/resource/") && !url.contains("https://dbpedia.org/data/")) {
            return false;
        }
        if (url.chars().filter(ch -> ch == ':').count() > 1) {
            return false;
        }
        int index = 0;
        for (int i = 0; i < 4; i++) {
            index = url.indexOf("/", index) + 1;
        }
        String name = DataHandling.unicodeDecode(url.replace(url.substring(0, index), ""));

        for (char c : BANNED_CHRS) {
            if (name.contains(Character.toString(c))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the entity is related to Vietnam.
     *
     * @param data String content of DBPedia JSON item.
     * @return Return {@code true} if it is related; otherwise return {@code false}.
     */
    public boolean checkRelated(String data) {
        for (String vietnamEntity : vietnamEntityHashSet) {
            if (data.contains(vietnamEntity)) {
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

    private void mapWithWikiEntitiesAndPropties(String wikiEntityPath, String wikiPropPath) throws Exception {
        if (!DataHandling.fileExist(LOGS_PATH + "wikiMapped.json") || !DataHandling.fileExist(LOGS_PATH + "wikiMappedProp.json")) {
            HashSet<String> files = DataHandling.listAllFiles(ENTITY_JSON_PATH);

            for (String fileName : files) {
                String filePath = ENTITY_JSON_PATH + fileName;
                String key1 = "";
                String key2 = "";
                JSONObject json = DataHandling.getJSONFromFile(filePath);
                Iterator<String> keys = (json).keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONObject value = json.getJSONObject(key);
                    if (value.has("http://xmlns.com/foaf/0.1/primaryTopic")) {
                        key1 = key;
                    }
                    if (value.has("http://xmlns.com/foaf/0.1/isPrimaryTopicOf")) {
                        key2 = value.getJSONArray("http://xmlns.com/foaf/0.1/isPrimaryTopicOf").getJSONObject(0).getString("value");
                    }
                }
                if (!key1.equals(key2)) {
                    DataHandling.print("Something wrong", key1, key2);
                    break;
                }
                key1 = DataHandling.unicodeDecode(key1).replace("http:", "https:");
                if (wikiUrlMapped.has(key1)) {
                    String qID = wikiUrlMapped.getString(key1);
                    if (qIDHashSet.contains(qID)) {
                        selectedQ.put(fileName, qID);
                    } else {
                        String label = WikiDataHandling.getWikiEntityViLabel(qID, wikiEntityPath, wikiPropPath);
                        if (!label.isEmpty()) {
                            selectedP.put(fileName, label);
                        }
                    }
                }
            }
            DataHandling.writeFile(LOGS_PATH + "wikiMapped.json", selectedQ.toString(), false);
            DataHandling.writeFile(LOGS_PATH + "wikiMappedProp.json", selectedP.toString(), false);
        } else {
            selectedQ = DataHandling.getJSONFromFile(LOGS_PATH + "wikiMapped.json");
            selectedP = DataHandling.getJSONFromFile(LOGS_PATH + "wikiMappedProp.json");
        }
    }

    private JSONObject translateDBPediaPropName(JSONObject mappedWikiProp) throws Exception {
        JSONObject dbpediaPropertyTranslate = new JSONObject();
        if (!DataHandling.fileExist(LOGS_PATH + "DBPediaPropertyTranslate.json")) {
            if (!DataHandling.fileExist(LOGS_PATH + "AllProperties.txt")) {
                Iterator<String> keys = selectedQ.keys();
                while (keys.hasNext()) {
                    JSONObject json = DataHandling.getJSONFromFile(ENTITY_JSON_PATH + keys.next());
                    for (String firstFloorKey : DataHandling.getAllKeys(json)) {
                        for (String propertyStr : DataHandling.getAllKeys(json.getJSONObject(firstFloorKey))) {
                            if (propertyStr.contains("wiki") || propertyStr.contains("Wiki")) continue;
                            String p;
                            if (propertyStr.contains("http://dbpedia.org/property/")) {
                                p = propertyStr.replace("http://dbpedia.org/property/", "");
                            } else if (propertyStr.contains("http://dbpedia.org/ontology/")) {
                                p = propertyStr.replace("http://dbpedia.org/ontology/", "");
                            } else continue;
                            if (p.matches(".*[0-9].*")) continue;
                            if (p.length() <= 2) continue;
                            String pConvert = convertCamelCase(p);
                            dbpediaPropertyTranslate.put(pConvert, "");
                        }
                    }
                }
                Iterator<String> propKeys = dbpediaPropertyTranslate.keys();
                while (propKeys.hasNext()) {
                    DataHandling.writeFile(LOGS_PATH + "AllProperties.txt", propKeys.next() + "\n", true);
                }
            }
            List<String> lines = DataHandling.readFileAllLine(LOGS_PATH + "AllProperties.txt");
            List<String> trans = DataHandling.readFileAllLine(LOGS_PATH + "Translate.txt");

            for (int i = 0; i < lines.size(); i++) {
                String propertyName = lines.get(i);
                if (mappedWikiProp.has(propertyName)) {
                    dbpediaPropertyTranslate.put(lines.get(i), mappedWikiProp.getString(propertyName));
                } else {

                    dbpediaPropertyTranslate.put(lines.get(i), trans.get(i));
                }
            }
            DataHandling.writeFile(LOGS_PATH + "DBPediaPropertyTranslate.json", dbpediaPropertyTranslate.toString(), false);
        } else {
            dbpediaPropertyTranslate = DataHandling.getJSONFromFile(LOGS_PATH + "DBPediaPropertyTranslate.json");
        }
        return dbpediaPropertyTranslate;
    }

    public JSONObject mapWithWikiPropName(String wikiEntityPath, String wikiPropPath) throws Exception {
        JSONObject mappedWikiProp = new JSONObject();
        if (!DataHandling.fileExist(LOGS_PATH + "MappedWikiProp.json")) {
            HashSet<String> files = DataHandling.listAllFiles(wikiPropPath);
            for (String fileName : files) {
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
                for (Object propEngAlias : propEngAliases) {
                    JSONObject propAliasObj = (JSONObject) propEngAlias;
                    String alias = propAliasObj.getString("value");
                    mappedWikiProp.put(alias, propViLabel);
                }
            }
            DataHandling.writeFile(LOGS_PATH + "MappedWikiProp.json", mappedWikiProp.toString(), false);
        } else {
            mappedWikiProp = DataHandling.getJSONFromFile(LOGS_PATH + "MappedWikiProp.json");
        }

        return mappedWikiProp;
    }

    public JSONArray createAnalizeJsonArrayForDbpediaProp(JSONArray secondFloorArray, String wikiEntityPath, String wikiPropPath) throws JSONException, Exception {
        JSONArray analizedJsonArray = new JSONArray();
        for (int i = 0; i < secondFloorArray.length(); ++i) {
            JSONObject thirdFloorProp = secondFloorArray.getJSONObject(i);
            if (thirdFloorProp.has("lang")) continue;
            if (thirdFloorProp.getString("type").equals("uri")) {
                String value = thirdFloorProp.getString("value");
                if (!value.contains("http://dbpedia.org/resource/")) continue;
                value = value.replace("http://dbpedia.org/resource/", "") + ".json";
                if (selectedQ.has(value)) {
                    JSONObject info = new JSONObject();
                    info.put("type", "wikibase-item");
                    String id = selectedQ.getString(value);
                    info.put("id", id);
                    info.put("value", WikiDataHandling.getWikiEntityViLabel(id, wikiEntityPath, wikiPropPath));
                    analizedJsonArray.put(info);
                } else if (selectedP.has(value)) {
                    JSONObject info = new JSONObject();
                    info.put("type", "string");
                    info.put("value", selectedP.getString(value));
                    analizedJsonArray.put(info);
                }
            } else if (thirdFloorProp.has("datatype")) {
                String datatype = thirdFloorProp.getString("datatype");
                if (datatype.equals("http://www.w3.org/2001/XMLSchema#date")) {
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
        return analizedJsonArray;
    }

    private void addActivePropertiesToClaims(JSONObject claims, JSONObject json, String mainKey, String wikiEntityPath, String wikiPropPath) throws JSONException, Exception {
        JSONObject mainJSON = json.getJSONObject(mainKey);
        for (String secondFloorKey : DataHandling.getAllKeys(mainJSON)) {
            String propertyName = convertCamelCase(secondFloorKey.replace("http://dbpedia.org/ontology/", "").replace("http://dbpedia.org/property/", ""));
            if (!dbpediaPropertyTranslate.has(propertyName)) continue;
            if (!mappedWikiProp.has(propertyName)) continue;
            propertyName = dbpediaPropertyTranslate.getString(propertyName);
            JSONArray secondFloorArray = mainJSON.getJSONArray(secondFloorKey);
            JSONArray analizedJsonArray = createAnalizeJsonArrayForDbpediaProp(secondFloorArray, wikiEntityPath, wikiPropPath);
            if (analizedJsonArray.length() > 0) {
                claims.put(propertyName, analizedJsonArray);
            }
        }
    }

    void addPassivePropertiesToClaims(JSONObject claims, JSONObject json, String firstFloorKey, String wikiEntityPath, String wikiPropPath) throws JSONException, Exception {
        if (!firstFloorKey.contains("http://dbpedia.org/resource/")) {
            return;
        }
        String key = firstFloorKey.replace("http://dbpedia.org/resource/", "") + ".json";
        if (!selectedQ.has(key) && !selectedP.has(key)) {
            return;
        }
        JSONObject info = new JSONObject();
        if (selectedQ.has(key)) {
            info.put("type", "wikibase-item");
            String id = selectedQ.getString(key);
            info.put("id", id);
            info.put("value", WikiDataHandling.getWikiEntityViLabel(id, wikiEntityPath, wikiPropPath));
        } else {
            info.put("type", "string");
            info.put("value", selectedP.getString(key));
        }
        JSONObject passiveJSON = json.getJSONObject(firstFloorKey);
        Iterator<String> secondFloorKeys = passiveJSON.keys();
        while (secondFloorKeys.hasNext()) {
            String propertyName = convertCamelCase(secondFloorKeys.next().replace("http://dbpedia.org/ontology/", "").replace("http://dbpedia.org/property/", ""));
            if (!dbpediaPropertyTranslate.has(propertyName)) continue;
            propertyName = dbpediaPropertyTranslate.getString(propertyName) + " của";

            if (!claims.has(propertyName)) {
                JSONArray jsonArr = new JSONArray();
                jsonArr.put(info);
                claims.put(propertyName, jsonArr);
            } else {
                claims.getJSONArray(propertyName).put(info);
            }
        }
    }

    private void filterDbpediaPropertiesInClaims(JSONObject claims) {
        for (String propertyName : DataHandling.getAllKeys(claims)) {
            JSONArray jsonArr = claims.getJSONArray(propertyName);
            HashSet<String> nameSet = new HashSet<>();
            List<Integer> eraseList = new ArrayList<>();
            for (int i = 0; i < jsonArr.length(); i++) {
                String value = jsonArr.getJSONObject(i).getString("value");
                if (nameSet.contains(value)) {
                    eraseList.add(i);
                } else {
                    nameSet.add(value);
                }
            }
            for (int i = eraseList.size() - 1; i >= 0; i--) {
                jsonArr.remove(eraseList.get(i));
            }
        }

    }

    @Override
    public void syncData(String wikiPath) throws Exception {
        String wikiEntityPath = wikiPath + "logs/EntityJson";
        String wikiPropPath = wikiPath + "logs/EntityProperties";
        String wikiDataPath = wikiPath + "data/";

        qIDHashSet = getAllQid(wikiDataPath);
        wikiUrlMapped = getWikiUrlToEntity(wikiPath);
        mapWithWikiEntitiesAndPropties(wikiEntityPath, wikiPropPath);
        mappedWikiProp = mapWithWikiPropName(wikiEntityPath, wikiPropPath);
        dbpediaPropertyTranslate = translateDBPediaPropName(mappedWikiProp);

        for (String fileName : DataHandling.getAllKeys(selectedQ)) {
            JSONObject analizedJSON = new JSONObject();
            JSONObject claims = new JSONObject();
            JSONObject json = DataHandling.getJSONFromFile(ENTITY_JSON_PATH + fileName);
            String mainKey = "http://dbpedia.org/resource/" + fileName.replace(".json", "");
            for (String firstFloorKey : DataHandling.getAllKeys(json)) {
                if (firstFloorKey.equals(mainKey)) {
                    addActivePropertiesToClaims(claims, json, mainKey, wikiEntityPath, wikiPropPath);
                } else {
                    addPassivePropertiesToClaims(claims, json, firstFloorKey, wikiEntityPath, wikiPropPath);
                }
            }
            filterDbpediaPropertiesInClaims(claims);
            if (claims.length() == 0) continue;
            analizedJSON.put("claims", claims);
            String qID = selectedQ.getString(fileName);
            String writePath = DATA_PATH + qID + ".json";
            DataHandling.writeFile(writePath, analizedJSON.toString(), false);
        }
    }

    @Override
    public void getData() throws Exception {
        getBruteForceData();
    }
}
