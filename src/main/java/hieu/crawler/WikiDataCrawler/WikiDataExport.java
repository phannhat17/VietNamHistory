package crawler.WikiDataCrawler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import crawler.DataManage.DataHandling;


public class WikiDataExport extends WikiFolder {
    public static void main(String[] args) throws Exception {
        WikiDataExport wikiDataExport = new WikiDataExport("E:/Code/Java/OOP_Project/saveddata/Wikipedia/");
        wikiDataExport.export();
    }

    HashSet<String> acceptedCountries = new HashSet<>(Arrays.asList("Việt Nam", "Đại Việt","Nam Việt", "Đại Cồ Việt", "Đại Ngu", "Xích Quỷ", "Văn Lang", "Âu Lạc", "Giao Chỉ", "Lĩnh Nam", "Giao Châu", "An Nam", "Trấn Nam", "Tĩnh Hải quân", "Đại Nam", "Việt Nam Cộng hòa", "Việt Nam Dân chủ Cộng hòa"));

    HashSet<String> bannedProperties = new HashSet<>(Arrays.asList("mã sân bay IATA", "chuyến bay vũ trụ", "Romaja quốc ngữ", "trang Commons Creator", "tập hình Commons", "có trong danh sách chú trọng của dự án Wikimedia", "thể loại ở Commons", "chuyển tự McCune–Reischauer", "thể loại chính của đề tài", "thể loại cho nhóm người", "thể loại có liên quan", "bài danh sách Wikimedia", "trang định hướng Wikimedia", "bản mẫu chính của chủ đề", "trang Web"));

    HashSet<String> acceptEntitySet = new HashSet<>();

    public WikiDataExport()
    {
        throw new IllegalArgumentException("File path must be provided.");
    }

    public WikiDataExport(String folderPath) throws Exception
    {
        super(folderPath);
    }

    private final boolean hasCitizenshipIncludeVietnamese(JSONObject claims){
        if (claims.has("quốc tịch"))
        {
            JSONArray quocTichs = claims.getJSONArray("quốc tịch");
            for(Object quocTich: quocTichs)
            {
                if (acceptedCountries.contains(((JSONObject)quocTich).getString("value")))
                {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    private void createFolderForExport(HashSet<String> categories){
        for(String bigCategory: categories){
            DataHandling.createFolder(DATA_PATH + bigCategory);
        }
    }

    private void writeToDataPath(JSONObject bigCategories) throws Exception{
        HashSet<String> files = DataHandling.listAllFiles(ENTITY_FINAL_PATH);
        for (String fileName: files)
        {
            JSONObject json = DataHandling.getJSONFromFile(ENTITY_FINAL_PATH + fileName);
            if(json.has("claims"))
            {
                JSONObject claims = json.getJSONObject("claims");
                if (claims.has("là một"))
                {
                    JSONArray isIncstanceOf = (claims.getJSONArray("là một"));
                    for(Object instance: isIncstanceOf)
                    {
                        JSONObject instanceObj = (JSONObject)instance;
                        String value = instanceObj.getString("value");
                        if (value.equals("người"))
                        {
                            if (claims.has("quốc tịch"))
                            {
                                if (!hasCitizenshipIncludeVietnamese(claims)){
                                    break;
                                }
                            }
                        }
                        for (String bigCate: DataHandling.getAllKeys(bigCategories)){
                            JSONObject subCategories = bigCategories.getJSONObject(bigCate);
                            if(subCategories.has(value))
                            {
                                DataHandling.writeFile(DATA_PATH + bigCate + "/" + fileName, json.toString(), false);                                    
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Get all accepted entities from data path
     */
    private HashSet<String> getAccepetedEntites(HashSet<String> categories){
        HashSet<String> acceptSet = new HashSet<>();
        for (String bigCategory: categories){
            for (String fileName: DataHandling.listAllFiles(DATA_PATH + bigCategory))
                {
                    acceptSet.add(fileName.replace(".json", ""));
                }
        }
        return acceptSet;
    }

    private void downgradedUnacceptEntites(JSONObject json, HashSet<String> acceptEntitySet){
        List<String> deleteList = new ArrayList<String>();
        for(String key: DataHandling.getAllKeys(json)){
            if (bannedProperties.contains(key))
            {
                deleteList.add(key);
                continue;
            }
            JSONArray jsonArr = json.getJSONArray(key);
            for (Object ele: jsonArr)
            {
                JSONObject propertyObj = (JSONObject)ele;
                if ( ((String)propertyObj.get("type")).equals("wikibase-item") )
                {
                    String qID = (String)propertyObj.get("id");
                    if (!acceptEntitySet.contains(qID))
                    {
                        propertyObj.remove("id");
                        propertyObj.put("type", "string");
                    }
                }
            }
        }
        for (String p: deleteList)
        {
            json.remove(p);
        }
    }

    public void export() throws Exception
    {
        JSONObject bigCategories = DataHandling.getJSONFromFile(INITIALIZE_PATH + "CategorySplit.json");

        HashSet<String> bigCategoriesList = DataHandling.getAllKeys(bigCategories);

        createFolderForExport(bigCategoriesList);
        writeToDataPath(bigCategories);

        acceptEntitySet = getAccepetedEntites(bigCategoriesList);

        for (String bigCategory: bigCategoriesList){
            String folderName = DATA_PATH + bigCategory;
            for (String fileName: DataHandling.listAllFiles(folderName))
            {
                StringBuffer filePath = new StringBuffer(folderName);
                filePath.append("/");
                filePath.append(fileName);
                JSONObject json = DataHandling.getJSONFromFile(filePath.toString());
                if (json.has("claims"))
                {
                    downgradedUnacceptEntites(json.getJSONObject("claims"), acceptEntitySet);
                }
                if (json.has("references"))
                {
                    downgradedUnacceptEntites(json.getJSONObject("references"), acceptEntitySet);
                }
                DataHandling.writeFile(filePath.toString(), json.toString(), false);
            }
        }
    }

}
