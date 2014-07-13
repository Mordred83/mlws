package task1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class HopLimitedBlockingQueue<T> implements BlockingQueue<T> {

	private final ArrayList<LinkedBlockingQueue<T>>	queues;

	public HopLimitedBlockingQueue(int hops) {
		queues = new ArrayList<LinkedBlockingQueue<T>>(hops);
		for (int i = 0; i < hops; i++) {
			queues.add(new LinkedBlockingQueue<T>());
		}
	}

	@Override
	public synchronized T remove() {
		if (queues.size() > 0) {
			BlockingQueue<T> queue = queues.get(0);
			if (queue.size() == 1) queues.remove(0);
			return queue.remove();
		}
		throw new NoSuchElementException();

	}

	@Override
	public synchronized T poll() {
		if (queues.size() > 0) {
			BlockingQueue<T> queue = queues.get(0);
			if (queue.size() == 1) queues.remove(0);
			return queue.poll();
		}
		return null;
	}

	@Override
	public synchronized T element() {
		if (queues.size() > 0) {
			BlockingQueue<T> queue = queues.get(0);
			return queue.element();
		}
		throw new NoSuchElementException();
	}

	@Override
	public synchronized T peek() {
		if (queues.size() > 0) {
			BlockingQueue<T> queue = queues.get(0);
			return queue.peek();
		}
		return null;
	}

	@Override
	public synchronized int size() {
		int result = 0;
		for (BlockingQueue<T> queue : queues)
			result += queue.size();
		return result;
	}

	@Override
	public synchronized boolean isEmpty() {
		return size() > 0;
	}

	@Override
	public synchronized Iterator<T> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized boolean containsAll(Collection<?> c) {
		boolean result = false;
		for (Object o : c) {
			result = false;
			for (BlockingQueue<T> queue : queues) {
				if (result = queue.contains(o)) break; // Found the element in a queue;
			}
			if (result == false) return false; //Object not in any queue
		}
		return true;// found an element for any object in collection c
	}

	@Override
	public synchronized boolean addAll(Collection<? extends T> c) {
		boolean result = false;
		for (Object o : c) {
			@SuppressWarnings("unchecked")
			//method signature implies safety of this cast
			T element = (T) o;
			if (add(element) && result == false) //true only on first element added successfully due to following assignation
				result = true;
		}
		return result;
	}

	@Override
	public synchronized boolean removeAll(Collection<?> c) {
		boolean result = false;
		for (BlockingQueue<T> queue : queues) {
			if (queue.removeAll(c) && result == false) {//true only on first list changed due to following assignation
				result = true;
			}
		}
		return result;
	}

	@Override
	public synchronized boolean retainAll(Collection<?> c) {
		boolean result = false;
		for (BlockingQueue<T> queue : queues) {
			if (queue.retainAll(c) && result == false) {//true only on first list changed due to following assignation
				result = true;
			}
		}
		return result;
	}

	@Override
	public synchronized void clear() {
		for (BlockingQueue<T> queue : queues) {
			queue.clear();
		}
	}

	@Override
	public synchronized boolean add(T e) {
		if (queues.isEmpty() == false) return queues.get(0).add(e);
		throw new IllegalStateException();
	}

	@Override
	public synchronized boolean offer(T e) {
		// XXX: Implementation implies no bounds on capacity returning add
		if (queues.size() > 0) {
			try {
				return add(e);
			} catch (IllegalStateException ex) {
				return false;
			}
		}
		return false;
	}

	@Override
	public synchronized void put(T e) throws InterruptedException {
		// XXX: Implementation implies no bounds on capacity
		if (queues.size() > 0) {
			try {
				add(e);
			} catch (IllegalStateException ex) {
				InterruptedException iEx = new HopLimitReachedInterruptedException();
				iEx.setStackTrace(ex.getStackTrace());
				throw iEx;
			}
		}
	}

	@Override
	public synchronized boolean offer(T e, long timeout, TimeUnit unit) throws InterruptedException {
		if (queues.size() > 0) { return queues.get(0).offer(e, timeout, unit); }
		return false;
	}

	@Override
	public synchronized T take() throws InterruptedException {
		if (queues.size() > 0) { return queues.get(0).take(); }
		throw new HopLimitReachedInterruptedException();
	}

	@Override
	public synchronized T poll(long timeout, TimeUnit unit) throws InterruptedException {
		if (queues.size() > 0) { return queues.get(0).poll(timeout, unit); }
		throw new HopLimitReachedInterruptedException();
	}

	@Override
	public synchronized int remainingCapacity() {
		return queues.size();
	}

	@Override
	public synchronized boolean remove(Object o) {
		if (queues.size() > 0) {
			int ind = 0;
			boolean result = false;
			while (result == false && ind < queues.size()){
				BlockingQueue<T> queue = null;
				result = queues.get(ind++).remove(o);
			}
			return result;
		}
		return false;
	}

	@Override
	public synchronized boolean contains(Object o) {
		if (queues.size() > 0) {
			int ind = 0;
			boolean result = false;
			while (result == false && ind < queues.size())
				result = queues.get(ind++).contains(o);
			return result;
		}
		return false;
	}

	@Override
	public int drainTo(Collection<? super T> c) {
		
		return 0;
	}

	@Override
	public int drainTo(Collection<? super T> c, int maxElements) {
		// TODO Auto-generated method stub
		return 0;
	}

}
