package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.GZIPInputStream;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;


public class FileParser {
	private static final String FILE_PREFIX = "TW-";
	private static final String FILE_EXT = ".gz";
	private final File file;
	private InputStream is ;
	
	public FileParser(File file) throws FileNotFoundException, IOException{
		this.file = file;
		this.is = getInputStream(file);
	}
	
	/**
	 * Decomprime un file .gz
	 * @param f File compresso
	 * @return InputStream del file decompresso
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static InputStream getInputStream(File f) throws FileNotFoundException, IOException{
		return new GZIPInputStream(new FileInputStream(f));
	}
	
	/**
	 * Legge il file riga per riga e crea il relativo status. 
	 * Ogni riga è un JSON corrispondente a un tweet
	 * @return lista di tweets
	 * @throws IOException
	 * @throws TwitterException
	 */
	public List<Status> getStatus() throws IOException, TwitterException{
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(getInputStream(file)))){
			ArrayList<Status> result = new ArrayList<>();
			String line = null;
			while ((line = reader.readLine())!=null){
				int firstBrac = line.indexOf('{');
				int lastBrac = line.lastIndexOf('}');
				String json = line.substring(firstBrac, lastBrac+1);
				Status status = TwitterObjectFactory.createStatus(json);
				if(status != null) result.add(status);
			}
			return (result.size() > 0) ? result:null;
		}
	}
	
}
