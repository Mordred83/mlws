package bean;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class TweetList {
	
	public List<Tweet> tweets = new ArrayList<Tweet>();
	
	public TweetList(List<Tweet> tweets){
		this.tweets=tweets;
	}
	
	public boolean addTweet(Tweet tweet) {
		return this.tweets.add(tweet);
	}
	
	public void saveToFile(String path) throws IOException {
		FileOutputStream fos = new FileOutputStream(path);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		
		for(Tweet s : tweets)
			oos.writeObject(s);
		
		oos.close();
		fos.close();
	}
	
	public void readFromFile(String path) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(path);
		ObjectInputStream ois = new ObjectInputStream(fis);
		
		Object in = ois.readObject();
		while(in!=null){
			this.tweets.add((Tweet) in);
			in = ois.readObject();
		}
		
		ois.close();
		fis.close();
	}
	
	public Tweet getIndex(int i) {
		return tweets.get(i);
	}
	
	public int getSize(){
		return tweets.size();
	}

}
