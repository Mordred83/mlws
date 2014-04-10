package edu.sapienza.informatica.websocial.homework2;

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

public class Hw2 {

	private static final String EXCEL_FILE = "Homework/Homework 1 vax-tweets.xls/vax-tweets.xls";
	private static final String STOP_FILE = "Homework/stopWords.txt";
	private static final String TERMS_VOCABULARY = "Output/Porter Stemmer.txt";

	static List<String> tweets = new ArrayList<String>();
	static List<String> cleanTweets = new ArrayList<String>();
	static List<List<String>> termsTweets = new ArrayList<List<String>>();

	public static void main(String[] args) {

		try {
			tweets = getTweetsFromXLS(); // ottiene tutti i tweets contenuti nel file
			termsTweets = getTermsTweets();
			cleanTweets = reconcatenateTweet(termsTweets);
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

	private static List<List<String>> getTermsTweets() throws IOException {
		AnalyzerStemmer analyzer = new AnalyzerStemmer(Version.LUCENE_47, getStopWords());
		for (String s : tweets) {
			List<String> tweet = new ArrayList<String>();
			TokenStream tokenStream = analyzer.tokenStream("tweets",
					new StringReader(s));
			CharTermAttribute charTermAttribute = tokenStream
					.addAttribute(CharTermAttribute.class);
			tokenStream.reset();
			while (tokenStream.incrementToken()) {
				String term = charTermAttribute.toString();
				tweet.add(term);
			}
			termsTweets.add(tweet);
			tokenStream.close();
		}
		return termsTweets;
	}

	private static List<String> reconcatenateTweet(List<List<String>> listTweets) {
		List<String> result = new ArrayList<String>();
		for (List<String> tweet : listTweets) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < tweet.size(); i++) {
				sb.append(tweet.get(i));
				if (i < tweet.size() - 1)
					sb.append(" ");
			}
			result.add(sb.toString());
		}
		return result;
	}

	private static void getTermsVocabulary()
			throws IOException {
		System.out.println("VOCABULARY OF SINGLE TERMS WITH STEMMER:");

		Set<String> singleTerms = new HashSet<String>();
		for (List<String> tweet : termsTweets)
			singleTerms.addAll(tweet);

		Map<String, Integer> map = getFrequencies(singleTerms, cleanTweets);

		printMapToFile(new File(TERMS_VOCABULARY), map);
			
		System.out.println("VOCABULARY OF SINGLE TERMS WITH STEMMER: END");

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

	private static Map<String, Integer> getFrequencies(Set<String> set,
			List<String> tweets) {
		Map<String, Integer> result = new HashMap<String, Integer>();
		for (String s : set)
			result.put(s, 0);
		for (String tweet : tweets) {
			for (String term : set) {
				if (tweet.contains(term))
					result.put(term, result.get(term) + 1);
			}
		}
		return result;
	}

	private static void printMapToFile(File f, Map<String, Integer> map)
			throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(f);
		List<String> sortedList = new ArrayList<String>(map.keySet());
		Collections.sort(sortedList);
		for (String s : sortedList) {
			writer.println(s + "\t" + map.get(s));
		}
		writer.flush();
		writer.close();
	}
	
//	String stemTerm (String term) {
//	    PorterStemmer stemmer = new PorterStemmer();
//	    return stemmer.stem(term);
//	}

}
