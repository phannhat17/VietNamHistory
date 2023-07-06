package crawler.WikiDataCrawler;

import crawler.DataManage.DataHandling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Element;

public class WikiSelectiveData extends WikiBruteForceData {

    public static void main(String[] args) throws Exception {
        WikiSelectiveData wikiSelectiveData = new WikiSelectiveData("E:/Code/Java/OOP_Project/saveddata/Wikipedia/");
        wikiSelectiveData.selectiveDataQueries();
        wikiSelectiveData.analyzeSelectiveData();
    }

    private HashSet<String> festivalHashSet = new HashSet<>();
    private HashSet<String> locationHashSet = new HashSet<>();
    private HashSet<String> humanHashSet = new HashSet<>();
    private HashSet<String> eventHashSet = new HashSet<>();

    public WikiSelectiveData()
    {
        throw new IllegalArgumentException("File path must be provided.");
    }

    public WikiSelectiveData(String folderPath) throws Exception
    {
        super(folderPath);
    }

    public void selectiveDataQueries() throws Exception{
        selectiveFestivalsQueries(); // done;
        selectiveHumansQueries();  // done;
        selectiveLocationsQueries(); // done;
        selectiveEventsQueries(); // done
    }

    public void analyzeSelectiveData()throws Exception{
        analyzeSelectiveFestivalData();   // done
        analyzeSelectiveHumanData();  // done
        analyzeSelectiveLocationData();   // done
        analyzeSelectiveEventData();    // done
    }

    private void getAllURL(String catString, int floor, boolean getCat, HashSet<String> urlSet) throws Exception
    {
        if (floor >= 4) return;
        String wikiPageData = "";
        while(true)
        {
            wikiPageData = DataHandling.getDataFromURL(catString).toString();
            for (String craftURL: WikiDataHandling.getAllWikiHref(wikiPageData,"mw-pages",false))
            {
                urlSet.add(craftURL);
            }
            Element divTag = WikiDataHandling.getWikiHtmlElement(wikiPageData, "mw-pages");
            String nextPageUrl = "";
            if (divTag!=null)
            {                
                for (Element aTag : divTag.select("a")) {
                    if (!aTag.text().equals("Trang sau")) continue;
                    nextPageUrl = ("https://vi.wikipedia.org" + aTag.attr("href"));
                    break;
                }
            }
            if (nextPageUrl.isEmpty())
                break;
            else catString = nextPageUrl;
        }
        if (getCat == true)
        {
            for (String craftURL: WikiDataHandling.getAllWikiHref(wikiPageData, "mw-subcategories", true)){
                getAllURL(craftURL, floor + 1, getCat, urlSet);
            }
        }
    }

    private void analyzeScarlarlyURLs(HashSet<String> urlSet, HashSet<String> categoryHashSet) throws Exception
    {
        List<String> removeURLs = new ArrayList<>();
        for (String urlString: urlSet)
        {
            String qID = urlToEntityHashMap.get(urlString);
            if (qID!=null)
            {
                categoryHashSet.add(qID);
                continue;
            }
            entityAnalys(urlString, 3, true);
            qID = urlToEntityHashMap.get(urlString);
            if (qID!=null)
            {
                categoryHashSet.add(qID);
            }
            else{
                removeURLs.add(urlString);
            }
        }
        for (String urlString: removeURLs){
            urlSet.remove(urlString);
        }
    }

    private void selectiveFestivalsQueries() throws Exception
    {
        HashSet<String> urlSet = new HashSet<>();
        if (!DataHandling.fileExist(SCARLARLY_PATH + "festivals.json"))
        {    
            Set<String> bannedFestivalURLs = new HashSet<>(Arrays.asList("https://vi.wikipedia.org/wiki/Lễ_hội_các_dân_tộc_Việt_Nam",
                "https://vi.wikipedia.org/wiki/Lễ_hội_Lào",
                "https://vi.wikipedia.org/wiki/Lễ_hội_Nhật_Bản",
                "https://vi.wikipedia.org/wiki/Lễ_hội_Thái_Lan", "https://vi.wikipedia.org/wiki/Lễ_hội", "https://vi.wikipedia.org/wiki/Lễ_hội_Việt_Nam"));
            String urls[] = {"https://vi.wikipedia.org/wiki/L%E1%BB%85_h%E1%BB%99i_c%C3%A1c_d%C3%A2n_t%E1%BB%99c_Vi%E1%BB%87t_Nam", "https://vi.wikipedia.org/wiki/L%E1%BB%85_h%E1%BB%99i_Vi%E1%BB%87t_Nam"};
            urlSet.add("https://vi.wikipedia.org/wiki/Giỗ_Tổ_Hùng_Vương");

            urlToEntityHashMap.forEach((key, value) -> {
                String urlString = DataHandling.urlDecode(key);
                if ((urlString.contains("/Lễ"))  && !bannedFestivalURLs.contains(urlString)){
                    festivalHashSet.add(value);
                    urlSet.add(urlString);
                }
            });
            for (String urlString: urls)
            {
                String wikiPageData = DataHandling.getDataFromURL(urlString).toString();

                for (String craftURL: WikiDataHandling.getAllWikiHref(wikiPageData)){
                    if ((craftURL.contains("Lễ_hội") || craftURL.contains("Hội")) && !bannedFestivalURLs.contains(craftURL)){
                        if (!urlSet.contains(craftURL)) {
                            urlSet.add(craftURL);
                        }
                    }
                }
            }

        }
        else{
            for (Object key: new JSONArray(DataHandling.readFileAll(SCARLARLY_PATH + "festivals.json")))
            {
                urlSet.add((String)key);
            }
        }

        analyzeScarlarlyURLs(urlSet, festivalHashSet);

        DataHandling.writeFile(SCARLARLY_PATH + "festivals.json", (new JSONArray(urlSet)).toString(), false);
        DataHandling.writeFile(LOGS_PATH +  "URLToEntities.json" , (new JSONObject(urlToEntityHashMap)).toString(), false);
    }


    private void selectiveHumansQueries() throws Exception {
        HashSet<String> urlSet = new HashSet<>();
        if (!DataHandling.fileExist(SCARLARLY_PATH + "humans.json"))
        {
            getAllURL("https://vi.wikipedia.org/wiki/Th%E1%BB%83_lo%E1%BA%A1i:Nh%C3%A0_c%C3%A1ch_m%E1%BA%A1ng_Vi%E1%BB%87t_Nam", 0 , false, urlSet);
            getAllURL("https://vi.wikipedia.org/wiki/Th%E1%BB%83_lo%E1%BA%A1i:Nh%C3%A2n_v%E1%BA%ADt_l%E1%BB%8Bch_s%E1%BB%AD_Vi%E1%BB%87t_Nam", 0 , true, urlSet);
        }
        else{
            for (Object key: new JSONArray(DataHandling.readFileAll(SCARLARLY_PATH + "humans.json")))
            {
                urlSet.add((String)key);
            }
        }

        analyzeScarlarlyURLs(urlSet, humanHashSet);

        DataHandling.writeFile(SCARLARLY_PATH + "humans.json", (new JSONArray(urlSet)).toString(), false);
        DataHandling.writeFile(LOGS_PATH +  "URLToEntities.json" , (new JSONObject(urlToEntityHashMap)).toString(), false);
    }


    private void selectiveLocationsQueries() throws Exception
    {
        HashSet<String> urlSet = new HashSet<>();
        if (!DataHandling.fileExist(SCARLARLY_PATH + "locations.json"))
        {
            String urlCat[] = {"https://vi.wikipedia.org/wiki/Thể_loại:Khu_bảo_tồn_Việt_Nam", "https://vi.wikipedia.org/wiki/Thể_loại:Di_tích_tại_Hà_Nội", "https://vi.wikipedia.org/wiki/Thể_loại:Di_tích_quốc_gia_đặc_biệt", "https://vi.wikipedia.org/wiki/Thể_loại:Di_tích_tại_Hà_Nội","https://vi.wikipedia.org/wiki/Thể_loại:Chùa_Việt_Nam_theo_tỉnh_thành","https://vi.wikipedia.org/wiki/Thể_loại:Di_tích_quốc_gia_Việt_Nam"};
            for (String catString: urlCat){
                getAllURL(catString, 0, true, urlSet);
            }
        }
        else{
            for (Object key: new JSONArray(DataHandling.readFileAll(SCARLARLY_PATH + "locations.json")))
            {
                urlSet.add((String)key);
            }
        }
        analyzeScarlarlyURLs(urlSet, locationHashSet);

        JSONArray allLocationsArr = new JSONArray(DataHandling.readFileAll(INITIALIZE_PATH + "HistoricalSite.json"));
        for (int i = 0; i < allLocationsArr.length(); i++)
        {
            JSONObject locationJSON = allLocationsArr.getJSONObject(i);
            String urlString = DataHandling.urlDecode(locationJSON.getString("link"));
            String qID = "";
            if (WikiDataHandling.checkURL(urlString, false)){
                if (!urlToEntityHashMap.containsKey(urlString)){
                    entityAnalys(urlString, 3, true);
                    qID = urlToEntityHashMap.get(urlString);
                    if (qID != null){
                        urlSet.add(urlString);
                        locationHashSet.add(qID);
                    }
                }
            }
        }

        DataHandling.writeFile(SCARLARLY_PATH + "locations.json", (new JSONArray(urlSet)).toString(), false);
        DataHandling.writeFile(LOGS_PATH +  "URLToEntities.json" , (new JSONObject(urlToEntityHashMap)).toString(), false);
    }

    private void selectiveEventsQueries() throws Exception{
        HashSet<String> urlSet = new HashSet<>();
        if (!DataHandling.fileExist(SCARLARLY_PATH + "events.json"))
        {
            String urlCat[] = {"https://vi.wikipedia.org/wiki/Thể_loại:Trận_đánh_liên_quan_tới_Việt_Nam", "https://vi.wikipedia.org/wiki/Thể_loại:Trận_đánh_và_chiến_dịch_trong_Chiến_tranh_Việt_Nam", "https://vi.wikipedia.org/wiki/Thể_loại:Sự_kiện_lịch_sử_Việt_Nam",
            "https://vi.wikipedia.org/wiki/Thể_loại:Chiến_tranh_liên_quan_tới_Việt_Nam"};
            for (String catString: urlCat){
                getAllURL(catString, 1, true, urlSet);
            }
            String accept[] = {"Chiến", "Trận", "Cuộc", "Nổi_dậy", "Hòa_ước", "Loạn", "Không_chiến", "Khởi_nghĩa", "Xung_đột", "Tạm_ước", "Hải_chiến", "Thảm_sát", "Sự_kiện"};
            List<String> erase = new ArrayList<String>();
            for (String urlString: urlSet)
            {
                boolean check = false;
                for (String acceptBegin: accept){
                    if (urlString.contains("/" + acceptBegin)){
                        check = true;
                        break;
                    }
                }
                if (check == false) erase.add(urlString);
            }
            for (String urlString: erase)
            {
                urlSet.remove(urlString);
            }
        }
        else{
            for (Object key: new JSONArray(DataHandling.readFileAll(SCARLARLY_PATH + "events.json")))
            {
                urlSet.add((String)key);
            }
        }
        analyzeScarlarlyURLs(urlSet, eventHashSet);
        DataHandling.writeFile(SCARLARLY_PATH + "events.json", (new JSONArray(urlSet)).toString(), false);
        DataHandling.writeFile(LOGS_PATH +  "URLToEntities.json" , (new JSONObject(urlToEntityHashMap)).toString(), false);
    }

    public final JSONObject getVietnameseWikiReadable(String qID) throws Exception{
        return WikiDataHandling.getVietnameseWikiReadable(qID, allQFile, allPFile, ENTITY_JSON_PATH, ENTITY_PROPERTIES_PATH, ENTITY_REF_FINAL_PATH, HTML_PATH);
    }

    private void analyzeSelectiveData(HashSet<String> qIDHashSet, String instance) throws Exception{
        for (String qID: qIDHashSet)
        {
            JSONObject json = getVietnameseWikiReadable(qID);
            JSONObject claims = json.getJSONObject("claims");
            WikiDataHandling.addProperties(claims, "là một", instance);
            if (instance.equals("người"))
            WikiDataHandling.addProperties(claims, instance.equals("người") ? "quốc tịch" : "quốc gia", "Việt Nam");
            DataHandling.writeFile(ENTITY_FINAL_PATH + qID + ".json", json.toString(), false);
        }
    }

    private void analyzeSelectiveFestivalData() throws Exception {
        analyzeSelectiveData(festivalHashSet, "lễ hội");
    }

    private void analyzeSelectiveHumanData() throws Exception {
        analyzeSelectiveData(humanHashSet, "người");
    }

    private void analyzeSelectiveLocationData() throws Exception {
        analyzeSelectiveData(locationHashSet, "địa điểm");
    }

    private void analyzeSelectiveEventData() throws Exception {
        analyzeSelectiveData(eventHashSet, "sự kiện lịch sử");
    }

}
