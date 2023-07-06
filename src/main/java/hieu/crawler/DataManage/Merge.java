package crawler.DataManage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Merge {
    public static void main(String[] args) throws Exception {
        //Merge mergeData = new Merge();
        //mergeData.merge("data","E:/Code/Java/OOP_Project/saveddata/Wikipedia/", "E:/Code/Java/OOP_Project/saveddata/DBPedia/data/", createSource("Wikipedia"), createSource("DBPedia"));
    }

    public static String[] BIG_CATEGORIES = {"địa điểm du lịch, di tích lịch sử", "lễ hội văn hóa", "nhân vật lịch sử", "sự kiện lịch sử", "triều đại lịch sử"};

    public void merge(String exportPath, String path1, String path2, JSONArray src1, JSONArray src2) throws Exception
    {
        path1 += "data/";
        path2 += "data/";
        DataHandling.createFolder(exportPath);
        for (String bigCategory: BIG_CATEGORIES)
        {
            String path = path1 + bigCategory + "/";
            String exportDataSubFolder = exportPath + bigCategory;
            DataHandling.createFolder(exportDataSubFolder);
            HashSet<String> fileList = DataHandling.listAllFiles(path);
            for (String fileName: fileList)
            {
                JSONObject objJson1 = DataHandling.getJSONFromFile(path + fileName);
                if (DataHandling.fileExist(path2 + fileName))
                {
                    JSONObject obj2JSON = DataHandling.getJSONFromFile(path2 + fileName);
                    JSONObject obj2Claims = obj2JSON.getJSONObject("claims");
                    JSONObject obj1Claims = objJson1.getJSONObject("claims");
                    JSONObject exportClaims = new JSONObject();
                    for (String propertyName: DataHandling.getAllKeys(obj1Claims))
                    {                        
                        JSONArray objPropertyArr1 = obj1Claims.getJSONArray(propertyName);
                        if (obj2Claims.has(propertyName))
                        {
                            JSONArray obj2PropertyArr = obj2Claims.getJSONArray(propertyName);
                            JSONArray fullJoinArr = fullJoin(objPropertyArr1, obj2PropertyArr, src1, src2);
                            exportClaims.put(propertyName, fullJoinArr);
                        }
                        else
                        {
                            for (int i = 0; i < objPropertyArr1.length(); i++)
                            {
                                objPropertyArr1.getJSONObject(i).put("source", src1);
                            }
                            exportClaims.put(propertyName, objPropertyArr1);
                        }
                    }
                    for (String propertyName: DataHandling.getAllKeys(obj2Claims))
                    {
                        if (!obj1Claims.has(propertyName))
                        {
                            JSONArray objPropertyArr2 = obj2Claims.getJSONArray(propertyName);
                            for (int i = 0; i < objPropertyArr2.length(); i++)
                            {
                                objPropertyArr2.getJSONObject(i).put("source", src2);
                            }
                            exportClaims.put(propertyName, obj2Claims.getJSONArray(propertyName));
                        }
                    }
                    objJson1.put("claims", exportClaims);
                }
                DataHandling.writeFile(exportDataSubFolder + "/" + fileName, objJson1.toString(), false);
            }
        }
    }

    public static JSONArray createSource(String... sources){
        JSONArray srcArray = new JSONArray();
        List<String> arr = new ArrayList<>();
        for (String source: sources){
            arr.add(source);
        }
        Collections.sort(arr); 
        for (String str: arr){
            srcArray.put(str);
        }
        return srcArray;
    }

    public static JSONArray createSource(JSONArray src1, JSONArray src2){
        JSONArray srcArray = new JSONArray();
        List<String> arr = new ArrayList<>();
        for (Object source: src1){
            arr.add((String )source);
        }
        for (Object source: src2){
            arr.add((String )source);
        }
        Collections.sort(arr); 
        for (String str: arr){
            srcArray.put(str);
        }
        return srcArray;

    }

    public static boolean cmpPropObj(JSONObject obj1, JSONObject obj2){
        JSONArray objSrc1 = new JSONArray();
        if (obj1.has("source")){
            objSrc1 = obj1.getJSONArray("source");
            obj1.remove("source");
        }
        JSONArray objSrc2 = new JSONArray();
        if (obj2.has("source")){
            objSrc2 = obj2.getJSONArray("source");
            obj2.remove("source");
        }
        boolean check = false;
        if (obj1.toString().equals(obj2.toString())){
            check = true;
        }
        if (objSrc1.length() > 0) obj1.put("source", objSrc1);
        if (objSrc2.length() > 0) obj2.put("source", objSrc2);
        return check;
    }

    private JSONArray fullJoin(JSONArray arr1, JSONArray arr2, JSONArray src1, JSONArray src2){
        JSONArray ansArr = new JSONArray();
        for (int i = 0; i < arr1.length(); i++)
        {
            JSONObject obj1 = arr1.getJSONObject(i);
            boolean isUnique = true;
            for (int j = 0; j < arr2.length(); j++)
            {
                JSONObject obj2 = arr2.getJSONObject(j);
                if (cmpPropObj(obj1, obj2)) {
                    obj1.put("source", createSource(obj1.has("source") ? obj1.getJSONArray("source") : src1, obj2.has("source") ? obj2.getJSONArray("source") : src2));
                    ansArr.put(obj1);
                    isUnique = false;
                    break;
                }
            }
            if (isUnique == true)
            {
                obj1.put("source", obj1.has("source") ? obj1.getJSONArray("source") : src1);
                ansArr.put(obj1);
            }
        }
        for (int i = 0; i < arr2.length(); i++)
        {
            JSONObject obj2 = arr2.getJSONObject(i);
            boolean isUnique = true;
            for (int j = 0; j < arr1.length(); j++)
            {
                JSONObject obj1 = arr1.getJSONObject(j);
                if (cmpPropObj(obj1, obj2)){
                    isUnique = false;
                    break;
                }
            }
            if (isUnique == true){
                obj2.put("source", obj2.has("source") ? obj2.getJSONArray("source") : src2);
                ansArr.put(obj2);
            }
        }
        return ansArr;
    }
}
