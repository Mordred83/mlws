package utils;

import java.util.Properties;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;
import utils.PropertyLoader;

public class Authenticate {
		
	public static TwitterStream getAuthenticationStreaming(){
		
		Properties p;
		TwitterStream twitterStream = null;
		
		try {
			p = PropertyLoader.load(PropertyLoader.AUTHENTICATE);
		
		ConfigurationBuilder builderStream = new ConfigurationBuilder();
		builderStream.setDebugEnabled(true);
		builderStream.setOAuthAccessToken(p.getProperty("ACCESS_TOKEN"));
		builderStream.setOAuthAccessTokenSecret(p.getProperty("ACCESS_TOKEN_SECRET"));
		builderStream.setOAuthConsumerKey(p.getProperty("CONSUMER_KEY"));
		builderStream.setOAuthConsumerSecret(p.getProperty("CONSUMER_SECRET"));
		builderStream.setJSONStoreEnabled(true);
    
	    TwitterStreamFactory twitterStreamFactory = new TwitterStreamFactory(builderStream.build());
	        	
        //AUTENTICAZIONE
        twitterStream = twitterStreamFactory.getInstance();
        twitterStream.getAuthorization();
        
        System.out.println("Authentication success");
        
		}catch(Exception e){
			System.out.println("Authentication failed: " + e);
		}
		return twitterStream;
	}
	
}
