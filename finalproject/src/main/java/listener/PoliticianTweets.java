package listener;

import java.util.ArrayList;
import java.util.List;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import utils.Authenticate;
import bean.Tweet;
import bean.TweetList;

public class PoliticianTweets implements Runnable{
	private final TwitterStream tStream;
	private final FilterQuery query ;
	private final String[] kwArray ;
	private final StatusListener listener = new StatusListener() {
		TweetList tweets = new TweetList(new ArrayList<Tweet>());
		
		@Override
		public void onStatus(Status status) {
			System.out.println("New tweet:");
            
            String username = status.getUser().getScreenName();
            long tweetId = status.getId(); 
            String content = status.getText();
            
            Tweet tweet = new Tweet(tweetId, username, content);
            tweets.addTweet(tweet);
            
            for(int i=0; i<tweets.getSize(); i++)
            	System.out.println(tweets.getIndex(i).toString());
			
		}

		@Override
		public void onException(Exception e) {
			e.printStackTrace();
			System.err.println("Stopping thread due to exception: "+e.getClass().getSimpleName());
			tStream.shutdown();
		};

		@Override
		public void onDeletionNotice(StatusDeletionNotice arg0) {};

		@Override
		public void onScrubGeo(long arg0, long arg1) {};

		@Override
		public void onStallWarning(StallWarning arg0) {};

		@Override
		public void onTrackLimitationNotice(int arg0) {};
		
	};
	
	public PoliticianTweets(TwitterStream tStream, List<String> kwList) {
		this.tStream = tStream;
		this.kwArray = kwList.toArray(new String[0]);
		query = new FilterQuery();
		query.track(kwArray);
	}

	@Override
	public void run() {
		System.out.println("Starting listener..");
		tStream.addListener(listener);
		tStream.filter(query);
	}
}