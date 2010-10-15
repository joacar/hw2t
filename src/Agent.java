import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.SoundSensor;


/**
 * The AI for the classic and legendary game Wumpus world!
 * 
 * The AI starts with percieving to see if anything new about
 * the world can be noticed. If so, it takes that new
 * information under considerations and via logic reasoning
 * and a knowledge base decides what the most appropriate
 * action to that percept and logic conclusion is. 
 * 
 * The sequence described above is looped over and over
 * until it reaches it final state. That is the gold is
 * collected and the robot is back to its starting location.
 * 
 * @author a0075840a and a0075885l
 *
 */
public class Agent {
	private WumpusWorld wumpusWorld;
	private Position position;
	private LightSensor light;
	private SoundSensor sound;
	
	/**
	 * Just a simple holder for
	 * x and y coordinates
	 * 
	 * @author a0075840a and a0075885l
	 *
	 */
	public static class Position {
		int x, y;
		
		Position(int x, int y) { 
			this.x = x; this.y = y; 
		}
	}
	public static void main(String args[]) {
		new Agent();
	}
	
	Agent() {		
		light = new LightSensor(SensorPort.S3, true);
		sound = new SoundSensor(SensorPort.S1);	// not sure which port
		
		wumpusWorld = new WumpusWorld(light);
	}
	
	void knowledgeBase() {
		/*
		 * A square [x,y] is breezy if adjacent to a pit ( and [x,y] might
		 * be a pit of some adjacent squares are breezy. )
		 * Formally: (fA s) Breezy(s) (<=)=> (tE r) Adjacent(r,s) & Pit(r)
		 */
		// boolean Breezy(Position p) { State[] adj = getAdj(p); for(State s : adj) if(s.BREEZE & s.PIT ?) return true; return false; }
		
		/*
		 * A square [x,y] is smelly if adjacent to wumpus ( and [x,y] might
		 * contain the wumpus of some adjacent squares are smelly. )
		 * Formally: (fA s) Smelly(s) (<=)=> (tE r) Adjacent(r,s) & Wumpus(r)
		 */
		// boolean Smelly(Position p) { State[] adj = getAdj(p); for(State s : adj) if(s.STENCH & s.WUMPUS ?) return true; return false; }
		
		/*
		 * A pit is OK if there is neither pit nor wumpus in [x,y]
		 * Formally: (fA p) not(Pit(p)) or not(Wumpus(p)) => Ok(p) 
		 */
		// boolean Ok(Position p) { return not(Pit(p)) || not(Wumpus(p)) }
	}
	
	/**
	 * Checks if two squares are adjacent or not
	 * 
	 * @param p1 Position p1
	 * @param p2 Position p2
	 * @return true iff adjacent
	 */
	private boolean adjacent(Position p1, Position p2) { 
		/*
		 * A square [i,j] is adjacent to [x,y] if, (fA x,y,i,j) |x-i| or |y-j|
		 * Formally: (fA i,j,x,y) Adjacent([i,j], [x,y]) <=> (|x-i| or |y-j|) 
		 */
		return (Math.max(Math.abs(p1.x-p2.x), Math.abs(p1.y-p2.y)) == 1) ? true : false; 
	}
	
	
	void welcomeMsg() throws InterruptedException {
		System.out.println("What took you so long?!\n"+
				"Been waiting forever for you to bring me back to life");		
		System.out.println("Configuring system.");
		Thread.sleep(10);
		System.out.print(".");
		Thread.sleep(10);
		System.out.print(".");
		Thread.sleep(10);
		System.out.print(".");
		System.out.println("System updated and ready");
	}
}
