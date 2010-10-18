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
	
	private int time = 0;
	private boolean wumpusAlive = true, gameOver = false;
	
	public static final int[][] VALID_MOVES = {
			{-1,-1}, {1,1}, {1,-1}, {-1,1},	// diagonal moves
			{-1,0}, {1,0},					// horizontal moves
			{0,-1}, {0,1}, 					// vertical moves
	};
	
	public static void main(String args[]) {
		new Agent();
	}
	
	Agent() {
		light = new LightSensor(SensorPort.S3, true);
		sound = new SoundSensor(SensorPort.S1);	// not sure which port
		
		wumpusWorld = new WumpusWorld(light);
		
		move(new Position(0, 0));
	}
	
	void move(Position position) {
		// Step 1. Percept
		State state = percept();
		
		// Step 2. Logically infer what to do next 
		
		// Step 3. Move out from information gathered above
		
	}
	
	/**
	 * Perceives data about the real world trough a ligth sensor
	 * 
	 * @return State newly discovered
	 */
	private State percept() {
		// Read in the value of the current square
		int lightValue = light.readValue();
		
		// Create a state of newly discovered square 
		return wumpusWorld.newState(position, lightValue);
	}
	
	/**
	 * Checks if two squares are adjacent or not
	 * 
	 * @param s1 State s1
	 * @param s2 State s2
	 * @return true iff adjacent
	 */
	private boolean adjacent(State s1, State s2) { 
		/*
		 * A square [i,j] is adjacent to [x,y] if, (fA x,y,i,j) |x-i| or |y-j|
		 * Formally: (fA i,j,x,y) Adjacent([i,j], [x,y]) <=> (|x-i| or |y-j|) 
		 */
		int x = s1.position.x, y = s1.position.y, i = s2.position.x, j = s2.position.y;
		
		if(x+1 == i && y == j ||  x-1 == i && y == j 
				|| x == i && y+1 == j || x == i && y-1 == j) return true;
		
		return false;
	}
	
	/**
	 * If the state we are currently in is neither
	 * breezy nor smelly, then we can conclude that
	 * the adjacent squares are free from the wumpus
	 * or the pit
	 * 
	 * @param state State currently in
	 */
	public void isOk(State state) {
		State[] adjacentStates = wumpusWorld.getAdjacentStates(state);

		for(State s : adjacentStates) {
			s.pit = -1;
			s.wumpus = -1;
			s.ok = true;
		}
	}
	
	/**
	 * If the state we currently are in is breezy,
	 * then we can conclude that any of its adjacent
	 * squares can be a pit
	 * 
	 * @param state State currently in
	 */
	private void isBreezy(State state) {
		/*
		 * A square [x,y] is breezy if adjacent to a pit ( and [x,y] might
		 * be a pit of some adjacent squares are breezy. )
		 * Formally: (fA s) Breezy(s) (<=)=> (tE r) Adjacent(r,s) & Pit(r)
		 */
		State[] adjacentStates = wumpusWorld.getAdjacentStates(state);
		
		for(State s : adjacentStates) {
			// If square is not ok and they are adjacent (double check..), it might be a pit
			if(!s.ok && adjacent(state, s)) {
				s.pit += 1;		// Increase the chance that there is a pit
			}
		}		
	}
	
	/** If the state we currently are in is smelly,
	 * then we can conclude that any of its adjacent
	 * squares can be the wumpus
	 * 
	 * @param state State currently in
	 */
	private void isSmelly(State state) {
		/*
		 * A square [x,y] is smelly if adjacent to the wumpus ( and [x,y] might
		 * be the wumpus of some adjacent squares are smelly. )
		 * Formally: (fA s) Breezy(s) (<=)=> (tE r) Adjacent(r,s) & Pit(r)
		 */
		State[] adjacentStates = wumpusWorld.getAdjacentStates(state);
		
		for(State s : adjacentStates) {
			// If square is not ok and they are adjacent (double check..), it might be the wumpus
			if(!s.ok && adjacent(state, s)) {
				s.wumpus += 1;		// Increase the chance that there is a wumpus in that square
			}
		}		
	}
	
	/**
	 * Just a stupid welcome message
	 * 
	 * @throws InterruptedException
	 */
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