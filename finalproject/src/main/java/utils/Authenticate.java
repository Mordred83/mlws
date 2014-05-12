package utils;

import java.util.Properties;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;
import utils.PropertyLoader;

public class Authenticate {

	//	ACCESS_TOKEN=2423979276-2QVOmHkUo8V3hZGcdNmajyOBl2HqT1gONV7rhEj
	//	ACCESS_TOKEN_SECRET=Ul4QgsCfVI6RG6Il1VvYNHvst9zD7CIEoKvvoRUqsmdBf
	//	CONSUMER_KEY=DqeNs2hyYsA5AwE1KKnGtg
	//	CONSUMER_SECRET=JMtOWhxqcseBaH2gmPiM8ZxnzQGnCxIdv5YhK6RzEA

	public static TwitterStream getAuthenticationStreaming() {

//		Properties p;
		TwitterStream twitterStream = null;

		try {
			//p = PropertyLoader.load(PropertyLoader.AUTHENTICATE);

			ConfigurationBuilder builderStream = new ConfigurationBuilder();
			builderStream.setDebugEnabled(true);
			builderStream.setOAuthAccessToken("2423979276-2QVOmHkUo8V3hZGcdNmajyOBl2HqT1gONV7rhEj");
			builderStream.setOAuthAccessTokenSecret("Ul4QgsCfVI6RG6Il1VvYNHvst9zD7CIEoKvvoRUqsmdBf");
			builderStream.setOAuthConsumerKey("DqeNs2hyYsA5AwE1KKnGtg");
			builderStream.setOAuthConsumerSecret("JMtOWhxqcseBaH2gmPiM8ZxnzQGnCxIdv5YhK6RzEA");
			builderStream.setJSONStoreEnabled(true);

			TwitterStreamFactory twitterStreamFactory = new TwitterStreamFactory(
																					builderStream.build());

			//AUTENTICAZIONE
			twitterStream = twitterStreamFactory.getInstance();
			twitterStream.getAuthorization();

			System.out.println("Authentication success");

		} catch (Exception e) {
			System.out.println("Authentication failed:");
			e.printStackTrace();
		}
		return twitterStream;
	}

}
