package crawl.wikitable;

import java.awt.RenderingHints.Key;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import util.DataIO;

public class War {
	private String uri;
	private Document doc;
	public War(String uri) {
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
	public static void main(String[] args) {
		War war = new War("https://vi.wikipedia.org/wiki/Chi%E1%BA%BFn_tranh_Vi%E1%BB%87t_Nam");
		Document document = war.getHTML();
		Elements td =  document.select("div[aria-labelledby=\"Trận_đánh_và_Chiến_dịch_trong_Chiến_tranh_Việt_Nam\"]").select("td");
		ArrayList<LinkedHashMap<String, String>> wars = new ArrayList();

		String key0 =  "Giai đoạn Mỹ thực hiện\r\n"
				+ "Chiến tranh đặc biệt (1960-1964)";
		int i = 0;
		Elements list = td.select("p");
		for (Element e: list ) {
			Elements links = e.select("a");
			if(links.size() > 1) {
				String key;
				if(i == 0) {
					key= key0;
				} else {
					key = td.select("p > b").get(i-1).text();
				}
				for (Element e2: links ) {
					System.out.println(e2);
					LinkedHashMap<String , String> hMap = new LinkedHashMap<>();
					hMap.put("title", e2.attr("title"));
					hMap.put("href", e2.attr("href"));
					hMap.put("type", key);
					wars.add(hMap);
				}
				i+=1;
			}
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String jsonString = gson.toJson(wars);
//		System.out.println(jsonString);
		DataIO.writeFile(jsonString, "wars");
	}
}
