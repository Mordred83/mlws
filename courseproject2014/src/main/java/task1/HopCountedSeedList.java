package task1;


public interface HopCountedSeedList<T> extends SeedList<T> {
	
	public Result<T> getElementAndHop() ;
	
	public class Result<T>{
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
