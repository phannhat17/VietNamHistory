package util;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class DataIO {
	private static int id = 0;
	public static void writeFile(String dataString,String fileName) {
		try {
			FileWriter fileWriter = new FileWriter("data/"+fileName+".txt");
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(dataString);
			bufferedWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOE error");
		}
	}
}
