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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import util.DataIO;

public class NKSHistoricalEvent {
	private String uri;
	private Document doc;
	
	public NKSHistoricalEvent(String uri) {
		this.uri = "https://nguoikesu.com" + uri;
		getHTML();
	}
	
	public Document getHTML() {
		try {
			doc = Jsoup.connect(uri).followRedirects(true).get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	private String br2nl(String html) {
	    if(html==null)
	        return html;
	    Document document = Jsoup.parse(html);
	    document.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
	    document.select("br").append("\\n");
	    document.select("p").prepend("\\n\\n");
	    String s = document.html().replaceAll("\\\\n", "\n");
	    return Jsoup.clean(s, "", null, new Document.OutputSettings().prettyPrint(false));
	}
	
	public JsonObject getInfoBox() {
		JsonObject charProperties = new JsonObject();
		JsonObject claims = new JsonObject();
		Elements outerRow = null;
		Element overview = doc.select("p").first();
		if(overview != null && overview.text().indexOf(".") != -1) {
			String overviewText = overview.text();
			charProperties.addProperty("overview", overviewText.substring(0, overview.text().indexOf(".")));
		} else {
			charProperties.addProperty("overview", "");
		}
		try {			
			 outerRow =  doc.select("table table").first().getElementsByTag("tbody").first().getElementsByTag("tr");
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(outerRow != null) {
			Element table1 = outerRow.select("table").get(0);
			int idxRest = 1;
			try {				
				Element table2 = outerRow.select("table").get(1);
				idxRest = outerRow.indexOf(table2.parent().parent()) + 1;
				claims.addProperty("tên khác", table1.select("span").text());
				for (Element row: table2.getElementsByTag("tr")) {	
					if(row.select("td").size() > 1) {	
						Element th = row.getElementsByTag("td").first();
						Element td = row.getElementsByTag("td").get(1);
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
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			Element idxRow = outerRow.get(idxRest);
			if(idxRow.select("td").size() > 1) {	
				Element th = idxRow.getElementsByTag("td").first();
				Element td = idxRow.getElementsByTag("td").get(1);
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
			for (int i = idxRest+1; i< outerRow.size(); i++) {
				try {			
					Element rowTh = outerRow.get(i).getElementsByTag("th").first();
					Elements rowTd = outerRow.get(i+1).getElementsByTag("td");
					if(rowTd.size() > 1) {	
						Element td1 = rowTd.get(0);
						Element td2 = rowTd.get(1);
						if(td1 != null && td2!= null) {
							JsonObject twoSide = new JsonObject();
							if(td1.getElementsByTag("ul").size() > 0) {
								JsonArray value1 = new JsonArray();
								Elements items = td1.select("ul li");
								for (Element item : items) {
									String liText = item.text();
									value1.add(liText);
								}
								claims.add(rowTh.text() + " bên 1", value1);
							} else {
								claims.addProperty(rowTh.text() + " bên 1", td1.wholeText());
							}
							if(td2.getElementsByTag("ul").size() > 0) {
								JsonArray value2 = new JsonArray();
								Elements items = td2.select("ul li");
								for (Element item : items) {
									String liText = item.text();
									value2.add(liText);
								}
								twoSide.add(rowTh.text() + " bên 2", value2);
							} else {
								claims.addProperty(rowTh.text() + " bên 2", td2.wholeText());
							}
						}
					}
				} catch (Exception e) {
					continue;
				}
					
			}
		}
		charProperties.add("claims", claims);
		charProperties.addProperty("label", getName());
		return charProperties;
	}
	
	public String getName() {
		return doc.getElementsByTag("h1").first().text();
	}
	
	public static void main(String[] args) {
		JsonArray array = new JsonArray();
		String string = null; 
		try {			
			FileReader file = new FileReader("./data/eventLinks.txt");
			BufferedReader bufferedReader = new BufferedReader(file);
			while((string = bufferedReader.readLine()) != null) {
				NKSHistoricalEvent event = new NKSHistoricalEvent(string);
				array.add(event.getInfoBox());
				
			}
		} catch (Exception e) {
			System.out.println(string);
			e.printStackTrace();
		}

		
		System.out.println(array);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		DataIO.writeFile(gson.toJson(array), "events");

	}
}
