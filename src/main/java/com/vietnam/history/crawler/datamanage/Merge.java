package com.vietnam.history.crawler.datamanage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class Merge {

    public static String[] BIG_CATEGORIES = {"triều đại lịch sử", "nhân vật lịch sử", "địa điểm du lịch, di tích lịch sử", "lễ hội văn hóa", "sự kiện lịch sử"};

    public static JSONArray createSource(String... sources) {
        JSONArray srcArray = new JSONArray();
        List<String> arr = new ArrayList<>();
        Collections.addAll(arr, sources);
        Collections.sort(arr);
        for (String str : arr) {
            srcArray.put(str);
        }
        return srcArray;
    }

    public static JSONArray createSource(JSONArray src1, JSONArray src2) {
        JSONArray srcArray = new JSONArray();
        List<String> arr = new ArrayList<>();
        for (Object source : src1) {
            arr.add((String) source);
        }
        for (Object source : src2) {
            arr.add((String) source);
        }
        Collections.sort(arr);
        for (String str : arr) {
            srcArray.put(str);
        }
        return srcArray;

    }

    public static boolean cmpPropObj(JSONObject obj1, JSONObject obj2) {
        JSONArray objSrc1 = new JSONArray();
        if (obj1.has("source")) {
            objSrc1 = obj1.getJSONArray("source");
            obj1.remove("source");
        }
        JSONArray objSrc2 = new JSONArray();
        if (obj2.has("source")) {
            objSrc2 = obj2.getJSONArray("source");
            obj2.remove("source");
        }
        boolean check = false;
        if (obj1.getString("value").equals(obj2.getString("value"))) {
            if (obj1.has("id") && !obj2.has("id")) {
                obj2.put("type", "wikibase-item");
                obj2.put("id", obj1.getString("id"));
            }
            if (obj2.has("id") && !obj1.has("id")) {
                obj1.put("type", "wikibase-item");
                obj1.put("id", obj2.getString("id"));
            }
        }
        if (obj1.toString().equals(obj2.toString())) {
            check = true;
        }
        if (objSrc1.length() > 0) obj1.put("source", objSrc1);
        if (objSrc2.length() > 0) obj2.put("source", objSrc2);
        return check;
    }

    private JSONObject getAllEntityFiles(String path) {
        JSONObject map = new JSONObject();
        HashSet<String> fileList = DataHandling.listAllFiles(path);
        if (fileList.isEmpty()) {
            for (String cat : BIG_CATEGORIES) {
                for (String fileName : DataHandling.listAllFiles(path + cat)) {
                    if (map.has(fileName)) {
                        DataHandling.print(fileName);
                    }
                    map.put(fileName, path + cat + "/" + fileName);
                }
            }
        } else {
            for (String fileName : fileList) {
                map.put(fileName, path + fileName);
            }
        }
        return map;
    }

    public void merge(String exportPath, String path1, String path2, JSONArray srcArr1, JSONArray srcArr2) throws Exception {
        path1 += "data/";
        path2 += "data/";
        DataHandling.createFolder(exportPath);

        for (String cat : BIG_CATEGORIES) {
            DataHandling.createFolder(exportPath + cat);
        }

        JSONObject fileMap1 = getAllEntityFiles(path1);
        JSONObject fileMap2 = getAllEntityFiles(path2);

        for (String fileName : DataHandling.getAllKeys(fileMap1)) {
            String filePath = fileMap1.getString(fileName);
            String exportDataSubFolder = null;
            for (String cat : BIG_CATEGORIES) {
                if (filePath.contains(cat)) {
                    exportDataSubFolder = exportPath + cat;
                    break;
                }
            }
            if (exportDataSubFolder == null) continue;
            JSONObject objJson1 = DataHandling.getJSONFromFile(filePath);
            JSONObject objClaims1 = objJson1.getJSONObject("claims");
            if (fileMap2.has(fileName)) {
                JSONObject obj2JSON = DataHandling.getJSONFromFile(fileMap2.getString(fileName));
                JSONObject objClaims2 = obj2JSON.getJSONObject("claims");
                JSONObject exportClaims = new JSONObject();
                for (String propertyName : DataHandling.getAllKeys(objClaims1)) {
                    JSONArray objPropertyArr1 = objClaims1.getJSONArray(propertyName);
                    if (objClaims2.has(propertyName)) {
                        JSONArray obj2PropertyArr = objClaims2.getJSONArray(propertyName);
                        JSONArray fullJoinArr = fullJoin(objPropertyArr1, obj2PropertyArr, srcArr1, srcArr2);
                        exportClaims.put(propertyName, fullJoinArr);
                    } else {
                        addSourceToArr(objPropertyArr1, srcArr1);
                        exportClaims.put(propertyName, objPropertyArr1);
                    }
                }
                for (String propertyName : DataHandling.getAllKeys(objClaims2)) {
                    if (!objClaims1.has(propertyName)) {
                        JSONArray objPropertyArr2 = objClaims2.getJSONArray(propertyName);
                        addSourceToArr(objPropertyArr2, srcArr2);
                        exportClaims.put(propertyName, objPropertyArr2);
                    }
                }
                objJson1.put("claims", exportClaims);
            }
            DataHandling.writeFile(exportDataSubFolder + "/" + fileName, objJson1.toString(), false);
        }

        writeUniqueSources(fileMap1, fileMap2, srcArr1, exportPath);
        writeUniqueSources(fileMap2, fileMap1, srcArr2, exportPath);
    }

    private void writeUniqueSources(JSONObject fileMap1, JSONObject fileMap2, JSONArray srcArr, String exportPath) throws Exception {
        for (String fileName : DataHandling.getAllKeys(fileMap1)) {
            String filePath = fileMap1.getString(fileName);
            String exportDataSubFolder = "";
            for (String cat : BIG_CATEGORIES) {
                if (filePath.contains(cat)) {
                    exportDataSubFolder = exportPath + cat;
                    break;
                }
            }
            if (exportDataSubFolder.isEmpty()) continue;
            if (!fileMap2.has(fileName)) {
                JSONObject objJson1 = DataHandling.getJSONFromFile(filePath);
                JSONObject objClaims1 = objJson1.getJSONObject("claims");

                for (String propertyName : DataHandling.getAllKeys(objClaims1)) {
                    addSourceToArr(objClaims1.getJSONArray(propertyName), srcArr);
                }
                DataHandling.writeFile(exportDataSubFolder + "/" + fileName, objJson1.toString(), false);
            }
        }
    }

    private void addSourceToArr(JSONArray objPropertyArr, JSONArray srcArr) {
        for (int i = 0; i < objPropertyArr.length(); i++) {
            JSONObject obj = objPropertyArr.getJSONObject(i);
            if (!obj.has("source")) {
                obj.put("source", srcArr);
            }
        }
    }

    private JSONArray fullJoin(JSONArray arr1, JSONArray arr2, JSONArray srcArr1, JSONArray srcArr2) {
        JSONArray ansArr = new JSONArray();
        for (int i = 0; i < arr1.length(); i++) {
            JSONObject obj1 = arr1.getJSONObject(i);
            boolean isUnique = true;
            for (int j = 0; j < arr2.length(); j++) {
                JSONObject obj2 = arr2.getJSONObject(j);
                if (cmpPropObj(obj1, obj2)) {
                    obj1.put("source", createSource(obj1.has("source") ? obj1.getJSONArray("source") : srcArr1, obj2.has("source") ? obj2.getJSONArray("source") : srcArr2));
                    ansArr.put(obj1);
                    isUnique = false;
                    break;
                }
            }
            if (isUnique) {
                obj1.put("source", obj1.has("source") ? obj1.getJSONArray("source") : srcArr1);
                ansArr.put(obj1);
            }
        }
        for (int i = 0; i < arr2.length(); i++) {
            JSONObject obj2 = arr2.getJSONObject(i);
            boolean isUnique = true;
            for (int j = 0; j < arr1.length(); j++) {
                JSONObject obj1 = arr1.getJSONObject(j);
                if (cmpPropObj(obj1, obj2)) {
                    isUnique = false;
                    break;
                }
            }
            if (isUnique) {
                obj2.put("source", obj2.has("source") ? obj2.getJSONArray("source") : srcArr2);
                ansArr.put(obj2);
            }
        }
        return ansArr;
    }
}
