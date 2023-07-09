package nks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import util.DataIO;

public class NKSHistoricalSite {
	private String uri;
	private Document doc;
	
	public NKSHistoricalSite(String uri) {
		this.uri = "https://nguoikesu.com" + uri;
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
	
	public JsonObject getInfoBox() {
		JsonObject charProperties = new JsonObject();
		JsonObject claims = new JsonObject();
		Elements rows = null;
		Element overview = doc.select("p").first();
		if(overview != null && overview.text().indexOf(".") != -1) {
			System.out.println(overview.text().indexOf("."));
			String overviewText = overview.text();
			charProperties.addProperty("overview", overviewText.substring(0, overview.text().indexOf(".")));
		} else {
			charProperties.addProperty("overview", "");
		}
		try {			
			 rows =  doc.select("table.infobox").first().getElementsByTag("tbody").first().getElementsByTag("tr");
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(rows != null) {			
			for (Element row: rows) {
				Element th = row.getElementsByTag("th").first();
				Element td = row.getElementsByTag("td").first();
				if(th != null && td!= null) {
					if(td.getElementsByTag("ul").size() > 0) {
						JsonArray value = new JsonArray();
						Elements items = td.select("ul li");
						for (Element item : items) {
							String liText = item.text();
							value.add(liText);
						}
						claims.add(th.text(), value);
					} else {
						claims.addProperty(th.text(), td.text());
					}
				}
			}
		}
		charProperties.add("claims", claims);
		charProperties.addProperty("label", getName());
		return charProperties;
	}
	
	public String getName() {
		return doc.getElementsByTag("h2").first().text();
	}
	
	public static void main(String[] args) {
		JsonArray array = new JsonArray();
		String string = null; 
		try {			
			FileReader file = new FileReader("./data/historicalsite.txt");
			BufferedReader bufferedReader = new BufferedReader(file);
			while((string = bufferedReader.readLine()) != null) {
				NKSHistoricalSite site  = new NKSHistoricalSite(string);
				array.add(site.getInfoBox());
			}
		} catch (Exception e) {
			System.out.println(string);
			e.printStackTrace();
		}
		System.out.println(array);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		DataIO.writeFile(gson.toJson(array), "historicalSite");
	}
}
