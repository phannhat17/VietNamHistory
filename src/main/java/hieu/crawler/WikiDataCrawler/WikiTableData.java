package crawler.WikiDataCrawler;

import crawler.DataManage.DataHandling;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class WikiTableData extends WikiBruteForceData {

    public static void main(String[] args) throws Exception {
        WikiTableData wikiTableData = new WikiTableData("E:/Code/Java/OOP_Project/saveddata/Wikipedia/");
        wikiTableData.tableDataQueries();
    }

    public void tableDataQueries() throws Exception{
        tableDynastiesQueries();    // done
        tableLocationsQueries();    // done
    }


    public WikiTableData(){
        throw new IllegalArgumentException("File path must be provided.");
    }

    public WikiTableData(String folderPath) throws Exception{
        super(folderPath);
        return;
    }

    private HashMap<String, String> matchDynasties() throws Exception{
        HashMap<String, String> dynastyHashMap = new HashMap<>();
        for (String fileName: DataHandling.listAllFiles(ENTITY_FINAL_PATH))
        {
            JSONObject json = DataHandling.getJSONFromFile(ENTITY_FINAL_PATH + fileName);
            if (json.getJSONObject("claims").has("là một")){
                JSONArray jsonArr = json.getJSONObject("claims").getJSONArray("là một");
                for (int i = 0; i < jsonArr.length(); i++)
                {
                    if (jsonArr.getJSONObject(i).getString("value").equals("triều đại"))
                    {
                        String dynastyName = json.getString("label");
                        dynastyHashMap.put(dynastyName, json.getString("id"));
                        break;
                    }
                }
            }
        }
        return dynastyHashMap;
    }

    private static String[] kingProp = {"Miếu hiệu", "Tôn hiệu hoặc Thụy hiệu", "Tôn hiệu", "Niên hiệu", "Thế thứ", "Trị vì"};

    private void addKingProp(JSONObject kingClaims, JSONObject rawKingObj){
        WikiDataHandling.addProperties(kingClaims, "là một", "người");
        WikiDataHandling.addProperties(kingClaims, "là một", "vua");
        WikiDataHandling.addProperties(kingClaims, "quốc tịch", "Việt Nam");

        for (String prop: kingProp)
        {
            if (!rawKingObj.has(prop)) continue;
            String value = rawKingObj.getString(prop);
            if (value.isEmpty()) continue;
            WikiDataHandling.addProperties(kingClaims, prop.toLowerCase(), value);
        }
    }

    private JSONObject createKingObj(JSONObject rawKingObj) throws Exception{
        String kingQID = "";
        String kingURL = DataHandling.urlDecode(rawKingObj.getString("link"));
        
        JSONObject kingJsonObject = new JSONObject();
        JSONObject kingClaims = new JSONObject();

        String kingName = "";
        if (urlToEntityHashMap.containsKey(kingURL))
        {
            kingQID = urlToEntityHashMap.get(kingURL);
            kingJsonObject = DataHandling.getJSONFromFile(ENTITY_FINAL_PATH + kingQID + ".json");
            kingClaims = kingJsonObject.getJSONObject("claims");
            kingName = kingJsonObject.getString("label");
        }
        else
        {
            String[] kingType = {"Vua", "Tước hiệu", "Thủ lĩnh", "Tiết độ sứ"};
            for (int j = 0; j < kingType.length; j++) {
                if (rawKingObj.has(kingType[j])) {
                    kingName = rawKingObj.getString(kingType[j]);
                    break;
                }
            }
            //kingQID = "Q" + Integer.toString(kingName.hashCode()).replace("-", "") + "X";
            kingQID = DataHandling.createQID(kingName);
        }

        addKingProp(kingClaims, rawKingObj);

        if (kingQID.contains("X"))
        {
            WikiDataHandling.createNewEntity(kingJsonObject, 
                kingQID, 
                kingName, 
                kingName + " là một vị vua trong lịch sử Việt Nam.", 
                "", 
                new JSONArray(), 
                kingClaims,
                new JSONObject()
            );
            urlToEntityHashMap.put(kingURL, kingQID);
        }
        return kingJsonObject;
    }

    private JSONObject createDynastyObj(String dynastyName, HashMap<String, String> dynastyHashMap) throws Exception{
        JSONObject dynastyJsonObject = new JSONObject();
        if (!dynastyHashMap.containsKey(dynastyName))
        {
            JSONObject claims = new JSONObject();
            String qID = DataHandling.createQID(dynastyName);

            WikiDataHandling.addProperties(claims, "quốc gia", "Việt Nam");
            WikiDataHandling.addProperties(claims, "là một", "triều đại");
            WikiDataHandling.createNewEntity(dynastyJsonObject, 
                qID, 
                dynastyName, 
                dynastyName + " là một triều đại phong kiến trong lịch sử Việt Nam.", 
                "", 
                new JSONArray(), 
                claims, 
                new JSONObject()
            );
            dynastyHashMap.put(dynastyName, qID);
        }
        else
        {
            try{
                dynastyJsonObject = DataHandling.getJSONFromFile(ENTITY_FINAL_PATH + dynastyHashMap.get(dynastyName) + ".json");
            }
            catch (Exception e) {
                System.out.println("[ERROR] Can't find file: data/triều đại lịch sử/" + dynastyHashMap.get(dynastyName));
            }
        }
        return dynastyJsonObject;
    }

    private void tableDynastiesQueries() throws Exception
    {
        JSONObject allDynastyJsonObject = DataHandling.getJSONFromFile(INITIALIZE_PATH + "VVN.json");
        HashMap<String, String> dynastyHashMap = matchDynasties();

        for (String dynastyName: DataHandling.getAllKeys(allDynastyJsonObject))
        {
            JSONObject dynastyJsonObject = createDynastyObj(dynastyName, dynastyHashMap);

            String dynastyQID = dynastyHashMap.get(dynastyName);
            JSONArray dynastyRefArr = new JSONArray();
            JSONArray kingArr = allDynastyJsonObject.getJSONArray(dynastyName);
            for (int i = 0; i < kingArr.length(); i++)
            {
                JSONObject rawKingObj = kingArr.getJSONObject(i);

                JSONObject kingJsonObject = createKingObj(rawKingObj);
                String kingQID = kingJsonObject.getString("id");
                JSONObject refJSONObj = WikiDataHandling.createPropValue(kingJsonObject.getString("label"), kingQID, null, null);
                dynastyRefArr.put(refJSONObj);

                JSONObject kingRefJsonObject = kingJsonObject.has("references") ? kingJsonObject.getJSONObject("references") : new JSONObject();

                WikiDataHandling.addProperties(kingRefJsonObject, "triều đại", dynastyName, dynastyQID);

                kingJsonObject.put("references", kingRefJsonObject);
                DataHandling.writeFile(ENTITY_FINAL_PATH + kingQID + ".json", kingJsonObject.toString(), false);
            }
            dynastyJsonObject.getJSONObject("references").put("vua", dynastyRefArr);
            DataHandling.writeFile(ENTITY_FINAL_PATH + dynastyQID + ".json", dynastyJsonObject.toString(), false);
        }
        
    }

    private JSONObject getVietnameseWikiReadable(String qID) throws Exception{
        return WikiDataHandling.getVietnameseWikiReadable(qID, allQFile, allPFile, ENTITY_JSON_PATH, ENTITY_PROPERTIES_PATH, ENTITY_REF_FINAL_PATH, HTML_PATH);
    }

    private JSONObject createLocationObj(String urlString) throws Exception{
        JSONObject json = new JSONObject();
        String qID = "";
        if (WikiDataHandling.checkURL(urlString, false)){
            if (!urlToEntityHashMap.containsKey(urlString)){
                entityAnalys(urlString, 3, true);
                qID = urlToEntityHashMap.get(urlString);
                if (qID != null){
                    json = getVietnameseWikiReadable(qID);
                }
                else{
                    json = WikiDataHandling.createNewEntity();
                }
            }
            else{
                qID = urlToEntityHashMap.get(urlString);
                if (DataHandling.fileExist(ENTITY_FINAL_PATH + qID + ".json"))
                {
                    json = DataHandling.getJSONFromFile(ENTITY_FINAL_PATH + qID + ".json");
                }
                else{
                    json = getVietnameseWikiReadable(qID);
                }
            }
        }
        else{
            json = WikiDataHandling.createNewEntity();
        }
        return json;
    }

    private void tableLocationsQueries() throws Exception {
        JSONArray allLocationsArr = new JSONArray(DataHandling.readFileAll(INITIALIZE_PATH + "HistoricalSite.json"));
        for (int i = 0; i < allLocationsArr.length(); i++)
        {
            JSONObject locationJSON = allLocationsArr.getJSONObject(i);
            JSONObject json = createLocationObj(DataHandling.urlDecode(locationJSON.getString("link")));
            String locationName = locationJSON.getString("Di tích");
            if (locationName.isEmpty())
                continue;
            if (json.getString("label").isEmpty()){
                json.put("label", locationName);
            }
            String locationType = locationJSON.has("Loại di tích") ? locationJSON.getString("Loại di tích").toLowerCase() : "";

            if (json.getString("overview").isEmpty()){
                json.put("overview", locationName + " là một di tích " + (locationType.isEmpty() ? "" : locationType + " ") + "tại Việt Nam.");
            }
            String qID = json.getString("id");
            if (qID.isEmpty()){
                qID = DataHandling.createQID(locationName);
                json.put("id", qID);
            }
            JSONObject claims = json.getJSONObject("claims");
            if (!locationType.isEmpty())
                WikiDataHandling.addProperties(claims, "loại di tích", locationType);
            WikiDataHandling.addProperties(claims, "là một", "di tích");
            WikiDataHandling.addProperties(claims, "quốc gia", "Việt Nam");
            if (locationJSON.has("Vị trí")){
                if (!locationJSON.getString("Vị trí").isEmpty()){
                    WikiDataHandling.addProperties(claims, "vị trí", locationJSON.getString("Vị trí"));
                }
            }  
            if (locationJSON.has("Năm CN") && !locationJSON.getString("Năm CN").isEmpty()){
                String date = locationJSON.getString("Năm CN");
                if (date.contains("/"))
                {
                    date = date.replaceFirst("/", " tháng ");
                    date = date.replaceFirst("/", " năm ");
                    date = "ngày " + date;
                }
                else date = "năm " + date;
                WikiDataHandling.addProperties(claims, "thời gian công nhận di tích", date);
            }
            DataHandling.writeFile(ENTITY_FINAL_PATH + qID + ".json", json.toString(), false);
        }
    }
}
