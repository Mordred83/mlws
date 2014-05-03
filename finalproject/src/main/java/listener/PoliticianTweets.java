package listener;

import java.util.ArrayList;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import utils.Authenticate;
import bean.Tweet;
import bean.TweetList;

public class PoliticianTweets {
		
	
	public static void startListener() {

		System.out.println("Starting listener..");
		
		// AVVIO STREAMING TWITTER
		TwitterStream twitterStream = Authenticate.getAuthenticationStreaming();
		
		StatusListener listener = new StatusListener() {
			TweetList tweets = new TweetList(new ArrayList<Tweet>());

			@Override
			public void onException(Exception arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onScrubGeo(long arg0, long arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStallWarning(StallWarning arg0) {
				// TODO Auto-generated method stub
				
			}

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
			public void onTrackLimitationNotice(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
		};
		
		final FilterQuery query = new FilterQuery();
		final String[] keywords = {"#pelu", "#crisi"};
		
		query.track(keywords);
		twitterStream.addListener(listener);
		twitterStream.filter(query);
	}
}