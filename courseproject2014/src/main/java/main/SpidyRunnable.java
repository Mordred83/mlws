package main;

import java.util.HashSet;
import java.util.Set;

import task1.HopLimitedCrawlerQueue;
import twitter.auth.TwitterConnectionManager;
import twitter4j.RateLimitStatus;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class SpidyRunnable implements Runnable {

	private static final int					FOLLOWERS_LIMIT			= 100;
	private static final int					FOLLOWEES_LIMIT			= 100;
	private static final int					NUMBER_OF_TWITTER_REQ	= 2;
	private final Set<Long>						followersSet;
	private final Set<Long>						followeesSet;
	private final HopLimitedCrawlerQueue<Long>	idToCrawl;
	private final Set<Long>						idSet;
	private final Set<Long>						processedSet;

	public SpidyRunnable(	Set<Long> idSet,
							Set<Long> processedSet,
							HopLimitedCrawlerQueue<Long> idToCrawl) {
		this.idSet = idSet;
		this.processedSet = processedSet;
		this.idToCrawl = idToCrawl;
		this.followeesSet = new HashSet<Long>();
		this.followersSet = new HashSet<Long>();
	}

	@Override
	public void run() {
		long idThread = Thread.currentThread().getId();

		//Get element
		HopLimitedCrawlerQueue<Long>.Result result = popID();
		if (popID() != null) {
			System.out.println("Thread n° " + idThread + "Taken result at hop: " + result.hop
					+ " with value: " + result.element.toString());
			//retrieve followers and followees TODO: expand to retrieve all friends and followers
			// TODO: check limit reached
			requestToTwitter(result);

			//Intersect followers and followees with our idSet
			intersectWithIDSet();

			System.out.println("Thread n° " + idThread + " followees: " + followeesSet.size()
					+ " followers: " + followersSet.size());
			//Remove from followers and followees all the already processed IDs
			removeAlreadyProcessed();

			System.out.println("Thread n° " + idThread + " followees: " + followeesSet.size()
					+ " followers: " + followersSet.size());
			//TODO aggiungere archi
			//Add i retrieved ids to the IDS to crawl Set
			try {
				if (result.hop < idToCrawl.getMaxHops()) {
					addIDsToCrawl(result.hop + 1);
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
	}

	private synchronized void requestToTwitter(HopLimitedCrawlerQueue<Long>.Result result) {
		TwitterConnectionManager cManager = TwitterConnectionManager.getInstance();
		long idThread = Thread.currentThread().getId();
		try {
			addFollowers(cManager.getConnection().getFollowersIDs(result.element, -1).getIDs());
			addFollowees(cManager.getConnection().getFriendsIDs(result.element, -1).getIDs());
			System.out.println("Thread n° " + idThread + " followees: " + followeesSet.size()
					+ " followers: " + followersSet.size());
		} catch (TwitterException e) {
			System.out.println("Thread n° " + idThread + " Error on getting followers or followees");
			e.printStackTrace();
		}
	}

	private synchronized void intersectWithIDSet() {
		followeesSet.retainAll(idSet);
		followersSet.retainAll(idSet);
	}

	private synchronized void removeAlreadyProcessed() {
		followeesSet.removeAll(processedSet);
		followersSet.removeAll(processedSet);
	}

	private synchronized HopLimitedCrawlerQueue<Long>.Result popID() {
		long idThread = Thread.currentThread().getId();
		HopLimitedCrawlerQueue<Long>.Result result = null;
		//acquire lock on processedSet
		synchronized (processedSet) {
			//acquire lock on idToCrawl
			synchronized (idToCrawl) { //TODO: maybe remove
				//get id from idToCrawl
				result = idToCrawl.getElementAndHop();
				if (result != null) {
					System.out.println("Thread n° " + idThread + " Got element from IdSet: "
							+ result.element.toString() + " at hop: " + result.hop);
					//add getted id to processedSet
					processedSet.add(result.element);
				}
			}//release lock on idToCrawl
		}//release lock on processedSet
		return result;
	}

	private synchronized void addIDsToCrawl(int distance) {
		idToCrawl.addAll(distance, followeesSet);
		//idToCrawl.addAll(distance, followersSet);
	}

	private synchronized void addFollowers(long[] ids) {
		addIDs(followersSet, ids);
	}

	private synchronized void addFollowees(long[] ids) {
		addIDs(followeesSet, ids);
	}

	private static void addIDs(Set<Long> set, long[] ids) {
		for (long id : ids)
			set.add(id);
	}
}
