package twitter.auth;

import java.io.File;

import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Authenticate {

	public TwitterStream getAuthenticationStreaming(File credentialsID) {
		TwitterAppCredentials credentials = null;
		TwitterStream twitterStream = null;

		try {
			ClassLoader cl = getClass().getClassLoader();
//			for (File f : new File(cl.getResource("conf/fautenticate.xml").getFile()).listFiles())
//				System.out.println(f.getAbsolutePath());
			File credentialsXMLFile = new File(
												cl.getResource(	"conf/fauthenticate.xml").getFile());
			credentials = TwitterAppCredentialManager.getCredentials(credentialsXMLFile).get(0);
			ConfigurationBuilder builderStream = new ConfigurationBuilder();
			builderStream.setDebugEnabled(true);
			builderStream.setOAuthAccessToken(credentials.getAPIKey());
			builderStream.setOAuthAccessTokenSecret(credentials.getAPISecret());
			builderStream.setOAuthConsumerKey(credentials.getConsumerKey());
			builderStream.setOAuthConsumerSecret(credentials.getConsumerSecret());
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
