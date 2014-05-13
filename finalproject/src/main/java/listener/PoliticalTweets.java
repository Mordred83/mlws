package listener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterObjectFactory;
import twitter4j.TwitterStream;

public class PoliticalTweets implements Runnable {

	private static final int			CONS_TIMEOUT	= 10;				//SECONDS
	private static final TimeUnit		CONS_TIMEUNIT	= TimeUnit.SECONDS;
	private Boolean						run;
	private final TwitterStream			tStream;
	private final FilterQuery			query;
	private final String[]				kwArray;
	private final BlockingQueue<String>	sharedQueue;
	private File					outFile;
	//private final Map<String, Integer>	keyWTweetMap;
	private final StatusListener		listener		= new StatusListener() {

															// TweetList tweets = new TweetList(new ArrayList<Tweet>());

															@Override
															public void onStatus(Status status) {
																System.out.println("New tweet with id: "
																		+ status.getId());
																String rawJSON = TwitterObjectFactory.getRawJSON(status); 
																sharedQueue.add(rawJSON);
																System.out.println("Tweet: "
																		+ status.getId()
																		+ " added to Queue");
															}

															@Override
															public void onException(Exception e) {
																// TODO: better use of this method
																e.printStackTrace();
																System.err.println("Stopping thread due to exception: "
																		+ e.getClass().getSimpleName());
															};

															@Override
															public void onDeletionNotice(
																	StatusDeletionNotice arg0) {};

															@Override
															public void onScrubGeo(long arg0,
																	long arg1) {};

															@Override
															public void onStallWarning(
																	StallWarning arg0) {};

															@Override
															public void onTrackLimitationNotice(
																	int arg0) {};

														};

	public PoliticalTweets(TwitterStream tStream,
							List<String> kwList,
							File outFile) {
		this.tStream = tStream;
		this.kwArray = kwList.toArray(new String[0]);
		this.outFile = outFile;
		this.sharedQueue = new LinkedBlockingQueue<String>();
//		this.keyWTweetMap = instantiateKeyWTweetNMap(kwList);
		this.run = true;
		query = new FilterQuery();
		query.track(kwArray);
	}

	@Override
	public void run() {
		System.out.println("Starting listener..");
		tStream.addListener(listener);
		tStream.filter(query);
		while (true) {
			String statusJSON = null;
			try {
				statusJSON = sharedQueue.poll(CONS_TIMEOUT, CONS_TIMEUNIT);
				if (statusJSON != null) {
					System.out.println("Saving tweet");
					saveTweet(statusJSON, outFile);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized (run) {
				if (!run) break;
			}
		}
	}

	public void terminated() {
		System.out.println("Terminating listner");
		synchronized (run) {
			run = false;
		}
	}
	
	public synchronized void changeOutFile(File newOutFile){
		System.out.println("Changinh output file to: "+newOutFile.getAbsolutePath());
		if(newOutFile.exists() && newOutFile.canWrite()){
			this.outFile = newOutFile;
		}else{
			String msg = newOutFile.getAbsolutePath()+(!newOutFile.exists() ? ": doesn't exists":" not enough permissions");
			throw new IllegalArgumentException(msg);
		}
	}

	private static void saveTweet(String tweet, File outFile) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new FileOutputStream(outFile, true)); //Open in append mode
			writer.println(tweet);
			writer.flush();
			System.out.println("Tweet Saved");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}