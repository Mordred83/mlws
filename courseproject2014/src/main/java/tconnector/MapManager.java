package tconnector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.python.constantine.Constant;

import common.Constants;

import twitter.auth.TwitterConnectionManager;
import twitter4j.Relationship;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class MapManager {
	
	static Twitter twitter;
		
	/**
	 * A partire da una lista di tweets, crea una mappa con chiave id del tweet
	 * @param list lista degli status (tweeets)
	 * @return mappa
	 */
	public static Map<Long, TRelations> createNetwork(List<Status> list) {
		Map<Long, TRelations> network = new HashMap<Long, TRelations>();
		for(Status status : list){
			long id = status.getId();
			if(!network.containsKey(id))
				network.put(id, null);
		}
		
		//network = fillNetwork(network);
		
		return network;
	}

//	private static Map<Long, TRelations> fillNetwork(Map<Long, TRelations> network) throws TwitterException {
//		twitter = Authenticate.getAuthentication(Constants.CRED_FILE_M);
//		Set<Long> users = network.keySet();
//		
//		for(long id : users){
//			Relationship relations = twitter.showFriendship(arg0, arg1);
//			relations.isSourceFollowedByTarget();
//		}
//		return null;
//	}

}
