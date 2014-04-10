package edu.sapienza.informatica.websocial.homework2;

public class Tweet {
	
	private static final String FSTRING = "id:%10d time:%20d data:%s";
	
	public static final String DKEY_ID = "id";
	public static final String DKEY_TIME = "time";
	public static final String DKEY_DATA = "data";
	
	public final long id;
	public final long time;
	public final String data;
	
	public Tweet(long id, long time, String data){
		this.id = id;
		this.time = time;
		this.data = data;
	}
	
	public Tweet(String id, String time, String data){
		this(Long.valueOf(id), Long.valueOf(time), data);
	}
	
	@Override
	public String toString() {
		return String.format(FSTRING, id,time,data);
	}
}
