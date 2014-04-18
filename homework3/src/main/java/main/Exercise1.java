package main;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import analyzer.AnalyzerStemmer;
import analyzer.AnalyzerStopWords;

public class Exercise1 {

	private static final String EXCEL_FILE = "src/main/resources/data/vax-tweets.xls";
	private static final String STOP_FILE = "src/main/resources/data/stopwords.txt";
	private static final String TERMS_VOCABULARY = "src/main/resources/output/porter.stemmer.txt";

	static List<String> tweets = new ArrayList<String>();
	static List<String> termsTweets = new ArrayList<String>();

	public static void main(String[] args) {

		try {
			tweets = getTweetsFromXLS(); // ottiene tutti i tweets contenuti nel
											// file

			getTermsVocabulary(); // costruisce il vocabolario per i
										// single terms

		} catch (IOException e) {
			System.out.println("Errore");
			e.printStackTrace();
		}

	}

	private static List<String> getTweetsFromXLS()
			throws FileNotFoundException, IOException {
		File excelTweets = new File(EXCEL_FILE);
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(
				excelTweets));
		HSSFSheet mySheet = new HSSFWorkbook(fs).getSheetAt(0);
		List<String> tweets = new ArrayList<String>();

		for (Row row : mySheet)
			if (row.getRowNum() != 0)
				for (Cell cell : row)
					if (cell.getColumnIndex() == 2)
						tweets.add(cell.getStringCellValue());

		return tweets;
	}

	
	private static void getTermsVocabulary() throws IOException {
		System.out.println("VOCABULARY OF SINGLE TERMS WITH STEMMER:");
		
		Map<String, List<String>> termsCorrispondence = getTermsCorrispondence();
		
		printMapToFile(new File(TERMS_VOCABULARY), termsCorrispondence);
		
		System.out.println("VOCABULARY OF SINGLE TERMS WITH STEMMER: END");
		
	}
	
	
	private static Map<String,List<String>> getTermsCorrispondence() throws IOException{
		
		Map<String, List<String>> termsCorrispondence = new HashMap<String, List<String>>();
		
		AnalyzerStemmer analyzerStemmer = new AnalyzerStemmer(Version.LUCENE_47, getStopWords());
		AnalyzerStopWords analyzerStopWords = new AnalyzerStopWords(Version.LUCENE_47, getStopWords());
		
		for (String s : tweets) {
			
			TokenStream tokenStreamStop = analyzerStopWords.tokenStream("tweets", new StringReader(s));		
			CharTermAttribute charTermStop = tokenStreamStop.addAttribute(CharTermAttribute.class);			
			tokenStreamStop.reset();
			
			while (tokenStreamStop.incrementToken()) {
				List<String> stemmedWords = new ArrayList<String>();
				String term = charTermStop.toString();
				String termStem;
				if(term.startsWith("#") || term.startsWith("@"))
					termStem = term;
				else {
					TokenStream tokenStreamStem = analyzerStemmer.tokenStream("term", new StringReader(term));
					CharTermAttribute charTermStem = tokenStreamStem.addAttribute(CharTermAttribute.class);
					tokenStreamStem.reset();
					tokenStreamStem.incrementToken();
					termStem = charTermStem.toString();
					tokenStreamStem.close();
				}
				if(termsCorrispondence.containsKey(termStem))
					stemmedWords=termsCorrispondence.get(termStem);
				stemmedWords.add(term);
				termsCorrispondence.put(termStem, stemmedWords);			
			}
			tokenStreamStop.close();
		}
		
		return termsCorrispondence;
		
	}


	private static CharArraySet getStopWords() throws IOException {
		CharArraySet stopWords = new CharArraySet(Version.LUCENE_47, 0, true);
		BufferedReader br = new BufferedReader(new FileReader(STOP_FILE));
		String currentLine;
		while ((currentLine = br.readLine()) != null) {
			stopWords.add(currentLine);
		}
		return stopWords;
	}

	
	private static void printMapToFile(File f, Map<String, List<String>> termsCorrispondence)
			throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(f);
		List<String> sortedList = new ArrayList<String>(termsCorrispondence.keySet());
		Collections.sort(sortedList);
		for (String s : sortedList) {
			writer.println(s + "\t" + termsCorrispondence.get(s));
		}
		writer.flush();
		writer.close();
	}

}
