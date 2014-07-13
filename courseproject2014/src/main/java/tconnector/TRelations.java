package tconnector;

import java.util.HashSet;
import java.util.Set;


public class TRelations {
	
	private final long id;
	private Set<Long> followees;
	private Set<Long> followers;
	
	/**
	 * Costruttore che lega uno user ai suoi followers e followees
	 * @param id id dello user
	 */
	public TRelations(long id){
		this.id = id;
		this.followees = new HashSet<Long>();
		this.followers = new HashSet<Long>();
	}
	
	public boolean addFollower(long id){
		return add(followers, id);
	}
	
	public boolean removeFollower(long id){
		return remove(followers, id);
	}
	
	public boolean containsFollower(long id){
		return contains(followers, id);
	}
	
	public boolean addFollowee(long id){
		return add(followees, id);
	}
	
	public boolean removeFollowee(long id){
		return remove(followees, id);
	}
	
	public boolean containsFollowee(long id){
		return contains(followees, id);
	}
	
	private static boolean add(Set<Long> set, long name){
		return set.add(name);
	}
	
	private static boolean remove(Set<Long> set, long name){
		return set.remove(name);
	}
	
	private static boolean contains(Set<Long> set, long name){
		return set.contains(name);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof TRelations)) return false;
		else return this.id == ((TRelations)obj).id;
	}
}
