package com.vietnam.history.statistics;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class EntityStatistic {
	private String folderPath;
	private final static String[] ENTITY_SOURCE = {"[\"Wikipedia\"]", "[\"DBPedia\"]", "[\"Người kể sử\"]"};
	protected final static String[] ENTITY_TYPES = {"lễ hội văn hóa", "nhân vật lịch sử", "sự kiện lịch sử", "triều đại lịch sử", "địa điểm du lịch, di tích lịch sử"};
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	public EntityStatistic(String folderPath) {
		this.folderPath = folderPath;
	}
	
	
	
	
	private File[] getAllFilesFromFolder(String subFolder) {
        File folder = new File(subFolder);
        File[] files = folder.listFiles();
        if(files == null) {
        	files = new File[0];
        }
        return files;
	}
	
	protected int countEntity(String type) {
		int count = 0;
		File[] files = getAllFilesFromFolder(folderPath + type);
        for (File file : files) {
            if (file.isFile()) {
                count++;
            }
        }
        System.out.println("Number of files in " + folderPath + type + ": " + count);
		return count;
	}
	
//	private int countEntity(String type, String keyWord) {
//
//		int count = 0;
//		File[] files = getAllFilesFromFolder(folderPath + type);
//        for (File file : files) {
//        	String content = readFile(file);
//        	if(content.contains(keyWord)) {
//        		count += 1;
//        	} else if (!content.contains("\"source\"") && keyWord.equals("[\"Wikipedia\"]")) {
//        		count += 1;
//        	}
//        }
//        System.out.println("Number of files in " + folderPath + type + ": " + count);
//		return count;
//	}
	
	
	
	protected TreeMap<String, Integer> countEntityProperty(String type) {
		HashMap<String, Integer> properties =  new HashMap<>();
		File[] files = getAllFilesFromFolder(folderPath + type);
		for (File file: files) {
			JsonObject content = parseJsonFile(file);
			JsonObject claims = content.getAsJsonObject("claims");
			for(String property: claims.keySet()) {
				if (properties.get(property) == null) {
					properties.put(property, 1);
				} else {
					properties.put(property, properties.get(property) + 1);
				}
			}
		}
		TreeMap<String, Integer> sortedProperties = new TreeMap<>((e1,e2) ->{
			int cmp = properties.get(e2).compareTo(properties.get(e1));
			if(cmp ==0) {				
				return e1.compareTo(e2);
			}
			return cmp;
		});
			
		sortedProperties.putAll(properties);
		return sortedProperties;
	}
	
//	private TreeMap<String, Integer> countEntityProperty(String type, String source) {
//		HashMap<String, Integer> properties =  new HashMap<>();
//		File[] files = getAllFilesFromFolder(folderPath + type);
//		for (File file: files) {
//			JsonObject content = parseJsonFile(file);
//			JsonObject claims = content.getAsJsonObject("claims");
//			for(String property: claims.keySet()) {
//				JsonArray values = claims.getAsJsonArray(property);
//				for (JsonElement i: values) {
//					try {						
//						String valueSource = i.getAsJsonObject().get("source").getAsString();
//						if(valueSource.equals(source)) {
//							if (properties.get(property) == null) {
//								properties.put(property, 1);
//							} else {
//								properties.put(property, properties.get(property) + 1);
//							}						
//						}
//					} catch (Exception e) {
//						if(source.equals("[\"Wikipedia\"]")) {
//							if (properties.get(property) == null) {
//								properties.put(property, 1);
//							} else {
//								properties.put(property, properties.get(property) + 1);
//							}						
//						}
//					}
//				}
//			}
//		}
//		TreeMap<String, Integer> sortedProperties = new TreeMap<>((e1,e2) ->{
//			int cmp = properties.get(e2).compareTo(properties.get(e1));
//			if(cmp ==0) {				
//				return e1.compareTo(e2);
//			}
//			return cmp;
//		});
//			
//		sortedProperties.putAll(properties);
//		return sortedProperties;
//	}
	
	protected int countConnectable(String type) {
		int count = 0;
		File[] files = getAllFilesFromFolder(folderPath + type);
		for (File file: files) {
			JsonObject content = parseJsonFile(file);
			JsonObject claims = content.getAsJsonObject("claims");
			for(String property: claims.keySet()) {
				JsonArray values = claims.getAsJsonArray(property);
				for (JsonElement i: values) {
					String valueType = i.getAsJsonObject().get("type").getAsString();
					if(valueType.equals("wikibase-item")) {
						count += 1;
					}
				}
			}
		}
		return count;
	}
	
	private JsonObject parseJsonFile(File file) {
		JsonObject content = new JsonObject();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			content = gson.fromJson(br, JsonObject.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content; 
	}
	private String readFile(File file) {
		String content = null;
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                content += line + "\n";
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content; 
	}
	
	public static void main(String[] args) {
//		
		StringBuilder s = new StringBuilder("Nguồn các thực thể: DBPedia, WikiPedia, Người kể sử \n");
		int totalEntities = 0;
		int totalConnection = 0;
		EntityStatistic entityStatistic = new EntityStatistic("src/data/");
		for (String type: EntityStatistic.ENTITY_TYPES) {
			int countEnity =  entityStatistic.countEntity(type);
			TreeMap<String, Integer> sortedProperties= entityStatistic.countEntityProperty(type);
			int countEnityProperty = sortedProperties.size();
			Set<String> topProperties = sortedProperties.keySet().stream().limit(10).collect(Collectors.toSet());
			int countConnectable = entityStatistic.countConnectable(type);
	        s.append("Số " + type +": " + countEnity + "\n");
	        s.append("Tống số thuộc tính của "+ type +":" + countEnityProperty + "\n");
	        s.append("Các thuộc tính chính: ").append(topProperties).append("\n\n");
	        totalEntities += countEnity;
	        totalConnection += countConnectable;
		}
		s.append("Tổng số thực thể: "+ totalEntities + "\n");
		s.append("Tổng số liên kết: "+ totalConnection + "\n\n");
		
		
//		s.append("Nguồn Wikipedia");
//		entityStatistic.countEntityProperty(ENTITY_TYPES[1]);
		System.out.println(s);

	}
	
	
}
