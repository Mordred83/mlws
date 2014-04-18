package main;

import static utils.Tweet.DKEY_DATA;

import utils.MyComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

public class Exercise6 {

	private static final Version VERSION = Version.LUCENE_47;
	private static final String INDEX_FOLDER = "src/main/resources/data/out.final.ndx";
	
	private static final boolean PRINT = true;
	private static final String FSTRING_HIT_NUMBER = "QUERY:\t%-32s\n\tHITS:%10d\n";
	private static final String FSTRING_3MOSTFREQWORDS = "TOP 3 WORDS:\n\t%s\n\t%s\n\t%s\n"; //XXX:HARDCODED 3
	private static final String FSTRING_WORD_FREQ = "TERM:\t%-16sFREQ:%5d";

	private static final Analyzer analyzer = new StandardAnalyzer(VERSION);
	private static final QueryParser parser = new QueryParser(VERSION,
			DKEY_DATA, analyzer);

	private static final int COLLECTED_HITS = 100;

	public static void main(String[] args) {
		Query query = null, newQuery = null;		
		
		try {
			/* 2nd Point: Execute query sore arm and get more than 270 results */
			query = parser.parse("sore arm");
			Directory directory = new SimpleFSDirectory(new File(INDEX_FOLDER));
			IndexReader reader = DirectoryReader.open(directory);
			IndexSearcher searcher = new IndexSearcher(reader);
			TopScoreDocCollector collector = TopScoreDocCollector.create(COLLECTED_HITS, true);
			searcher.search(query, collector);
			TopDocs docs = collector.topDocs();
			if(PRINT) System.out.println(String.format(FSTRING_HIT_NUMBER, query.toString(), docs.totalHits));
			/* 2nd Point : END */
			
			/* 3rd Point: Get the three most frequent term in the first 100 result of previous point and build a new query with those */ 
			ScoreDoc[] hits = docs.scoreDocs;
			Directory rDirectory = new RAMDirectory();
			IndexWriterConfig config = new IndexWriterConfig(VERSION, analyzer);
			IndexWriter rWriter = new IndexWriter(rDirectory, config);
			for (ScoreDoc sDoc : hits){
				rWriter.addDocument(reader.document(sDoc.doc));
			}
			rWriter.close();
			IndexReader rReader = DirectoryReader.open(rDirectory);
			TermsEnum tEnum = MultiFields.getTerms(rReader, DKEY_DATA).iterator(null);
			Map<String, Integer> frequencyMap = new HashMap<String, Integer>();
			List<String> termList = new ArrayList<String>();
			while (tEnum.next() != null) {
			      BytesRef term = tEnum.term();
			      String termStr = term.utf8ToString(); 
			      int frequency = rReader.docFreq(new Term(DKEY_DATA, term));
			      termList.add(termStr);
			      frequencyMap.put(termStr, frequency);
			      }
			Collections.sort(termList, new MyComparator<String>(frequencyMap));
			Collections.reverse(termList);
			StringBuffer sb = new StringBuffer();
			String[] formattedStrings = new String[3]; 
			
			for(int i=0; i<3; i++){// XXX: HARDCODED 3
				String termStr = termList.get(i);
				sb.append(termStr);
				if(i<(3-1))sb.append(" ");
				formattedStrings[i] = String.format(FSTRING_WORD_FREQ,termStr,frequencyMap.get(termStr));
			}
			if(PRINT)System.out.println(String.format(FSTRING_3MOSTFREQWORDS, formattedStrings[0],formattedStrings[1],formattedStrings[2]));
			
			newQuery = parser.parse(sb.toString().trim());
			collector = TopScoreDocCollector.create(COLLECTED_HITS, true);
			searcher.search(newQuery, collector);
			docs = collector.topDocs();
			if(PRINT) System.out.println(String.format(FSTRING_HIT_NUMBER, newQuery.toString(), docs.totalHits));
			/* 3rd Point: END */
			reader.close();
			rReader.close();

		} catch (Exception e) {
			System.out.println("Errore");
			e.printStackTrace();
		}

	}

}
