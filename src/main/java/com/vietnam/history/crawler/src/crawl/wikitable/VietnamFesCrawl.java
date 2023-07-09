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

public class VietnamFesCrawl {
	private String uri;
	private Document doc;
	public VietnamFesCrawl(String uri) {
		// TODO Auto-generated constructor stub
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
	
	public ArrayList<LinkedHashMap<String, String>> getTable(Element table) {
		ArrayList<LinkedHashMap<String, String>> list = new ArrayList<>();
		Elements rows = table.select("tr");
		Elements th = rows.get(0).select("th,td");
		List<String> headers = th.eachText();
		for (int i = 1; i < rows.size(); i++) {
			Elements td = rows.get(i).select("td");
			LinkedHashMap<String, String> hMap = new LinkedHashMap<>();
			for (int j = 0; j < headers.size(); j++) {
				hMap.put(headers.get(j).replaceAll("\\[\\w{0,3}\\]", ""), td.get(j).text().replaceAll("\\[\\w{0,3}\\]", ""));
			}
			String link = "https://vi.wikipedia.org" + td.get(2).select("a").attr("href");
			hMap.put("link", link );
			list.add(hMap);
			
		}
		return list;
	}
	public static void main(String[] args) {
		VietnamFesCrawl vietnamFesCrawl =  new VietnamFesCrawl("https://vi.wikipedia.org/wiki/L%E1%BB%85_h%E1%BB%99i_Vi%E1%BB%87t_Nam");
		Element table = vietnamFesCrawl.getHTML().select("table").get(1);
		ArrayList<LinkedHashMap<String, String>> data = vietnamFesCrawl.getTable(table);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		DataIO.writeFile(gson.toJson(data), "festival");
	}
}
