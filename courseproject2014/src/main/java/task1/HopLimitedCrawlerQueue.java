package task1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.python.modules.synchronize;


public class HopLimitedCrawlerQueue<T> {
	
	private final ArrayList<LinkedBlockingQueue<T>>	queues;

	public HopLimitedCrawlerQueue(int hops) {
		int listSize = hops+1;
		queues = new ArrayList<LinkedBlockingQueue<T>>(listSize);
		for (int i = 0; i < listSize; i++) {
			queues.add(new LinkedBlockingQueue<T>());
		}
	}
	
	public HopLimitedCrawlerQueue(int hops, Collection <T> startingSet) {
		this(hops);
		queues.get(0).addAll(startingSet);
	}
	
	public synchronized Result getElementAndHop(){
		for(int i=0; i<queues.size(); i++){
			BlockingQueue<T> queue = queues.get(i);
			if(queue.isEmpty() == false){
				return new Result(i, queue.remove());
			}
		}
		return null;
	}
	
	public synchronized boolean addElement(int distance, T element){
		if(distance < queues.size()){
			BlockingQueue<T> queue = queues.get(distance);
			return queue.add(element);
		}throw new IllegalArgumentException("Distance exceeds the hop limit");
	}
	
	public synchronized boolean addAll(int distance, Collection<? extends T> elements){
		if(distance < queues.size()){
			boolean result = false;
			BlockingQueue<T> queue = queues.get(distance);
			for(T element : elements){
				result = queue.add(element) || result;
			}
			return result;
		}throw new IllegalArgumentException("Distance: "+distance+" exceeds the hop limit: "+queues.size());
	}
	
	public synchronized int getMaxHops(){
		return queues.size()-1;
	}
	
	public class Result{
		public final T element;
		public final int hop; 
		private Result(int hop, T element){
			this.element = element;
			this.hop = hop;
		}
		
		public T getElement(){
			return element;
		}
		
		public int getHop(){
			return hop;
		}
	}
}
