package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import listener.PoliticianTweets;
import twitter4j.TwitterStream;
import utils.Authenticate;

public class EuropeanElections {
	/**
	 * Default resources directory
	 */
	public static final File DEF_RES_DIR = new File("src/main/resources");
	/**
	 * Default keyword file
	 */
	public static final File DEF_KWFILE = new File(DEF_RES_DIR, "keywords.txt");
	/**
	 * Default output file
	 */
	public static final File DEF_OUTFILE = new File(DEF_RES_DIR, "output.txt");
	/**
	 * Generic error EXIT status {@value #EXST_ERR_GEN}
	 */
	private static final int EXST_ERR_GEN = 255;
	/**
	 * Singleton reference
	 */
	private static EuropeanElections INSTANCE = null;
	/**
	 * Runtime keyword file
	 */
	private File kwFile = null;
	/**
	 * Runtime output file
	 */
	private File outFile = null;
	/**
	 * Keyword List
	 */
	private List<String> kwList = null;
	
	/* MULTITHREADING */
	private BlockingQueue<String> sharedQueue = new LinkedBlockingQueue<String>();

	/**
	 * Access twitter via internet and downloads any tweet that contains at
	 * least one of the keyword in the keywords file
	 * 
	 * @param kwFile
	 *            one keyword per line file
	 * @param outFile
	 *            output file
	 * @throws IOException
	 *             if <b>kwFile</b> or <b>outFile</b> don't exists or cannot be
	 *             used
	 */
	private EuropeanElections(final File kwFile, final File outFile)
			throws IOException {
		this.kwFile = kwFile;
		this.outFile = outFile;
		// Validating arguments
		if (!kwFile.exists())
			throw new IllegalArgumentException(kwFile.getAbsolutePath()
					+ " does not exists");
		else if (!kwFile.canRead())
			throw new IllegalArgumentException(kwFile.getAbsolutePath()
					+ " not enough permissions to read");
		if (!outFile.exists())
			throw new IllegalArgumentException(outFile.getAbsolutePath()
					+ " does not exists");
		else if (!outFile.canWrite())
			throw new IllegalArgumentException(outFile.getAbsolutePath()
					+ " not enough permission to write file");
		kwList = getKeywords(kwFile);
		if (kwList.size() < 1)
			throw new IllegalArgumentException(kwFile.getAbsolutePath()
					+ " must contain at least one word");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EuropeanElections instance = null;
		File kwFile = null, outFile = null;
		TwitterStream twitterStream = null;
		PoliticianTweets pTweet = null;
		//MULTITHREADING
		Thread prodThread=null;
		try {
			// AVVIO STREAMING TWITTER
			kwFile = args.length > 0 ? new File(args[0]) : DEF_KWFILE;
			outFile = args.length > 1 ? new File(args[1]) : DEF_OUTFILE;
			instance = EElectionFactory(kwFile, outFile);
			twitterStream = Authenticate.getAuthenticationStreaming();
			// START LISTENER
			pTweet = new PoliticianTweets(twitterStream, instance.kwList, outFile);
			//MULTITHREADING
			prodThread = new Thread(pTweet);
			prodThread.start();
			while(prodThread.isAlive()){
				Thread.sleep(5*1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(EXST_ERR_GEN);
		} finally {
			System.out.println("finally block called");
			//tSaver.terminate();
		}
	}

	/**
	 * @return
	 * @throws IOException
	 *             if {@value file} can't be read
	 */
	private static final List<String> getKeywords(File file) throws IOException {
		ArrayList<String> result = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (checkKeyWord(line)) {
					result.add(line);
				} else {
					System.err.println(line + " malformed keyword. Skipped.");
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (reader != null)
				reader.close();
		}
		return result;
	}

	private static final EuropeanElections EElectionFactory(
			File keyWordFilePath, File outputFile) throws IOException {
		if (INSTANCE == null)
			INSTANCE = new EuropeanElections(keyWordFilePath, outputFile);
		return INSTANCE;
	}

	private static boolean checkKeyWord(String kw) {
		return kw.trim().matches("[a-zA-Z ]+");
	}

}
