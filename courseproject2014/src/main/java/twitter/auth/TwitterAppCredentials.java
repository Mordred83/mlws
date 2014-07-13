package twitter.auth;


public class TwitterAppCredentials {

	private final String		apiKey;
	private final String		apiSecret;
	private final String		consKey;
	private final String		consSecret;

	public TwitterAppCredentials(	String apiKey,
									String apiSecret,
									String consKey,
									String consSecret) {
		this.apiKey = apiKey;
		this.apiSecret = apiSecret;
		this.consKey = consKey;
		this.consSecret = consSecret;
	}

	public String getAccessToken() {
		return apiKey;
	}

	public String getAccessTokenSecret() {
		return apiSecret;
	}

	public String getConsumerKey() {
		return consKey;
	}

	public String getConsumerSecret() {
		return consSecret;
	}
}
