package main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.python.modules.synchronize;

import task1.data.ValueDistancePair;
import twitter.auth.TwitterConnectionManager;
import twitter4j.TwitterException;

public class SpideyRunnable implements Runnable {

	private static final int						FOLLOWERS_LIMIT	= 100;
	private static final int						FOLLOWEES_LIMIT	= 100;
	private static final int						MAX_CONNECTIONS	= 25;
	/**
	 * ---------------------------------------------------------- Shared variables
	 */
	private final Set<Long>							idSet;
	private final Set<Long>							processedSet;
	private final Map<Long, Collection<Long>>				graphMap;
	/**
	 * ---------------------------------------------------------- Private variables
	 */
	//private final Collection<Long>							followersSet;
	private Collection<Long>						followeesSet;
	private final BlockingQueue<ValueDistancePair>	idToCrawl;
	private final int								distance;
	private final long								value;

	public SpideyRunnable(	Set<Long> idSet,
							Set<Long> processedSet,
							BlockingQueue<ValueDistancePair> idToCrawl,
							ValueDistancePair pair,
							Map<Long, Collection<Long>> graphMap) {
		this.idSet = idSet;
		this.processedSet = processedSet;
		this.idToCrawl = idToCrawl;
		this.followeesSet = new HashSet<Long>();
		//this.followersSet = new HashSet<Long>();
		this.distance = pair.distance;
		this.value = pair.value;
		this.graphMap = graphMap;
	}

	@Override
	public void run() {
		long idThread = Thread.currentThread().getId();
		//Get element
		System.out.println("Thread n° " + idThread + "Taken result at distance: " + distance
				+ " with value: " + value);
		
		requestToTwitter(value);

		//Intersect followers and followees with our idSet
		intersectWithIDSet();

		reduceToMaxConnections();

		System.out.println("Thread n° " + idThread + " followees: " + followeesSet.size());
		//+ " followers: " + followersSet.size());
		//Remove from followers and followees all the already processed IDs
		addtoGraph();

		removeAlreadyProcessed();

		System.out.println("Thread n° " + idThread + " followees: " + followeesSet.size());
		//+ " followers: " + followersSet.size());
		
		//Add i retrieved ids to the IDS to crawl Set
		try {
			if (distance < ValueDistancePair.MAX_DISTANCE) {
				addIDsToCrawl(distance + 1);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

	}

	private synchronized void addtoGraph() {
		graphMap.put(value, followeesSet);

	}

	private void reduceToMaxConnections() {
		List<Long> list = new ArrayList<Long>(followeesSet);
		Collections.shuffle(list);
		followeesSet = new HashSet<Long>();
		for (int i = 0; i < MAX_CONNECTIONS; i++) {
			followeesSet.add(list.get(i));
		}

	}

	private synchronized void requestToTwitter(long id) {
		TwitterConnectionManager cManager = TwitterConnectionManager.getInstance();
		long idThread = Thread.currentThread().getId();
		try {
			//addFollowers(cManager.getConnection().getFollowersIDs(id, -1).getIDs());
			addFollowees(cManager.getConnection().getFriendsIDs(id, -1).getIDs());
			System.out.println("Thread n° " + idThread + " followees: " + followeesSet.size());
			//+ " followers: " + followersSet.size());
		} catch (TwitterException e) {
			System.out.println("Thread n° " + idThread + " Error on getting followers or followees");
			e.printStackTrace();
		}
	}

	private synchronized void intersectWithIDSet() {
		followeesSet.retainAll(idSet);
		//followersSet.retainAll(idSet);
	}

	private synchronized void removeAlreadyProcessed() {
		followeesSet.removeAll(processedSet);
		//followersSet.removeAll(processedSet);
	}

	private synchronized void includeInGraph() {

	}

	private synchronized void addIDsToCrawl(int distance) {

		for (long l : followeesSet)
			idToCrawl.add(new ValueDistancePair(l, distance));
		//		for (long l : followersSet)
		//			idToCrawl.add(new ValueDistancePair(l, distance));
	}

	//	private synchronized void addFollowers(long[] ids) {
	//		addIDs(followersSet, ids);
	//	}

	private synchronized void addFollowees(long[] ids) {
		addIDs(followeesSet, ids);
	}

	private static void addIDs(Collection<Long> set, long[] ids) {
		for (long id : ids)
			set.add(id);
	}
}
