import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
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
public class AgentTesting {
	public static final boolean DEBUG = true;
	
	private final String STATUS_STRING[] = {
			"DUMMY :D", "BORDER", "NOTHING", "BREEZE", "STENCH", "GLITTER", 
			"STENCH_BREEZE", "GLITTER_BREEZE", "GLITTER_STENCH"
			, "STENCH_GLITTER_BREEZE" };
	
	public static final byte MASK_B[] = {0,0,1,0,0};
	public static final byte MASK_S[] = {0,0,0,1,0};
	public static final byte MASK_G[] = {0,0,0,0,1};
	public static final byte MASK_N[] = {1,0,0,0,0};
	public static final byte MASK_O[] = {0,1,0,0,0};

	private TestWorld testWorld;

	private LinkedList<Position> queue;
	private LinkedList<Position> path;
	private HashSet<Position> visited;
	private HashSet<Position> adjacentToBase;
	private HashSet<Position> unwantedStates;
	
	private Hashtable<Position, Direction> dirLookUp;
	private Hashtable<Direction, int[]> posLookUp;
	private Hashtable<Integer, EnumSet<StatusT>> statusLookUp;
	private Hashtable<Position, StateT> wumpusWorld;
	
	private KnowledgeBaseTest kb;
	private Position current, previous;

	private boolean fetchedGold = false;

	public static final int[][] VALID_MOVES = {
		//{-1,0}, {1,0},					// horizontal moves
		//{0,-1}, {0,1}, 					// vertical moves
		//{-1,-1}, {1,1}, {1,-1}, {-1,1},	// diagonal moves 
		{1,1}, {1,0}, {1,-1}, {0,-1}, {-1,-1}, {-1,0}, {-1,1}, {0,1} 	
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
		kb = new KnowledgeBaseTest(this);

		// Set up a tables
		dirLookUp = new Hashtable<Position, Direction>(10);
		posLookUp = new Hashtable<Direction, int[]>(10);
		setUpTables();


		adjacentToBase = new HashSet<Position>(8);
		unwantedStates = new HashSet<Position>();
		visited = new HashSet<Position>();
				
		wumpusWorld = new Hashtable<Position, StateT>();

		Position current = new Position(0,0, Direction.FORWARD);
		int cnt = 0;
		Position t;
		do {

			System.out.printf("\ndecideMove called %d times\n\n",++cnt);
			t = decideMove(current);
			previous = current;
			current = t;
		} while(true);
	}
	
	public boolean addState(StateT state, Position position) {
		if(wumpusWorld.contains(position)) return false;
		
		wumpusWorld.put(position, state);
		return true;
	}

	private StateT percept(Position current) {

		// Make the new position for this state
		Position position = current.newPosition();
		
		// Get the value (percept)
		int value = testWorld.getPercept(position);
		StateT state = new StateT(position, true); 
		state.setValue(value);
		
		// Find the corresponding status to that value
		switch(value) {
		case -1:
			state.setStatus(MASK_O);
		case 0:
			state.setStatus(MASK_N);
		case 1:
			state.setStatus(MASK_B);
		case 2:
			state.setStatus(MASK_S);
		case 3:
			state.setStatus(MASK_G);
		case 4:
			state.setStatus(MASK_B);
			state.or(MASK_S);
		case 5:
			state.setStatus(MASK_B);
			state.or(MASK_G);
		case 6:
			state.setStatus(MASK_S);
			state.or(MASK_G);
		case 7:
			state.setStatus(MASK_B);
			state.or(MASK_S);
			state.or(MASK_G);
		}
		
		wumpusWorld.put(position, state);
		visited.add(position);

		return state;
	}

	private void infer(StateT state, Position current) {
		int value = state.getValue();
		
		// Start infer
		switch(value) {
		case -1:
			// Set flag and go back (reverse)
			state.and(MASK_O);
			break;
		case 0:
			kb.setOk(current, wumpusWorld);
			state.nothing = true;
			// update stats
			break;
		case 1:
			state.status = EnumSet.of(st);
			kb.setPitPossibility(current);
			break;
		case 2:
			kb.setWumpusPossibility(current);
			break;
		case 3:
			kb.setOk(current, wumpusWorld);
			state.glitter = true;
			break;
		case 4:
			kb.setWumpusPossibility(current);
			kb.setPitPossibility(current);
			break;
		case 5:
			kb.setPitPossibility(current);
			state.glitter = true;
			break;
		case 6:
			kb.setWumpusPossibility(current);
			state.glitter = true;
			break;
		case 7:
			kb.setWumpusPossibility(current);
			kb.setPitPossibility(current);
			state.glitter = true;
			break;	
		}
		
		System.out.printf("Infer %s at %s\n",STATUS_STRING[value+1],current.toString());

		if(state.glitter) {
			/*
			 * The gold is found and thereby our mission is done. 
			 * It is time for the great escape
			 */
			fetchedTheGold();
		}
		
	}

	private Position decideMove(Position current) {
		//printWorld(current, true);
		//System.out.println("Base square : "+current);

		StateT state = percept(current);
		infer(state, current);

		Position lastKnownSafePosition = null;
		
		
		int count = 0;
		Position newPos = null;
		System.out.printf("=== Adjacent to [%d,%d] ===\n", 
				current.getX(),current.getY());
		for(int[] pos : VALID_MOVES) {
			++count;
			newPos = current.newPosition(pos[0], pos[1]);
			Direction dir = dirLookUp.get(newPos);
			newPos.setHeading(dir);


			if(!visited.contains(newPos) && !unwantedStates.contains(newPos)) {
				state = percept(newPos);
				adjacentToBase.add(newPos);
				
				System.out.printf("  %d. %s status %s\n",count,newPos.toString(),getStateStatus(state));

				infer(state, newPos);

			}
		}
		// We couldnt move from our new base. Go back to old base.
		if(lastKnownSafePosition == null);
		return newPos;
	}

	/**
	 * Moves to the direction we are currently looking
	 */
	private void move(Position current) {
		if(current == null) System.out.println(true);
		int[] move = posLookUp.get(current.getHeading());
		current.change(move);
	}

	/**
	 * Go back to our previous location facing the
	 * same direction as when we entered the state
	 */
	private void reverse(Position current) {
		int[] undo = new int[2];
		for(Direction dir : Direction.values())
			if(dir.getHeading() == -current.getHeadingInt())
				undo = posLookUp.get(dir);

		System.out.printf("\tReverse [%d,%d]",current.getX(), current.getY());
		current.change(undo);
		System.out.printf(" back to [%d,%d]\n", current.getX(), current.getY());
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
			int[] pos = {x,y};
			Position position = new Position(x,y);
			
			dirLookUp.put(position, dir);
			posLookUp.put(dir, pos);
		}
	}

	/**
	 * Returns the adjacent states to the position specified
	 * 
	 * @param position current
	 * @return array of adjacent states
	 */
	public StateT[] getAdjacentStates(Position position, boolean ourWorld) {
		final int[][] adjacentLocations = Agent.VALID_MOVES;
		int length = adjacentLocations.length;
		StateT[] adjacentStates = new StateT[length];
		Position newPosition;
		int i = 0;
		for(int[] pos : adjacentLocations) {
			int x = position.getX() + pos[0];
			int y = position.getY() + pos[1];
			newPosition = new Position(x, y);
			if(ourWorld) {
				if(wumpusWorld.containsKey(newPosition))
					adjacentStates[i] = wumpusWorld.get(newPosition);
			} else {
				if(testWorld.stateExists(newPosition)) {// Should never fail.
					adjacentStates[i] = testWorld.getState(newPosition);
				} else {
					System.err.println("=== FAIL AT getAdjcentStates when"
							+" ourWorld = "+ourWorld);
				}
			}
			i += 1;
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

	/**
	 * Return the state corresponding to the position
	 * 
	 * @param position key to state
	 * @return state in our world
	 */
	public StateT getState(Position position) {
		return wumpusWorld.get(position);
	}
	
	private void printWorld(Position position, boolean world) {
		StateT sta[] = getAdjacentStates(position, world);
		if(world) {
			System.out.println("The world wumpusWorld, ie our robots knowledge");
		} else {
			System.out.println("The test world from testWorld");
		}
		if(sta != null)
			for(StateT s : sta) {
				if(s != null) System.out.println(s.toString());
			}
	}
}