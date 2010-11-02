import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.SoundSensor;
import robot.CalibrateLightSensor;
import robot.Move;
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
public class Agent implements Ai {
	private final float TRAVEL_STRAIGHT = 27.9f;
	private final float TRAVEL_PERP = 21.6f;
	private final float TRAVEL_DIAG = 35.3f;
	
	private LightSensor light;
	private SoundSensor sound;
	private Move move;
	
	private WumpusWorld wumpusWorld;
	private LinkedList<Position> queue;
	private LinkedList<Position> path;
	private HashSet<Position> visited;
	private HashSet<Position> dangerousStates;
	
	private HashMap<Position, Float> dirLookUp;
	private boolean wumpusAlive = true, fetchedGold = false;
	//private final boolean array[] = new boolean[4];

	public static final int[][] VALID_MOVES = {
		{-1,0}, {1,0},					// horizontal moves
		{0,-1}, {0,1}, 					// vertical moves
		{-1,-1}, {1,1}, {1,-1}, {-1,1},	// diagonal moves
		
	};
	
	/**
	 * Entry point. No args needed at moment.
	 *  
	 * @param args
	 */
	public static void main(String args[]) {
		new Agent();
	}
	
	/**
	 * Basic constructor
	 */
	private Agent() {
		// Set up the movement. Parameters are (Motor left, Motor right)
		move = new Move(Motor.A, Motor.C);
		// Initialize light sensor
		light = new LightSensor(SensorPort.S3, true);
		// Calibrate the newly created light sensor
		int squareValues[] = new CalibrateLightSensor(light).getLightValues();
		// Set up wumpus world with the values obtained from calibrating
		wumpusWorld = new WumpusWorld(squareValues);
		
		// Set up a directions table
		dirLookUp = new HashMap<Position, Float>(8);
		setUpDirLookUp();
		
		/*
		 *  The start location is always [0,0] and we will always
		 *  move "forward" to [1,0] facing "north". Actually we 
		 *  have no clue about positions or compass directions,
		 *  they are all relative to our starting location. 
		 */
		Position origin = new Position(0, 0);
		origin.setHeading(Direction.FORWARD);
		
		move.travel(TRAVEL_STRAIGHT);
		
		
		// Movement to the origin
		State base = percept(origin);
		
		queue.add(origin);
		while(!fetchedGold) {
			// Fetch all the adjacent squares and add the safe ones to the queue
			State adjacent[] = wumpusWorld.getAdjacentStates(base);
			for(State s : adjacent) {
				if(!visited.contains(s) && s.wumpus <= 0 && s.pit <= 0) {
					queue.add(s.position);
					visited.add(s.position);
				}
			}
			
			// For those adjacent squares added, search them and go back to base
			while(!queue.isEmpty()) {
				// Get the next position
				Position next = queue.poll();
				// Move the robot to that position
				// Actual movement needed
				State state = percept(next);
				// If state is out of range, then go straight back
				if(state == null) {
					// Go back 
				} else {
					// Infer what we can from the kb
					infer(state);
				}
				
				// If queue is empty, make the last adjacent square the new base
				if(queue.isEmpty()) {
					/*
					 *  If last square was out of range, then we have to think
					 *  of something else. Perheps chose one of the adjacent
					 *  by random and make it the new base.
					 */
					if(state == null) {
						
					}
					base = state;
					break;
				}
				
				// Move the robot back to the base position
				// Actual movement needed
			}
		}
	}
	
	/**
	 * Index the relative positions to their corresponding
	 * direction as a float value. Instead of calculating
	 * each direction trough the changes in the coordinates
	 * they can be accessed instantly with this table
	 */
	private void setUpDirLookUp() {
		/*
		 * <=== IMPORTANT ===>
		 * Must be sorted the same way as Direction enum
		 */
		int[][] validMoves = {
			{1,1}, {1,0}, {1,-1}, {0,-1}, {-1,-1}, {0,-1}, {-1,1}, {0,1} 
		};
		
		int i = 0;
		for(Direction dir : Direction.values()) {
			i = dir.ordinal();
			int x = validMoves[i][0];
			int y = validMoves[i][1];
			
			dirLookUp.put(new Position(x,y), new Float(dir.getHeading()));
		}
	}
	
	private int explore(Position curPosition, Position prevPosition) {
		// Step 1. Percept
		State state = percept(curPosition);

		/*
		 *  If we went outside the board, the state returned
		 *  will be null. Update the world limits relative to
		 *  our position accordingly so we wont make this
		 *  misstake again, go back to where we came from
		 *  and figure out the next move.
		 */
		if(state == null) {
			/*
			 *  Go back to where we came from by rotating 180 degrees
			 *  and then move forward the correct distance.
			 */
			queue.addFirst(prevPosition);
			
			return -1;
		}

		// Step 2. Logically infer the status of adjacent squares 
		infer(state);

		// Step 3. Move out from information gathered above
		makeMove(state);
		
		return 0;
		
	}
	
	private void makeMove(State state) {
	}

	/**
	 * From the current state, using our knowledge base, we
	 * are able to draw some conclusions about the adjacent
	 * squares and make new statements about the unknown
	 * world surrounding our pour agent.
	 * 
	 * @param state State currently in
	 */
	private void infer(State state) {
		if(state == null)	return;
		
		if(state.nothing) {	// !state.breeze && !state.stench
			setOk(state);
		} else {
			int statusInt = 0;

			for(int i = 1; i < 4; i++)  
				if(state.states[i])	
					statusInt += i;

			if(statusInt > 3) statusInt += 1;

			switch(statusInt) {
			case 1:		// breezy
				setPitPossibility(state);
			case 2:		// smelly
				setWumpusPossibility(state);
			case 3: 	// glittery
				fetchedGold = true;
			case 4: 	// breezy and smelly
				setPitPossibility(state);
				setWumpusPossibility(state);
			case 5:		// breezy and glittery
				setPitPossibility(state);
				fetchedGold = true;
			case 6:		// smelly and glittery
				setWumpusPossibility(state);
				fetchedGold = true;
			case 7:		// breezy, smelly and glittery
				setPitPossibility(state);
				setWumpusPossibility(state);
				fetchedGold = true;
			}

			if(fetchedGold) {
				fetchedTheGold();
			}
		}
	}
	
	/**
	 * Returns the adjacent states to the position specified
	 * 
	 * @param position current
	 * @return array of adjacent states
	 */
	public State[] getAdjacentStates(Position position) {
		final int[][] adjacentLocations = Agent.VALID_MOVES;
		int length = adjacentLocations.length;
		State[] adjacentStates = new State[length];
		Position newPosition;
		
		for(int i = 0; i < length; i++) {
			int x = position.getX() + adjacentLocations[i][0];
			int y = position.getY() + adjacentLocations[i][1];
			newPosition = new Position(x, y);
			if(wumpusWorld.stateExists(newPosition))
				adjacentStates[i] = wumpusWorld.getState(newPosition);
		}
		
		return adjacentStates; 
	}
	/**
	 * Just a functions that indicates that we have found and
	 * retrieved the gold
	 */
	private void fetchedTheGold() {
		System.out.println("I'm rich! Now it's time"+
		" to get the f*ck out of this world!");
		Sound.beep();
	}

	/**
	 * Perceives data about the real world trough a ligth sensor
	 * 
	 * @return State newly discovered
	 */
	private State percept(Position position) {
		// Implement the actual movement of the robot
		// ======== IMPORTANT =========
		
		// Read in the value of the current square
		int lightValue = light.readValue();

		// Create a state of newly discovered square 
		State state = wumpusWorld.newState(position, lightValue);
		// Add this state as visited
		visited.add(state.position);
		
		return state;
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
		int x = s1.position.getX(), y = s1.position.getY(), 
		i = s2.position.getX(), j = s2.position.getY();

		if(x+1 == i && y == j ||  x-1 == i && y == j 
				|| x == i && y+1 == j || x == i && y-1 == j) return true;

		return false;
	}

	/**
	 * If the state we are currently in is neither
	 * breezy nor smelly (it is nothing), then we 
	 * can conclude that the adjacent squares are 
	 * free from the either wumpus or the pit or
	 * both
	 * 
	 * @param state State currently in
	 */
	private void setOk(State state) {
		State[] adjacentStates = wumpusWorld.getAdjacentStates(state);

		for(State s : adjacentStates) {
			s.pit = -1;
			s.wumpus = -1;
		}
	}

	/**
	 * If the state we currently are in is breezy,
	 * then we can conclude that any of its adjacent
	 * squares can be a pit
	 * 
	 * @param state State currently in
	 */
	private void setPitPossibility(State state) {
		/*
		 * A square [x,y] is breezy if adjacent to a pit ( and [x,y] might
		 * be a pit of some adjacent squares are breezy. )
		 * Formally: (fA s) Breezy(s) (<=)=> (tE r) Adjacent(r,s) & Pit(r)
		 */
		State[] adjacentStates = wumpusWorld.getAdjacentStates(state);

		for(State s : adjacentStates) {
			// If square is not ok and they are adjacent (double check..), it might be a pit
			if(!s.nothing && adjacent(state, s)) {
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
	private void setWumpusPossibility(State state) {
		/*
		 * A square [x,y] is smelly if adjacent to the wumpus ( and [x,y] might
		 * be the wumpus of some adjacent squares are smelly. )
		 * Formally: (fA s) Breezy(s) (<=)=> (tE r) Adjacent(r,s) & Pit(r)
		 */
		State[] adjacentStates = wumpusWorld.getAdjacentStates(state);

		for(State s : adjacentStates) {
			/*
			 *  If square is _not_ containing anything and are adjacent to a smelly
			 *  square that square might be home of the horrible wumpus
			 */
			if(!s.nothing && adjacent(state, s)) {
				s.wumpus += 1;		// Increase the chance that there is a wumpus in that square
			}
		}		
	}

	/**
	 * Just a stupid welcome message
	 * 
	 * @throws InterruptedException
	 */
	private void welcomeMsg() throws InterruptedException {
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

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}