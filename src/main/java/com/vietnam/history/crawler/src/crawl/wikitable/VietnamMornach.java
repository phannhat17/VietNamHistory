package crawl.wikitable;

import org.jsoup.nodes.*;
import org.jsoup.parser.*;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import util.DataIO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.jsoup.*;

public class VietnamMornach {
	private String uri;
	private Document doc;
	public VietnamMornach(String uri) {
		this.uri = uri;
		getHTML();
	}
	
	public Document getHTML() {
		try {
			doc = Jsoup.connect(uri).followRedirects(true).get();
		} catch (IOException e) {
			System.out.println("IOE error.");
		}
		return doc;
	}
	
	public LinkedHashMap<String, ArrayList<LinkedHashMap<String, String>>> getTables() {
		LinkedHashMap<String, ArrayList<LinkedHashMap<String, String>>> data = new LinkedHashMap<>();
		Elements tables = doc.select("table");
		List <String> l = doc.select("h3 > span.mw-headline").eachText();
		int i = 0;
		for (Element table:tables) {
			ArrayList<LinkedHashMap<String, String>> dynasty = convertTableToHashMapList(table);
			if(dynasty != null) {
				data.put(l.get(i++), dynasty);				
			}
		}
		return data;
	}
	
	public ArrayList<LinkedHashMap<String, String>> convertTableToHashMapList(Element table) {
		ArrayList<LinkedHashMap<String, String>> list = new ArrayList<>();
		Elements rows = table.select("tr");
		Elements th = rows.get(0).select("th");
		List<String> headers = th.eachText();
		if (!headers.contains("Chân dung")) {
			return null;
		}
		for (int i = 1; i < rows.size(); i++) {
			Elements td = rows.get(i).select("td");
			LinkedHashMap<String, String> hMap = new LinkedHashMap<>();
			String avatar = "https" + td.get(0).select("img").attr("src");
			hMap.put("Chân dung", avatar);
			for (int j = 1; j < headers.size()-1; j++) {
				hMap.put(headers.get(j).replaceAll("\\[\\w{0,3}\\]", ""), td.get(j).text().replaceAll("\\[\\w{0,3}\\]", ""));
			}
			int j = headers.size()-1;
			String fromTo = td.get(j).text();
			try {
				fromTo = td.get(j).text()+td.get(j+1).text()+td.get(j+2).text();
			} catch (Exception e){
				
			}
			hMap.put(headers.get(j).replaceAll("\\[\\w{0,3}\\]", ""), fromTo.replaceAll("\\[\\w{0,3}\\]", ""));
			hMap.put("link", "https://vi.wikipedia.org" + td.get(1).select("a").attr("href"));
			list.add(hMap);
			
		}
		return list;
	}
	public static void main(String[] args) {
		VietnamMornach vietnamEmperial =  new VietnamMornach("https://vi.wikipedia.org/wiki/Vua_Vi%E1%BB%87t_Nam");
		LinkedHashMap<String, ArrayList<LinkedHashMap<String, String>>> data = vietnamEmperial.getTables();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		DataIO.writeFile(gson.toJson(data), "monarch");
	}
	
}
