package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class SyncData {
    public static void entityMap() throws IOException {
        String directory = "/src/data";
        Map<String, String> a = new HashMap<>();

        // Get a list of all files in the directory and its subdirectories
        File[] files = new File(directory).listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    FileReader reader = new FileReader(file);
                    Map<String, String> d = new Gson().fromJson(reader, Map.class);
                    a.put(d.get("label"), d.get("id"));
                    reader.close();
                }
            }
        }

        // Write the result to a JSON file
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(a);
        FileWriter writer = new FileWriter("map2.json");
        writer.write(json);
        writer.close();
    }
    
    public static void addID(String file) {
    	BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	Map<String, Object>[] data = new Gson().fromJson(reader, Map[].class);
    	try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Map<String, String> keymap = null;
    	try {
    		BufferedReader keymapReader = new BufferedReader(new FileReader("map2.json"));
    		 keymap = new Gson().fromJson(keymapReader, Map.class);
			keymapReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	for (Map<String, Object> item : data) {
    		String label = (String) item.get("label");
    		String id = keymap.get(label);
    		if (id == null) {
    			id = Integer.toString(label.hashCode()).replaceAll("-", "")+"X";
    		}
    		item.put("id", id);
    	}
    	
    	BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter("id"+file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Gson gson = new Gson();
    	gson.toJson(data, writer);
    	try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    
    public static void sync(String file) throws IOException {
    	String inputFile = file;
        String keymapFile = "map2.json";
        String outputFile = "sync" + file;

        BufferedReader inputReader = new BufferedReader(new FileReader(inputFile));
        Map<String, Object>[] data = new Gson().fromJson(inputReader, Map[].class);
        inputReader.close();

        BufferedReader keymapReader = new BufferedReader(new FileReader(keymapFile));
        Map<String, String> keymap = new Gson().fromJson(keymapReader, Map.class);
        keymapReader.close();

        for (Map<String, Object> item : data) {
            Map<String, Object> claims = (Map<String, Object>) item.get("claims");
            Map<String, Object> claims2 = new Gson().fromJson(new Gson().toJson(claims), Map.class);
            for (String key : claims.keySet()) {
                Object values = claims.get(key);
                if (values instanceof Object[]) {
                    Object[] valuesArray = (Object[]) values;
                    Object[] res = new Object[valuesArray.length];
                    for (int i = 0; i < valuesArray.length; i++) {
                        res[i] = addValue(valuesArray[i], keymap);
                    }
                    claims2.put(key.toLowerCase(), res);
                } else {
                    claims2.put(key.toLowerCase(), addValue(values, keymap));
                }
            }
            item.put("claims", claims2);
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        Gson gson = new Gson();
        gson.toJson(data, writer);
        writer.close();
    }

    private static Map<String, Object> addValue(Object value, Map<String, String> keymap) {
        String s = value.toString().trim();
        Map<String, Object> a = new Gson().fromJson("{\"value\": \"" + s + "\"}", Map.class);
        String id = keymap.get(s);
        if (id != null) {
            a.put("id", id);
            a.put("type", "wikibase-item");
        } else {
            a.put("type", "string");
        }
        return a;
    }
    
    public static void splitSyncFile(String outputFolder, String file) throws IOException {
        Path folderPath = Paths.get(outputFolder);
        if (!Files.exists(folderPath)) {
            Files.createDirectories(folderPath);
            System.out.println("Folder created: " + folderPath.toAbsolutePath());
        } else {
            System.out.println("Folder already exists: " + folderPath.toAbsolutePath());
        }
        
        BufferedReader reader = new BufferedReader(new FileReader(file));
        Map<String, Object>[] data = new Gson().fromJson(reader, Map[].class);
        reader.close();

        for (Map<String, Object> item : data) {
            String id = (String) item.get("id");
            String fileName = outputFolder + "/" + id + ".json";
            String outputData = new Gson().toJson(item);
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(outputData);
            writer.close();
        }
    }
    
}