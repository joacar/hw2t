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
public class AgentTesting implements Ai {
	public enum StatusTesting {
		BORDER(-1), NOTHING(0), BREEZE(1), STENCH(2), GLITTER(3), STENCH_BREEZE(4),
		GLITTER_BREEZE(5), GLITTER_STENCH(6), STENCH_GLITTER_BREEZE(7);
		
		private final int status;
		StatusTesting(int status) { this.status = status; } 
		int getStatus() { return status; }
	}
	
	private final boolean DEBUG = true;
	
	private TestWorld testWorld;
	
	private LinkedList<Position> queue;
	private LinkedList<Position> path;
	private HashSet<Position> visited;
	
	private HashMap<Position, Direction> dirLookUp;
	private HashMap<Direction, Position> posLookUp;
	
	private HashMap<Position, State> wumpusWorld;
	private KnowledgeBase kb;
	private Position current, previous;
	
	private boolean fetchedGold = false;

	public static final int[][] VALID_MOVES = {
		{-1,0}, {1,0},					// horizontal moves
		{0,-1}, {0,1}, 					// vertical moves
		{-1,-1}, {1,1}, {1,-1}, {-1,1},	// diagonal moves
		
	};
	
	/**
	 * Entry point
	 *  
	 * @param args input test file
	 */
	public static void main(String args[]) {
		if(args.length != 1) {
			System.out.printf("Usage: \tjava AgentTesting <input_file>\n");
			System.exit(0);	
		}
		
		new AgentTesting(args[0]);
	}
	
	/**
	 * Constructor taking a input file
	 * 
	 * @param inputFile containg the world data
	 */
	private AgentTesting(String inputFile) {		
		// Load the test world with the input fule
		testWorld = new TestWorld(inputFile);
		
		// Load the knowledge base
		kb = new KnowledgeBase(this);
		
		// Set up a directions table
		dirLookUp = new HashMap<Position, Direction>(9);
		posLookUp = new HashMap<Direction, Position>(9);
		setUpTables();
		
		wumpusWorld = new HashMap<Position, State>();
		
		/*
		 *  Start location is [0,0] heading NORTH
		 *  
		 *  The start location will always be [0,0] and we will 
		 *  always move forward to [1,0] facing "north". 
		 *  Actually we have no clue about positions or compass 
		 *  directions, they are all relative to our starting 
		 *  location. 
		 */
		Position start = new Position(0, 0, Direction.FORWARD);
		State state = new State(start, true);
		state.setNothing();
		addState(state);
		
		Position next = move(start);
		previous = current;
		current = next;
		
		run();
		
	}
		
	/**
	 * Run
	 */
	public void run() {
		
		// Start Perceive
		State state = testWorld.getState(current);
		state.setVisited();
		addState(state);
		
		int status = state.getValue();
		StatusTesting statusT = null;
		for(StatusTesting st : StatusTesting.values())
			if(status == st.getStatus()) statusT = st;
		
		// End perceive
		
		if(DEBUG) System.err.println(current.toString()+" and perceived "+statusT+"("+status+")");
		
		// Start infer
		switch(statusT) {
		case BORDER:
			// Set flag and go back (reverse)
			state.border = true;
			reverse();
			current = previous;
			break;
		case NOTHING:
			kb.setOk(current);
			break;
		case BREEZE:
			kb.setPitPossibility(current);
			break;
		case STENCH:
			kb.setWumpusPossibility(current);
			break;
		case GLITTER:
			kb.setOk(current);
			state.glitter = true;
			break;
		case STENCH_BREEZE:
			kb.setWumpusPossibility(current);
			kb.setPitPossibility(current);
			break;
		case GLITTER_BREEZE:
			kb.setPitPossibility(current);
			state.glitter = true;
			break;
		case GLITTER_STENCH:
			kb.setWumpusPossibility(current);
			state.glitter = true;
			break;
		case STENCH_GLITTER_BREEZE:
			kb.setWumpusPossibility(current);
			kb.setPitPossibility(current);
			state.glitter = true;
			break;	
		}
		
		if(state.glitter) {
			/*
			 * The gold is found and thereby our mission is done. 
			 * It is time for the great escape
			 */
			fetchedTheGold();
		}
		
		// Consult database (infer)
		
	}
	
	/**
	 * Returns the next position depending on
	 * the direction the agent is looking
	 * 
	 * @param position current
	 * @return next position
	 */
	private Position move(Position position) {
		int x = position.getX(), y = position.getY();
		Direction heading = position.getHeading();
		
		switch(heading) {
		case FORWARD:
			return new Position(x+1, y, heading);
		case LEFT:
			return new Position(x, y+1, heading);
		case RIGHT:
			return new Position(x, y-1, heading);
		case BACK:
			return new Position(x-1, y, heading);
		case BACK_LEFT:
			return new Position(x-1, y+1, heading);
		case BACK_RIGHT:
			return new Position(x-1, y-1, heading);
		case FORWARD_LEFT:
			return new Position(x+1, y+1, heading);
		case FORWARD_RIGHT:
			return new Position(x+1, y-1, heading);
		default:
			return null; // Never gonna happen
		}
	}
	
	/**
	 * Go back to our previous location facing the
	 * same direction as when we entered the state
	 */
	private void reverse() {
		Position undo = posLookUp.get(Direction.BACK);
		
		current.add(undo); 
	}
	
	
	/**
	 * Index the relative positions to their corresponding
	 * direction as a float value. Instead of calculating
	 * each direction trough the changes in the coordinates
	 * they can be accessed instantly with this table
	 */
	private void setUpTables() {
		/*
		 * <=== IMPORTANT ===>
		 * Must be sorted the same way as Direction enum
		 */
		int[][] validMoves = {
			{1,1}, {1,0}, {1,-1}, {0,-1}, {-1,-1}, {-1,0}, {-1,1}, {0,1} 
		};
		
		int i = 0;
		for(Direction dir : Direction.values()) {
			i = dir.ordinal();
			int x = validMoves[i][0];
			int y = validMoves[i][1];
			Position position = new Position(x,y);
			
			dirLookUp.put(position, dir);
			posLookUp.put(dir, position);
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
			if(testWorld.stateExists(newPosition))
				adjacentStates[i] = testWorld.getState(newPosition);
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
		fetchedGold = true;
	}

	
	private void addState(State state) {
		wumpusWorld.put(state.position, state);
		visited.add(state.position);
	}
	
	public State getState(Position position) {
		return wumpusWorld.get(position);
	}
}