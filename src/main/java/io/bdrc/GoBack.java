package io.bdrc;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;

import io.bdrc.ewtsconverter.EwtsConverter;

public class GoBack {
	public static EwtsConverter converter = Main.converter;
	
	public static void handleLine(final List<String[]> newLines, final String[] line, final String[] errLine) {
		String firstPart = line[0];
		if (firstPart.startsWith("LNG:"))
			newLines.add(new String[] {errLine[0], errLine[1], "LNG"});
		if (!firstPart.startsWith("OK"))
			return;
		String orig = errLine[1];
		final List<String> warns = new ArrayList<>();
		converter.toUnicode(orig, warns, true);
		if (warns.isEmpty())
			return;
		newLines.add(new String[] {errLine[0], errLine[1], firstPart.substring(3)});
	}
	
	public static void main( String[] args ) throws IOException {
		
		File corrFile = new File("corrections.csv");
		File errsFile = new File("errors-ewts.csv");
		File resFile = new File("result.txt");
		FileReader corrFileReader = new FileReader(corrFile);
		FileReader errsFileReader = new FileReader(errsFile);
		final CSVReader csvReader = new CSVReader(corrFileReader);
		final CSVReader errsCsvReader = new CSVReader(errsFileReader);
		
		List<String[]> newLines = new ArrayList<>();

	     String [] nextLine;
	     String [] nextErrLine;
	     while ((nextLine = csvReader.readNext()) != null) {
	    	 nextErrLine = errsCsvReader.readNext();
	         handleLine(newLines, nextLine, nextErrLine);
	     }
	     
	    csvReader.close();
		corrFileReader.close();
		errsCsvReader.close();
		errsFileReader.close();
		
		FileWriter fw = new FileWriter(resFile);
		
		for (String[] line : newLines) {
			fw.append(line[0]+":::"+line[1]+":::"+line[2]+"\n");
		}
		
		fw.close();
		
	}
}
