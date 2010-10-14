/**
 * 
 * @author a0075840a and a0075885l
 *
 */
public class Agent {
	private WumpusWorld wumpusWorld;
	private Location location;
	
	private static class Location {
		Position prev = null;
		Position cur = null;
		
		Location(Position cur) {
			this.cur = cur;
		}
	}
	
	private static class Position {
		int x, y;
		
		Position(int x, int y) { 
			this.x = x; this.y = y; 
		}
	}
	
	Agent() {
		
	}
	
	void welcomeMsg() throws InterruptedException {
		System.out.println("What took you so long?!\n"+
				"Been waiting forever for you to bring me back to life");
		System.out.println("Initiliazing envoirnment settings");
		Thread.sleep(10);
		System.out.print(".");
		Thread.sleep(10);
		System.out.print(".");
		Thread.sleep(10);
		System.out.print(".");
		System.out.println("System updated and ready");
	}
}
