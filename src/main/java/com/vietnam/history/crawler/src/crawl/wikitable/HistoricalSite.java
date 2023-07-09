package crawl.wikitable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import util.DataIO;

public class HistoricalSite {
	private String uri;
	private Document doc;
	public HistoricalSite(String uri) {
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
	
	public ArrayList<LinkedHashMap<String, String>> getTables() {
		ArrayList<LinkedHashMap<String, String>> data = new ArrayList<LinkedHashMap<String, String>>();
		Elements tables = doc.select("table.wikitable");
		System.out.println(tables.size());
		List <String> provinces = doc.select("h3 > span.mw-headline").eachText();
		int idx = provinces.indexOf("Hà Nam");
		provinces.add(idx+1, "Hà Nội");
		int i = 0;
		for (Element table:tables) {
			String province = provinces.get(i++);
			for (LinkedHashMap<String, String>l: convertTableToHashMapList(table, province)) {
				data.add(l);
			};
		}
		return data;
	}
	
	public ArrayList<LinkedHashMap<String, String>> convertTableToHashMapList(Element table, String province) {
		ArrayList<LinkedHashMap<String, String>> list = new ArrayList<>();
		Elements rows = table.select("tr");
		Elements th = rows.get(0).select("th");
		List<String> headers = th.eachText();
		for (int i = 1; i < rows.size(); i++) {
			Elements td = rows.get(i).select("td");
			LinkedHashMap<String, String> hMap = new LinkedHashMap<>();
			for (int j = 0; j < headers.size(); j++) {
				String value = "";
				try {
					value = td.get(j).text().replaceAll("\\[\\w{0,3}\\]", "");
				} catch (Exception e) {
				
				}
				if(j == 1) {
					value += ", " + province;
				}
				hMap.put(headers.get(j).replaceAll("\\[\\w{0,3}\\]", ""), value );

			}
			String url = "";
			String urlTitle = td.get(0).select("a").text();
//			System.out.println(td.get(0).text().indexOf(urlTitle) + " " + urlTitle );
			if(td.get(0).text().indexOf(urlTitle)<= 0 &&  !td.get(0).select("a").attr("href").isEmpty()) {	
				url = "https://vi.wikipedia.org" + td.get(0).select("a").attr("href");
			};
			hMap.put("link", url );				
			list.add(hMap);
			
		}
		return list;
	}
	public static void main(String[] args) {
		HistoricalSite historicalSite = new HistoricalSite("https://vi.wikipedia.org/wiki/Danh_s%C3%A1ch_Di_t%C3%ADch_qu%E1%BB%91c_gia_Vi%E1%BB%87t_Nam");
		ArrayList<LinkedHashMap<String, String>> data = historicalSite.getTables();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		DataIO.writeFile(gson.toJson(data), "site");
	}
	
}
