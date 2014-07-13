package twitter.auth;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;

import twitter4j.RateLimitStatus;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import common.Constants;

public class TwitterConnectionManager {
	
	private static TwitterConnectionManager INSTANCE = null;
	
	private File[] credentialsIDs = new File[]{Constants.CRED_FILE_M,Constants.CRED_FILE_F};
	private int currentCredentialsIndex = 0;
	private Twitter connection = null;
	
	public static TwitterConnectionManager getInstance(){
		if(INSTANCE == null){
			INSTANCE = new TwitterConnectionManager();
		}
		return INSTANCE;
	}
	
	/**
	 * Ottiene l'autenticazione a Twitter
	 * @param credentialsID file xml contenente le credenziali di autenticazione
	 * @return
	 */
	public synchronized Twitter nextTwitterConnection() {
		TwitterAppCredentials credentials = null;
		Twitter twitter = null;

		try {
//			ClassLoader cl = getClass().getClassLoader();
////			for (File f : new File(cl.getResource("conf/fautenticate.xml").getFile()).listFiles())
////				System.out.println(f.getAbsolutePath());
//			File credentialsXMLFile = new File(cl.getResource(	"conf/fauthenticate.xml").getFile());
			
//			ClassPathResource resource = new ClassPathResource(Constants.RES_CONF_FOLDER+"/"+nextCredentialsFile().getName());
			ClassPathResource resource = new ClassPathResource(Constants.RES_CONF_FOLDER+"/"+Constants.CRED_FILE_M.getName());
			InputStream credentialsXMLFile = resource.getInputStream();
			credentials = TwitterAppCredentialManager.getCredentials(credentialsXMLFile).get(0);
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setDebugEnabled(true);
			builder.setOAuthAccessToken(credentials.getAccessToken());
			builder.setOAuthAccessTokenSecret(credentials.getAccessTokenSecret());
			builder.setOAuthConsumerKey(credentials.getConsumerKey());
			builder.setOAuthConsumerSecret(credentials.getConsumerSecret());
			builder.setJSONStoreEnabled(true);

			TwitterFactory twitterFactory = new TwitterFactory(builder.build());

			//AUTENTICAZIONE
			twitter = twitterFactory.getInstance();
			twitter.getAuthorization();

			System.out.println("Authentication success");

		} catch (Exception e) {
			System.out.println("Authentication failed:");
			e.printStackTrace();
		}
		return twitter;
	}
	
	private int nextCredentialsId(){
		currentCredentialsIndex = ((currentCredentialsIndex+1)%credentialsIDs.length);
		return currentCredentialsIndex;
	}
	
	private File nextCredentialsFile(){
		return credentialsIDs[nextCredentialsId()];
	}

	public synchronized Twitter getConnection() {
		try {
			int timeWait = 0;
			if(connection == null){
				connection = nextTwitterConnection();
			}
			if((timeWait=connectionLimitReached())>0) {
				Thread.sleep(timeWait);
				connection = nextTwitterConnection();
			}
		} catch (TwitterException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return connection;
	}

	private int connectionLimitReached() throws TwitterException {
		int timeToWait = 0;
		Map<String, RateLimitStatus> map = connection.getRateLimitStatus();
		for(String key : map.keySet()){
			RateLimitStatus status = map.get(key);
			if(status.getLimit() > status.getRemaining()){
				System.out.println("Limit changing on endpoint:"+key);
				System.out.println("Limit-Remaining: "+status.getLimit()+" - "+status.getRemaining()+" | time to reset: "+status.getSecondsUntilReset());
			}
			if(status.getRemaining()==0){
				System.out.println("------LIMITE SUPERATO------");
				timeToWait = status.getSecondsUntilReset();
				return (timeToWait+1)*1000;
			}
		}
		return timeToWait;
	}
	
	

}