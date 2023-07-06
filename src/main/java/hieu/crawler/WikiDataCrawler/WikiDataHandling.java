package crawler.WikiDataCrawler;

import crawler.DataManage.DataHandling;
import crawler.DataManage.Merge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WikiDataHandling extends DataHandling {

    public static final String VIETNAM_WORD[] = {"Vi\\u1ec7t Nam", "Vietnam","Việt Nam", "Viet Nam", "việt nam", };

    private static HashMap<String, String> viLabelHashMap = new HashMap<>();

    private final static String[] FILTER = {
        "#","T%E1%BA%ADp_tin", "B%E1%BA%A3n_m%E1%BA%ABu"
    };

    /*
     * Create a chunk of prop
     */
    public static JSONObject createPropValue(String value, String qID, String source, JSONObject qualifiers){
        JSONObject obj = new JSONObject();
        obj.put("value", value);
        if (qID != null){
            obj.put("type", "wikibase-item");
            obj.put("id", qID);
        }
        else{
            obj.put("type", "string");
        }
        if (source != null){
            obj.put("source", source);
        }
        if (qualifiers != null){
            obj.put("qualifiers", qualifiers);
        }
        return obj;
    }

    public static Element getWikiHtmlElement(String wikiPageData, String subID)
    {
        Document doc = Jsoup.parse(wikiPageData);
        
        Elements elements = doc.select("#catlinks");
        elements.remove();

        Element divTag = doc.getElementById("mw-content-text"); 
        if (divTag == null) return null;
        
        Elements tables = divTag.select("table[align=right]");
        for (Element table : tables) {
            table.remove();
        }

        Element xemThemTag = divTag.selectFirst("h2:has(span#Xem_th\\.C3\\.AAm)"); // Get the Xem thêm tag
        if (xemThemTag != null) {
            Element nextElement = xemThemTag.nextElementSibling(); // Get the next element after Xem thêm tag
            while (nextElement != null) {
                Element toRemove = nextElement; // Store the current element to remove
                nextElement = nextElement.nextElementSibling(); // Get the next element
                toRemove.remove(); // Remove the current element from the DOM
            }
        }

        Elements navboxElements = divTag.select("div.navbox"); // Get all elements with class navbox
        for (Element navboxElement : navboxElements) {
            navboxElement.remove(); // Remove each navbox element from the DOM
        }

        if (!subID.isEmpty())
        {
            divTag = divTag.getElementById(subID);
        }
        return divTag;
    }

    public static HashSet<String> getAllWikiHref(String wikiPageData, String subID, boolean getCategory) throws Exception{
        
        HashSet<String> hrefList = new HashSet<>();
        Element divTag = getWikiHtmlElement(wikiPageData, subID);

        if (divTag!=null)
        {
            for (Element aTag : divTag.select("a")) {
                String href = aTag.attr("href");
                String fullURL = "https://vi.wikipedia.org" + href;
                if (!checkURL(fullURL, getCategory)) continue;
                fullURL = urlDecode(fullURL);
                hrefList.add(fullURL);
            }
        }
        return hrefList;
    }

    public static HashSet<String> getAllWikiHref(String wikiPageData) throws Exception
    {
        return getAllWikiHref(wikiPageData, "", false);
    }

    public static JSONObject addProperties(JSONObject myJsonClaims, String propName, String value)
    {
        return addProperties(myJsonClaims, propName, value, "");
    }

    public static JSONObject addProperties(JSONObject myJsonClaims, String propName, String value, String qID)
    {
        JSONObject addObj = createPropValue(value, qID.isEmpty() ? null : qID, null, null);
        if (!myJsonClaims.has(propName)){
            JSONArray jsonArr = new JSONArray();
            jsonArr.put(addObj);
            myJsonClaims.put(propName, jsonArr);
        }
        else{
            JSONArray jsonArr = myJsonClaims.getJSONArray(propName);
            boolean check = false;
            for (int i = 0; i < jsonArr.length(); i++)
            {
                JSONObject obj = jsonArr.getJSONObject(i);
                if (Merge.cmpPropObj(obj, addObj)){
                    check = true;
                    break;
                }
            }
            if (check == false)
            {
                jsonArr.put(addObj);
            }
        }
        return myJsonClaims;
    }

    public static boolean checkURL(String urlString, boolean getCategory) throws Exception
    {
        if (urlString == null || urlString.isEmpty()) return false;  
        if (!urlString.contains("http")) return false;  
        if (!urlString.contains("/wiki/")) return false;
        if (getCategory == true)
        {
            if (urlDecode(urlString).contains("wiki/Thể_loại:")){
                return true;
            }
        }
        for (String text : FILTER) {
            if (urlString.contains(text)) return false;
        }
        if (urlString.chars().filter(ch -> ch == ':').count() > 1) {
            return false;  
        }
        return true;  
    }

    public static JSONObject createNewEntity(JSONObject myJsonObject, String qID, String label,  String overview, String description, JSONArray aliases, JSONObject claims, JSONObject references)
    {
        myJsonObject.put("id", qID);
        myJsonObject.put("label", label);
        myJsonObject.put("overview",  overview);
        myJsonObject.put("description",  description);
        myJsonObject.put("aliases", aliases);
        myJsonObject.put("claims", claims);
        myJsonObject.put("references", references);
        return myJsonObject;
    }

    /**
     * 
     * @return
     */
    public static JSONObject createNewEntity()
    {
        return createNewEntity(new JSONObject(), "", "", "", "", new JSONArray(), new JSONObject(), new JSONObject());
    }

    /**
     * Get the label of entity
     */
    public static String getWikiEntityViLabel(String qID, String jsonPath1, String jsonPath2) throws Exception
    {
        if (viLabelHashMap.containsKey(qID)) {
            return viLabelHashMap.get(qID);
        }
        String viLabelValue = "";
        JSONObject jsonContent;
        if (fileExist(jsonPath1 + "/" + qID + ".json"))
            jsonContent = getJSONFromFile(jsonPath1 + "/" + qID + ".json");
        else if (fileExist(jsonPath2 + "/" + qID + ".json"))
            jsonContent = getJSONFromFile(jsonPath2 + "/" + qID + ".json");
        else return viLabelValue;
        return getWikiEntityViLabel(jsonContent, qID);
    }

    /**
     * Get the label of entity
     */
    public static String getWikiEntityViLabel(JSONObject jsonContent, String qID) throws Exception
    {
        if (viLabelHashMap.containsKey(qID)) {
            return viLabelHashMap.get(qID);
        }
        String viLabelValue = "";
        if (!jsonContent.has("entities")) return viLabelValue;
        JSONObject entities = jsonContent.getJSONObject("entities");
        
        if (!entities.has(qID)) return viLabelValue;
        JSONObject entity = entities.getJSONObject(qID);
       
        if (!entity.has("labels")) return viLabelValue;
        JSONObject labels = entity.getJSONObject("labels");

        if (labels.has("vi"))
        {
            JSONObject viLabel = labels.getJSONObject("vi");
            if (viLabel.has("value"))
            {
                viLabelValue = viLabel.getString("value");
                viLabelHashMap.put(qID, viLabelValue);
            }
        }
        else if (entity.has("sitelinks"))
        {
            JSONObject sitelinks =  entity.getJSONObject("sitelinks");;
            if (sitelinks.has("viwiki"))
            {
                JSONObject viwiki = sitelinks.getJSONObject("viwiki");
                if (viwiki.has("title")){
                    viLabelValue = viwiki.getString("title");
                    viLabelHashMap.put(qID, viLabelValue);
                }
            }
        }
        return viLabelValue;
    }

    /**
     * Analyze a JSON Object and add all properties into propertyHashSet
     * @param entityJSON A Wikidata JSON Object.
     * @param entityJSONFileList A list of files in "EntityJSON" folder.
     */
    public static void jsonGetPropertiesFromEntity(Object entityJSON, HashSet<String> entityJSONFileList, HashSet<String> propertyHashSet)
    {
        if (entityJSON instanceof JSONArray) {
            for (int i = 0; i < ((JSONArray) entityJSON).length(); i++) { 
                jsonGetPropertiesFromEntity(((JSONArray) entityJSON).get(i), entityJSONFileList, propertyHashSet);
            }
        }
        else if (entityJSON instanceof JSONObject) {
            JSONObject qJSON = (JSONObject) entityJSON;
            if (qJSON.has("datavalue") && qJSON.has("property") && qJSON.has("datatype")) {
                propertyHashSet.add(qJSON.getString("property"));
                String datatype = (String)qJSON.getString("datatype");
                if (!datatype.equals("wikibase-item") && !datatype.equals("wikibase-property")) {
                    return;
                }
                JSONObject datavalue = qJSON.getJSONObject("datavalue");
                JSONObject value = datavalue.getJSONObject("value");
                String id = value.getString("id");
                if (!entityJSONFileList.contains(id + ".json")) {
                    propertyHashSet.add(id);
                }
                return;
            }
            for (String key: getAllKeys(qJSON)) {
                Object value = qJSON.get(key);
                
                if (value instanceof JSONObject) {
                    jsonGetPropertiesFromEntity((JSONObject) value, entityJSONFileList, propertyHashSet);
                } else if (value instanceof JSONArray) {
                    jsonGetPropertiesFromEntity((JSONArray) value, entityJSONFileList, propertyHashSet);
                }
            }
        }
    }

    public static final JSONObject propertyCompression(JSONObject infoObj, HashSet<String> allQFile, HashSet<String> allPFile, String jsonPath1, String jsonPath2) throws Exception
    {
        JSONObject jsonObj = new JSONObject();
        String datatype = infoObj.getString("datatype");
        if (!infoObj.has("datavalue")){
            return jsonObj;
        }
        JSONObject datavalue = infoObj.getJSONObject("datavalue");
        if (datatype.equals("wikibase-item") || datatype.equals("wikibase-property")){
            String qID = datavalue.getJSONObject("value").getString("id");
            if (allQFile.contains(qID + ".json")){
                jsonObj = WikiDataHandling.createPropValue(getWikiEntityViLabel(qID, jsonPath1, jsonPath2), qID, null, null);
            }
            else if (allPFile.contains(qID + ".json")){
                String viLabel = getWikiEntityViLabel(qID, jsonPath1, jsonPath2);
                if (!viLabel.isEmpty()){
                    jsonObj = WikiDataHandling.createPropValue(getWikiEntityViLabel(qID, jsonPath1, jsonPath2), null, null, null);
                }
            }
        }
        else if (datatype.equals("quantity")){
            JSONObject value = datavalue.getJSONObject("value");
            String amount = value.getString("amount").replace("+", "");
            String unit = value.getString("unit");
            unit = unit.equals("1") ? " " : getWikiEntityViLabel(unit.replace("http://www.wikidata.org/entity/", ""), jsonPath1, jsonPath2);
            jsonObj = WikiDataHandling.createPropValue(amount + " " + unit, null, null, null);
        }
        else if (datatype.equals("string")){
            jsonObj = WikiDataHandling.createPropValue(datavalue.getString("value"), null, null, null);
        }
        else if (datatype.equals("monolingualtext"))
        {
            JSONObject value = datavalue.getJSONObject("value");
            if (value.getString("language").equals("vi")){
                jsonObj = WikiDataHandling.createPropValue(value.getString("text"), null, null, null);
            }
        }
        else if (datatype.equals("time"))
        {
            JSONObject value = datavalue.getJSONObject("value");
            String time = value.getString("time");
            String year = time.substring(0, 5);
            String formatDMY = "";
            if (!year.contains("0000")) {
                String month = time.substring(6, 8);
                if (!month.contains("00") && ! month.equals("01")) {
                    String day = time.substring(9, 11);
                    if (!day.equals("00") && ! day.equals("01")) {
                        formatDMY = "ngày " + day + " ";
                    }
                    formatDMY += "tháng " + month + " ";
                }
                formatDMY += "năm " + year.substring(1, 5);
                if (year.contains("-")) {
                    formatDMY += " trước công nguyên";
                }
            }
            jsonObj = WikiDataHandling.createPropValue(formatDMY, null, null, null);
        }
        return jsonObj;
    }

    public static final String getWikiEntityInstance(JSONObject entitiyContent)
    {
        String instance = "";
        JSONObject claims = (JSONObject)entitiyContent.get("claims");
        if (!claims.has("P31")){
            return instance;
        }
        JSONArray p31Arr = (JSONArray)(claims.get("P31"));
        for (int i = 0 ; i < p31Arr.length() ; i++)
        {
            JSONObject mainsnak = ((JSONObject)(p31Arr.getJSONObject(i).get("mainsnak")));
            if (mainsnak.has("datavalue"))
            {
                JSONObject datavalue = (JSONObject)(mainsnak.get("datavalue"));
                JSONObject value = (JSONObject)datavalue.get("value");
                instance = (String )value.get("id");
                if (!instance.equals("")) 
                    break;
            }
        }
        return instance;
    }

    public static String getWikiSitelink(JSONObject entitiyContent, String qID, String wikiLang) throws Exception
    {
        JSONObject sitelinks = (JSONObject )entitiyContent.get("sitelinks");
        String sitelink = "";
        if (sitelinks.has(wikiLang)) {
            sitelink = sitelinks.getJSONObject(wikiLang).getString("url");
        }
        return sitelink;
    }


    public static int getMinYear(Object entityJSON)
    {
        int minYear = 100000;
        if (entityJSON instanceof JSONArray) {
            for (int i = 0; i < ((JSONArray) entityJSON).length(); i++) { 
                minYear = Math.min(getMinYear(((JSONArray)entityJSON).get(i)), minYear);
                if (minYear < 1962) {
                    return minYear;
                }
            }
        }
        else if (entityJSON instanceof JSONObject) {
            JSONObject json = (JSONObject) entityJSON;
            if (json.has("datatype")) {
                if ((json.getString("datatype")).equals("time")) {
                    if (!json.has("datavalue"))
                        return minYear;
                    JSONObject datavalue = json.getJSONObject("datavalue");
                    if (datavalue.has("value")) {
                        String time = datavalue.getJSONObject("value").getString("time");
                        String sign = time.substring(0,1);
                        if (sign.equals("-")) {
                            minYear = 0; 
                        }
                        else minYear = Integer.parseInt(time.substring(1,5));
                    }
                }
                return minYear;
            }
            for (String key: getAllKeys(json)){
                if (key.equals("references")){
                    continue;
                }
                Object value = json.get(key);
                if (value instanceof JSONObject) {
                    minYear = Math.min(getMinYear((JSONObject) value), minYear);
                } else if (value instanceof JSONArray) {
                    minYear = Math.min(getMinYear((JSONArray) value), minYear);
                }
                if (minYear < 1962) {
                    return minYear;
                }
            }
        }
        return minYear;
    }

    public static String getOverview(String qID, String htmlPath){
        String filePath = htmlPath + qID + ".html";
        String overview = "";
        if (fileExist(filePath))
        {
            String data = "";
            try {
                data = readFileAll(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Document doc = Jsoup.parse(data);
            Element divID = doc.getElementById("mw-content-text"); 
            Element divTag = divID.select("div.mw-parser-output").first(); 
            StringBuffer overviewSB = new StringBuffer();
            for (Element tag: divTag.children())
            {
                if ((tag.tagName()).equals("meta")) break;
                if ((tag.tagName()).equals("h2")) break;
                if ((tag.tagName()).equals("p"))
                {
                    String tagContent = tag.text();
                    if (tagContent.isEmpty()) continue;
                    String regex = "\\s*\\[[^\\]]*\\]\\s*";
                    if (tagContent.matches(regex)){
                        tagContent = tagContent.replaceAll(regex, " ");
                    }
                    overviewSB.append(tagContent);
                    break;
                }
            }
            if (overviewSB.length() > 0 && overviewSB.charAt(overviewSB.length()-1) == (char)(':'))
            {
                int dot = overviewSB.lastIndexOf(".", overviewSB.length()-1);
                overview = overviewSB.substring(0, dot + 1);
            }
            else{
                overview = overviewSB.toString();
            }
        }
        overview = overview.replaceAll("\\s*\\[[^\\]]*\\]\\s*", " ");
        return overview;
    }

    public static String getRawEntityFirstInstance(String qID, String ENTITY_JSON_PATH, String ENTITY_PROPERTIES_PATH) throws Exception
    {
        String instance = "";
        JSONObject json;
        if (fileExist(ENTITY_JSON_PATH + qID + ".json"))
            json = getJSONFromFile(ENTITY_JSON_PATH + qID + ".json");
        else if (fileExist(ENTITY_PROPERTIES_PATH + qID + ".json"))
            json = getJSONFromFile(ENTITY_PROPERTIES_PATH + qID + ".json");
        else return instance;
        JSONObject claims = json.getJSONObject("entities").getJSONObject(qID).getJSONObject("claims");
        if (!claims.has("P31")){
            return instance;
        }
        JSONArray p31Arr = (JSONArray)(claims.get("P31"));
        for (int i = 0 ; i < p31Arr.length() ; i++)
        {
            JSONObject mainsnak = p31Arr.getJSONObject(i).getJSONObject("mainsnak");
            if (mainsnak.has("datavalue"))
            {
                JSONObject datavalue = mainsnak.getJSONObject("datavalue");
                JSONObject value = datavalue.getJSONObject("value");
                instance = value.getString("id");
                if (!instance.equals("")) 
                    break;
            }
        }
        return instance;
    }

    public static JSONObject getWikiEntityReferences(String fileName, String ENTITY_REF_FINAL_PATH, String ENTITY_JSON_PATH, String ENTITY_PROPERTIES_PATH) throws Exception{
        JSONObject myRef = new JSONObject();
        if (fileExist(ENTITY_REF_FINAL_PATH + fileName))
        {
            HashMap<String, String> idInstance = new HashMap<>();
            String fileContent = readFileAll(ENTITY_REF_FINAL_PATH + fileName);
            JSONArray jsonArray = new JSONArray(fileContent);
            for (Object iter: jsonArray)
            {
                String refEntityID = (String)iter;
                String instanceName = "";

                if (!idInstance.containsKey(refEntityID))
                {
                    String instanceID = getRawEntityFirstInstance(refEntityID, ENTITY_JSON_PATH,ENTITY_PROPERTIES_PATH);
                    if(instanceID.isEmpty()) continue;
                    instanceName = getWikiEntityViLabel(instanceID, ENTITY_JSON_PATH, ENTITY_PROPERTIES_PATH);
                    idInstance.put(refEntityID, instanceName);
                }
                else
                    instanceName = idInstance.get(refEntityID);
                if (instanceName.isEmpty()) continue;
                JSONObject refEntityIdObject = WikiDataHandling.createPropValue(getWikiEntityViLabel(refEntityID, ENTITY_JSON_PATH, ENTITY_PROPERTIES_PATH), refEntityID, null, null);
                if (!myRef.has(instanceName))
                {
                    JSONArray h = new JSONArray();
                    h.put(refEntityIdObject);
                    myRef.put(instanceName, h);
                }
                else
                {
                    myRef.getJSONArray(instanceName).put(refEntityIdObject);
                }
            }
        }
        return myRef;
    }

    public static String getWikiEntityDescription(JSONObject entityJSON){
        String viDescriptionValue = "";
        JSONObject descriptions = entityJSON.getJSONObject("descriptions");
        if (descriptions.has("vi"))
        {
            JSONObject viDescriptions = descriptions.getJSONObject("vi");
            viDescriptionValue = viDescriptions.getString("value");
        }
        return viDescriptionValue;
    }

    public static ArrayList<String> getWikiEntityAliases(JSONObject entityJSON){
        if (!entityJSON.has("aliases")) 
            return new ArrayList<>();
        JSONObject aliases = entityJSON.getJSONObject("aliases");
        ArrayList<String> myAliases = new ArrayList<>();
        if (aliases.has("vi"))
        {
            JSONArray viAlias = aliases.getJSONArray("vi");
            for (int i = 0 ; i < viAlias.length() ; i++)
            {
                String viAliasValue = viAlias.getJSONObject(i).getString("value");
                myAliases.add(viAliasValue);
            }
        }
        return myAliases;
    }

    public static JSONObject getWikiEntityClaims(JSONObject entityJSON, HashSet<String> allQFile, HashSet<String> allPFile, String ENTITY_JSON_PATH, String ENTITY_PROPERTIES_PATH) throws Exception{
        JSONObject myClaims = new JSONObject();
        JSONObject claims = entityJSON.getJSONObject("claims");
        for (String propertyID: getAllKeys(claims))
        {
            /* Choose that entity if that entity has a name in Vietnamese */
            String propertyName = getWikiEntityViLabel(propertyID, ENTITY_JSON_PATH, ENTITY_PROPERTIES_PATH);
            if (propertyName.isEmpty())
                continue;
            
            JSONArray propertyInfoArr = claims.getJSONArray(propertyID);
            JSONArray jsonArray = new JSONArray();
            for (Object info: propertyInfoArr)
            {
                JSONObject infoObj = (JSONObject) info;
                JSONObject jsonObj = propertyCompression(infoObj.getJSONObject("mainsnak"), allQFile, allPFile, ENTITY_JSON_PATH, ENTITY_PROPERTIES_PATH);

                if (jsonObj.length() == 0) continue;
                
                /*
                * Get qualifiers of a property (a qualifier field will describe a property more clear)
                */
                if (infoObj.has("qualifiers"))
                {
                    JSONObject qualifiersJsonObj = new JSONObject();
                    JSONObject qualifiers = infoObj.getJSONObject("qualifiers");
                    for (String key: getAllKeys(qualifiers))
                    {
                        JSONArray myQualifiersPropertyJsonArray = new JSONArray();
                        JSONArray qualifiersPropertyJsonArr = qualifiers.getJSONArray(key);
                        for (Object propertyInfo: qualifiersPropertyJsonArr)
                        {
                            JSONObject propertyInfoJson = (JSONObject)propertyInfo;
                            JSONObject propertyJsonObj = propertyCompression(propertyInfoJson, allQFile, allPFile, ENTITY_JSON_PATH, ENTITY_PROPERTIES_PATH);
                            if (propertyJsonObj.length()>0)
                                myQualifiersPropertyJsonArray.put(propertyJsonObj);
                        }
                        if (myQualifiersPropertyJsonArray.length()>0)
                        {
                            qualifiersJsonObj.put(getWikiEntityViLabel(key, ENTITY_JSON_PATH, ENTITY_PROPERTIES_PATH), myQualifiersPropertyJsonArray);
                        }
                    }
                    if (qualifiersJsonObj.length()>0)
                        jsonObj.put("qualifiers", qualifiersJsonObj);

                }
                if (jsonObj.length()>0)
                    jsonArray.put(jsonObj);

            }
            if  (jsonArray.length()>0)
                myClaims.put(propertyName, jsonArray);
        }
        return myClaims;
    }

    public static JSONObject getVietnameseWikiReadable(String qID, HashSet<String> allQFile, HashSet<String> allPFile, String ENTITY_JSON_PATH, String ENTITY_PROPERTIES_PATH, String ENTITY_REF_FINAL_PATH, String HTML_PATH) throws Exception
    {
        String fileName = qID + ".json";
        JSONObject json = new JSONObject();

        JSONObject content = getJSONFromFile(ENTITY_JSON_PATH + fileName);
        JSONObject entities = content.getJSONObject("entities");
        JSONObject entityJSON = entities.getJSONObject(qID);

        json.put("id",entityJSON.getString("id"));
        json.put("label", getWikiEntityViLabel(qID, ENTITY_JSON_PATH, ENTITY_PROPERTIES_PATH));
        json.put("description", getWikiEntityDescription(entityJSON));
        json.put("overview", getOverview(qID, HTML_PATH));
        json.put("aliases", new JSONArray(getWikiEntityAliases(entityJSON)));
        json.put("claims", getWikiEntityClaims(entityJSON, allQFile, allPFile, ENTITY_JSON_PATH, ENTITY_PROPERTIES_PATH));
        json.put("references",getWikiEntityReferences(fileName, ENTITY_REF_FINAL_PATH, ENTITY_JSON_PATH, ENTITY_PROPERTIES_PATH));

        return json;
    }

    /**
     * Get the ID of an entity from a Wikipedia page.
     * @param soupHWND HTML content parsed by Jsoup library.
     * @return That entity's ID of that soupHWND
     */
    public static String getEntityIdFromHtml(Document soupHWND) 
    {
        String entityURL = "";
        Element liTag = soupHWND.getElementById("t-wikibase");
        if (liTag == null)
            return "";
        for (Element aTag : liTag.select("a")) {
            entityURL = aTag.attr("href");
            break;
        }
        if (entityURL.equals(""))
            return "";
        String qID = entityURL.replace("https://www.wikidata.org/wiki/Special:EntityPage/","");
        return qID;
    }

    /**
     * Check if the JSON of an entity has any properties that are related to Vietnam.
     * @param entityJSON the JSON of an entity.
     * @return Member variable isRelated is {@code true} if that entity has any properties that are related to Vietnam, else {@code false}.
     */
    public static boolean jsonAnalysis(Object entityJSON, HashSet<String> vietnamEntityHashSet)
    {
        if (entityJSON instanceof JSONArray)
        {
            for (int i = 0; i < ((JSONArray) entityJSON).length(); i++) { 
                if (jsonAnalysis(((JSONArray) entityJSON).get(i), vietnamEntityHashSet) == true) {
                    return true;
                }
            }
        }
        else if (entityJSON instanceof JSONObject)
        {
            JSONObject qJSON = (JSONObject) entityJSON;
            if (qJSON.has("numeric-id"))
            {
                if (vietnamEntityHashSet.contains(qJSON.getString("id"))) {
                    return true;
                }
            }
            for(String key: getAllKeys(qJSON)){
                Object value = qJSON.get(key);
                if (value instanceof JSONObject) {
                    if (jsonAnalysis((JSONObject) value, vietnamEntityHashSet) == true){
                        return true;
                    }
                } else if (value instanceof JSONArray) {
                    if (jsonAnalysis((JSONArray) value, vietnamEntityHashSet) == true){
                        return true;
                    }
                }
            }
        }
        return false;
    }


}
