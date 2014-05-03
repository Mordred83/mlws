package bean;


public class Tweet {
	
	private static final String FSTRING = "idStatus:%10d username:%25s tweet:%140s";
	
	public long idStatus;
	public String username;
	public String tweet;
	
	
	public Tweet(long idStatus, String username, String tweet) {
		this.idStatus=idStatus;
		this.username=username;
		this.tweet=tweet;
	}


	public long getIdStatus() {
		return idStatus;
	}


	public void setIdStatus(long idStatus) {
		this.idStatus = idStatus;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getTweet() {
		return tweet;
	}


	public void setTweet(String tweet) {
		this.tweet = tweet;
	}
	
	@Override
	public String toString() {
		return String.format(FSTRING, idStatus, username, tweet);
	}

}
