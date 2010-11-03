import java.util.HashMap;
import java.util.HashSet;
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
	private final boolean DEBUG = true;

	private TestWorld testWorld;

	private LinkedList<Position> queue;
	private LinkedList<Position> path;
	private HashSet<Position> visited;
	private HashSet<Position> adjacentToBase;
	private HashSet<Position> unwantedStates;

	private HashMap<Position, Direction> dirLookUp;
	private HashMap<Direction, int[]> posLookUp;

	private HashMap<Position, StateTesting> wumpusWorld;
	private KnowledgeBaseTest kb;
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
		kb = new KnowledgeBaseTest();

		// Set up a tables
		dirLookUp = new HashMap<Position, Direction>(10);
		posLookUp = new HashMap<Direction, int[]>(10);
		setUpTables();	

		adjacentToBase = new HashSet<Position>(8);
		unwantedStates = new HashSet<Position>();
		visited = new HashSet<Position>();

		wumpusWorld = new HashMap<Position, StateTesting>();

		/*int startPosition[] = testWorld.getStartPosition();
		Position current = new Position(startPosition[0], startPosition[1], 
				Direction.FORWARD);*/
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

	private StateTesting percept(Position current) {

		// Start Perceive
		Position newPosition = current.newPosition();
		StateTesting state = testWorld.getState(newPosition);
		state.setVisited();

		wumpusWorld.put(newPosition, state);
		visited.add(newPosition);

		return state;
	}

	private StatusTesting getStateStatus(StateTesting state) {
		int status = state.getValue();
		StatusTesting statusT = null;
		for(StatusTesting st : StatusTesting.values())
			if(status == st.getStatus()) statusT = st;

		return statusT;
	}

	private void infer(StateTesting state, StatusTesting st, Position current) {
		// Start infer
		switch(st) {
		case BORDER:
			// Set flag and go back (reverse)
			state.border = true;
			//reverse(current);
			break;
		case NOTHING:
			kb.setOk(current);
			state.nothing = true;
			state.setValue(st.getStatus());
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
	}

	private Position decideMove(Position current) {
		System.out.println("Base square : "+current);

		StateTesting state = percept(current);
		StatusTesting st = getStateStatus(state);
		infer(state, st, current);
		
		boolean deadEnd = true;
		int count = 0;
		Position newPos = null;
		System.out.printf("=== Adjacent to [%d,%d] ===\n", 
				current.getX(),current.getY());
		for(int[] pos : VALID_MOVES) {
			++count;
			newPos = current.newPosition(pos[0], pos[1]);
			Direction dir = dirLookUp.get(newPos);
			newPos.setHeading(dir);

			System.out.printf("  %d. %s\n",count,newPos.toString());

			if(!visited.contains(newPos) && !unwantedStates.contains(newPos)) {
				state = percept(newPos);
				adjacentToBase.add(newPos);
				st = getStateStatus(state);
				infer(state, st, newPos);
				deadEnd = false;
			}
		}
		if(deadEnd) reverse(newPos);
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
	public StateTesting[] getAdjacentStates(Position position) {
		final int[][] adjacentLocations = Agent.VALID_MOVES;
		int length = adjacentLocations.length;
		StateTesting[] adjacentStates = new StateTesting[length];
		Position newPosition;
		int i = 0;
		for(int[] pos : adjacentLocations) {
			int x = position.getX() + pos[0];
			int y = position.getY() + pos[1];
			newPosition = new Position(x, y);
			//if(testWorld.stateExists(newPosition))
			adjacentStates[i] = testWorld.getState(newPosition);
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
	public StateTesting getState(Position position) {
		return wumpusWorld.get(position);
	}
}