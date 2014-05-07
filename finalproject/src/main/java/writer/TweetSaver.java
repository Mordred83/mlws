package writer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


public class TweetSaver implements Runnable {
	private static final int CONS_TIMEOUT = 10 ; //SECONDS
	private static final TimeUnit CONS_TIMEUNIT = TimeUnit.SECONDS; 
	private Boolean run ;
	private final BlockingQueue<String> sharedQueue;
	private final File outFile;
	
	public TweetSaver(BlockingQueue<String> sharedQueue, File outFile){
		this.sharedQueue = sharedQueue;
		this.outFile = outFile;
		this.run = true;
	}

	@Override
	public void run() {
		while(true){
			String tweet = null;
			try {
				tweet = sharedQueue.poll(CONS_TIMEOUT, CONS_TIMEUNIT);
				if(tweet != null){
					System.out.println("Saving tweet");
					saveTweet(tweet, outFile);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				terminate();
			}
			synchronized (run) {
				if(!run)break;
			}
		}
	}

	public void terminate(){
		synchronized (run) {
			run = false;
		}
	}
	
	private static void saveTweet(String tweet, File outFile) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new FileOutputStream(outFile, true));
			writer.println(tweet);
			writer.flush();
			System.out.println("Tweet Saved");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			if(writer != null){
				writer.close();
			}
		}
	}

}
