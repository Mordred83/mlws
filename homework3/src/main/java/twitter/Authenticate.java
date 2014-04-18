package twitter;

import java.io.File;
import java.util.Properties;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import utils.PropertyLoader;

public class Authenticate {
	private static final String T_API_XML_FILEPATH = "";
	private static final File T_API_XML_FILE = new File(T_API_XML_FILEPATH);
		
	public static Twitter getAuthentication(){
		
		Properties p;
		Twitter twitter = null;
		
		try {
			p = PropertyLoader.load(PropertyLoader.AUTHENTICATE);
		
		ConfigurationBuilder builderStream = new ConfigurationBuilder();
	    builderStream.setDebugEnabled(true);
	    builderStream.setOAuthAccessToken(p.getProperty("ACCESS_TOKEN"));
	    builderStream.setOAuthAccessTokenSecret(p.getProperty("ACCESS_TOKEN_SECRET"));
	    builderStream.setOAuthConsumerKey(p.getProperty("CONSUMER_KEY"));
	    builderStream.setOAuthConsumerSecret(p.getProperty("CONSUMER_SECRET"));
	    builderStream.setUseSSL(true);
    
	    
	    TwitterFactory twitterFactory = new TwitterFactory(builderStream.build());
	        	
        //AUTENTICAZIONE
        twitter = twitterFactory.getInstance();
        twitter.getAuthorization();
        
		}catch(Exception e){
			
		}
		return twitter;
	}
	
}
