package task1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;



public class HopLimitedCrawlerQueue<T> {
	
	private final ArrayList<LinkedBlockingQueue<T>>	queues;
	private int counter ;

	public HopLimitedCrawlerQueue(int hops) {
		counter = 0;
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
				if(i<getMaxHops()) counter++;
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
			--counter;
			boolean result = false;
			BlockingQueue<T> queue = queues.get(distance);
			queue.addAll(elements);
			return result;
		}throw new IllegalArgumentException("Distance: "+distance+" exceeds the hop limit: "+queues.size());
	}
	
	public synchronized int getMaxHops(){
		return queues.size()-1;
	}
	
	public synchronized boolean asFinished(){
		return isEmpty() && counter < 1;
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

	public synchronized boolean isEmpty() {
		for(BlockingQueue<T> queue : queues){
			if(queue.isEmpty()==false) return false;
		}
		return true;
	}
}
