package edu.sapienza.informatica.websocial.homework2;

import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

public class AnalyzerStemmer extends Analyzer {
	
	public static final CharArraySet STOP_WORDS_SET = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
	protected final CharArraySet stopwords;
	protected final Version matchVersion;
	public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;
	
	
	public AnalyzerStemmer(Version version, CharArraySet stopwords) {
		 matchVersion = version;
		    // analyzers should use char array set for stopwords!
		    this.stopwords = stopwords == null ? CharArraySet.EMPTY_SET : CharArraySet
		        .unmodifiableSet(CharArraySet.copy(version, stopwords));
	  }


		 @Override
		 protected TokenStreamComponents createComponents(String arg0, Reader arg1) {
		  Tokenizer lTokenizer = new LetterTokenizer(matchVersion, arg1){
		   @Override
		   protected boolean isTokenChar(int c) {
		    return super.isTokenChar(c) || c == '#';
		   }
		  };
		     TokenStream filter = new LowerCaseFilter(matchVersion, lTokenizer);
		     filter = new PorterStemFilter(new StopFilter(matchVersion, filter, stopwords));
		     return new TokenStreamComponents(lTokenizer, filter);
		 }

}
