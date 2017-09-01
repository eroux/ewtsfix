package io.bdrc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import io.bdrc.ewtsconverter.EwtsConverter;

public class Main {
	
	public static int badLines = 0;
	public static int allLines = 0;
	public static EwtsConverter converter = new EwtsConverter();
	
	public static String[] handleLine(final String[] line) {
		allLines += 1;
		String firstPart = line[0];
		if (firstPart.startsWith("OK:") || firstPart.startsWith("LNG:"))
			return line;
		final List<String> warns = new ArrayList<>();
		converter.toUnicode(firstPart, warns, true);
		if (warns.isEmpty())
			return new String[] {"OK:"+firstPart};
		badLines += 1;
		StringBuilder warnsStr = new StringBuilder();
		for (String warning : warns) {
            warnsStr.append(warning.replace('"', '`').replace("line1: ", ""));
        }
		return new String[] {firstPart, warnsStr.toString()};
	}
	
	public static void main( String[] args ) throws IOException {
		
		File corrFile = new File("corrections.csv");
		FileReader corrFileReader = new FileReader(corrFile);
		final CSVReader csvReader = new CSVReader(corrFileReader);
		
		List<String[]> newLines = new ArrayList<>();

	     String [] nextLine;
	     while ((nextLine = csvReader.readNext()) != null) {
	        newLines.add(handleLine(nextLine));
	     }
	     
	     csvReader.close();
		corrFileReader.close();
		
		FileWriter fw = new FileWriter(corrFile);
		CSVWriter csvWriter = new CSVWriter(fw);
		
		csvWriter.writeAll(newLines, true);
		
		csvWriter.close();
		fw.close();
		
		System.out.println(badLines+" problematic on "+allLines+" (rate: "+((double)badLines/(double)allLines)+")");
		
	}
}
