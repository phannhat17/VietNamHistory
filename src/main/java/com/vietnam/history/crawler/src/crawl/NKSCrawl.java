package crawl;

import java.io.BufferedReader;
import java.io.FileReader;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import nks.NKSCharacter;
import nks.NKSHistoricalEvent;
import nks.NKSHistoricalSite;
import util.DataIO;

public class NKSCrawl {
	public void getCharacterLinks() {
		StringBuilder character = new StringBuilder("");
		for (int i=0;i<=1450;i+=5) {
			NKSCharacter baseCrawl = new NKSCharacter("/nhan-vat?start="+i);
			Document document = baseCrawl.getHTML();
	
			Elements rows =  document.getElementsByTag("h2");
			for (Element e: rows ) {
				for(Element e2: e.getElementsByTag("a")) {
					character.append(e2.attr("href")+"\n");
				}
			}
		}
		DataIO.writeFile(character.toString(), "characterLinks");
	}
	
	public void getSiteLinks() {
		StringBuilder character = new StringBuilder("");
		for (int i=0;i<3;i+=1) {
			NKSCharacter baseCrawl = new NKSCharacter("/di-tich-lich-su?types[0]=1&start="+(i*10));
			Document document = baseCrawl.getHTML();
	
			Elements rows =  document.getElementsByTag("h2");
			for (Element e: rows ) {
				for(Element e2: e.getElementsByTag("a")) {
					character.append(e2.attr("href")+"\n");
				}
			}
		}
		DataIO.writeFile(character.toString(), "siteLinks");
	}
	
	public void getEventLinks() {
		StringBuilder character = new StringBuilder("");
		for (int i=0;i<=70;i+=5) {
			NKSCharacter baseCrawl = new NKSCharacter("/tu-lieu/quan-su?start="+i);
			Document document = baseCrawl.getHTML();
	
			Elements rows =  document.getElementsByTag("h2");
			for (Element e: rows ) {
				for(Element e2: e.getElementsByTag("a")) {
					character.append(e2.attr("href")+"\n");
				}
			}
		}
		DataIO.writeFile(character.toString(), "eventLinks");
	}
	
	public void getCharacters() {
		JsonArray array = new JsonArray();
		String string = null; 
		try {			
			FileReader file = new FileReader("./data/characterLinks.txt");
			BufferedReader bufferedReader = new BufferedReader(file);
			while((string = bufferedReader.readLine()) != null) {
				NKSCharacter character  = new NKSCharacter(string);
				array.add(character.getInfoBox());
			}
		} catch (Exception e) {
			System.out.println(string);
			e.printStackTrace();
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		DataIO.writeFile(gson.toJson(array), "character");
	}
	
	public void getEvents() {
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
	
	public void getSites() {
		JsonArray array = new JsonArray();
		String string = null; 
		try {			
			FileReader file = new FileReader("./data/siteLinks.txt");
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
		DataIO.writeFile(gson.toJson(array), "historicalSites");
	}
	
	public static void main(String[] args) {
		NKSCrawl nksCrawl = new NKSCrawl();
		nksCrawl.getCharacterLinks();
		nksCrawl.getSiteLinks();
		nksCrawl.getEventLinks();
		nksCrawl.getCharacters();
		nksCrawl.getSites();
		nksCrawl.getEvents();
	}
}
